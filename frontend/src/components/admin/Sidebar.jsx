import { NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';
import {
  LayoutDashboard,
  Users,
  FolderTree,
  Package,
  ShoppingCart,
  TrendingUp,
  LogOut,
  ChefHat,
  Bell
} from 'lucide-react';

const menuItems = [
  {
    path: '/',
    icon: LayoutDashboard,
    label: 'Dashboard',
    end: true
  },
  {
    path: '/users',
    icon: Users,
    label: 'Quản lý Người dùng'
  },
  {
    path: '/categories',
    icon: FolderTree,
    label: 'Quản lý Danh mục'
  },
  {
    path: '/products',
    icon: Package,
    label: 'Quản lý Sản phẩm'
  },
  {
    path: '/orders',
    icon: ShoppingCart,
    label: 'Quản lý Đơn hàng'
  },
  {
    path: '/revenue',
    icon: TrendingUp,
    label: 'Báo cáo Doanh thu'
  }
];

const Sidebar = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  return (
    <aside className="fixed left-0 top-0 h-screen w-64 bg-sidebar-bg border-r border-cream-200 flex flex-col z-40">
      <div className="h-16 flex items-center px-6 border-b border-cream-200">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 bg-gradient-to-br from-primary-400 to-primary-600 rounded-xl flex items-center justify-center shadow-soft">
            <ChefHat className="w-6 h-6 text-white" />
          </div>
          <div>
            <h1 className="font-display font-bold text-lg text-gray-800">Food&Drink</h1>
            <span className="text-xs text-gray-500">Quản trị viên</span>
          </div>
        </div>
      </div>

      <nav className="flex-1 py-6 px-3 overflow-y-auto">
        <div className="space-y-1">
          {menuItems.map((item) => (
            <NavLink
              key={item.path}
              to={item.path}
              end={item.end}
              className={({ isActive }) =>
                `flex items-center gap-3 px-4 py-3 rounded-xl transition-all duration-200 group ${
                  isActive
                    ? 'bg-primary-50 text-primary-600 shadow-sm'
                    : 'text-gray-600 hover:bg-sidebar-hover hover:text-gray-800'
                }`
              }
            >
              {({ isActive }) => (
                <>
                  <item.icon
                    className={`w-5 h-5 transition-colors ${
                      isActive ? 'text-primary-500' : 'text-gray-400 group-hover:text-gray-600'
                    }`}
                  />
                  <span className="font-medium text-sm">{item.label}</span>
                  {isActive && (
                    <div className="ml-auto w-1.5 h-1.5 rounded-full bg-primary-500" />
                  )}
                </>
              )}
            </NavLink>
          ))}
        </div>

      </nav>

      <div className="p-4 border-t border-cream-200">
        <div className="flex items-center gap-3 px-3 py-2 mb-3">
          <div className="w-10 h-10 bg-gradient-to-br from-primary-300 to-primary-500 rounded-full flex items-center justify-center text-white font-semibold text-sm">
            {user?.email?.charAt(0).toUpperCase() || 'A'}
          </div>
          <div className="flex-1 min-w-0">
            <p className="text-sm font-medium text-gray-800 truncate">
              {user?.email || 'Admin'}
            </p>
            <p className="text-xs text-gray-500">Quản trị viên</p>
          </div>
          <button className="p-2 text-gray-400 hover:text-gray-600 hover:bg-sidebar-hover rounded-lg transition-colors">
            <Bell className="w-5 h-5" />
          </button>
        </div>

        <button
          onClick={handleLogout}
          className="w-full flex items-center gap-3 px-4 py-3 rounded-xl text-red-500 hover:bg-red-50 transition-all duration-200 group"
        >
          <LogOut className="w-5 h-5" />
          <span className="font-medium text-sm">Đăng xuất</span>
        </button>
      </div>
    </aside>
  );
};

export default Sidebar;
