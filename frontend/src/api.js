// The ONLY file that knows where the backend lives and how to call it.
const API_URL = import.meta.env.VITE_API_URL;

// One helper for every call: JSON headers, optional token, readable errors.
async function request(path, { method = 'GET', body, token } = {}) {
  const headers = {};
  if (body) headers['Content-Type'] = 'application/json';
  if (token) headers['Authorization'] = `Bearer ${token}`;

  const response = await fetch(`${API_URL}${path}`, {
    method,
    headers,
    body: body ? JSON.stringify(body) : undefined,
  });

  if (!response.ok) {   // 4xx / 5xx: fetch does NOT throw by itself, we must check
    // our backend sends { status, message, errors? } → show the real reason to the user
    const error = await response.json().catch(() => null);
    const fieldErrors = error?.errors ? Object.values(error.errors).join(', ') : null;
    throw new Error(fieldErrors ?? error?.message ?? `Request failed (HTTP ${response.status})`);
  }
  return response.status === 204 ? null : response.json();
}

export function getProducts(page = 0) {
  return request(`/products?page=${page}&size=20`);
}

export function login(phone, password) {
  return request('/auth/login', { method: 'POST', body: { phone, password } });
}
