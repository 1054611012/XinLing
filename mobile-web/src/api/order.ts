import request from './request'
import type { ApiResponse, PayOrder, CreateOrderParams } from '@/types/api'

export function getOrderList(page: number = 1, size: number = 20): Promise<ApiResponse<PayOrder[]>> {
  return request.get('/order/list', { params: { page, size } })
}

export function getOrderDetail(orderNo: string): Promise<ApiResponse<PayOrder>> {
  return request.get(`/order/detail/${orderNo}`)
}

export function createOrder(data: CreateOrderParams): Promise<ApiResponse<{ orderNo: string }>> {
  return request.post('/pay/createOrder', null, { params: { packageId: data.productId, couponId: data.couponId } })
}

export function cancelOrder(orderNo: string): Promise<ApiResponse<null>> {
  return request.post(`/order/cancel/${orderNo}`)
}

export function getPayUrl(orderNo: string, payType: string): Promise<ApiResponse<{ payUrl: string }>> {
  return request.post('/pay/getPayUrl', null, { params: { orderNo, payType } })
}

export function queryOrderStatus(orderNo: string): Promise<ApiResponse<any>> {
  return request.post(`/pay/queryOrderStatus/${orderNo}`)
}
