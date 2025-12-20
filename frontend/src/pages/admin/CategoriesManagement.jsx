import { useState, useEffect, useRef } from 'react';
import {
  Search,
  Plus,
  Edit2,
  Trash2,
  FolderTree,
  Image as ImageIcon,
  X,
  AlertCircle,
  Upload,
  Package,
  Loader2
} from 'lucide-react';
import api, { extractPageData } from '../../services/api';
import Modal from '../../components/ui/Modal';
import Pagination from '../../components/ui/Pagination';
import { useAuth } from '../../contexts/AuthContext';

const CategoriesManagement = () => {
  const { user } = useAuth();
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [showModal, setShowModal] = useState(false);
  const [editingCategory, setEditingCategory] = useState(null);
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(null);
  const [formData, setFormData] = useState({
    name: '',
    imgCategory: ''
  });
  const [formError, setFormError] = useState('');
  const [uploading, setUploading] = useState(false);
  const fileInputRef = useRef(null);

  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [pageSize, setPageSize] = useState(10);

  useEffect(() => {
    fetchCategories();
  }, [currentPage, pageSize, searchTerm]);

  const fetchCategories = async () => {
    try {
      setLoading(true);
      const response = await api.categories.getAll({
        page: currentPage,
        size: pageSize,
        name: searchTerm
      });
      const pageData = extractPageData(response);
      setCategories(Array.isArray(pageData.content) ? pageData.content : []);
      setTotalPages(pageData.totalPages);
      setTotalElements(pageData.totalElements);
    } catch (error) {
      console.error('Error fetching categories:', error);
      setCategories([]);
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

  const handleSubmit = async (e) => {
    e.preventDefault();
    setFormError('');

    if (!formData.name.trim()) {
      setFormError('Vui lòng nhập tên danh mục');
      return;
    }
    if (!formData.imgCategory.trim()) {
      setFormError('Vui lòng upload hoặc nhập URL hình ảnh');
      return;
    }

    try {
      const payload = {
        name: formData.name,
        imgCategory: formData.imgCategory,
        userId: user?.userId || 1
      };

      if (editingCategory) {
        await api.categories.update(editingCategory.id, payload);
      } else {
        await api.categories.create(payload);
      }
      fetchCategories();
      closeModal();
    } catch (error) {
      console.error('Error saving category:', error);
      const errorMsg = error.response?.data?.message || 'Có lỗi xảy ra. Vui lòng thử lại!';
      setFormError(errorMsg);
    }
  };

  const handleDelete = async (categoryId) => {
    try {
      await api.categories.delete(categoryId);
      fetchCategories();
      setShowDeleteConfirm(null);
    } catch (error) {
      console.error('Error deleting category:', error);
      alert('Không thể xóa danh mục này!');
    }
  };

  const openEditModal = (category) => {
    setEditingCategory(category);
    setFormData({
      name: category.name || '',
      imgCategory: category.imgCategory || ''
    });
    setFormError('');
    setShowModal(true);
  };

  const openCreateModal = () => {
    setEditingCategory(null);
    setFormData({ name: '', imgCategory: '' });
    setFormError('');
    setShowModal(true);
  };

  const closeModal = () => {
    setShowModal(false);
    setEditingCategory(null);
    setFormData({ name: '', imgCategory: '' });
    setFormError('');
  };

  const handleImageUpload = async (e) => {
    const file = e.target.files?.[0];
    if (!file) return;

    if (!file.type.startsWith('image/')) {
      alert('Vui lòng chọn file ảnh!');
      return;
    }

    if (file.size > 5 * 1024 * 1024) {
      alert('Kích thước ảnh không được vượt quá 5MB!');
      return;
    }

    try {
      setUploading(true);
      const response = await api.upload.uploadImage(file, 'foodbe/categories');
      const data = extractData(response);
      if (data?.url) {
        setFormData({ ...formData, imgCategory: data.url });
      }
    } catch (error) {
      console.error('Error uploading image:', error);
      alert('Không thể upload ảnh. Vui lòng thử lại!');
    } finally {
      setUploading(false);
      if (fileInputRef.current) {
        fileInputRef.current.value = '';
      }
    }
  };

  const filteredCategories = categories;

  return (
    <div className="animate-fadeIn">
      {/* Header */}
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-800 font-display">
          Quản lý Danh mục
        </h1>
        <p className="text-gray-500 mt-1">
          Quản lý các danh mục sản phẩm trong hệ thống
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
              placeholder="Tìm kiếm danh mục..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full pl-10 pr-4 py-2.5 rounded-xl border border-cream-200 focus:border-primary-500 focus:ring-2 focus:ring-primary-100 transition-all"
            />
          </div>

          <button
            onClick={openCreateModal}
            className="flex items-center gap-2 px-4 py-2.5 bg-primary-500 text-white rounded-xl hover:bg-primary-600 transition-all shadow-sm hover:shadow-md"
          >
            <Plus className="w-5 h-5" />
            <span className="font-medium">Thêm danh mục</span>
          </button>
        </div>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
        <div className="bg-white rounded-xl p-4 border border-cream-100 shadow-card">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 bg-primary-100 rounded-lg flex items-center justify-center">
              <FolderTree className="w-5 h-5 text-primary-600" />
            </div>
            <div>
              <div className="text-2xl font-bold text-gray-800">{categories.length}</div>
              <div className="text-sm text-gray-500">Tổng danh mục</div>
            </div>
          </div>
        </div>
        <div className="bg-white rounded-xl p-4 border border-cream-100 shadow-card">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 bg-green-100 rounded-lg flex items-center justify-center">
              <Package className="w-5 h-5 text-green-600" />
            </div>
            <div>
              <div className="text-2xl font-bold text-gray-800">
                {categories.reduce((acc, cat) => acc + (cat.productCount || 0), 0)}
              </div>
              <div className="text-sm text-gray-500">Tổng sản phẩm</div>
            </div>
          </div>
        </div>
        <div className="bg-white rounded-xl p-4 border border-cream-100 shadow-card">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 bg-blue-100 rounded-lg flex items-center justify-center">
              <ImageIcon className="w-5 h-5 text-blue-600" />
            </div>
            <div>
              <div className="text-2xl font-bold text-gray-800">
                {categories.filter(cat => cat.imgCategory).length}
              </div>
              <div className="text-sm text-gray-500">Có hình ảnh</div>
            </div>
          </div>
        </div>
      </div>

      {/* Categories Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {loading ? (
          <div className="col-span-full py-12 text-center text-gray-500">
            <div className="flex items-center justify-center gap-2">
              <div className="w-5 h-5 border-2 border-primary-500 border-t-transparent rounded-full animate-spin" />
              Đang tải dữ liệu...
            </div>
          </div>
        ) : filteredCategories.length === 0 ? (
          <div className="col-span-full py-12 text-center text-gray-500">
            <FolderTree className="w-12 h-12 mx-auto mb-3 text-gray-300" />
            <p>Không tìm thấy danh mục nào</p>
          </div>
        ) : (
          filteredCategories.map((category) => (
            <div
              key={category.id}
              className="bg-white rounded-2xl shadow-card border border-cream-100 overflow-hidden hover:shadow-soft transition-all duration-300 group"
            >
              {/* Image */}
              <div className="relative h-40 bg-gradient-to-br from-cream-100 to-cream-200 overflow-hidden">
                {category.imgCategory ? (
                  <img
                    src={category.imgCategory}
                    alt={category.name}
                    className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
                  />
                ) : (
                  <div className="w-full h-full flex items-center justify-center">
                    <FolderTree className="w-16 h-16 text-cream-300" />
                  </div>
                )}
                <div className="absolute top-3 right-3 flex gap-2 opacity-0 group-hover:opacity-100 transition-opacity">
                  <button
                    onClick={() => openEditModal(category)}
                    className="p-2 bg-white/90 backdrop-blur-sm text-gray-600 hover:text-primary-600 rounded-lg shadow-sm transition-all"
                  >
                    <Edit2 className="w-4 h-4" />
                  </button>
                  <button
                    onClick={() => setShowDeleteConfirm(category.id)}
                    className="p-2 bg-white/90 backdrop-blur-sm text-gray-600 hover:text-red-600 rounded-lg shadow-sm transition-all"
                  >
                    <Trash2 className="w-4 h-4" />
                  </button>
                </div>
              </div>

              {/* Content */}
              <div className="p-5">
                <div className="flex items-start justify-between mb-2">
                  <h3 className="text-lg font-semibold text-gray-800">{category.name}</h3>
                  <span className="px-2.5 py-1 bg-primary-50 text-primary-600 rounded-full text-xs font-medium">
                    {category.productCount || 0} sản phẩm
                  </span>
                </div>
                <p className="text-sm text-gray-500 line-clamp-2">
                  {category.description || 'Chưa có mô tả'}
                </p>
              </div>
            </div>
          ))
        )}
      </div>

      {/* Pagination */}
      <Pagination
        currentPage={currentPage}
        totalPages={totalPages}
        totalElements={totalElements}
        pageSize={pageSize}
        onPageChange={handlePageChange}
        onPageSizeChange={handlePageSizeChange}
      />

      {/* Create/Edit Modal */}
      <Modal
        isOpen={showModal}
        onClose={closeModal}
        title={editingCategory ? 'Chỉnh sửa danh mục' : 'Thêm danh mục mới'}
      >
        <form onSubmit={handleSubmit} className="p-6 space-y-4">
          {formError && (
            <div className="p-3 bg-red-50 border border-red-200 rounded-xl text-red-600 text-sm">
              {formError}
            </div>
          )}

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1.5">
              Tên danh mục <span className="text-red-500">*</span>
            </label>
            <input
              type="text"
              value={formData.name}
              onChange={(e) => setFormData({ ...formData, name: e.target.value })}
              className="w-full px-4 py-2.5 rounded-xl border border-cream-200 focus:border-primary-500 focus:ring-2 focus:ring-primary-100 transition-all"
              placeholder="Nhập tên danh mục"
              required
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1.5">
              Hình ảnh <span className="text-red-500">*</span>
            </label>

            <div className="mb-3">
              <input
                ref={fileInputRef}
                type="file"
                accept="image/*"
                onChange={handleImageUpload}
                className="hidden"
                id="category-image-upload"
              />
              <label
                htmlFor="category-image-upload"
                className={`flex items-center justify-center gap-2 w-full px-4 py-3 border-2 border-dashed border-cream-300 rounded-xl cursor-pointer hover:border-primary-400 hover:bg-primary-50 transition-all ${uploading ? 'opacity-50 cursor-not-allowed' : ''}`}
              >
                {uploading ? (
                  <>
                    <Loader2 className="w-5 h-5 text-primary-500 animate-spin" />
                    <span className="text-sm text-gray-600">Đang upload...</span>
                  </>
                ) : (
                  <>
                    <Upload className="w-5 h-5 text-gray-400" />
                    <span className="text-sm text-gray-600">Click để chọn ảnh từ máy</span>
                  </>
                )}
              </label>
            </div>

            <div className="relative">
              <ImageIcon className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
              <input
                type="url"
                value={formData.imgCategory}
                onChange={(e) => setFormData({ ...formData, imgCategory: e.target.value })}
                className="w-full pl-10 pr-4 py-2.5 rounded-xl border border-cream-200 focus:border-primary-500 focus:ring-2 focus:ring-primary-100 transition-all"
                placeholder="Hoặc nhập URL ảnh..."
              />
            </div>

            {formData.imgCategory && (
              <div className="mt-3 relative group">
                <img
                  src={formData.imgCategory}
                  alt="Preview"
                  className="w-full h-32 object-cover rounded-xl border border-cream-200"
                  onError={(e) => {
                    e.target.src = '';
                    e.target.style.display = 'none';
                  }}
                />
                <button
                  type="button"
                  onClick={() => setFormData({ ...formData, imgCategory: '' })}
                  className="absolute top-2 right-2 p-1.5 bg-red-500 text-white rounded-lg opacity-0 group-hover:opacity-100 transition-opacity"
                >
                  <X className="w-4 h-4" />
                </button>
              </div>
            )}
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
              disabled={uploading}
              className="flex-1 px-4 py-2.5 bg-primary-500 text-white rounded-xl hover:bg-primary-600 transition-all font-medium shadow-sm disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {editingCategory ? 'Cập nhật' : 'Tạo mới'}
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
              <p className="text-sm text-gray-500">Bạn có chắc muốn xóa danh mục này?</p>
            </div>
          </div>
          <p className="text-sm text-gray-600 mb-6">
            Hành động này không thể hoàn tác. Tất cả sản phẩm trong danh mục sẽ không còn thuộc danh mục nào.
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

export default CategoriesManagement;
