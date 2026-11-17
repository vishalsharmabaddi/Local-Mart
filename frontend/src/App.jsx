import { Link, Route, Routes } from 'react-router-dom';
import { useAuth } from './auth/AuthContext';
import LoginPage from './pages/LoginPage';
import ProductsPage from './pages/ProductsPage';

export default function App() {
  const { user, logout } = useAuth();

  return (
    <div className="min-h-screen bg-gray-50">
      <header className="flex items-center justify-between bg-green-700 px-6 py-4 text-white">
        {/* <Link> changes the page WITHOUT reloading the whole site (single-page app) */}
        <Link to="/" className="text-xl font-bold">🛒 LocalMart</Link>

        {user ? (
          <div className="flex items-center gap-4">
            <span className="text-sm">{user.name} ({user.role})</span>
            <button onClick={logout} className="rounded-lg bg-white/20 px-3 py-1 text-sm hover:bg-white/30">
              Logout
            </button>
          </div>
        ) : (
          <Link to="/login" className="rounded-lg bg-white px-3 py-1 text-sm font-semibold text-green-800">
            Login
          </Link>
        )}
      </header>

      {/* URL → which page to show */}
      <Routes>
        <Route path="/" element={<ProductsPage />} />
        <Route path="/login" element={<LoginPage />} />
      </Routes>
    </div>
  );
}
