import { useEffect, useState } from 'react';
import { getProducts } from '../api';
import ProductCard from '../components/ProductCard';

export default function ProductsPage() {
  // STATE = data that can change. When it changes, React re-draws the page.
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // EFFECT = "do this after the page appears". [] = only once, not on every re-draw.
  useEffect(() => {
    getProducts()
      .then((data) => setProducts(data.content))
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <p className="p-6 text-gray-500">Loading products...</p>;
  if (error) return <p className="p-6 text-red-600">⚠️ {error}</p>;

  return (
    <main className="mx-auto max-w-5xl p-6">
      <h1 className="mb-6 text-2xl font-bold">Products</h1>

      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
        {products.map((product) => (
          <ProductCard key={product.id} product={product} />
        ))}
      </div>
    </main>
  );
}
