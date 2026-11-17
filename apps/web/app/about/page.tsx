import type { Metadata } from "next";
import Link from "next/link";

export const metadata: Metadata = { title: "About" };

export default function AboutPage() {
  return (
    <article className="site-width info-page">
      <p className="eyebrow">About Foodmarkt</p>
      <h1 className="page-title">Good things for the table.</h1>
      <p className="intro">Foodmarkt is an online food market for the meals and small moments that make up the day.</p>
      <div className="info-body">
        <h2>From breakfast to the pantry</h2>
        <p>Browse bakery favorites such as simit and poğaça, pick up chocolate for a sweet break, or stock up on staples like olive oil and lentils.</p>
        <p>Product descriptions, prices, and availability are listed in the shop. You can put together an order and check out as a guest.</p>
        <Link href="/shop" className="button">Explore the market <span aria-hidden="true">↗</span></Link>
      </div>
    </article>
  );
}
