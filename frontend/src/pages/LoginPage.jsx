import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { login as loginApi } from '../api';
import { useAuth } from '../auth/AuthContext';

const inputClass = 'mt-1 w-full rounded-lg border border-gray-300 px-3 py-2 focus:border-green-700 focus:outline-none';

export default function LoginPage() {
  // CONTROLLED FORM: React state is the single source of truth for what's typed
  const [phone, setPhone] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  const auth = useAuth();
  const navigate = useNavigate();

  async function handleSubmit(event) {
    event.preventDefault();   // stop the browser's old-style full-page form submit

    if (!/^\d{10}$/.test(phone)) {   // quick hint for the user; the SERVER validates again anyway
      setError('Phone must be exactly 10 digits');
      return;
    }

    setError(null);
    setSubmitting(true);   // disables the button → no double login on double click
    try {
      const response = await loginApi(phone, password);
      auth.login(response);
      navigate('/');   // go to products
    } catch (err) {
      setError(err.message);   // e.g. "Invalid phone or password" from our backend
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <main className="mx-auto mt-12 max-w-sm p-6">
      <h1 className="mb-6 text-2xl font-bold">Login</h1>

      <form onSubmit={handleSubmit} className="space-y-4 rounded-xl border border-gray-200 bg-white p-6 shadow-sm">
        <label className="block">
          <span className="text-sm font-medium text-gray-700">Phone</span>
          <input
            type="tel"
            inputMode="numeric"
            maxLength={10}
            value={phone}
            onChange={(e) => setPhone(e.target.value)}
            placeholder="10-digit phone"
            className={inputClass}
            required
          />
        </label>

        <label className="block">
          <span className="text-sm font-medium text-gray-700">Password</span>
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className={inputClass}
            required
          />
        </label>

        {error && <p className="text-sm text-red-600">⚠️ {error}</p>}

        <button
          type="submit"
          disabled={submitting}
          className="w-full rounded-lg bg-green-700 py-2 font-semibold text-white hover:bg-green-800 disabled:opacity-60"
        >
          {submitting ? 'Logging in...' : 'Login'}
        </button>
      </form>

      {/* shown ONLY in "npm run dev"; removed from the production build */}
      {import.meta.env.DEV && (
        <p className="mt-4 text-xs text-gray-500">
          Dev accounts: Geeta 9111111111 · Ramesh 9876543210 (password: secret123)
        </p>
      )}
    </main>
  );
}
