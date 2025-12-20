import { useState, useEffect } from 'react';
import {
  TrendingUp,
  TrendingDown,
  DollarSign,
  ShoppingBag,
  Calendar,
  ArrowUpRight,
  BarChart3,
  PieChart as PieChartIcon,
  LineChart as LineChartIcon,
  RefreshCw
} from 'lucide-react';
import {
  AreaChart,
  Area,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  BarChart,
  Bar,
  PieChart,
  Pie,
  Cell,
  LineChart,
  Line
} from 'recharts';
import api, { extractData } from '../../services/api';

const COLORS = ['#f97316', '#3b82f6', '#10b981', '#8b5cf6', '#ec4899', '#6b7280'];

const formatCurrency = (value) => {
  if (value === null || value === undefined) return '0 ₫';
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND',
    minimumFractionDigits: 0
  }).format(value);
};

const formatShortCurrency = (value) => {
  if (value === null || value === undefined) return '0';
  if (value >= 1000000000) {
    return (value / 1000000000).toFixed(1) + ' tỷ';
  } else if (value >= 1000000) {
    return (value / 1000000).toFixed(0) + ' tr';
  } else if (value >= 1000) {
    return (value / 1000).toFixed(0) + 'K';
  }
  return value.toString();
};

const CustomTooltip = ({ active, payload, label }) => {
  if (active && payload && payload.length) {
    return (
      <div className="bg-white px-4 py-3 rounded-xl shadow-lg border border-cream-200">
        <p className="text-sm font-medium text-gray-600 mb-1">{label}</p>
        <p className="text-lg font-bold text-primary-600">
          {formatCurrency(payload[0].value)}
        </p>
        {payload[1] && (
          <p className="text-sm text-gray-500">
            {payload[1].value} đơn hàng
          </p>
        )}
      </div>
    );
  }
  return null;
};

const RevenueManagement = () => {
  const [timeFilter, setTimeFilter] = useState('month');
  const [chartType, setChartType] = useState('area');
  const [loading, setLoading] = useState(true);
  const [stats, setStats] = useState(null);
  const [revenueData, setRevenueData] = useState([]);
  const [categoryData, setCategoryData] = useState([]);
  const [topProducts, setTopProducts] = useState([]);
  const [selectedYear, setSelectedYear] = useState(new Date().getFullYear());

  useEffect(() => {
    fetchDashboardStats();
    fetchCategoryData();
    fetchTopProducts();
  }, []);

  useEffect(() => {
    fetchRevenueData();
  }, [timeFilter, selectedYear]);

  const fetchDashboardStats = async () => {
    try {
      const response = await api.statistics.getDashboard();
      const data = extractData(response);
      setStats(data);
    } catch (error) {
      console.error('Error fetching dashboard stats:', error);
    }
  };

  const fetchRevenueData = async () => {
    try {
      setLoading(true);
      let response;

      if (timeFilter === 'day') {
        // Lấy 7 ngày gần nhất
        const endDate = new Date().toISOString().split('T')[0];
        const startDate = new Date(Date.now() - 7 * 24 * 60 * 60 * 1000).toISOString().split('T')[0];
        response = await api.statistics.getRevenueDaily(startDate, endDate);
      } else if (timeFilter === 'month') {
        response = await api.statistics.getRevenueMonthly(selectedYear);
      } else {
        response = await api.statistics.getRevenueYearly();
      }

      const data = extractData(response);
      setRevenueData(Array.isArray(data) ? data : []);
    } catch (error) {
      console.error('Error fetching revenue data:', error);
      setRevenueData([]);
    } finally {
      setLoading(false);
    }
  };

  const fetchCategoryData = async () => {
    try {
      const response = await api.statistics.getRevenueByCategory();
      const data = extractData(response);
      if (Array.isArray(data)) {
        setCategoryData(data.map((item, index) => ({
          ...item,
          color: COLORS[index % COLORS.length]
        })));
      }
    } catch (error) {
      console.error('Error fetching category data:', error);
    }
  };

  const fetchTopProducts = async () => {
    try {
      const response = await api.statistics.getTopProducts(5);
      const data = extractData(response);
      setTopProducts(Array.isArray(data) ? data : []);
    } catch (error) {
      console.error('Error fetching top products:', error);
    }
  };

  const handleRefresh = () => {
    fetchDashboardStats();
    fetchRevenueData();
    fetchCategoryData();
    fetchTopProducts();
  };

  const calculateTotalRevenue = () => {
    return revenueData.reduce((sum, item) => sum + (item.revenue || 0), 0);
  };

  const calculateTotalOrders = () => {
    return revenueData.reduce((sum, item) => sum + (item.orders || 0), 0);
  };

  const renderChart = () => {
    switch (chartType) {
      case 'bar':
        return (
          <BarChart data={revenueData}>
            <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
            <XAxis dataKey="date" axisLine={false} tickLine={false} tick={{ fill: '#6b7280', fontSize: 12 }} />
            <YAxis axisLine={false} tickLine={false} tick={{ fill: '#6b7280', fontSize: 12 }} tickFormatter={formatShortCurrency} />
            <Tooltip content={<CustomTooltip />} />
            <Bar dataKey="revenue" fill="#f97316" radius={[6, 6, 0, 0]} />
          </BarChart>
        );
      case 'line':
        return (
          <LineChart data={revenueData}>
            <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
            <XAxis dataKey="date" axisLine={false} tickLine={false} tick={{ fill: '#6b7280', fontSize: 12 }} />
            <YAxis axisLine={false} tickLine={false} tick={{ fill: '#6b7280', fontSize: 12 }} tickFormatter={formatShortCurrency} />
            <Tooltip content={<CustomTooltip />} />
            <Line type="monotone" dataKey="revenue" stroke="#f97316" strokeWidth={3} dot={{ fill: '#f97316', strokeWidth: 2, r: 4 }} activeDot={{ r: 6, stroke: '#fff', strokeWidth: 2 }} />
          </LineChart>
        );
      default:
        return (
          <AreaChart data={revenueData}>
            <defs>
              <linearGradient id="colorRevenue" x1="0" y1="0" x2="0" y2="1">
                <stop offset="5%" stopColor="#f97316" stopOpacity={0.3} />
                <stop offset="95%" stopColor="#f97316" stopOpacity={0} />
              </linearGradient>
            </defs>
            <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
            <XAxis dataKey="date" axisLine={false} tickLine={false} tick={{ fill: '#6b7280', fontSize: 12 }} />
            <YAxis axisLine={false} tickLine={false} tick={{ fill: '#6b7280', fontSize: 12 }} tickFormatter={formatShortCurrency} />
            <Tooltip content={<CustomTooltip />} />
            <Area type="monotone" dataKey="revenue" stroke="#f97316" strokeWidth={3} fill="url(#colorRevenue)" />
          </AreaChart>
        );
    }
  };

  return (
    <div className="animate-fadeIn">
      {/* Header */}
      <div className="mb-8">
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div>
            <h1 className="text-3xl font-bold text-gray-800 font-display">
              Báo cáo Doanh thu
            </h1>
            <p className="text-gray-500 mt-1">
              Thống kê và phân tích doanh thu kinh doanh
            </p>
          </div>
          <div className="flex items-center gap-3">
            <button
              onClick={handleRefresh}
              className="flex items-center gap-2 px-4 py-2.5 border border-cream-200 text-gray-600 rounded-xl hover:bg-cream-50 transition-all"
            >
              <RefreshCw className="w-5 h-5" />
              <span>Làm mới</span>
            </button>
          </div>
        </div>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
        <div className="bg-gradient-to-br from-primary-50 to-cream-100 border border-primary-100 rounded-2xl p-6">
          <div className="flex items-start justify-between">
            <div>
              <p className="text-sm font-medium text-gray-500 mb-1">Doanh thu hôm nay</p>
              <h3 className="text-2xl font-bold text-gray-800">
                {formatShortCurrency(stats?.todayRevenue || 0)}
              </h3>
              {stats?.revenueGrowth !== undefined && (
                <div className={`flex items-center gap-1 mt-2 text-sm ${stats.revenueGrowth >= 0 ? 'text-green-600' : 'text-red-500'}`}>
                  {stats.revenueGrowth >= 0 ? <TrendingUp className="w-4 h-4" /> : <TrendingDown className="w-4 h-4" />}
                  <span className="font-medium">{stats.revenueGrowth >= 0 ? '+' : ''}{stats.revenueGrowth?.toFixed(1)}%</span>
                </div>
              )}
            </div>
            <div className="w-12 h-12 bg-primary-100 text-primary-600 rounded-xl flex items-center justify-center">
              <DollarSign className="w-6 h-6" />
            </div>
          </div>
        </div>

        <div className="bg-gradient-to-br from-green-50 to-emerald-50 border border-green-100 rounded-2xl p-6">
          <div className="flex items-start justify-between">
            <div>
              <p className="text-sm font-medium text-gray-500 mb-1">Doanh thu tháng</p>
              <h3 className="text-2xl font-bold text-gray-800">
                {formatShortCurrency(stats?.monthRevenue || 0)}
              </h3>
              <p className="text-xs text-gray-400 mt-2">Tổng trong tháng này</p>
            </div>
            <div className="w-12 h-12 bg-green-100 text-green-600 rounded-xl flex items-center justify-center">
              <Calendar className="w-6 h-6" />
            </div>
          </div>
        </div>

        <div className="bg-gradient-to-br from-blue-50 to-sky-50 border border-blue-100 rounded-2xl p-6">
          <div className="flex items-start justify-between">
            <div>
              <p className="text-sm font-medium text-gray-500 mb-1">Đơn hàng hôm nay</p>
              <h3 className="text-2xl font-bold text-gray-800">{stats?.todayOrders || 0}</h3>
              {stats?.orderGrowth !== undefined && (
                <div className={`flex items-center gap-1 mt-2 text-sm ${stats.orderGrowth >= 0 ? 'text-green-600' : 'text-red-500'}`}>
                  {stats.orderGrowth >= 0 ? <TrendingUp className="w-4 h-4" /> : <TrendingDown className="w-4 h-4" />}
                  <span className="font-medium">{stats.orderGrowth >= 0 ? '+' : ''}{stats.orderGrowth?.toFixed(1)}%</span>
                </div>
              )}
            </div>
            <div className="w-12 h-12 bg-blue-100 text-blue-600 rounded-xl flex items-center justify-center">
              <ShoppingBag className="w-6 h-6" />
            </div>
          </div>
        </div>

        <div className="bg-gradient-to-br from-purple-50 to-violet-50 border border-purple-100 rounded-2xl p-6">
          <div className="flex items-start justify-between">
            <div>
              <p className="text-sm font-medium text-gray-500 mb-1">Tổng đơn hàng</p>
              <h3 className="text-2xl font-bold text-gray-800">{stats?.totalOrders || 0}</h3>
              <p className="text-xs text-gray-400 mt-2">Tất cả thời gian</p>
            </div>
            <div className="w-12 h-12 bg-purple-100 text-purple-600 rounded-xl flex items-center justify-center">
              <BarChart3 className="w-6 h-6" />
            </div>
          </div>
        </div>
      </div>

      {/* Charts Section */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 mb-6">
        {/* Main Revenue Chart */}
        <div className="lg:col-span-2 bg-white rounded-2xl shadow-card border border-cream-100 p-6">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-6">
            <div>
              <h2 className="text-lg font-semibold text-gray-800">Biểu đồ Doanh thu</h2>
              <p className="text-sm text-gray-500">
                {timeFilter === 'day' ? '7 ngày gần nhất' :
                 timeFilter === 'month' ? `Theo tháng năm ${selectedYear}` :
                 'Theo năm'}
              </p>
            </div>
            <div className="flex items-center gap-2 flex-wrap">
                {timeFilter === 'month' && (
                <select
                  value={selectedYear}
                  onChange={(e) => setSelectedYear(parseInt(e.target.value))}
                  className="px-3 py-1.5 text-sm rounded-lg border border-cream-200 focus:outline-none focus:border-primary-500"
                >
                  {[2024, 2025, 2026].map(year => (
                    <option key={year} value={year}>{year}</option>
                  ))}
                </select>
              )}

              <div className="flex items-center bg-cream-100 rounded-xl p-1">
                {['day', 'month', 'year'].map((filter) => (
                  <button
                    key={filter}
                    onClick={() => setTimeFilter(filter)}
                    className={`px-3 py-1.5 text-sm font-medium rounded-lg transition-all ${
                      timeFilter === filter
                        ? 'bg-white text-primary-600 shadow-sm'
                        : 'text-gray-600 hover:text-gray-800'
                    }`}
                  >
                    {filter === 'day' ? 'Ngày' : filter === 'month' ? 'Tháng' : 'Năm'}
                  </button>
                ))}
              </div>

              <div className="flex items-center bg-cream-100 rounded-xl p-1">
                <button
                  onClick={() => setChartType('area')}
                  className={`p-1.5 rounded-lg transition-all ${chartType === 'area' ? 'bg-white shadow-sm text-primary-600' : 'text-gray-500'}`}
                  title="Biểu đồ vùng"
                >
                  <BarChart3 className="w-4 h-4" />
                </button>
                <button
                  onClick={() => setChartType('bar')}
                  className={`p-1.5 rounded-lg transition-all ${chartType === 'bar' ? 'bg-white shadow-sm text-primary-600' : 'text-gray-500'}`}
                  title="Biểu đồ cột"
                >
                  <BarChart3 className="w-4 h-4 rotate-90" />
                </button>
                <button
                  onClick={() => setChartType('line')}
                  className={`p-1.5 rounded-lg transition-all ${chartType === 'line' ? 'bg-white shadow-sm text-primary-600' : 'text-gray-500'}`}
                  title="Biểu đồ đường"
                >
                  <LineChartIcon className="w-4 h-4" />
                </button>
              </div>
            </div>
          </div>

          <div className="h-80">
            {loading ? (
              <div className="h-full flex items-center justify-center">
                <div className="w-8 h-8 border-4 border-primary-500 border-t-transparent rounded-full animate-spin" />
              </div>
            ) : revenueData.length === 0 ? (
              <div className="h-full flex items-center justify-center text-gray-500">
                Chưa có dữ liệu
              </div>
            ) : (
              <ResponsiveContainer width="100%" height="100%">
                {renderChart()}
              </ResponsiveContainer>
            )}
          </div>
        </div>

        {/* Category Pie Chart */}
        <div className="bg-white rounded-2xl shadow-card border border-cream-100 p-6">
          <div className="mb-6">
            <h2 className="text-lg font-semibold text-gray-800">Doanh thu theo Danh mục</h2>
            <p className="text-sm text-gray-500">Phân bổ doanh thu theo loại món</p>
          </div>

          {categoryData.length > 0 ? (
            <>
              <div className="h-64">
                <ResponsiveContainer width="100%" height="100%">
                  <PieChart>
                    <Pie
                      data={categoryData}
                      cx="50%"
                      cy="50%"
                      innerRadius={50}
                      outerRadius={80}
                      paddingAngle={2}
                      dataKey="percentage"
                      nameKey="categoryName"
                    >
                      {categoryData.map((entry, index) => (
                        <Cell key={`cell-${index}`} fill={entry.color} />
                      ))}
                    </Pie>
                    <Tooltip
                      formatter={(value) => [`${value?.toFixed(1)}%`, 'Tỷ lệ']}
                      contentStyle={{
                        backgroundColor: '#fff',
                        border: '1px solid #f0f0f0',
                        borderRadius: '12px',
                        boxShadow: '0 4px 12px rgba(0,0,0,0.1)'
                      }}
                    />
                  </PieChart>
                </ResponsiveContainer>
              </div>

              <div className="mt-4 space-y-2">
                {categoryData.map((item, index) => (
                  <div key={index} className="flex items-center justify-between text-sm">
                    <div className="flex items-center gap-2">
                      <div className="w-3 h-3 rounded-full" style={{ backgroundColor: item.color }} />
                      <span className="text-gray-600">{item.categoryName}</span>
                    </div>
                    <span className="font-medium text-gray-800">{item.percentage?.toFixed(1)}%</span>
                  </div>
                ))}
              </div>
            </>
          ) : (
            <div className="h-64 flex items-center justify-center text-gray-500">
              Chưa có dữ liệu
            </div>
          )}
        </div>
      </div>

      {/* Top Products */}
      <div className="bg-white rounded-2xl shadow-card border border-cream-100 p-6">
        <div className="flex items-center justify-between mb-6">
          <div>
            <h2 className="text-lg font-semibold text-gray-800">Sản phẩm bán chạy</h2>
            <p className="text-sm text-gray-500">Top 5 sản phẩm có doanh thu cao nhất</p>
          </div>
          <button className="text-primary-500 text-sm font-medium hover:text-primary-600 flex items-center gap-1">
            Xem tất cả
            <ArrowUpRight className="w-4 h-4" />
          </button>
        </div>

        {topProducts.length > 0 ? (
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead>
                <tr className="border-b border-cream-100">
                  <th className="text-left py-3 px-4 text-xs font-semibold text-gray-500 uppercase tracking-wider">STT</th>
                  <th className="text-left py-3 px-4 text-xs font-semibold text-gray-500 uppercase tracking-wider">Sản phẩm</th>
                  <th className="text-left py-3 px-4 text-xs font-semibold text-gray-500 uppercase tracking-wider">Doanh thu</th>
                  <th className="text-left py-3 px-4 text-xs font-semibold text-gray-500 uppercase tracking-wider">Số lượng</th>
                  <th className="text-left py-3 px-4 text-xs font-semibold text-gray-500 uppercase tracking-wider">Đơn hàng</th>
                </tr>
              </thead>
              <tbody>
                {topProducts.map((product, index) => (
                  <tr key={index} className="table-row border-b border-cream-50">
                    <td className="py-4 px-4">
                      <div className={`w-8 h-8 rounded-full flex items-center justify-center font-semibold text-sm ${
                        index === 0 ? 'bg-yellow-100 text-yellow-700' :
                        index === 1 ? 'bg-gray-100 text-gray-600' :
                        index === 2 ? 'bg-orange-100 text-orange-700' :
                        'bg-cream-100 text-gray-500'
                      }`}>
                        {index + 1}
                      </div>
                    </td>
                    <td className="py-4 px-4">
                      <span className="font-medium text-gray-800">{product.productName}</span>
                    </td>
                    <td className="py-4 px-4">
                      <span className="font-semibold text-primary-600">{formatCurrency(product.revenue)}</span>
                    </td>
                    <td className="py-4 px-4 text-gray-600">{product.quantitySold} sản phẩm</td>
                    <td className="py-4 px-4 text-gray-600">{product.orderCount} đơn</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : (
          <div className="py-12 text-center text-gray-500">
            Chưa có dữ liệu sản phẩm bán chạy
          </div>
        )}
      </div>
    </div>
  );
};

export default RevenueManagement;
