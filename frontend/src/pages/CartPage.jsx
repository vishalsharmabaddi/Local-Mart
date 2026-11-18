import { Link } from 'react-router-dom';
import { useCart } from '../cart/CartContext';
import QuantityStepper from '../components/QuantityStepper';

export default function CartPage() {
  const { shopName, lines, total, setQuantity, clear } = useCart();

  if (lines.length === 0) {
    return (
      <main className="mx-auto max-w-2xl p-6 text-center">
        <h1 className="mb-2 text-2xl font-bold">Your cart is empty</h1>
        <Link to="/" className="text-green-700 underline">Browse products</Link>
      </main>
    );
  }

  return (
    <main className="mx-auto max-w-2xl p-6">
      <h1 className="text-2xl font-bold">Your cart</h1>
      <p className="mb-6 text-sm text-gray-500">From {shopName}</p>

      <ul className="divide-y divide-gray-200 rounded-xl border border-gray-200 bg-white">
        {lines.map(({ product, quantity }) => (
          <li key={product.id} className="flex items-center justify-between gap-4 p-4">
            <div>
              <p className="font-semibold">{product.name}</p>
              <p className="text-sm text-gray-500">₹{product.price} each</p>
            </div>
            <QuantityStepper product={product} />
            <div className="w-20 text-right font-semibold">₹{product.price * quantity}</div>
            <button onClick={() => setQuantity(product, 0)} className="text-sm text-red-600 hover:underline">
              Remove
            </button>
          </li>
        ))}
      </ul>

      <div className="mt-6 flex items-center justify-between">
        <button onClick={clear} className="text-sm text-gray-500 hover:underline">Empty cart</button>
        <p className="text-xl font-bold">Total: ₹{total}</p>
      </div>

      {/* "Place order" comes in Step 4. The SERVER will recalculate the real total. */}
    </main>
  );
}
