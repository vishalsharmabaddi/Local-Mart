import { useCart } from '../cart/CartContext';

// −  2  +   buttons for one product. Reused on the products page AND the cart page.
export default function QuantityStepper({ product }) {
  const { quantityOf, setQuantity } = useCart();
  const quantity = quantityOf(product.id);
  const atLimit = quantity >= Math.min(product.stock, 50);

  const buttonClass = 'h-8 w-8 rounded-lg border border-gray-300 font-bold hover:bg-gray-100 disabled:opacity-40';

  return (
    <div className="flex items-center gap-2">
      <button onClick={() => setQuantity(product, quantity - 1)} className={buttonClass} aria-label="Remove one">−</button>
      <span className="w-6 text-center font-semibold">{quantity}</span>
      <button onClick={() => setQuantity(product, quantity + 1)} disabled={atLimit} className={buttonClass} aria-label="Add one">+</button>
    </div>
  );
}
