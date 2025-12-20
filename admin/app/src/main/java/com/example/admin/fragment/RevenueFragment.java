package com.example.admin.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.admin.R;
import com.example.admin.adapter.TopProductAdapter;
import com.example.admin.api.RetrofitClient;
import com.example.admin.databinding.FragmentRevenueBinding;
import com.example.admin.model.ApiResponse;
import com.example.admin.model.DashboardStats;
import com.example.admin.model.RevenueByCategory;
import com.example.admin.model.RevenueByDate;
import com.example.admin.model.TopProduct;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RevenueFragment extends Fragment {
    private FragmentRevenueBinding binding;
    private TopProductAdapter topProductAdapter;
    private final NumberFormat currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));

    private static final int[] CHART_COLORS = {
            Color.parseColor("#f97316"),
            Color.parseColor("#3b82f6"),
            Color.parseColor("#10b981"),
            Color.parseColor("#8b5cf6"),
            Color.parseColor("#ec4899"),
            Color.parseColor("#6b7280")
    };

    private enum ViewMode { DAY, MONTH, YEAR }
    private ViewMode currentMode = ViewMode.DAY;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentRevenueBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        setupChips();
        setupCharts();
        loadAllData();
    }

    private void setupRecyclerView() {
        topProductAdapter = new TopProductAdapter();
        binding.recyclerViewTopProducts.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerViewTopProducts.setAdapter(topProductAdapter);
    }

    private void setupChips() {
        binding.chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;

            int checkedId = checkedIds.get(0);
            if (checkedId == R.id.chipDay) {
                currentMode = ViewMode.DAY;
                binding.tvChartPeriod.setText("7 ngày gần nhất");
            } else if (checkedId == R.id.chipMonth) {
                currentMode = ViewMode.MONTH;
                binding.tvChartPeriod.setText("Theo tháng năm " + Calendar.getInstance().get(Calendar.YEAR));
            } else if (checkedId == R.id.chipYear) {
                currentMode = ViewMode.YEAR;
                binding.tvChartPeriod.setText("Theo năm");
            }
            loadRevenueData();
        });
    }

    private void setupCharts() {
        binding.barChart.getDescription().setEnabled(false);
        binding.barChart.setDrawGridBackground(false);
        binding.barChart.setDrawBarShadow(false);
        binding.barChart.setHighlightFullBarEnabled(false);
        binding.barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        binding.barChart.getXAxis().setGranularity(1f);
        binding.barChart.getXAxis().setDrawGridLines(false);
        binding.barChart.getAxisRight().setEnabled(false);
        binding.barChart.getAxisLeft().setDrawGridLines(true);
        binding.barChart.getAxisLeft().setGridColor(Color.parseColor("#f0f0f0"));
        binding.barChart.getLegend().setEnabled(false);
        binding.barChart.animateY(1000);

        binding.pieChart.getDescription().setEnabled(false);
        binding.pieChart.setUsePercentValues(true);
        binding.pieChart.setDrawHoleEnabled(true);
        binding.pieChart.setHoleColor(Color.WHITE);
        binding.pieChart.setHoleRadius(50f);
        binding.pieChart.setTransparentCircleRadius(55f);
        binding.pieChart.getLegend().setEnabled(false);
        binding.pieChart.setDrawEntryLabels(false);
        binding.pieChart.animateY(1000);
    }

    private void loadAllData() {
        loadDashboardStats();
        loadRevenueData();
        loadCategoryData();
        loadTopProducts();
    }

    private void loadDashboardStats() {
        RetrofitClient.getApiService().getDashboardStats().enqueue(new Callback<ApiResponse<DashboardStats>>() {
            @Override
            public void onResponse(Call<ApiResponse<DashboardStats>> call,
                                   Response<ApiResponse<DashboardStats>> response) {
                if (response.isSuccessful() && response.body() != null &&
                        response.body().isSuccess() && response.body().getData() != null) {
                    updateDashboardUI(response.body().getData());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<DashboardStats>> call, Throwable t) {
                showError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    private void updateDashboardUI(DashboardStats stats) {
        binding.tvTodayRevenue.setText(formatCurrency(stats.getTodayRevenue()));
        binding.tvMonthRevenue.setText(formatCurrency(stats.getMonthRevenue()));
        binding.tvTodayOrders.setText(String.valueOf(stats.getTodayOrders()));
        binding.tvTotalOrders.setText(String.valueOf(stats.getTotalOrders()));

        if (stats.getRevenueGrowth() != null) {
            binding.layoutRevenueGrowth.setVisibility(View.VISIBLE);
            String growthText = (stats.getRevenueGrowth() >= 0 ? "+" : "") +
                    String.format(Locale.getDefault(), "%.1f%%", stats.getRevenueGrowth());
            binding.tvRevenueGrowth.setText(growthText);
            binding.ivRevenueGrowth.setRotation(stats.getRevenueGrowth() >= 0 ? 0 : 180);
        }

        if (stats.getOrderGrowth() != null) {
            binding.layoutOrderGrowth.setVisibility(View.VISIBLE);
            String growthText = (stats.getOrderGrowth() >= 0 ? "+" : "") +
                    String.format(Locale.getDefault(), "%.1f%%", stats.getOrderGrowth());
            binding.tvOrderGrowth.setText(growthText);
            binding.ivOrderGrowth.setRotation(stats.getOrderGrowth() >= 0 ? 0 : 180);
        }
    }

    private void loadRevenueData() {
        Callback<ApiResponse<List<RevenueByDate>>> callback = new Callback<ApiResponse<List<RevenueByDate>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<RevenueByDate>>> call,
                                   Response<ApiResponse<List<RevenueByDate>>> response) {
                if (response.isSuccessful() && response.body() != null &&
                        response.body().isSuccess() && response.body().getData() != null) {
                    updateBarChart(response.body().getData());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<RevenueByDate>>> call, Throwable t) {
                showError("Lỗi tải dữ liệu biểu đồ");
            }
        };

        switch (currentMode) {
            case DAY:
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Calendar cal = Calendar.getInstance();
                String endDate = dateFormat.format(cal.getTime());
                cal.add(Calendar.DAY_OF_MONTH, -6);
                String startDate = dateFormat.format(cal.getTime());
                RetrofitClient.getApiService().getRevenueDaily(startDate, endDate).enqueue(callback);
                break;
            case MONTH:
                int year = Calendar.getInstance().get(Calendar.YEAR);
                RetrofitClient.getApiService().getRevenueMonthly(year).enqueue(callback);
                break;
            case YEAR:
                RetrofitClient.getApiService().getRevenueYearly().enqueue(callback);
                break;
        }
    }

    private void updateBarChart(List<RevenueByDate> data) {
        if (data == null || data.isEmpty()) {
            binding.barChart.clear();
            return;
        }

        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (int i = 0; i < data.size(); i++) {
            RevenueByDate item = data.get(i);
            float value = item.getRevenue().floatValue() / 1000;
            entries.add(new BarEntry(i, value));
            labels.add(item.getDate());
        }

        BarDataSet dataSet = new BarDataSet(entries, "Doanh thu (nghìn VNĐ)");
        dataSet.setColor(Color.parseColor("#f97316"));
        dataSet.setValueTextSize(10f);
        dataSet.setDrawValues(false);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.6f);

        binding.barChart.setData(barData);
        binding.barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        binding.barChart.getXAxis().setLabelCount(labels.size());
        binding.barChart.invalidate();
    }

    private void loadCategoryData() {
        RetrofitClient.getApiService().getRevenueByCategory().enqueue(new Callback<ApiResponse<List<RevenueByCategory>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<RevenueByCategory>>> call,
                                   Response<ApiResponse<List<RevenueByCategory>>> response) {
                if (response.isSuccessful() && response.body() != null &&
                        response.body().isSuccess() && response.body().getData() != null) {
                    updatePieChart(response.body().getData());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<RevenueByCategory>>> call, Throwable t) {
                showError("Lỗi tải dữ liệu danh mục");
            }
        });
    }

    private void updatePieChart(List<RevenueByCategory> data) {
        if (data == null || data.isEmpty()) {
            binding.pieChart.clear();
            binding.legendContainer.removeAllViews();
            return;
        }

        List<PieEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        for (int i = 0; i < data.size(); i++) {
            RevenueByCategory item = data.get(i);
            if (item.getPercentage() > 0) {
                entries.add(new PieEntry(item.getPercentage().floatValue(), item.getCategoryName()));
                colors.add(CHART_COLORS[i % CHART_COLORS.length]);
            }
        }

        if (entries.isEmpty()) {
            entries.add(new PieEntry(1, "Chưa có dữ liệu"));
            colors.add(Color.GRAY);
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colors);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueFormatter(new PercentFormatter(binding.pieChart));

        PieData pieData = new PieData(dataSet);
        binding.pieChart.setData(pieData);
        binding.pieChart.invalidate();

        updateLegend(data, colors);
    }

    private void updateLegend(List<RevenueByCategory> data, List<Integer> colors) {
        binding.legendContainer.removeAllViews();

        for (int i = 0; i < data.size(); i++) {
            RevenueByCategory item = data.get(i);

            LinearLayout legendItem = new LinearLayout(requireContext());
            legendItem.setOrientation(LinearLayout.HORIZONTAL);
            legendItem.setGravity(Gravity.CENTER_VERTICAL);
            legendItem.setPadding(0, 8, 0, 8);

            View colorDot = new View(requireContext());
            LinearLayout.LayoutParams dotParams = new LinearLayout.LayoutParams(24, 24);
            dotParams.setMarginEnd(16);
            colorDot.setLayoutParams(dotParams);
            colorDot.setBackgroundColor(colors.get(i % colors.size()));

            TextView tvName = new TextView(requireContext());
            LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            tvName.setLayoutParams(nameParams);
            tvName.setText(item.getCategoryName());
            tvName.setTextColor(Color.parseColor("#4B5563"));
            tvName.setTextSize(13f);

            TextView tvPercent = new TextView(requireContext());
            tvPercent.setText(String.format(Locale.getDefault(), "%.1f%%", item.getPercentage()));
            tvPercent.setTextColor(Color.parseColor("#1F2937"));
            tvPercent.setTextSize(13f);

            legendItem.addView(colorDot);
            legendItem.addView(tvName);
            legendItem.addView(tvPercent);

            binding.legendContainer.addView(legendItem);
        }
    }

    private void loadTopProducts() {
        RetrofitClient.getApiService().getTopProducts(5).enqueue(new Callback<ApiResponse<List<TopProduct>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<TopProduct>>> call,
                                   Response<ApiResponse<List<TopProduct>>> response) {
                if (response.isSuccessful() && response.body() != null &&
                        response.body().isSuccess() && response.body().getData() != null) {
                    List<TopProduct> products = response.body().getData();
                    topProductAdapter.setProducts(products);
                    binding.tvNoProducts.setVisibility(products.isEmpty() ? View.VISIBLE : View.GONE);
                    binding.recyclerViewTopProducts.setVisibility(products.isEmpty() ? View.GONE : View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<TopProduct>>> call, Throwable t) {
                binding.tvNoProducts.setVisibility(View.VISIBLE);
                binding.recyclerViewTopProducts.setVisibility(View.GONE);
            }
        });
    }

    private String formatCurrency(Double value) {
        if (value == null) return "0 VNĐ";
        if (value >= 1000000000) {
            return String.format(Locale.getDefault(), "%.1f tỷ", value / 1000000000);
        } else if (value >= 1000000) {
            return String.format(Locale.getDefault(), "%.0f tr", value / 1000000);
        } else if (value >= 1000) {
            return currencyFormat.format(value) + " VNĐ";
        }
        return currencyFormat.format(value) + " VNĐ";
    }

    private void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
