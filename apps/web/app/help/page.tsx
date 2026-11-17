import type { Metadata } from "next";
import Link from "next/link";

export const metadata: Metadata = { title: "Help" };

export default function HelpPage() {
  return (
    <article className="site-width info-page">
      <h1 className="page-title">How can we help?</h1>
      <p className="intro">Answers to common questions about shopping and placing an order.</p>
      <div className="faq">
        <details>
          <summary>Do I need an account to order?</summary>
          <p>You can check out as a guest. Have your contact details and delivery address ready.</p>
        </details>
        <details>
          <summary>What if a product is unavailable?</summary>
          <p>Unavailable products are marked in the shop. If a product becomes unavailable while you’re checking out, reload the catalog and update your order.</p>
        </details>
        <details>
          <summary>How do I know my order went through?</summary>
          <p>A successful order shows a reference and total in checkout. If checkout says it couldn’t confirm the order, check with the shop before submitting it again.</p>
        </details>
        <details>
          <summary>What currency are prices shown in?</summary>
          <p>Prices and order totals are shown in Turkish lira (TRY).</p>
        </details>
      </div>
      <Link href="/delivery" className="text-link">Read about ordering and delivery <span aria-hidden="true">↗</span></Link>
    </article>
  );
}
