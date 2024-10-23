import request from '@/utils/request'

// 查询商品规格列表
export function listSpec(query) {
  return request({
    url: '/product/productSpec/list',
    method: 'get',
    params: query
  })
}

// 新增商品规格
export function addSpec(data) {
    return request({
      url: '/product/productSpec',
      method: 'post',
      data: data
    })
}

// 根据分类获取分类规格
export function getCategorySpecAll(categoryId) {
  return request({
    url: '/product/productSpec/productSpecList/' + categoryId,
    method: 'get'
  })
}