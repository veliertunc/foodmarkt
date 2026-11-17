"use client";

import { useRef, useState, type FormEvent } from "react";
import { submitOrder } from "./order-action";
import type { OrderResult, Product } from "./store-api";

const price = new Intl.NumberFormat("tr-TR", { style: "currency", currency: "TRY" });
const fieldClass = "w-full rounded-md border border-amber-900/25 bg-white px-3 py-2";

export function Checkout({ products }: { products: Product[] }) {
  const [quantities, setQuantities] = useState<Record<number, number>>({});
  const [result, setResult] = useState<OrderResult>();
  const [pending, setPending] = useState(false);
  const submitting = useRef(false);
  const selected = products.filter((product) => product.available && (quantities[product.id] ?? 0) > 0);
  const estimate = selected.reduce(
    (sum, product) => sum + Math.round(product.price * 100) * (quantities[product.id] ?? 0),
    0,
  ) / 100;

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (submitting.current || selected.length === 0 || selected.length > 50) return;

    const form = event.currentTarget;
    const data = new FormData(form);
    submitting.current = true;
    setPending(true);
    setResult(undefined);
    try {
      const response = await submitOrder({
        customerName: String(data.get("customerName") ?? ""),
        email: String(data.get("email") ?? ""),
        phone: String(data.get("phone") ?? ""),
        deliveryAddress: String(data.get("deliveryAddress") ?? ""),
        items: selected.map(({ id }) => ({ productId: id, quantity: quantities[id] ?? 0 })),
      });

      setResult(response);
      if ("receipt" in response) {
        setQuantities({});
        form.reset();
      }
    } catch {
      setResult({ error: "We couldn't confirm your order. Contact the shop before submitting it again." });
    } finally {
      submitting.current = false;
      setPending(false);
    }
  }

  return (
    <form onSubmit={handleSubmit}>
      <fieldset disabled={pending} className="grid gap-8 md:grid-cols-[1fr_22rem]">
        <section aria-labelledby="products-heading">
          <h2 id="products-heading" className="mb-4 text-2xl font-semibold">Products</h2>
          <ul className="grid gap-3">
            {products.map((product) => (
              <li key={product.id} className="grid grid-cols-[1fr_auto] gap-4 rounded-lg border border-amber-900/15 bg-white p-4">
                <div>
                  <p className="text-sm text-stone-600">{product.category.name}</p>
                  <h3 className="font-semibold">{product.name}</h3>
                  {product.description && <p className="text-sm">{product.description}</p>}
                </div>
                <div className="text-right">
                  <p className="font-medium">{price.format(product.price)}</p>
                  {product.available ? (
                    <label className="mt-1 block text-sm">
                      Quantity
                      <input
                        aria-label={`Quantity for ${product.name}`}
                        className="ml-2 w-16 rounded border border-amber-900/25 px-2 py-1"
                        type="number"
                        min={0}
                        max={99}
                        value={quantities[product.id] ?? 0}
                        onChange={(event) => {
                          const quantity = event.target.valueAsNumber;
                          setQuantities((previous) => ({
                            ...previous,
                            [product.id]: Number.isInteger(quantity) ? quantity : 0,
                          }));
                        }}
                      />
                    </label>
                  ) : <p className="text-sm text-stone-600">Unavailable</p>}
                </div>
              </li>
            ))}
          </ul>
        </section>
        <section aria-labelledby="checkout-heading" className="space-y-4 rounded-lg border border-amber-900/15 bg-white p-5 self-start">
          <h2 id="checkout-heading" className="text-2xl font-semibold">Checkout</h2>
          <label className="block">Name<input className={fieldClass} name="customerName" autoComplete="name" required maxLength={120} /></label>
          <label className="block">Email<input className={fieldClass} name="email" type="email" autoComplete="email" required maxLength={254} /></label>
          <label className="block">Phone<input className={fieldClass} name="phone" type="tel" autoComplete="tel" required minLength={7} maxLength={32} /></label>
          <label className="block">Delivery address<textarea className={fieldClass} name="deliveryAddress" autoComplete="street-address" required maxLength={500} /></label>
          <p className="font-medium">Estimated total {price.format(estimate)}</p>
          {selected.length > 50 && <p role="alert">Choose up to 50 different products per order.</p>}
          <button className="w-full rounded-md bg-amber-900 px-4 py-2 font-semibold text-white disabled:opacity-50" disabled={selected.length === 0 || selected.length > 50}>
            {pending ? "Submitting…" : "Place order"}
          </button>
          {result && ("error" in result ? (
            <p role="alert" className="text-red-800">{result.error}</p>
          ) : (
            <div role="status" className="rounded-md bg-green-50 p-3 text-green-900">
              <p className="font-semibold">Order received · {price.format(result.receipt.total)}</p>
              <p className="break-all text-sm">Reference {result.receipt.id}</p>
            </div>
          ))}
        </section>
      </fieldset>
    </form>
  );
}
