import axios from 'axios';

const api = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export const authAPI = {
  login: (email, password) => api.post('/auth/login', { email, password }),
  register: (data) => api.post('/auth/register', data),
  refresh: (refreshToken) => api.post('/auth/refresh', { refreshToken }),
  logout: (token) => api.put('/auth/revoke', token),
};

export const categoryAPI = {
  getAll: (params) => api.get('/categories', { params }),
  getById: (id) => api.get(`/categories/${id}`),
  create: (data) => api.post('/categories', data),
  update: (id, data) => api.put(`/categories/${id}`, data),
  delete: (id) => api.delete(`/categories/${id}`),
};

export const productAPI = {
  getAll: (params) => api.get('/products', { params }),
  getById: (id) => api.get(`/products/${id}`),
  create: (data) => api.post('/products', data),
  update: (id, data) => api.put(`/products/${id}`, data),
  delete: (id) => api.delete(`/products/${id}`),
};

export const cartAPI = {
  getAll: () => api.get('/carts'),
  create: (data) => api.post('/carts', data),
  update: (id, data) => api.patch(`/carts/${id}`, data),
  delete: (id) => api.delete(`/carts/${id}`),
};

export const orderAPI = {
  getAll: (params) => api.get('/orders', { params }),
  getById: (id) => api.get(`/orders/${id}`),
  create: (data) => api.post('/orders', data),
  update: (id, data) => api.put(`/orders/${id}`, data),
  delete: (id) => api.delete(`/orders/${id}`),
};

export const reviewAPI = {
  getAll: (params) => api.get('/reviews', { params }),
  create: (data) => api.post('/reviews', data),
  update: (id, data) => api.put(`/reviews/${id}`, data),
  delete: (id) => api.delete(`/reviews/${id}`),
};

export const userAPI = {
  getAll: (params) => api.get('/users', { params }),
  getById: (id) => api.get(`/users/${id}`),
  create: (data) => api.post('/users', data),
  update: (id, data) => api.put(`/users/${id}`, data),
  delete: (id) => api.delete(`/users/${id}`),
};

export const statisticsAPI = {
  getDashboard: () => api.get('/statistics/dashboard'),
  getRevenueDaily: (startDate, endDate) => api.get('/statistics/revenue/daily', {
    params: { startDate, endDate }
  }),
  getRevenueMonthly: (year) => api.get('/statistics/revenue/monthly', {
    params: { year }
  }),
  getRevenueYearly: () => api.get('/statistics/revenue/yearly'),
  getRevenueByCategory: () => api.get('/statistics/revenue/category'),
  getTopProducts: (limit = 10) => api.get('/statistics/top-products', {
    params: { limit }
  }),
  getTopProductsInRange: (startDate, endDate, limit = 10) => api.get('/statistics/top-products/range', {
    params: { startDate, endDate, limit }
  }),
};

export const uploadAPI = {
  uploadImage: (file, folder = 'foodbe') => {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('folder', folder);
    return api.post('/upload/image', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    });
  },
  deleteImage: (publicId) => api.delete('/upload/image', {
    params: { publicId }
  }),
};

export const extractData = (response) => {
  const axiosData = response?.data;

  if (axiosData && typeof axiosData === 'object' && 'data' in axiosData) {
    const innerData = axiosData.data;

    if (innerData && typeof innerData === 'object' && 'content' in innerData) {
      return innerData.content;
    }

    return innerData;
  }

  if (axiosData && typeof axiosData === 'object' && 'content' in axiosData) {
    return axiosData.content;
  }

  return axiosData;
};

export const extractPageData = (response) => {
  const axiosData = response?.data;

  if (axiosData && typeof axiosData === 'object' && 'data' in axiosData) {
    const innerData = axiosData.data;

    if (innerData && typeof innerData === 'object' && 'content' in innerData) {
      return {
        content: innerData.content || [],
        totalElements: innerData.totalElements || 0,
        totalPages: innerData.totalPages || 0,
        currentPage: innerData.currentPage || 0,
        pageSize: innerData.pageSize || 10
      };
    }

    return { content: innerData, totalElements: 0, totalPages: 0, currentPage: 0, pageSize: 10 };
  }

  return { content: axiosData || [], totalElements: 0, totalPages: 0, currentPage: 0, pageSize: 10 };
};

const apiService = {
  auth: authAPI,
  categories: categoryAPI,
  products: productAPI,
  carts: cartAPI,
  orders: orderAPI,
  reviews: reviewAPI,
  users: userAPI,
  statistics: statisticsAPI,
  upload: uploadAPI,
};

export default apiService;
