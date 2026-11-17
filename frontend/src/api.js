// The ONLY file that knows where the backend lives and how to call it.
const API_URL = import.meta.env.VITE_API_URL;

export async function getProducts(page = 0) {
  const response = await fetch(`${API_URL}/products?page=${page}&size=20`);

  if (!response.ok) {   // 4xx / 5xx: fetch does NOT throw by itself, we must check
    throw new Error(`Could not load products (HTTP ${response.status})`);
  }
  return response.json();   // { content: [...], page, totalPages, ... }
}
