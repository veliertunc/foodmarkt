import Link from "next/link";

export default function NotFound() {
  return (
    <div className="site-width info-page">
      <h1 className="page-title">Page not found</h1>
      <p className="intro">Find your way back to the market.</p>
      <Link href="/" className="button">Back to home <span aria-hidden="true">↗</span></Link>
    </div>
  );
}
