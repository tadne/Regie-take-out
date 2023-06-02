function loginApi(data) {
  return $axios({
    'url': '/employee/login',
    'method': 'post',
    data
  })
}
// 1. 请求拦截
axios.interceptors.request.use(
    config => {
      console.log(111)
      console.log(localStorage.getItem('token'))
      config.defaults.headers['Authorization'] = localStorage.getItem('token')
      return config
    }
);

function logoutApi(){
  return $axios({
    'url': '/employee/logout',
    'method': 'post',
  })
}
