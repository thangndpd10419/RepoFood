package com.example.foodbe.config;

import com.example.foodbe.models.*;
import com.example.foodbe.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) {
            log.info("Database already has data. Skipping seeder.");
            return;
        }

        log.info("Database is empty. Starting data seeding...");
        seedData();
    }

    private void seedData() {
        AppUser admin = AppUser.builder()
                .name("Admin")
                .email("admin@gmail.com")
                .password(passwordEncoder.encode("Admin@123"))
                .phone("0123456789")
                .status(UserStatus.ACTIVE)
                .role(Role.ADMIN)
                .build();

        AppUser staff1 = AppUser.builder()
                .name("Nguyễn Văn A")
                .email("staff1@gmail.com")
                .password(passwordEncoder.encode("Staff@123"))
                .phone("0123456780")
                .status(UserStatus.ACTIVE)
                .role(Role.STAFF)
                .build();

        AppUser staff2 = AppUser.builder()
                .name("Trần Thị B")
                .email("staff2@gmail.com")
                .password(passwordEncoder.encode("Staff@123"))
                .phone("0123456781")
                .status(UserStatus.ACTIVE)
                .role(Role.STAFF)
                .build();

        userRepository.saveAll(List.of(admin, staff1, staff2));
        log.info("Created 3 users");

        Category catFood = Category.builder()
                .name("Đồ ăn chính")
                .imgCategory("https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=400")
                .user(admin)
                .build();

        Category catDrink = Category.builder()
                .name("Đồ uống")
                .imgCategory("https://images.unsplash.com/photo-1544145945-f90425340c7e?w=400")
                .user(admin)
                .build();

        Category catDessert = Category.builder()
                .name("Tráng miệng")
                .imgCategory("https://images.unsplash.com/photo-1551024601-bec78aea704b?w=400")
                .user(admin)
                .build();

        Category catFastFood = Category.builder()
                .name("Đồ ăn nhanh")
                .imgCategory("https://images.unsplash.com/photo-1561758033-d89a9ad46330?w=400")
                .user(admin)
                .build();

        categoryRepository.saveAll(List.of(catFood, catDrink, catDessert, catFastFood));
        log.info("Created 4 categories");

        Product pho = Product.builder()
                .name("Phở bò tái nạm")
                .quality(100)
                .price(new BigDecimal("45000"))
                .imgProduct("https://images.unsplash.com/photo-1582878826629-29b7ad1cdc43?w=400")
                .description("Phở bò truyền thống Việt Nam, nước dùng ngọt thanh từ xương bò hầm 12 tiếng")
                .category(catFood)
                .build();

        Product bunCha = Product.builder()
                .name("Bún chả Hà Nội")
                .quality(80)
                .price(new BigDecimal("40000"))
                .imgProduct("https://images.unsplash.com/photo-1569058242567-93de6f36f8eb?w=400")
                .description("Bún chả đặc sản Hà Nội với thịt nướng thơm lừng, nước mắm chua ngọt")
                .category(catFood)
                .build();

        Product comTam = Product.builder()
                .name("Cơm tấm sườn bì chả")
                .quality(90)
                .price(new BigDecimal("35000"))
                .imgProduct("https://images.unsplash.com/photo-1512058564366-18510be2db19?w=400")
                .description("Cơm tấm Sài Gòn với sườn nướng, bì, chả trứng và nước mắm đặc biệt")
                .category(catFood)
                .build();

        Product banhMi = Product.builder()
                .name("Bánh mì thịt nguội")
                .quality(120)
                .price(new BigDecimal("25000"))
                .imgProduct("https://images.unsplash.com/photo-1600688640154-9619e002df30?w=400")
                .description("Bánh mì giòn rụm với pate, thịt nguội, rau thơm và nước sốt đặc biệt")
                .category(catFood)
                .build();

        Product bunBo = Product.builder()
                .name("Bún bò Huế")
                .quality(85)
                .price(new BigDecimal("50000"))
                .imgProduct("https://images.unsplash.com/photo-1576577445504-6af96477db52?w=400")
                .description("Bún bò Huế cay nồng đặc trưng, thịt bò mềm, chả cua thơm ngon")
                .category(catFood)
                .build();

        Product traSua = Product.builder()
                .name("Trà sữa trân châu đường đen")
                .quality(200)
                .price(new BigDecimal("25000"))
                .imgProduct("https://images.unsplash.com/photo-1558857563-b371033873b8?w=400")
                .description("Trà sữa thơm ngon với trân châu đường đen dẻo dai")
                .category(catDrink)
                .build();

        Product caphe = Product.builder()
                .name("Cà phê sữa đá")
                .quality(150)
                .price(new BigDecimal("20000"))
                .imgProduct("https://images.unsplash.com/photo-1461023058943-07fcbe16d735?w=400")
                .description("Cà phê phin truyền thống Việt Nam pha với sữa đặc")
                .category(catDrink)
                .build();

        Product nuocCam = Product.builder()
                .name("Nước cam tươi")
                .quality(100)
                .price(new BigDecimal("15000"))
                .imgProduct("https://images.unsplash.com/photo-1621506289937-a8e4df240d0b?w=400")
                .description("Nước cam ép tươi 100% không đường, giàu vitamin C")
                .category(catDrink)
                .build();

        Product sinhTo = Product.builder()
                .name("Sinh tố bơ")
                .quality(80)
                .price(new BigDecimal("30000"))
                .imgProduct("https://images.unsplash.com/photo-1623065422902-30a2d299bbe4?w=400")
                .description("Sinh tố bơ béo ngậy, thơm ngon, bổ dưỡng")
                .category(catDrink)
                .build();

        Product traDao = Product.builder()
                .name("Trà đào cam sả")
                .quality(120)
                .price(new BigDecimal("28000"))
                .imgProduct("https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=400")
                .description("Trà đào thơm mát với cam tươi và sả thơm lừng")
                .category(catDrink)
                .build();

        Product cheThai = Product.builder()
                .name("Chè Thái")
                .quality(50)
                .price(new BigDecimal("20000"))
                .imgProduct("https://images.unsplash.com/photo-1563805042-7684c019e1cb?w=400")
                .description("Chè Thái trái cây với nước cốt dừa béo ngậy")
                .category(catDessert)
                .build();

        Product kemBo = Product.builder()
                .name("Kem bơ")
                .quality(60)
                .price(new BigDecimal("25000"))
                .imgProduct("https://images.unsplash.com/photo-1497034825429-c343d7c6a68f?w=400")
                .description("Kem bơ mịn màng, thơm ngon, tan ngay trong miệng")
                .category(catDessert)
                .build();

        Product banhFlan = Product.builder()
                .name("Bánh flan caramen")
                .quality(70)
                .price(new BigDecimal("15000"))
                .imgProduct("https://images.unsplash.com/photo-1528975604071-b4dc52a2d18c?w=400")
                .description("Bánh flan mềm mịn với lớp caramen ngọt ngào")
                .category(catDessert)
                .build();

        Product rauCauDua = Product.builder()
                .name("Rau câu dừa")
                .quality(65)
                .price(new BigDecimal("18000"))
                .imgProduct("https://images.unsplash.com/photo-1488477181946-6428a0291777?w=400")
                .description("Rau câu dừa mát lạnh, vị dừa thơm béo")
                .category(catDessert)
                .build();

        Product hamburger = Product.builder()
                .name("Hamburger bò phô mai")
                .quality(70)
                .price(new BigDecimal("50000"))
                .imgProduct("https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=400")
                .description("Hamburger với thịt bò Úc 100%, phô mai tan chảy và rau tươi")
                .category(catFastFood)
                .build();

        Product pizza = Product.builder()
                .name("Pizza hải sản")
                .quality(40)
                .price(new BigDecimal("120000"))
                .imgProduct("https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=400")
                .description("Pizza đế mỏng giòn với tôm, mực, cá hồi và phô mai Mozzarella")
                .category(catFastFood)
                .build();

        Product gaRan = Product.builder()
                .name("Gà rán giòn")
                .quality(100)
                .price(new BigDecimal("35000"))
                .imgProduct("https://images.unsplash.com/photo-1626082927389-6cd097cdc6ec?w=400")
                .description("Gà rán giòn tan với lớp vỏ vàng ruộm, thịt mềm ngọt")
                .category(catFastFood)
                .build();

        Product khoaiTayChien = Product.builder()
                .name("Khoai tây chiên")
                .quality(120)
                .price(new BigDecimal("20000"))
                .imgProduct("https://images.unsplash.com/photo-1573080496219-bb080dd4f877?w=400")
                .description("Khoai tây chiên giòn rụm, ăn kèm sốt mayonnaise hoặc tương cà")
                .category(catFastFood)
                .build();

        Product hotdog = Product.builder()
                .name("Hotdog xúc xích")
                .quality(90)
                .price(new BigDecimal("30000"))
                .imgProduct("https://images.unsplash.com/photo-1612392166886-ee8475b03571?w=400")
                .description("Hotdog với xúc xích Đức, sốt mù tạt và hành phi")
                .category(catFastFood)
                .build();

        productRepository.saveAll(List.of(
                pho, bunCha, comTam, banhMi, bunBo,
                traSua, caphe, nuocCam, sinhTo, traDao,
                cheThai, kemBo, banhFlan, rauCauDua,
                hamburger, pizza, gaRan, khoaiTayChien, hotdog
        ));
        log.info("Created 19 products");

        log.info("========================================");
        log.info("Data seeding completed successfully!");
        log.info("========================================");
        log.info("Test accounts:");
        log.info("  Admin: admin@gmail.com / Admin@123");
        log.info("  Staff: staff1@gmail.com / Staff@123");
        log.info("  Staff: staff2@gmail.com / Staff@123");
        log.info("========================================");
    }
}
