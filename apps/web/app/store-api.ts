export type Product = {
  id: number;
  name: string;
  description: string;
  price: number;
  available: boolean;
  category: { slug: string; name: string };
};

export type OrderRequest = {
  customerName: string;
  email: string;
  phone: string;
  deliveryAddress: string;
  items: { productId: number; quantity: number }[];
};

export type OrderResult =
  | { receipt: { id: string; total: number; currency: string } }
  | { error: string };

function apiUrl(path: string) {
  const base = process.env.FOODMARKT_API_URL ?? "http://localhost:8080";
  return new URL(path, base);
}

export async function getProducts(): Promise<Product[]> {
  const products: Product[] = [];
  let page = 0;
  let totalPages: number;
  do {
    const response = await fetch(apiUrl(`/api/products?page=${page}&size=100`), {
      cache: "no-store",
      signal: AbortSignal.timeout(5_000),
    });
    if (!response.ok) throw new Error("Catalog request failed");
    const result: { items: Product[]; totalPages: number } = await response.json();
    products.push(...result.items);
    totalPages = result.totalPages;
    page += 1;
  } while (page < totalPages);

  return products;
}

export async function placeOrder(order: OrderRequest): Promise<OrderResult> {
  try {
    const response = await fetch(apiUrl("/api/orders"), {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(order),
      signal: AbortSignal.timeout(10_000),
    });

    if (response.status === 409) {
      return { error: "An item is no longer available. Reload the catalog and update your order." };
    }
    if (response.status === 400) {
      return { error: "Check your contact details and item quantities, then try again." };
    }
    if (response.status === 201) {
      const { id, total, currency } = await response.json();
      return { receipt: { id, total, currency } };
    }
  } catch {
    // A network failure can occur after the backend has accepted the order.
  }
  return { error: "We couldn't confirm your order. Contact the shop before submitting it again." };
}
