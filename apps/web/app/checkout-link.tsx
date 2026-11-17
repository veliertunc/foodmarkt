"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";

export function CheckoutLink() {
  const pathname = usePathname();
  const label = <>Checkout <span aria-hidden="true">↗</span></>;
  const className = "button button-small";

  return pathname === "/shop" ? (
    <a href="#checkout" className={className}>{label}</a>
  ) : (
    <Link href="/shop#checkout" className={className}>{label}</Link>
  );
}
