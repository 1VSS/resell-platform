import { AuthResponse, LoginRequest, RegisterRequest } from '@/types/auth';
import { Item, ItemRequest, ItemResponse, PaginatedItemsResponse } from '@/types/item';

const API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

// Get token from local storage
const getToken = () => {
  if (typeof window !== 'undefined') {
    return localStorage.getItem('token');
  }
  return null;
};

// API service with methods for all endpoints
export const api = {
  // Auth endpoints
  auth: {
    register: async (data: RegisterRequest): Promise<AuthResponse> => {
      const response = await fetch(`${API_URL}/register`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(data),
      });
      
      if (!response.ok) {
        throw new Error('Registration failed');
      }
      
      return await response.json();
    },
    
    login: async (data: LoginRequest): Promise<AuthResponse> => {
      const response = await fetch(`${API_URL}/authenticate`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(data),
      });
      
      if (!response.ok) {
        throw new Error('Login failed');
      }
      
      return await response.json();
    },
  },
  
  // Item endpoints
  items: {
    getFeed: async (page = 0, pageSize = 10): Promise<PaginatedItemsResponse> => {
      const response = await fetch(`${API_URL}/feed?page=${page}&pageSize=${pageSize}`);
      
      if (!response.ok) {
        throw new Error('Failed to fetch items');
      }
      
      return await response.json();
    },
    
    searchItems: async (query: string, page = 0, pageSize = 10): Promise<PaginatedItemsResponse> => {
      const response = await fetch(`${API_URL}/items/search?query=${encodeURIComponent(query)}&page=${page}&pageSize=${pageSize}`);
      
      if (!response.ok) {
        throw new Error('Search failed');
      }
      
      return await response.json();
    },
    
    createItem: async (data: ItemRequest): Promise<Item> => {
      const token = getToken();
      
      if (!token) {
        throw new Error('Authentication required');
      }
      
      const response = await fetch(`${API_URL}/items`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
        body: JSON.stringify(data),
      });
      
      if (!response.ok) {
        throw new Error('Failed to create item');
      }
      
      return await response.json();
    },
    
    updateItem: async (id: number, data: ItemRequest): Promise<Item> => {
      const token = getToken();
      
      if (!token) {
        throw new Error('Authentication required');
      }
      
      const response = await fetch(`${API_URL}/items/${id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
        body: JSON.stringify(data),
      });
      
      if (!response.ok) {
        throw new Error('Failed to update item');
      }
      
      return await response.json();
    },
    
    deleteItem: async (id: number): Promise<void> => {
      const token = getToken();
      
      if (!token) {
        throw new Error('Authentication required');
      }
      
      const response = await fetch(`${API_URL}/items/${id}`, {
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${token}`,
        },
      });
      
      if (!response.ok) {
        throw new Error('Failed to delete item');
      }
    },
  },
  
  // Transaction endpoints
  transactions: {
    purchaseItem: async (itemId: number): Promise<void> => {
      const token = getToken();
      
      if (!token) {
        throw new Error('Authentication required');
      }
      
      const response = await fetch(`${API_URL}/transactions/purchase/${itemId}`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
        },
      });
      
      if (!response.ok) {
        throw new Error('Purchase failed');
      }
    },
  },
}; 