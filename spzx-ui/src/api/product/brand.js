import request from '@/utils/request'

// 获取全部品牌
export function getBrandAll() {
  return request({
    url: '/product/brand/getBrandAll',
    method: 'get'
  })
}

// 分页列表
export function listBrand(brand) {
  return request({
    url: '/product/brand/list', //接口路径
    method: 'get',//请求方式
    // 参数部分如何写，根据后端接口
    // 后端接口接收参数，使用@RequestBody     写法 data:参数 以json格式传递
    // 后端接口接收参数，没有使用@RequestBody 写法 params:参数 
    params:brand
  })
}

// 添加
export function addBrand(brand) {
    return request({
      url: '/product/brand/add', //接口路径
      method: 'post',//请求方式
      // 参数部分如何写，根据后端接口
      // 后端接口接收参数，使用@RequestBody     写法 data:参数 以json格式传递
      // 后端接口接收参数，没有使用@RequestBody 写法 params:参数 
      data:brand
    })
}

//修改-根据id查询
export function getBrand(id) {
  return request({ 
    url: '/product/brand/getBrand/'+id, //接口路径
    method: 'get'
  })
}

// 修改
export function updateBrand(brand) {
  return request({
    url: '/product/brand/update', //接口路径
    method: 'put',//请求方式
    // 参数部分如何写，根据后端接口
    // 后端接口接收参数，使用@RequestBody     写法 data:参数 以json格式传递
    // 后端接口接收参数，没有使用@RequestBody 写法 params:参数 
    data:brand
  })
}

// 删除
export function delBrand(id) {
  return request({
    url: '/product/brand/remove/'+id, //接口路径
    method: 'delete'
  })
}