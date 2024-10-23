import request from '@/utils/request'

// 分页列表
export function listProductUnit(productUnit) {
  return request({
    url: '/product/productUnit/list', //接口路径
    method: 'get',//请求方式
    // 参数部分如何写，根据后端接口
    // 后端接口接收参数，使用@RequestBody     写法 data:参数 以json格式传递
    // 后端接口接收参数，没有使用@RequestBody 写法 params:参数 
    params:productUnit
  })
}

// 添加
export function addProductUnit(productUnit) {
    return request({
      url: '/product/productUnit/add', //接口路径
      method: 'post',//请求方式
      // 参数部分如何写，根据后端接口
      // 后端接口接收参数，使用@RequestBody     写法 data:参数 以json格式传递
      // 后端接口接收参数，没有使用@RequestBody 写法 params:参数 
      data:productUnit
    })
}

// 查询全部商品单位
export function getUnitAll() {
  return request({
    url: '/product/productUnit/getUnitAll',
    method: 'get'
  })
}