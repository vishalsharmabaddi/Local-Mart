import { useCart } from '../cart/CartContext';
import QuantityStepper from './QuantityStepper';

// A component = a function that returns UI. "product" comes in as a PROP (input).
export default function ProductCard({ product }) {
  const { quantityOf, addItem } = useCart();
  const inStock = product.stock > 0;
  const inCart = quantityOf(product.id) > 0;

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

      <div className="mt-4">
        {inCart ? (
          <QuantityStepper product={product} />
        ) : (
          <button
            onClick={() => addItem(product)}
            disabled={!inStock}
            className="w-full rounded-lg bg-green-700 py-2 text-sm font-semibold text-white hover:bg-green-800 disabled:bg-gray-300"
          >
            Add to cart
          </button>
        )}
      </div>
    </div>
  );
}
