import type { Metadata } from "next";
import { Checkout } from "../checkout";
import { getProducts } from "../store-api";

export const metadata: Metadata = { title: "Shop" };

export default async function ShopPage({
  searchParams,
}: {
  searchParams: Promise<{ category?: string | string[] }>;
}) {
  const [{ category }, products] = await Promise.all([
    searchParams,
    getProducts().catch(() => null),
  ]);

  return (
    <div className="site-width shop-page">
      <h1 className="page-title">Shop the market</h1>
      {products === null ? (
        <p id="checkout" role="alert">The catalog is temporarily unavailable. Please try again later.</p>
      ) : products.length === 0 ? (
        <p id="checkout">There are no products in the catalog yet.</p>
      ) : (
        <Checkout key={typeof category === "string" ? category : ""} products={products} initialCategory={typeof category === "string" ? category : undefined} />
      )}
    </div>
  );
}
