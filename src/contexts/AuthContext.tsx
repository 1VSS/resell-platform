'use client';

import React, { createContext, useContext, useEffect, useState } from 'react';
import { AuthResponse, LoginRequest, RegisterRequest, User } from '@/types/auth';
import { api } from '@/services/api';
import { useRouter } from 'next/navigation';

interface AuthContextType {
  user: User | null;
  loading: boolean;
  login: (data: LoginRequest) => Promise<void>;
  register: (data: RegisterRequest) => Promise<void>;
  logout: () => void;
  isAuthenticated: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children }: { children: React.ReactNode }) => {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);
  const router = useRouter();

  useEffect(() => {
    // Check if user is logged in on mount
    const checkAuth = () => {
      try {
        const token = localStorage.getItem('token');
        const username = localStorage.getItem('username');
        
        if (token && username) {
          setUser({
            id: 0, // We don't store the ID in local storage
            username,
            email: '', // We don't store the email in local storage
          });
        }
      } catch (error) {
        // Handle error
        console.error('Error checking authentication:', error);
      } finally {
        setLoading(false);
      }
    };
    
    checkAuth();
  }, []);

  const handleAuthResponse = (response: AuthResponse) => {
    localStorage.setItem('token', response.token);
    localStorage.setItem('username', response.username);
    
    setUser({
      id: 0, // We don't have the ID in the response
      username: response.username,
      email: '', // We don't have the email in the response
    });
  };

  const login = async (data: LoginRequest) => {
    setLoading(true);
    try {
      const response = await api.auth.login(data);
      handleAuthResponse(response);
      router.push('/');
    } catch (error) {
      console.error('Login error:', error);
      throw error;
    } finally {
      setLoading(false);
    }
  };

  const register = async (data: RegisterRequest) => {
    setLoading(true);
    try {
      const response = await api.auth.register(data);
      handleAuthResponse(response);
      router.push('/');
    } catch (error) {
      console.error('Registration error:', error);
      throw error;
    } finally {
      setLoading(false);
    }
  };

  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    setUser(null);
    router.push('/login');
  };

  return (
    <AuthContext.Provider value={{
      user,
      loading,
      login,
      register,
      logout,
      isAuthenticated: !!user,
    }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}; 