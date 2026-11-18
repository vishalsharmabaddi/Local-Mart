import { useEffect, useState } from 'react';
import { CartContext } from './CartContext';

const STORAGE_KEY = 'localmart.cart';
const MAX_PER_ITEM = 50;   // same limit as the backend (@Max(50) on quantity)

// The cart is a MAP: productId → { product, quantity }. One cart = one shop (one order = one shop).
const EMPTY_CART = { shopId: null, shopName: null, items: {} };

function loadCart() {
  try {
    return JSON.parse(localStorage.getItem(STORAGE_KEY)) ?? EMPTY_CART;
  } catch {
    return EMPTY_CART;   // corrupted → start fresh
  }
}

export default function CartProvider({ children }) {
  const [cart, setCart] = useState(loadCart);

  // save to localStorage whenever the cart changes → survives refresh
  useEffect(() => {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(cart));
  }, [cart]);

  function setQuantity(product, quantity) {
    setCart((current) => {
      const items = { ...current.items };   // a NEW copy: never change React state in place
      if (quantity <= 0) {
        delete items[product.id];
      } else {
        items[product.id] = { product, quantity: Math.min(quantity, product.stock, MAX_PER_ITEM) };
      }
      const isEmpty = Object.keys(items).length === 0;
      return {
        shopId: isEmpty ? null : product.shopId,
        shopName: isEmpty ? null : product.shopName,
        items,
      };
    });
  }

  function addItem(product) {
    // product from a DIFFERENT shop → ask before throwing the old cart away
    if (cart.shopId !== null && cart.shopId !== product.shopId) {
      const ok = window.confirm(
        `Your cart has items from ${cart.shopName}. One order can only be from one shop.\n\n` +
        `Empty the cart and add ${product.name}?`
      );
      if (!ok) return;
      setCart({
        shopId: product.shopId,
        shopName: product.shopName,
        items: { [product.id]: { product, quantity: 1 } },
      });
      return;
    }
    setQuantity(product, quantityOf(product.id) + 1);
  }

  function quantityOf(productId) {
    return cart.items[productId]?.quantity ?? 0;   // HashMap get, with 0 as default
  }

  function clear() {
    setCart(EMPTY_CART);
  }

  const lines = Object.values(cart.items);
  const itemCount = lines.reduce((sum, line) => sum + line.quantity, 0);
  const total = lines.reduce((sum, line) => sum + line.quantity * line.product.price, 0);   // display only!

  const value = {
    shopId: cart.shopId, shopName: cart.shopName, lines, itemCount, total,
    quantityOf, addItem, setQuantity, clear,
  };
  return <CartContext.Provider value={value}>{children}</CartContext.Provider>;
}
