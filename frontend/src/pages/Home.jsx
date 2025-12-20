import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { categoryAPI, productAPI } from '../services/api';

const Home = () => {
  const [categories, setCategories] = useState([]);
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [catRes, prodRes] = await Promise.all([
          categoryAPI.getAll({ size: 10 }),
          productAPI.getAll({ size: 8 })
        ]);
        setCategories(catRes.data.data.content || []);
        setProducts(prodRes.data.data.content || []);
      } catch (error) {
        console.error('Error fetching data:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-xl text-gray-600">Đang tải...</div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="bg-gradient-to-r from-orange-500 to-red-500 text-white py-16">
        <div className="container mx-auto px-4 text-center">
          <h1 className="text-4xl font-bold mb-4">Chào mừng đến với Food&Drink</h1>
          <p className="text-xl mb-8">Hệ thống quản lý nhà hàng chuyên nghiệp</p>
          <Link
            to="/products"
            className="bg-white text-orange-500 px-6 py-3 rounded-lg font-semibold hover:bg-gray-100 transition"
          >
            Xem Thực Đơn
          </Link>
        </div>
      </div>

      <div className="container mx-auto px-4 py-12">
        <h2 className="text-2xl font-bold text-gray-800 mb-6">Danh Mục</h2>
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
          {categories.map((cat) => (
            <div
              key={cat.id}
              className="bg-white rounded-lg shadow-md p-6 text-center hover:shadow-lg transition cursor-pointer"
            >
              <div className="text-4xl mb-2">
                {cat.name.includes('an') ? '🍜' : cat.name.includes('uong') ? '🥤' : cat.name.includes('trang') ? '🍰' : '🍔'}
              </div>
              <h3 className="font-semibold text-gray-800">{cat.name}</h3>
            </div>
          ))}
        </div>
      </div>

      <div className="container mx-auto px-4 py-12 bg-white">
        <div className="flex justify-between items-center mb-6">
          <h2 className="text-2xl font-bold text-gray-800">Sản Phẩm Nổi Bật</h2>
          <Link to="/products" className="text-orange-500 hover:text-orange-600 font-semibold">
            Xem tất cả →
          </Link>
        </div>
        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
          {products.map((product) => (
            <div
              key={product.id}
              className="bg-gray-50 rounded-lg shadow-md overflow-hidden hover:shadow-lg transition"
            >
              <div className="h-40 bg-gradient-to-br from-orange-100 to-orange-200 flex items-center justify-center text-6xl">
                🍽️
              </div>
              <div className="p-4">
                <h3 className="font-semibold text-gray-800 mb-2">{product.name}</h3>
                <p className="text-sm text-gray-600 mb-2 line-clamp-2">{product.description}</p>
                <div className="flex justify-between items-center">
                  <span className="text-lg font-bold text-orange-500">
                    {product.price?.toLocaleString('vi-VN')} VND
                  </span>
                  <button className="bg-orange-500 text-white px-3 py-1 rounded-md text-sm hover:bg-orange-600 transition">
                    Thêm
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default Home;
