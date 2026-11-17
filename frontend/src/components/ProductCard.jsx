// A component = a function that returns UI. "product" comes in as a PROP (input).
export default function ProductCard({ product }) {
  const inStock = product.stock > 0;

  return (
    <div className="rounded-xl border border-gray-200 bg-white p-4 shadow-sm">
      <h2 className="font-semibold text-gray-900">{product.name}</h2>
      <p className="text-sm text-gray-500">{product.shopName}</p>

      <div className="mt-3 flex items-center justify-between">
        <span className="text-lg font-bold">₹{product.price}</span>
        <span className={inStock ? 'text-sm text-green-700' : 'text-sm text-red-600'}>
          {inStock ? `${product.stock} in stock` : 'Out of stock'}
        </span>
      </div>
    </div>
  );
}
