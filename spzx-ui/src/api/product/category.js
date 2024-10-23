import request from '@/utils/request'

// 商品分类列表
export function listCategory(id) {
  return request({
    url: '/product/category/list/'+id, //接口路径
    method: 'get',//请求方式
  })
}