import type { Metadata } from "next";
import Link from "next/link";

export const metadata: Metadata = { title: "Ordering & delivery" };

export default function DeliveryPage() {
  return (
    <article className="site-width info-page">
      <h1 className="page-title">Ordering & delivery</h1>
      <p className="intro">Choose your groceries and tell us where the order should go.</p>
      <ol className="steps">
        <li><h2>Choose your products</h2><p>Set a quantity for each available item you’d like to order. Review your selections and estimated total in checkout.</p></li>
        <li><h2>Enter your delivery details</h2><p>Provide your name, email, phone number, and full delivery address, including any apartment or unit number.</p></li>
        <li><h2>Save your order reference</h2><p>After a successful submission, the checkout shows your order reference and confirmed total. Keep the reference for any questions about the order.</p></li>
      </ol>
      <Link href="/shop" className="button">Start shopping <span aria-hidden="true">↗</span></Link>
    </article>
  );
}
