import { createContext, useContext } from 'react';

// A "global box" for login info. AuthProvider fills it, any component reads it with useAuth().
export const AuthContext = createContext(null);

export function useAuth() {
  const auth = useContext(AuthContext);
  if (!auth) {
    throw new Error('useAuth() must be used inside <AuthProvider>');
  }
  return auth;   // { user, token, login(), logout() }
}
