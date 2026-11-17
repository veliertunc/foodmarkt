"use server";

import { placeOrder, type OrderRequest } from "./store-api";

export async function submitOrder(order: OrderRequest) {
  return placeOrder(order);
}
