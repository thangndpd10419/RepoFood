import { useState, useEffect } from 'react';
import { useAuth } from '../../contexts/AuthContext';
import {
  DollarSign,
  ShoppingBag,
  Users,
  Package,
  TrendingUp,
  TrendingDown,
  ArrowUpRight,
  Calendar,
  Loader2
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
  Bar
} from 'recharts';
import api, { extractData } from '../../services/api';

const formatCurrency = (value) => {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND',
    minimumFractionDigits: 0
  }).format(value);
};

const formatShortNumber = (value) => {
  if (value >= 1000000) {
    return (value / 1000000).toFixed(1) + 'M';
  } else if (value >= 1000) {
    return (value / 1000).toFixed(0) + 'K';
  }
  return value;
};

const StatCard = ({ title, value, icon: Icon, trend, trendValue, color, subtitle }) => {
  const colorClasses = {
    orange: 'from-primary-50 to-cream-100 border-primary-100',
    green: 'from-green-50 to-emerald-50 border-green-100',
    blue: 'from-blue-50 to-sky-50 border-blue-100',
    purple: 'from-purple-50 to-violet-50 border-purple-100'
  };

  const iconColorClasses = {
    orange: 'bg-primary-100 text-primary-600',
    green: 'bg-green-100 text-green-600',
    blue: 'bg-blue-100 text-blue-600',
    purple: 'bg-purple-100 text-purple-600'
  };

  return (
    <div className={`stat-card bg-gradient-to-br ${colorClasses[color]} border rounded-2xl p-6 relative overflow-hidden`}>
      <div className="flex items-start justify-between">
        <div className="flex-1">
          <p className="text-sm font-medium text-gray-500 mb-1">{title}</p>
          <h3 className="text-2xl font-bold text-gray-800 mb-1">{value}</h3>
          {subtitle && (
            <p className="text-xs text-gray-400">{subtitle}</p>
          )}
          {trend && (
            <div className={`flex items-center gap-1 mt-2 text-sm ${trend === 'up' ? 'text-green-600' : 'text-red-500'}`}>
              {trend === 'up' ? <TrendingUp className="w-4 h-4" /> : <TrendingDown className="w-4 h-4" />}
              <span className="font-medium">{trendValue}</span>
              <span className="text-gray-400 text-xs">so với tháng trước</span>
            </div>
          )}
        </div>
        <div className={`w-12 h-12 ${iconColorClasses[color]} rounded-xl flex items-center justify-center`}>
          <Icon className="w-6 h-6" />
        </div>
      </div>

      <div className="absolute -bottom-4 -right-4 w-24 h-24 bg-white/20 rounded-full blur-2xl" />
    </div>
  );
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

const Dashboard = () => {
  const { user } = useAuth();
  const [stats, setStats] = useState({
    todayRevenue: 0,
    totalOrders: 0,
    totalCustomers: 0,
    totalProducts: 0,
    revenueGrowth: 0,
    orderGrowth: 0,
    customerGrowth: 0
  });
  const [revenueData, setRevenueData] = useState([]);
  const [recentOrders, setRecentOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [chartLoading, setChartLoading] = useState(true);
  const [ordersLoading, setOrdersLoading] = useState(true);
  const [timeFilter, setTimeFilter] = useState('month');

  useEffect(() => {
    const fetchStats = async () => {
      try {
        setLoading(true);
        const response = await api.statistics.getDashboard();
        const data = extractData(response);

        if (data) {
          setStats({
            todayRevenue: data.todayRevenue || 0,
            totalOrders: data.totalOrders || 0,
            totalCustomers: data.totalCustomers || 0,
            totalProducts: data.totalProducts || 0,
            revenueGrowth: data.revenueGrowth || 0,
            orderGrowth: data.orderGrowth || 0,
            customerGrowth: data.customerGrowth || 0
          });
        }
      } catch (error) {
        console.error('Error fetching dashboard stats:', error);
        try {
          const [usersRes, productsRes] = await Promise.all([
            api.users.getAll().catch(() => null),
            api.products.getAll().catch(() => null)
          ]);
          const usersData = usersRes ? extractData(usersRes) : [];
          const productsData = productsRes ? extractData(productsRes) : [];
          setStats(prev => ({
            ...prev,
            totalCustomers: Array.isArray(usersData) ? usersData.length : 0,
            totalProducts: Array.isArray(productsData) ? productsData.length : 0
          }));
        } catch (err) {
          console.error('Error fetching fallback stats:', err);
        }
      } finally {
        setLoading(false);
      }
    };

    fetchStats();
  }, []);

  useEffect(() => {
    const fetchRecentOrders = async () => {
      try {
        setOrdersLoading(true);
        const response = await api.orders.getAll(0, 5);
        const data = extractData(response);

        if (data && data.content) {
          setRecentOrders(data.content);
        } else if (Array.isArray(data)) {
          setRecentOrders(data.slice(0, 5));
        }
      } catch (error) {
        console.error('Error fetching recent orders:', error);
        setRecentOrders([]);
      } finally {
        setOrdersLoading(false);
      }
    };

    fetchRecentOrders();
  }, []);

  useEffect(() => {
    const fetchRevenueData = async () => {
      try {
        setChartLoading(true);
        let response;
        const currentYear = new Date().getFullYear();

        if (timeFilter === 'week') {
            const endDate = new Date().toISOString().split('T')[0];
          const startDate = new Date(Date.now() - 7 * 24 * 60 * 60 * 1000).toISOString().split('T')[0];
          response = await api.statistics.getRevenueDaily(startDate, endDate);
        } else if (timeFilter === 'month') {
          response = await api.statistics.getRevenueMonthly(currentYear);
        } else {
          response = await api.statistics.getRevenueYearly();
        }

        const data = extractData(response);

        if (Array.isArray(data) && data.length > 0) {
          const formattedData = data.map(item => ({
            name: item.label || item.date || item.month || item.year,
            revenue: item.revenue || 0,
            orders: item.orderCount || item.orders || 0
          }));
          setRevenueData(formattedData);
        } else {
          setRevenueData([]);
        }
      } catch (error) {
        console.error('Error fetching revenue data:', error);
        setRevenueData([]);
      } finally {
        setChartLoading(false);
      }
    };

    fetchRevenueData();
  }, [timeFilter]);

  const currentDate = new Date().toLocaleDateString('vi-VN', {
    weekday: 'long',
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  });

  return (
    <div className="animate-fadeIn">
      {/* Header */}
      <div className="mb-8">
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-3xl font-bold text-gray-800 font-display">
              Dashboard Tổng Quan
            </h1>
            <p className="text-gray-500 mt-1 flex items-center gap-2">
              <Calendar className="w-4 h-4" />
              {currentDate}
            </p>
          </div>
          <div className="flex items-center gap-2">
            <span className="text-gray-500">Xin chào,</span>
            <span className="font-semibold text-primary-600">{user?.email || 'Admin'}</span>
            <div className="w-2 h-2 bg-green-500 rounded-full animate-pulse" />
          </div>
        </div>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
        <StatCard
          title="Doanh thu hôm nay"
          value={loading ? '...' : formatCurrency(stats.todayRevenue)}
          icon={DollarSign}
          trend={stats.revenueGrowth >= 0 ? 'up' : 'down'}
          trendValue={`${stats.revenueGrowth >= 0 ? '+' : ''}${stats.revenueGrowth.toFixed(1)}%`}
          color="orange"
          subtitle="So với hôm qua"
        />
        <StatCard
          title="Đơn hàng"
          value={loading ? '...' : stats.totalOrders}
          icon={ShoppingBag}
          trend={stats.orderGrowth >= 0 ? 'up' : 'down'}
          trendValue={`${stats.orderGrowth >= 0 ? '+' : ''}${stats.orderGrowth.toFixed(1)}%`}
          color="green"
          subtitle="Tổng số đơn hàng"
        />
        <StatCard
          title="Khách hàng"
          value={loading ? '...' : stats.totalCustomers}
          icon={Users}
          trend={stats.customerGrowth >= 0 ? 'up' : 'down'}
          trendValue={`${stats.customerGrowth >= 0 ? '+' : ''}${stats.customerGrowth.toFixed(1)}%`}
          color="blue"
          subtitle="Người dùng đăng ký"
        />
        <StatCard
          title="Sản phẩm"
          value={loading ? '...' : stats.totalProducts}
          icon={Package}
          color="purple"
          subtitle="Đang kinh doanh"
        />
      </div>

      {/* Charts Section */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Revenue Chart */}
        <div className="lg:col-span-2 bg-white rounded-2xl shadow-card border border-cream-100 p-6">
          <div className="flex items-center justify-between mb-6">
            <div>
              <h2 className="text-lg font-semibold text-gray-800">Biểu đồ Doanh thu</h2>
              <p className="text-sm text-gray-500">Thống kê doanh thu theo tháng</p>
            </div>
            <div className="flex items-center gap-2">
              {['week', 'month', 'year'].map((filter) => (
                <button
                  key={filter}
                  onClick={() => setTimeFilter(filter)}
                  className={`px-4 py-2 text-sm font-medium rounded-lg transition-all ${
                    timeFilter === filter
                      ? 'bg-primary-500 text-white shadow-sm'
                      : 'bg-cream-100 text-gray-600 hover:bg-cream-200'
                  }`}
                >
                  {filter === 'week' ? 'Tuần' : filter === 'month' ? 'Tháng' : 'Năm'}
                </button>
              ))}
            </div>
          </div>

          <div className="h-80">
            {chartLoading ? (
              <div className="h-full flex items-center justify-center">
                <Loader2 className="w-8 h-8 text-primary-500 animate-spin" />
              </div>
            ) : revenueData.length === 0 ? (
              <div className="h-full flex items-center justify-center text-gray-500">
                Chưa có dữ liệu doanh thu
              </div>
            ) : (
              <ResponsiveContainer width="100%" height="100%">
                <AreaChart data={revenueData}>
                  <defs>
                    <linearGradient id="colorRevenue" x1="0" y1="0" x2="0" y2="1">
                      <stop offset="5%" stopColor="#f97316" stopOpacity={0.3} />
                      <stop offset="95%" stopColor="#f97316" stopOpacity={0} />
                    </linearGradient>
                  </defs>
                  <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
                  <XAxis
                    dataKey="name"
                    axisLine={false}
                    tickLine={false}
                    tick={{ fill: '#6b7280', fontSize: 12 }}
                  />
                  <YAxis
                    axisLine={false}
                    tickLine={false}
                    tick={{ fill: '#6b7280', fontSize: 12 }}
                    tickFormatter={formatShortNumber}
                  />
                  <Tooltip content={<CustomTooltip />} />
                  <Area
                    type="monotone"
                    dataKey="revenue"
                    stroke="#f97316"
                    strokeWidth={3}
                    fill="url(#colorRevenue)"
                  />
                </AreaChart>
              </ResponsiveContainer>
            )}
          </div>
        </div>

        {/* Recent Activity / Orders Summary */}
        <div className="bg-white rounded-2xl shadow-card border border-cream-100 p-6">
          <div className="flex items-center justify-between mb-6">
            <h2 className="text-lg font-semibold text-gray-800">Đơn hàng theo tháng</h2>
            <button className="text-primary-500 text-sm font-medium hover:text-primary-600 flex items-center gap-1">
              Xem tất cả
              <ArrowUpRight className="w-4 h-4" />
            </button>
          </div>

          <div className="h-64">
            {chartLoading ? (
              <div className="h-full flex items-center justify-center">
                <Loader2 className="w-6 h-6 text-primary-500 animate-spin" />
              </div>
            ) : revenueData.length === 0 ? (
              <div className="h-full flex items-center justify-center text-gray-500 text-sm">
                Chưa có dữ liệu
              </div>
            ) : (
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={revenueData} layout="vertical">
                  <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" horizontal={true} vertical={false} />
                  <XAxis type="number" axisLine={false} tickLine={false} tick={{ fill: '#6b7280', fontSize: 11 }} />
                  <YAxis
                    dataKey="name"
                    type="category"
                    axisLine={false}
                    tickLine={false}
                    tick={{ fill: '#6b7280', fontSize: 12 }}
                    width={30}
                  />
                  <Tooltip
                    formatter={(value) => [value + ' đơn', 'Số đơn hàng']}
                    contentStyle={{
                      backgroundColor: '#fff',
                      border: '1px solid #f0f0f0',
                      borderRadius: '12px',
                      boxShadow: '0 4px 12px rgba(0,0,0,0.1)'
                    }}
                  />
                  <Bar
                    dataKey="orders"
                    fill="#f97316"
                    radius={[0, 6, 6, 0]}
                    barSize={20}
                  />
                </BarChart>
              </ResponsiveContainer>
            )}
          </div>

          {/* Quick Stats */}
          <div className="mt-4 pt-4 border-t border-cream-100">
            <div className="flex items-center justify-between text-sm">
              <span className="text-gray-500">Tổng đơn hàng</span>
              <span className="font-semibold text-gray-800">{stats.totalOrders} đơn</span>
            </div>
            <div className="flex items-center justify-between text-sm mt-2">
              <span className="text-gray-500">Tăng trưởng</span>
              <span className={`font-semibold flex items-center gap-1 ${stats.orderGrowth >= 0 ? 'text-green-600' : 'text-red-500'}`}>
                {stats.orderGrowth >= 0 ? <TrendingUp className="w-4 h-4" /> : <TrendingDown className="w-4 h-4" />}
                {stats.orderGrowth >= 0 ? '+' : ''}{stats.orderGrowth.toFixed(1)}%
              </span>
            </div>
          </div>
        </div>
      </div>

      {/* Recent Orders Table Preview */}
      <div className="mt-6 bg-white rounded-2xl shadow-card border border-cream-100 p-6">
        <div className="flex items-center justify-between mb-6">
          <h2 className="text-lg font-semibold text-gray-800">Đơn hàng gần đây</h2>
          <button className="text-primary-500 text-sm font-medium hover:text-primary-600 flex items-center gap-1">
            Xem tất cả đơn hàng
            <ArrowUpRight className="w-4 h-4" />
          </button>
        </div>

        <div className="overflow-x-auto">
          {ordersLoading ? (
            <div className="py-12 flex items-center justify-center">
              <Loader2 className="w-6 h-6 text-primary-500 animate-spin" />
            </div>
          ) : recentOrders.length === 0 ? (
            <div className="py-12 text-center text-gray-500">
              Chưa có đơn hàng nào
            </div>
          ) : (
            <table className="w-full">
              <thead>
                <tr className="border-b border-cream-100">
                  <th className="text-left py-3 px-4 text-xs font-semibold text-gray-500 uppercase tracking-wider">Mã đơn</th>
                  <th className="text-left py-3 px-4 text-xs font-semibold text-gray-500 uppercase tracking-wider">Khách hàng</th>
                  <th className="text-left py-3 px-4 text-xs font-semibold text-gray-500 uppercase tracking-wider">Sản phẩm</th>
                  <th className="text-left py-3 px-4 text-xs font-semibold text-gray-500 uppercase tracking-wider">Tổng tiền</th>
                  <th className="text-left py-3 px-4 text-xs font-semibold text-gray-500 uppercase tracking-wider">Trạng thái</th>
                </tr>
              </thead>
              <tbody>
                {recentOrders.map((order, index) => {
                  const status = order.status?.toLowerCase() || 'pending';
                  const customerName = order.fullName || order.customerName || order.user?.fullName || 'Khách hàng';
                  const productName = order.orderDetails?.[0]?.productName || order.items?.[0]?.productName || 'Sản phẩm';
                  const productCount = order.orderDetails?.length || order.items?.length || 0;

                  return (
                    <tr key={order.id || index} className="table-row border-b border-cream-50">
                      <td className="py-4 px-4">
                        <span className="font-medium text-gray-800">#{order.id}</span>
                      </td>
                      <td className="py-4 px-4 text-gray-600">{customerName}</td>
                      <td className="py-4 px-4 text-gray-600">
                        {productName}
                        {productCount > 1 && <span className="text-gray-400 ml-1">(+{productCount - 1})</span>}
                      </td>
                      <td className="py-4 px-4 font-medium text-gray-800">{formatCurrency(order.totalPrice || order.total || 0)}</td>
                      <td className="py-4 px-4">
                        <span className={`inline-flex items-center px-3 py-1 rounded-full text-xs font-medium ${
                          status === 'completed' || status === 'delivered'
                            ? 'bg-green-100 text-green-700'
                            : status === 'processing' || status === 'confirmed'
                            ? 'bg-blue-100 text-blue-700'
                            : status === 'cancelled'
                            ? 'bg-red-100 text-red-700'
                            : 'bg-yellow-100 text-yellow-700'
                        }`}>
                          {status === 'completed' || status === 'delivered' ? 'Hoàn thành'
                            : status === 'processing' ? 'Đang xử lý'
                            : status === 'confirmed' ? 'Đã xác nhận'
                            : status === 'cancelled' ? 'Đã hủy'
                            : 'Chờ xác nhận'}
                        </span>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          )}
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
