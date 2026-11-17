import { useState } from 'react';
import { AuthContext } from './AuthContext';

const STORAGE_KEY = 'localmart.auth';

// ⚠️ SECURITY TRADE-OFF: localStorage survives refresh (good), but ANY JavaScript running on our
// page can read it. If an attacker ever injects a script (XSS), they can steal the token.
// Safer industry option: an httpOnly cookie (JS can't read it at all). We keep localStorage
// for now because it's simple, and rely on React escaping all text (blocks most XSS).
function loadSavedAuth() {
  try {
    const saved = JSON.parse(localStorage.getItem(STORAGE_KEY));
    if (saved && saved.expiresAt > Date.now()) {
      return saved;   // still valid → stay logged in after refresh
    }
  } catch {
    // corrupted value → treat as logged out
  }
  localStorage.removeItem(STORAGE_KEY);   // expired or broken → clean up
  return null;
}

export default function AuthProvider({ children }) {
  // passing the FUNCTION (not its result) → React runs it only once, on first render
  const [auth, setAuth] = useState(loadSavedAuth);

  function login(loginResponse) {
    const value = {
      token: loginResponse.token,
      user: loginResponse.user,                                           // { id, name, phone, role }
      expiresAt: Date.now() + loginResponse.expiresInSeconds * 1000,    // same expiry as the JWT
    };
    localStorage.setItem(STORAGE_KEY, JSON.stringify(value));
    setAuth(value);
  }

  function logout() {
    localStorage.removeItem(STORAGE_KEY);
    setAuth(null);
  }

  const value = { user: auth?.user ?? null, token: auth?.token ?? null, login, logout };
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
