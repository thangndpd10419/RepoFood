package com.example.staff.util;

import com.example.staff.model.CartItem;
import com.example.staff.model.Product;

import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static CartManager instance;
    private List<CartItem> cartItems;
    private Integer selectedTable;
    private String orderNote;
    private List<CartChangeListener> listeners;

    public interface CartChangeListener {
        void onCartChanged();
    }

    private CartManager() {
        cartItems = new ArrayList<>();
        listeners = new ArrayList<>();
    }

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void addListener(CartChangeListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(CartChangeListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        for (CartChangeListener listener : listeners) {
            listener.onCartChanged();
        }
    }

    public void addToCart(Product product) {
        for (CartItem item : cartItems) {
            if (item.getProduct().getId().equals(product.getId())) {
                item.setQuantity(item.getQuantity() + 1);
                notifyListeners();
                return;
            }
        }
        cartItems.add(new CartItem(product, 1));
        notifyListeners();
    }

    public void removeFromCart(Product product) {
        cartItems.removeIf(item -> item.getProduct().getId().equals(product.getId()));
        notifyListeners();
    }

    public void updateQuantity(Product product, int quantity) {
        for (CartItem item : cartItems) {
            if (item.getProduct().getId().equals(product.getId())) {
                if (quantity <= 0) {
                    removeFromCart(product);
                } else {
                    item.setQuantity(quantity);
                    notifyListeners();
                }
                return;
            }
        }
    }

    public void increaseQuantity(Product product) {
        for (CartItem item : cartItems) {
            if (item.getProduct().getId().equals(product.getId())) {
                item.setQuantity(item.getQuantity() + 1);
                notifyListeners();
                return;
            }
        }
    }

    public void decreaseQuantity(Product product) {
        for (CartItem item : cartItems) {
            if (item.getProduct().getId().equals(product.getId())) {
                if (item.getQuantity() > 1) {
                    item.setQuantity(item.getQuantity() - 1);
                } else {
                    removeFromCart(product);
                }
                notifyListeners();
                return;
            }
        }
    }

    public List<CartItem> getCartItems() {
        return new ArrayList<>(cartItems);
    }

    public int getCartItemCount() {
        int count = 0;
        for (CartItem item : cartItems) {
            count += item.getQuantity();
        }
        return count;
    }

    public double getTotalPrice() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getTotalPrice();
        }
        return total;
    }

    public void clearCart() {
        cartItems.clear();
        selectedTable = null;
        orderNote = null;
        notifyListeners();
    }

    public Integer getSelectedTable() {
        return selectedTable;
    }

    public void setSelectedTable(Integer table) {
        this.selectedTable = table;
        notifyListeners();
    }

    public String getOrderNote() {
        return orderNote;
    }

    public void setOrderNote(String note) {
        this.orderNote = note;
    }

    public boolean isEmpty() {
        return cartItems.isEmpty();
    }

    public int getQuantityForProduct(Long productId) {
        for (CartItem item : cartItems) {
            if (item.getProduct().getId().equals(productId)) {
                return item.getQuantity();
            }
        }
        return 0;
    }
}
