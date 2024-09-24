import axios from "axios";

const isDev = process.env.NODE_ENV === "development";

const myAxios = axios.create({
  baseURL: isDev ?  'http://localhost:8080/api/' : '线上接口',
  timeout: 1000,
});

myAxios.defaults.withCredentials = true;   // 设置 withCredentials 属性，允许跨域请求带 cookie

// Add a request interceptor
myAxios.interceptors.request.use(function (config) {
  // Do something before request is sent
  console.log("准备发送请求", config);
  // config.withCredentials = true; // 设置 withCredentials 属性，允许跨域请求带 cookie
  return config;
}, function (error) {
  // Do something with request error
  return Promise.reject(error);
});

// Add a response interceptor
myAxios.interceptors.response.use(function (response) {
  // Any status code that lie within the range of 2xx cause this function to trigger
  // Do something with response data
  console.log("收到响应", response);
  // 未登录跳转到登录页
  if (response?.data?.code === 40100) {
    window.location.href = '/user/login';
  }
  return response.data;
}, function (error) {
  // Any status codes that falls outside the range of 2xx cause this function to trigger
  // Do something with response error
  return Promise.reject(error);
});

export default myAxios;
