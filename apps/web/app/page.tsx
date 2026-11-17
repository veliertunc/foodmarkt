import Image from "next/image";
import Link from "next/link";
import { shopCategories } from "./shop-categories";

export default function Page() {
  return (
    <>
      <section className="home-hero site-width" aria-labelledby="hero-title">
        <div className="hero-copy">
          <p className="eyebrow">The online food market</p>
          <h1 id="hero-title">Good food for<br className="hero-break" /> <em>every day.</em></h1>
          <p>Golden simit, rich chocolate, and pantry staples worth keeping close. Find something for the table, whatever the occasion.</p>
          <Link href="/shop" className="button">Shop the market <span aria-hidden="true">↗</span></Link>
        </div>
        <div className="hero-image">
          <Image src="/market-table.webp" alt="Sesame simit, apricots, olives, and chocolate on a sunlit table" fill preload sizes="(max-width: 760px) 100vw, 57vw" />
        </div>
      </section>
      <section className="site-width category-section" aria-labelledby="category-title">
        <div className="section-heading">
          <h2 id="category-title">Shop by category</h2>
          <Link href="/shop">See all products <span aria-hidden="true">↗</span></Link>
        </div>
        <ul className="category-grid">
          {shopCategories.map(({ slug, name, description }) => (
            <li key={slug}>
              <Link href={`/shop?category=${slug}`}>
                <h3>{name}</h3>
                <p>{description}</p>
                <span aria-hidden="true">↗</span>
              </Link>
            </li>
          ))}
        </ul>
      </section>
    </>
  );
}
