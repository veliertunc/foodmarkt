import assert from "node:assert/strict";
import test from "node:test";
import { getProducts, placeOrder } from "../app/store-api.ts";

test("reads all catalog pages in order", async (t) => {
  const fetch = t.mock.method(globalThis, "fetch", async (url) => {
    const page = Number(url.searchParams.get("page"));
    return Response.json({ items: [{ id: page + 1 }], totalPages: 2 });
  });
  assert.deepEqual(await getProducts(), [{ id: 1 }, { id: 2 }]);
  assert.equal(fetch.mock.callCount(), 2);
});

const order = {
  customerName: "Test Customer",
  email: "test@example.com",
  phone: "+905551112233",
  deliveryAddress: "Test address",
  items: [{ productId: 4, quantity: 2 }],
};

test("posts a guest order and exposes only receipt fields", async (t) => {
  const receipt = { id: "order-id", total: 52.5, currency: "TRY" };
  t.mock.method(globalThis, "fetch", async (url, request) => {
    assert.equal(url.pathname, "/api/orders");
    assert.equal(request.method, "POST");
    assert.deepEqual(JSON.parse(request.body), order);
    return Response.json({ ...receipt, email: order.email }, { status: 201 });
  });
  assert.deepEqual(await placeOrder(order), { receipt });
});

test("handles unavailable items and ambiguous network failures", async (t) => {
  const fetch = t.mock.method(globalThis, "fetch", async () => new Response(null, { status: 409 }));
  assert.match((await placeOrder(order)).error, /no longer available/);
  fetch.mock.mockImplementation(async () => { throw new Error("Network error"); });
  assert.match((await placeOrder(order)).error, /before submitting it again/);
  assert.equal(fetch.mock.callCount(), 2);
});
