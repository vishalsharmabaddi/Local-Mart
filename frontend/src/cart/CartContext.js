import { createContext, useContext } from 'react';

// Same pattern as AuthContext: CartProvider fills it, any component reads it with useCart().
export const CartContext = createContext(null);

export function useCart() {
  const cart = useContext(CartContext);
  if (!cart) {
    throw new Error('useCart() must be used inside <CartProvider>');
  }
  return cart;
}
