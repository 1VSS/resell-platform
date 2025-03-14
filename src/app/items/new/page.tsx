'use client';

import React, { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { useAuth } from '@/contexts/AuthContext';
import { Card, CardContent, CardHeader } from '@/components/ui/Card';
import { ItemForm } from '@/components/items/ItemForm';
import { ItemRequest } from '@/types/item';
import { api } from '@/services/api';

export default function NewItemPage() {
  const router = useRouter();
  const { isAuthenticated } = useAuth();
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');
  
  // Redirect to login if not authenticated
  useEffect(() => {
    if (!isAuthenticated) {
      router.push('/login');
    }
  }, [isAuthenticated, router]);
  
  const handleSubmit = async (data: ItemRequest) => {
    setIsLoading(true);
    setError('');
    
    try {
      await api.items.createItem(data);
      router.push('/');
    } catch (error) {
      console.error('Error creating item:', error);
      setError('Failed to create item. Please try again.');
    } finally {
      setIsLoading(false);
    }
  };
  
  return (
    <div className="max-w-2xl mx-auto">
      <Card>
        <CardHeader>
          <h1 className="text-2xl font-bold">Sell an Item</h1>
        </CardHeader>
        <CardContent>
          {error && (
            <div className="mb-4 p-3 bg-red-50 text-red-600 rounded-md">
              {error}
            </div>
          )}
          
          <ItemForm
            onSubmit={handleSubmit}
            submitButtonText="Create Listing"
            isLoading={isLoading}
          />
        </CardContent>
      </Card>
    </div>
  );
} 