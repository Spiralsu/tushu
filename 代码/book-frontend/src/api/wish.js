import request from '@/utils/request'

export function getWishList() { return request({ url: '/wish/list', method: 'get' }) }
export function addWish(data) { return request({ url: '/wish/add', method: 'post', data }) }
export function fulfillWish(wishId, fulfillUserId) { return request({ url: '/wish/fulfill', method: 'post', params: { wishId, fulfillUserId } }) }
export function deleteWish(wishId) { return request({ url: '/wish/delete', method: 'post', params: { wishId } }) }
