import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

const Layout = ({ children }) => {
  const { user, logout, isAuthenticated } = useAuth();
  const navigate = useNavigate();

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  return (
    <div className="min-h-screen flex flex-col">
      <header className="bg-white shadow-md sticky top-0 z-50">
        <div className="container mx-auto px-4">
          <div className="flex items-center justify-between h-16">
            <Link to="/" className="text-2xl font-bold text-orange-500">
              Food&Drink
            </Link>

            <nav className="hidden md:flex items-center space-x-6">
              <Link to="/" className="text-gray-600 hover:text-orange-500 transition">
                Trang chủ
              </Link>
              <Link to="/products" className="text-gray-600 hover:text-orange-500 transition">
                Thực đơn
              </Link>
              {isAuthenticated && (
                <>
                  <Link to="/orders" className="text-gray-600 hover:text-orange-500 transition">
                    Đơn hàng
                  </Link>
                  <Link to="/cart" className="text-gray-600 hover:text-orange-500 transition">
                    Giỏ hàng
                  </Link>
                </>
              )}
            </nav>

            <div className="flex items-center space-x-4">
              {isAuthenticated ? (
                <>
                  <span className="text-gray-600 hidden md:inline">
                    Xin chào, {user?.email}
                  </span>
                  <button
                    onClick={handleLogout}
                    className="bg-red-500 text-white px-4 py-2 rounded-md hover:bg-red-600 transition"
                  >
                    Đăng xuất
                  </button>
                </>
              ) : (
                <Link
                  to="/login"
                  className="bg-orange-500 text-white px-4 py-2 rounded-md hover:bg-orange-600 transition"
                >
                  Đăng nhập
                </Link>
              )}
            </div>
          </div>
        </div>
      </header>

      <main className="flex-1">
        {children}
      </main>

      <footer className="bg-gray-800 text-white py-8">
        <div className="container mx-auto px-4">
          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            <div>
              <h3 className="text-xl font-bold mb-4">Food&Drink</h3>
              <p className="text-gray-400">
                Hệ thống quản lý nhà hàng chuyên nghiệp
              </p>
            </div>
            <div>
              <h3 className="text-lg font-semibold mb-4">Liên hệ</h3>
              <p className="text-gray-400">Email: contact@foodbe.vn</p>
              <p className="text-gray-400">Điện thoại: 0123 456 789</p>
            </div>
            <div>
              <h3 className="text-lg font-semibold mb-4">Theo dõi chúng tôi</h3>
              <div className="flex space-x-4">
                <span className="text-gray-400 hover:text-white cursor-pointer">Facebook</span>
                <span className="text-gray-400 hover:text-white cursor-pointer">Instagram</span>
              </div>
            </div>
          </div>
          <div className="border-t border-gray-700 mt-8 pt-8 text-center text-gray-400">
            © 2024 Food&Drink. All rights reserved.
          </div>
        </div>
      </footer>
    </div>
  );
};

export default Layout;
