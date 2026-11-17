import { Checkout } from "./checkout";
import { getProducts } from "./store-api";

export default async function Page() {
  const products = await getProducts().catch(() => null);

  return (
    <main className="min-h-screen bg-amber-50 text-stone-900">
      <div className="mx-auto max-w-6xl px-5 py-12">
        <h1 className="mb-8 font-serif text-5xl font-semibold">Foodmarkt</h1>
        {products === null ? (
          <p role="alert">The catalog is temporarily unavailable. Please try again later.</p>
        ) : products.length === 0 ? (
          <p>There are no products in the catalog yet.</p>
        ) : (
          <Checkout products={products} />
        )}
      </div>
    </main>
  );
}
