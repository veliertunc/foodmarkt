import "@repo/ui/styles.css";
import "./globals.css";
import type { Metadata } from "next";
import { GeistSans } from "geist/font/sans";
import Link from "next/link";
import { CheckoutLink } from "./checkout-link";

export const metadata: Metadata = {
  title: { default: "Foodmarkt", template: "%s | Foodmarkt" },
  description: "Shop bakery, chocolate, breakfast, pantry essentials, and drinks.",
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="en">
      <body className={GeistSans.className}>
        <a href="#main-content" className="skip-link">Skip to content</a>
        <header className="site-header">
          <div className="site-width header-inner">
            <Link href="/" className="wordmark">foodmarkt<span>.</span></Link>
            <nav aria-label="Main navigation">
              <Link href="/shop">Shop</Link>
              <Link href="/about">About</Link>
              <Link href="/help">Help</Link>
            </nav>
            <CheckoutLink />
          </div>
        </header>
        <main id="main-content" tabIndex={-1}>{children}</main>
        <footer className="site-footer">
          <div className="site-width footer-inner">
            <Link href="/" className="wordmark">foodmarkt<span>.</span></Link>
            <nav aria-label="Footer navigation">
              <Link href="/shop">Shop</Link>
              <Link href="/about">About</Link>
              <Link href="/delivery">Ordering & delivery</Link>
              <Link href="/help">Help</Link>
            </nav>
          </div>
        </footer>
      </body>
    </html>
  );
}
