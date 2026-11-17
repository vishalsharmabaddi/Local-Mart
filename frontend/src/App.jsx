import ProductsPage from './pages/ProductsPage';

export default function App() {
  return (
    <div className="min-h-screen bg-gray-50">
      <header className="bg-green-700 px-6 py-4 text-white">
        <span className="text-xl font-bold">🛒 LocalMart</span>
      </header>

      <ProductsPage />
    </div>
  );
}
