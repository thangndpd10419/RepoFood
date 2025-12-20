import { useState, useEffect } from 'react';
import {
  Search,
  Plus,
  Edit2,
  Trash2,
  MoreVertical,
  Filter,
  UserPlus,
  Mail,
  Phone,
  Shield,
  X,
  Check,
  AlertCircle,
  User
} from 'lucide-react';
import api, { extractPageData } from '../../services/api';
import Modal from '../../components/ui/Modal';
import Pagination from '../../components/ui/Pagination';

const initialFormData = {
  name: '',
  email: '',
  password: '',
  confirmPassword: '',
  phone: '',
  status: 'ACTIVE',
  role: 'STAFF'
};

const UsersManagement = () => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [filterRole, setFilterRole] = useState('all');
  const [showModal, setShowModal] = useState(false);
  const [editingUser, setEditingUser] = useState(null);
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(null);
  const [formData, setFormData] = useState(initialFormData);
  const [formError, setFormError] = useState('');

  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [pageSize, setPageSize] = useState(10);

  useEffect(() => {
    fetchUsers();
  }, [currentPage, pageSize, searchTerm]);

  const fetchUsers = async () => {
    try {
      setLoading(true);
      const response = await api.users.getAll({
        page: currentPage,
        size: pageSize,
        email: searchTerm
      });
      const pageData = extractPageData(response);
      setUsers(Array.isArray(pageData.content) ? pageData.content : []);
      setTotalPages(pageData.totalPages);
      setTotalElements(pageData.totalElements);
    } catch (error) {
      console.error('Error fetching users:', error);
      setUsers([]);
      setTotalPages(0);
      setTotalElements(0);
    } finally {
      setLoading(false);
    }
  };

  const handlePageChange = (page) => {
    setCurrentPage(page);
  };

  const handlePageSizeChange = (size) => {
    setPageSize(size);
    setCurrentPage(0);
  };

  const validateForm = () => {
    if (!formData.name.trim()) {
      setFormError('Vui lòng nhập họ tên');
      return false;
    }
    if (!formData.email.trim()) {
      setFormError('Vui lòng nhập email');
      return false;
    }
    if (!editingUser) {
      if (!formData.password) {
        setFormError('Vui lòng nhập mật khẩu');
        return false;
      }
      if (formData.password.length < 6) {
        setFormError('Mật khẩu phải có ít nhất 6 ký tự');
        return false;
      }
      if (!/^(?=.*[a-z])(?=.*[A-Z])(?=.*\W).{6,}$/.test(formData.password)) {
        setFormError('Mật khẩu phải có chữ hoa, chữ thường và ký tự đặc biệt');
        return false;
      }
      if (formData.password !== formData.confirmPassword) {
        setFormError('Mật khẩu xác nhận không khớp');
        return false;
      }
    }
    if (formData.phone && !/^0[0-9]{9}$/.test(formData.phone)) {
      setFormError('Số điện thoại không hợp lệ (10 số, bắt đầu bằng 0)');
      return false;
    }
    setFormError('');
    return true;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validateForm()) return;

    try {
      const payload = {
        name: formData.name,
        email: formData.email,
        phone: formData.phone || '0000000000',
        status: formData.status,
        role: formData.role
      };

      if (!editingUser) {
        payload.password = formData.password;
        payload.confirmPassword = formData.confirmPassword;
        await api.users.create(payload);
      } else {
        if (formData.password) {
          payload.password = formData.password;
          payload.confirmPassword = formData.confirmPassword;
        }
        await api.users.update(editingUser.id, payload);
      }
      fetchUsers();
      closeModal();
    } catch (error) {
      console.error('Error saving user:', error);
      const errorMsg = error.response?.data?.message || 'Có lỗi xảy ra. Vui lòng thử lại!';
      setFormError(errorMsg);
    }
  };

  const handleDelete = async (userId) => {
    try {
      await api.users.delete(userId);
      fetchUsers();
      setShowDeleteConfirm(null);
    } catch (error) {
      console.error('Error deleting user:', error);
      alert('Không thể xóa người dùng này!');
    }
  };

  const openEditModal = (user) => {
    setEditingUser(user);
    setFormData({
      name: user.name || '',
      email: user.email || '',
      password: '',
      confirmPassword: '',
      phone: user.phone || '',
      status: user.status || 'ACTIVE',
      role: user.role || 'STAFF'
    });
    setFormError('');
    setShowModal(true);
  };

  const openCreateModal = () => {
    setEditingUser(null);
    setFormData(initialFormData);
    setFormError('');
    setShowModal(true);
  };

  const closeModal = () => {
    setShowModal(false);
    setEditingUser(null);
    setFormData(initialFormData);
    setFormError('');
  };

  const filteredUsers = users.filter(user => {
    const matchesRole = filterRole === 'all' || user.role === filterRole;
    return matchesRole;
  });

  const getRoleBadge = (role) => {
    const styles = {
      ADMIN: 'bg-red-100 text-red-700 border-red-200',
      STAFF: 'bg-blue-100 text-blue-700 border-blue-200'
    };
    const labels = {
      ADMIN: 'Quản trị viên',
      STAFF: 'Nhân viên'
    };
    return (
      <span className={`inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-medium border ${styles[role] || styles.STAFF}`}>
        <Shield className="w-3 h-3" />
        {labels[role] || role}
      </span>
    );
  };

  const getStatusBadge = (status) => {
    const isActive = status === 'ACTIVE';
    return (
      <span className={`inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-medium ${
        isActive ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-600'
      }`}>
        <span className={`w-1.5 h-1.5 rounded-full ${isActive ? 'bg-green-500' : 'bg-gray-400'}`} />
        {isActive ? 'Hoạt động' : 'Ngừng'}
      </span>
    );
  };

  return (
    <div className="animate-fadeIn">
      {/* Header */}
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-800 font-display">
          Quản lý Người dùng
        </h1>
        <p className="text-gray-500 mt-1">
          Quản lý tài khoản người dùng trong hệ thống
        </p>
      </div>

      {/* Actions Bar */}
      <div className="bg-white rounded-2xl shadow-card border border-cream-100 p-4 mb-6">
        <div className="flex flex-col md:flex-row gap-4 items-center justify-between">
          {/* Search */}
          <div className="relative flex-1 max-w-md">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
            <input
              type="text"
              placeholder="Tìm kiếm theo email..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full pl-10 pr-4 py-2.5 rounded-xl border border-cream-200 focus:border-primary-500 focus:ring-2 focus:ring-primary-100 transition-all"
            />
          </div>

          {/* Filters & Actions */}
          <div className="flex items-center gap-3">
            <select
              value={filterRole}
              onChange={(e) => setFilterRole(e.target.value)}
              className="px-4 py-2.5 rounded-xl border border-cream-200 text-gray-600 focus:border-primary-500 focus:ring-2 focus:ring-primary-100 transition-all"
            >
              <option value="all">Tất cả vai trò</option>
              <option value="ADMIN">Quản trị viên</option>
              <option value="STAFF">Nhân viên</option>
            </select>

            <button className="p-2.5 rounded-xl border border-cream-200 text-gray-600 hover:bg-cream-50 transition-all">
              <Filter className="w-5 h-5" />
            </button>


            <button
              onClick={openCreateModal}
              className="flex items-center gap-2 px-4 py-2.5 bg-primary-500 text-white rounded-xl hover:bg-primary-600 transition-all shadow-sm hover:shadow-md"
            >
              <UserPlus className="w-5 h-5" />
              <span className="font-medium">Thêm mới</span>
            </button>
          </div>
        </div>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
        <div className="bg-white rounded-xl p-4 border border-cream-100 shadow-card">
          <div className="text-2xl font-bold text-gray-800">{users.length}</div>
          <div className="text-sm text-gray-500">Tổng số người dùng</div>
        </div>
        <div className="bg-white rounded-xl p-4 border border-cream-100 shadow-card">
          <div className="text-2xl font-bold text-red-600">{users.filter(u => u.role === 'ADMIN').length}</div>
          <div className="text-sm text-gray-500">Quản trị viên</div>
        </div>
        <div className="bg-white rounded-xl p-4 border border-cream-100 shadow-card">
          <div className="text-2xl font-bold text-blue-600">{users.filter(u => u.role === 'STAFF').length}</div>
          <div className="text-sm text-gray-500">Nhân viên</div>
        </div>
        <div className="bg-white rounded-xl p-4 border border-cream-100 shadow-card">
          <div className="text-2xl font-bold text-green-600">{users.filter(u => u.status === 'ACTIVE').length}</div>
          <div className="text-sm text-gray-500">Đang hoạt động</div>
        </div>
      </div>

      {/* Users Table */}
      <div className="bg-white rounded-2xl shadow-card border border-cream-100 overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="bg-cream-50">
              <tr>
                <th className="text-left py-4 px-6 text-xs font-semibold text-gray-500 uppercase tracking-wider">Người dùng</th>
                <th className="text-left py-4 px-6 text-xs font-semibold text-gray-500 uppercase tracking-wider">Vai trò</th>
                <th className="text-left py-4 px-6 text-xs font-semibold text-gray-500 uppercase tracking-wider">Trạng thái</th>
                <th className="text-left py-4 px-6 text-xs font-semibold text-gray-500 uppercase tracking-wider">SĐT</th>
                <th className="text-right py-4 px-6 text-xs font-semibold text-gray-500 uppercase tracking-wider">Thao tác</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-cream-100">
              {loading ? (
                <tr>
                  <td colSpan={5} className="py-12 text-center text-gray-500">
                    <div className="flex items-center justify-center gap-2">
                      <div className="w-5 h-5 border-2 border-primary-500 border-t-transparent rounded-full animate-spin" />
                      Đang tải dữ liệu...
                    </div>
                  </td>
                </tr>
              ) : filteredUsers.length === 0 ? (
                <tr>
                  <td colSpan={5} className="py-12 text-center text-gray-500">
                    Không tìm thấy người dùng nào
                  </td>
                </tr>
              ) : (
                filteredUsers.map((user) => (
                  <tr key={user.id} className="table-row hover:bg-cream-50 transition-colors">
                    <td className="py-4 px-6">
                      <div className="flex items-center gap-3">
                        <div className="w-10 h-10 bg-gradient-to-br from-primary-300 to-primary-500 rounded-full flex items-center justify-center text-white font-semibold text-sm">
                          {(user.name || user.email)?.charAt(0).toUpperCase()}
                        </div>
                        <div>
                          <p className="font-medium text-gray-800">{user.name || 'Chưa có tên'}</p>
                          <p className="text-sm text-gray-500">{user.email}</p>
                        </div>
                      </div>
                    </td>
                    <td className="py-4 px-6">
                      {getRoleBadge(user.role)}
                    </td>
                    <td className="py-4 px-6">
                      {getStatusBadge(user.status)}
                    </td>
                    <td className="py-4 px-6 text-sm text-gray-600">
                      {user.phone || '-'}
                    </td>
                    <td className="py-4 px-6">
                      <div className="flex items-center justify-end gap-2">
                        <button
                          onClick={() => openEditModal(user)}
                          className="p-2 text-gray-500 hover:text-primary-600 hover:bg-primary-50 rounded-lg transition-all"
                          title="Chỉnh sửa"
                        >
                          <Edit2 className="w-4 h-4" />
                        </button>
                        <button
                          onClick={() => setShowDeleteConfirm(user.id)}
                          className="p-2 text-gray-500 hover:text-red-600 hover:bg-red-50 rounded-lg transition-all"
                          title="Xóa"
                        >
                          <Trash2 className="w-4 h-4" />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        <Pagination
          currentPage={currentPage}
          totalPages={totalPages}
          totalElements={totalElements}
          pageSize={pageSize}
          onPageChange={handlePageChange}
          onPageSizeChange={handlePageSizeChange}
        />
      </div>

      {/* Create/Edit Modal */}
      <Modal
        isOpen={showModal}
        onClose={closeModal}
        title={editingUser ? 'Chỉnh sửa người dùng' : 'Thêm người dùng mới'}
        maxWidth="max-w-lg"
      >
        <form onSubmit={handleSubmit} className="p-6 space-y-4">
          {formError && (
            <div className="p-3 bg-red-50 border border-red-200 rounded-xl text-red-600 text-sm">
              {formError}
            </div>
          )}

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1.5">
              Họ tên <span className="text-red-500">*</span>
            </label>
            <div className="relative">
              <User className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
              <input
                type="text"
                value={formData.name}
                onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                className="w-full pl-10 pr-4 py-2.5 rounded-xl border border-cream-200 focus:border-primary-500 focus:ring-2 focus:ring-primary-100 transition-all"
                placeholder="Nhập họ tên"
                required
              />
            </div>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1.5">
              Email <span className="text-red-500">*</span>
            </label>
            <div className="relative">
              <Mail className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
              <input
                type="email"
                value={formData.email}
                onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                className="w-full pl-10 pr-4 py-2.5 rounded-xl border border-cream-200 focus:border-primary-500 focus:ring-2 focus:ring-primary-100 transition-all"
                placeholder="email@example.com"
                required
              />
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1.5">
                Mật khẩu {!editingUser && <span className="text-red-500">*</span>}
              </label>
              <input
                type="password"
                value={formData.password}
                onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                className="w-full px-4 py-2.5 rounded-xl border border-cream-200 focus:border-primary-500 focus:ring-2 focus:ring-primary-100 transition-all"
                placeholder={editingUser ? 'Để trống nếu không đổi' : 'Mật khẩu'}
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1.5">
                Xác nhận MK {!editingUser && <span className="text-red-500">*</span>}
              </label>
              <input
                type="password"
                value={formData.confirmPassword}
                onChange={(e) => setFormData({ ...formData, confirmPassword: e.target.value })}
                className="w-full px-4 py-2.5 rounded-xl border border-cream-200 focus:border-primary-500 focus:ring-2 focus:ring-primary-100 transition-all"
                placeholder="Nhập lại mật khẩu"
              />
            </div>
          </div>
          {!editingUser && (
            <p className="text-xs text-gray-500">
              Mật khẩu cần có chữ hoa, chữ thường và ký tự đặc biệt
            </p>
          )}

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1.5">
              Số điện thoại
            </label>
            <div className="relative">
              <Phone className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
              <input
                type="tel"
                value={formData.phone}
                onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
                className="w-full pl-10 pr-4 py-2.5 rounded-xl border border-cream-200 focus:border-primary-500 focus:ring-2 focus:ring-primary-100 transition-all"
                placeholder="0912345678"
              />
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1.5">
                Vai trò <span className="text-red-500">*</span>
              </label>
              <select
                value={formData.role}
                onChange={(e) => setFormData({ ...formData, role: e.target.value })}
                className="w-full px-4 py-2.5 rounded-xl border border-cream-200 focus:border-primary-500 focus:ring-2 focus:ring-primary-100 transition-all"
              >
                <option value="STAFF">Nhân viên</option>
                <option value="ADMIN">Quản trị viên</option>
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1.5">
                Trạng thái <span className="text-red-500">*</span>
              </label>
              <select
                value={formData.status}
                onChange={(e) => setFormData({ ...formData, status: e.target.value })}
                className="w-full px-4 py-2.5 rounded-xl border border-cream-200 focus:border-primary-500 focus:ring-2 focus:ring-primary-100 transition-all"
              >
                <option value="ACTIVE">Hoạt động</option>
                <option value="INACTIVE">Ngừng hoạt động</option>
              </select>
            </div>
          </div>

          <div className="flex gap-3 pt-4">
            <button
              type="button"
              onClick={closeModal}
              className="flex-1 px-4 py-2.5 border border-cream-200 text-gray-600 rounded-xl hover:bg-cream-50 transition-all font-medium"
            >
              Hủy bỏ
            </button>
            <button
              type="submit"
              className="flex-1 px-4 py-2.5 bg-primary-500 text-white rounded-xl hover:bg-primary-600 transition-all font-medium shadow-sm"
            >
              {editingUser ? 'Cập nhật' : 'Tạo mới'}
            </button>
          </div>
        </form>
      </Modal>

      {/* Delete Confirmation Modal */}
      <Modal
        isOpen={!!showDeleteConfirm}
        onClose={() => setShowDeleteConfirm(null)}
        maxWidth="max-w-sm"
      >
        <div className="p-6">
          <div className="flex items-center gap-4 mb-4">
            <div className="w-12 h-12 bg-red-100 rounded-full flex items-center justify-center">
              <AlertCircle className="w-6 h-6 text-red-600" />
            </div>
            <div>
              <h3 className="text-lg font-semibold text-gray-800">Xác nhận xóa</h3>
              <p className="text-sm text-gray-500">Bạn có chắc muốn xóa người dùng này?</p>
            </div>
          </div>
          <p className="text-sm text-gray-600 mb-6">
            Hành động này không thể hoàn tác. Tất cả dữ liệu liên quan đến người dùng sẽ bị xóa vĩnh viễn.
          </p>
          <div className="flex gap-3">
            <button
              onClick={() => setShowDeleteConfirm(null)}
              className="flex-1 px-4 py-2.5 border border-cream-200 text-gray-600 rounded-xl hover:bg-cream-50 transition-all font-medium"
            >
              Hủy bỏ
            </button>
            <button
              onClick={() => handleDelete(showDeleteConfirm)}
              className="flex-1 px-4 py-2.5 bg-red-500 text-white rounded-xl hover:bg-red-600 transition-all font-medium"
            >
              Xóa
            </button>
          </div>
        </div>
      </Modal>
    </div>
  );
};

export default UsersManagement;
