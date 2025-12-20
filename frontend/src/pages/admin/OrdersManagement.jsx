import { useState, useEffect } from 'react';
import {
  Search,
  ShoppingCart,
  Eye,
  Clock,
  CheckCircle,
  XCircle,
  Loader2,
  RefreshCw,
  Filter,
  User,
  Calendar,
  Hash,
  Package
} from 'lucide-react';
import api, { extractPageData } from '../../services/api';
import Modal from '../../components/ui/Modal';
import Pagination from '../../components/ui/Pagination';

const formatCurrency = (value) => {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND',
    minimumFractionDigits: 0
  }).format(value || 0);
};

const formatDate = (dateString) => {
  if (!dateString) return 'N/A';
  const date = new Date(dateString);
  return date.toLocaleString('vi-VN', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });
};

const STATUS_CONFIG = {
  PENDING: { label: 'Chờ xử lý', color: 'bg-yellow-100 text-yellow-700', icon: Clock },
  PROCESSING: { label: 'Đang xử lý', color: 'bg-blue-100 text-blue-700', icon: Loader2 },
  COMPLETED: { label: 'Hoàn thành', color: 'bg-green-100 text-green-700', icon: CheckCircle },
  CANCELLED: { label: 'Đã hủy', color: 'bg-red-100 text-red-700', icon: XCircle }
};

const OrdersManagement = () => {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filterStatus, setFilterStatus] = useState('all');
  const [selectedOrder, setSelectedOrder] = useState(null);
  const [showDetailModal, setShowDetailModal] = useState(false);
  const [showStatusModal, setShowStatusModal] = useState(false);
  const [updating, setUpdating] = useState(false);

  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [pageSize, setPageSize] = useState(10);

  useEffect(() => {
    fetchOrders();
  }, [currentPage, pageSize]);

  const fetchOrders = async () => {
    try {
      setLoading(true);
      const response = await api.orders.getAll({
        page: currentPage,
        size: pageSize
      });
      const pageData = extractPageData(response);
      setOrders(Array.isArray(pageData.content) ? pageData.content : []);
      setTotalPages(pageData.totalPages);
      setTotalElements(pageData.totalElements);
    } catch (error) {
      console.error('Error fetching orders:', error);
      setOrders([]);
    } finally {
      setLoading(false);
    }
  };

  const handleUpdateStatus = async (orderId, newStatus) => {
    try {
      setUpdating(true);
      await api.orders.update(orderId, { status: newStatus });
      fetchOrders();
      setShowStatusModal(false);
      setSelectedOrder(null);
    } catch (error) {
      console.error('Error updating order:', error);
      alert('Không thể cập nhật trạng thái đơn hàng!');
    } finally {
      setUpdating(false);
    }
  };

  const handlePageChange = (page) => {
    setCurrentPage(page);
  };

  const handlePageSizeChange = (size) => {
    setPageSize(size);
    setCurrentPage(0);
  };

  const openDetailModal = (order) => {
    setSelectedOrder(order);
    setShowDetailModal(true);
  };

  const openStatusModal = (order) => {
    setSelectedOrder(order);
    setShowStatusModal(true);
  };

  const filteredOrders = orders.filter(order => {
    if (filterStatus === 'all') return true;
    return order.status === filterStatus;
  });

  const stats = {
    total: orders.length,
    pending: orders.filter(o => o.status === 'PENDING').length,
    processing: orders.filter(o => o.status === 'PROCESSING').length,
    completed: orders.filter(o => o.status === 'COMPLETED').length,
    cancelled: orders.filter(o => o.status === 'CANCELLED').length
  };

  return (
    <div className="animate-fadeIn">
      {/* Header */}
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-800 font-display">
          Quản lý Đơn hàng
        </h1>
        <p className="text-gray-500 mt-1">
          Xem và cập nhật trạng thái các đơn hàng từ nhân viên
        </p>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-1 md:grid-cols-5 gap-4 mb-6">
        <div className="bg-white rounded-xl p-4 border border-cream-100 shadow-card">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 bg-gray-100 rounded-lg flex items-center justify-center">
              <ShoppingCart className="w-5 h-5 text-gray-600" />
            </div>
            <div>
              <div className="text-2xl font-bold text-gray-800">{stats.total}</div>
              <div className="text-sm text-gray-500">Tổng đơn</div>
            </div>
          </div>
        </div>
        <div className="bg-white rounded-xl p-4 border border-cream-100 shadow-card">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 bg-yellow-100 rounded-lg flex items-center justify-center">
              <Clock className="w-5 h-5 text-yellow-600" />
            </div>
            <div>
              <div className="text-2xl font-bold text-gray-800">{stats.pending}</div>
              <div className="text-sm text-gray-500">Chờ xử lý</div>
            </div>
          </div>
        </div>
        <div className="bg-white rounded-xl p-4 border border-cream-100 shadow-card">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 bg-blue-100 rounded-lg flex items-center justify-center">
              <Loader2 className="w-5 h-5 text-blue-600" />
            </div>
            <div>
              <div className="text-2xl font-bold text-gray-800">{stats.processing}</div>
              <div className="text-sm text-gray-500">Đang xử lý</div>
            </div>
          </div>
        </div>
        <div className="bg-white rounded-xl p-4 border border-cream-100 shadow-card">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 bg-green-100 rounded-lg flex items-center justify-center">
              <CheckCircle className="w-5 h-5 text-green-600" />
            </div>
            <div>
              <div className="text-2xl font-bold text-gray-800">{stats.completed}</div>
              <div className="text-sm text-gray-500">Hoàn thành</div>
            </div>
          </div>
        </div>
        <div className="bg-white rounded-xl p-4 border border-cream-100 shadow-card">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 bg-red-100 rounded-lg flex items-center justify-center">
              <XCircle className="w-5 h-5 text-red-600" />
            </div>
            <div>
              <div className="text-2xl font-bold text-gray-800">{stats.cancelled}</div>
              <div className="text-sm text-gray-500">Đã hủy</div>
            </div>
          </div>
        </div>
      </div>

      {/* Actions Bar */}
      <div className="bg-white rounded-2xl shadow-card border border-cream-100 p-4 mb-6">
        <div className="flex flex-col lg:flex-row gap-4 items-center justify-between">
          {/* Filter */}
          <div className="flex items-center gap-3">
            <Filter className="w-5 h-5 text-gray-400" />
            <select
              value={filterStatus}
              onChange={(e) => setFilterStatus(e.target.value)}
              className="px-4 py-2.5 rounded-xl border border-cream-200 text-gray-600 focus:border-primary-500 focus:ring-2 focus:ring-primary-100 transition-all"
            >
              <option value="all">Tất cả trạng thái</option>
              <option value="PENDING">Chờ xử lý</option>
              <option value="PROCESSING">Đang xử lý</option>
              <option value="COMPLETED">Hoàn thành</option>
              <option value="CANCELLED">Đã hủy</option>
            </select>
          </div>

          {/* Refresh */}
          <button
            onClick={fetchOrders}
            disabled={loading}
            className="flex items-center gap-2 px-4 py-2.5 bg-primary-500 text-white rounded-xl hover:bg-primary-600 transition-all shadow-sm hover:shadow-md disabled:opacity-50"
          >
            <RefreshCw className={`w-5 h-5 ${loading ? 'animate-spin' : ''}`} />
            <span className="font-medium">Làm mới</span>
          </button>
        </div>
      </div>

      {/* Orders Table */}
      {loading ? (
        <div className="py-12 text-center text-gray-500">
          <div className="flex items-center justify-center gap-2">
            <div className="w-5 h-5 border-2 border-primary-500 border-t-transparent rounded-full animate-spin" />
            Đang tải dữ liệu...
          </div>
        </div>
      ) : filteredOrders.length === 0 ? (
        <div className="bg-white rounded-2xl shadow-card border border-cream-100 py-12 text-center text-gray-500">
          <ShoppingCart className="w-12 h-12 mx-auto mb-3 text-gray-300" />
          <p>Không có đơn hàng nào</p>
        </div>
      ) : (
        <div className="bg-white rounded-2xl shadow-card border border-cream-100 overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead className="bg-cream-50">
                <tr>
                  <th className="text-left py-4 px-6 text-xs font-semibold text-gray-500 uppercase tracking-wider">Mã đơn</th>
                  <th className="text-left py-4 px-6 text-xs font-semibold text-gray-500 uppercase tracking-wider">Bàn</th>
                  <th className="text-left py-4 px-6 text-xs font-semibold text-gray-500 uppercase tracking-wider">Nhân viên</th>
                  <th className="text-left py-4 px-6 text-xs font-semibold text-gray-500 uppercase tracking-wider">Thời gian</th>
                  <th className="text-left py-4 px-6 text-xs font-semibold text-gray-500 uppercase tracking-wider">Tổng tiền</th>
                  <th className="text-left py-4 px-6 text-xs font-semibold text-gray-500 uppercase tracking-wider">Trạng thái</th>
                  <th className="text-right py-4 px-6 text-xs font-semibold text-gray-500 uppercase tracking-wider">Thao tác</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-cream-100">
                {filteredOrders.map((order) => {
                  const statusConfig = STATUS_CONFIG[order.status] || STATUS_CONFIG.PENDING;
                  const StatusIcon = statusConfig.icon;

                  return (
                    <tr key={order.id} className="hover:bg-cream-50 transition-colors">
                      <td className="py-4 px-6">
                        <div className="flex items-center gap-2">
                          <Hash className="w-4 h-4 text-gray-400" />
                          <span className="font-semibold text-gray-800">{order.id}</span>
                        </div>
                      </td>
                      <td className="py-4 px-6">
                        <span className="px-3 py-1 bg-primary-100 text-primary-700 rounded-full text-sm font-medium">
                          Bàn {order.table || 'N/A'}
                        </span>
                      </td>
                      <td className="py-4 px-6">
                        <div className="flex items-center gap-2">
                          <User className="w-4 h-4 text-gray-400" />
                          <span className="text-gray-700">{order.userName || 'N/A'}</span>
                        </div>
                      </td>
                      <td className="py-4 px-6">
                        <div className="flex items-center gap-2">
                          <Calendar className="w-4 h-4 text-gray-400" />
                          <span className="text-gray-600 text-sm">{formatDate(order.createdAt)}</span>
                        </div>
                      </td>
                      <td className="py-4 px-6">
                        <span className="font-semibold text-primary-600">{formatCurrency(order.totalPrice)}</span>
                      </td>
                      <td className="py-4 px-6">
                        <button
                          onClick={() => openStatusModal(order)}
                          className={`inline-flex items-center gap-1.5 px-3 py-1.5 rounded-full text-sm font-medium transition-all hover:opacity-80 ${statusConfig.color}`}
                        >
                          <StatusIcon className="w-4 h-4" />
                          {statusConfig.label}
                        </button>
                      </td>
                      <td className="py-4 px-6">
                        <div className="flex items-center justify-end gap-2">
                          <button
                            onClick={() => openDetailModal(order)}
                            className="p-2 text-gray-500 hover:text-primary-600 hover:bg-primary-50 rounded-lg transition-all"
                            title="Xem chi tiết"
                          >
                            <Eye className="w-4 h-4" />
                          </button>
                        </div>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* Pagination */}
      <Pagination
        currentPage={currentPage}
        totalPages={totalPages}
        totalElements={totalElements}
        pageSize={pageSize}
        onPageChange={handlePageChange}
        onPageSizeChange={handlePageSizeChange}
      />

      {/* Order Detail Modal */}
      <Modal
        isOpen={showDetailModal}
        onClose={() => { setShowDetailModal(false); setSelectedOrder(null); }}
        title={`Chi tiết đơn hàng #${selectedOrder?.id || ''}`}
        maxWidth="max-w-2xl"
      >
        {selectedOrder && (
          <div className="p-6">
            {/* Order Info */}
            <div className="grid grid-cols-2 gap-4 mb-6">
              <div className="bg-cream-50 rounded-xl p-4">
                <div className="text-sm text-gray-500 mb-1">Bàn số</div>
                <div className="font-semibold text-gray-800">{selectedOrder.table || 'N/A'}</div>
              </div>
              <div className="bg-cream-50 rounded-xl p-4">
                <div className="text-sm text-gray-500 mb-1">Nhân viên</div>
                <div className="font-semibold text-gray-800">{selectedOrder.userName || 'N/A'}</div>
              </div>
              <div className="bg-cream-50 rounded-xl p-4">
                <div className="text-sm text-gray-500 mb-1">Thời gian</div>
                <div className="font-semibold text-gray-800">{formatDate(selectedOrder.createdAt)}</div>
              </div>
              <div className="bg-cream-50 rounded-xl p-4">
                <div className="text-sm text-gray-500 mb-1">Trạng thái</div>
                <div className={`inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-sm font-medium ${STATUS_CONFIG[selectedOrder.status]?.color || 'bg-gray-100 text-gray-700'}`}>
                  {STATUS_CONFIG[selectedOrder.status]?.label || selectedOrder.status}
                </div>
              </div>
            </div>

            {/* Note */}
            {selectedOrder.note && (
              <div className="mb-6 p-4 bg-yellow-50 rounded-xl border border-yellow-200">
                <div className="text-sm text-yellow-700 font-medium mb-1">Ghi chú</div>
                <div className="text-yellow-800">{selectedOrder.note}</div>
              </div>
            )}

            {/* Order Items */}
            <div className="mb-6">
              <h3 className="text-lg font-semibold text-gray-800 mb-3">Chi tiết sản phẩm</h3>
              <div className="border border-cream-200 rounded-xl overflow-hidden">
                <table className="w-full">
                  <thead className="bg-cream-50">
                    <tr>
                      <th className="text-left py-3 px-4 text-xs font-semibold text-gray-500 uppercase">Sản phẩm</th>
                      <th className="text-center py-3 px-4 text-xs font-semibold text-gray-500 uppercase">SL</th>
                      <th className="text-right py-3 px-4 text-xs font-semibold text-gray-500 uppercase">Đơn giá</th>
                      <th className="text-right py-3 px-4 text-xs font-semibold text-gray-500 uppercase">Thành tiền</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-cream-100">
                    {selectedOrder.orderDetails?.map((item, index) => (
                      <tr key={index}>
                        <td className="py-3 px-4">
                          <div className="flex items-center gap-2">
                            <Package className="w-4 h-4 text-gray-400" />
                            <span className="text-gray-800">{item.name || 'Sản phẩm'}</span>
                          </div>
                        </td>
                        <td className="py-3 px-4 text-center text-gray-600">x{item.quantity}</td>
                        <td className="py-3 px-4 text-right text-gray-600">{formatCurrency(item.price)}</td>
                        <td className="py-3 px-4 text-right font-semibold text-gray-800">
                          {formatCurrency(item.price * item.quantity)}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>

            {/* Total */}
            <div className="flex justify-between items-center p-4 bg-primary-50 rounded-xl">
              <span className="text-lg font-semibold text-gray-800">Tổng cộng</span>
              <span className="text-2xl font-bold text-primary-600">{formatCurrency(selectedOrder.totalPrice)}</span>
            </div>

            {/* Actions */}
            <div className="flex gap-3 mt-6">
              <button
                onClick={() => { setShowDetailModal(false); setSelectedOrder(null); }}
                className="flex-1 px-4 py-2.5 border border-cream-200 text-gray-600 rounded-xl hover:bg-cream-50 transition-all font-medium"
              >
                Đóng
              </button>
              <button
                onClick={() => { setShowDetailModal(false); openStatusModal(selectedOrder); }}
                className="flex-1 px-4 py-2.5 bg-primary-500 text-white rounded-xl hover:bg-primary-600 transition-all font-medium"
              >
                Cập nhật trạng thái
              </button>
            </div>
          </div>
        )}
      </Modal>

      {/* Update Status Modal */}
      <Modal
        isOpen={showStatusModal}
        onClose={() => { setShowStatusModal(false); setSelectedOrder(null); }}
        title="Cập nhật trạng thái"
        maxWidth="max-w-sm"
      >
        {selectedOrder && (
          <div className="p-6">
            <p className="text-gray-600 mb-4">
              Chọn trạng thái mới cho đơn hàng <span className="font-semibold">#{selectedOrder.id}</span>
            </p>

            <div className="space-y-2">
              {Object.entries(STATUS_CONFIG).map(([status, config]) => {
                const StatusIcon = config.icon;
                const isSelected = selectedOrder.status === status;

                return (
                  <button
                    key={status}
                    onClick={() => handleUpdateStatus(selectedOrder.id, status)}
                    disabled={updating || isSelected}
                    className={`w-full flex items-center gap-3 p-3 rounded-xl border-2 transition-all ${
                      isSelected
                        ? 'border-primary-500 bg-primary-50'
                        : 'border-cream-200 hover:border-primary-300 hover:bg-cream-50'
                    } ${updating ? 'opacity-50 cursor-not-allowed' : ''}`}
                  >
                    <div className={`w-10 h-10 rounded-lg flex items-center justify-center ${config.color.replace('text-', 'text-').split(' ')[0]}`}>
                      <StatusIcon className="w-5 h-5" />
                    </div>
                    <span className="font-medium text-gray-800">{config.label}</span>
                    {isSelected && (
                      <span className="ml-auto text-xs text-primary-600 font-medium">Hiện tại</span>
                    )}
                  </button>
                );
              })}
            </div>

            <button
              onClick={() => { setShowStatusModal(false); setSelectedOrder(null); }}
              className="w-full mt-4 px-4 py-2.5 border border-cream-200 text-gray-600 rounded-xl hover:bg-cream-50 transition-all font-medium"
            >
              Hủy
            </button>
          </div>
        )}
      </Modal>
    </div>
  );
};

export default OrdersManagement;
