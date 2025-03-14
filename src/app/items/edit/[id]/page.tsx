'use client';

import React, { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { useAuth } from '@/contexts/AuthContext';
import { Card, CardContent, CardHeader } from '@/components/ui/Card';
import { ItemForm } from '@/components/items/ItemForm';
import { Item, ItemRequest } from '@/types/item';
import { api } from '@/services/api';
import { Button } from '@/components/ui/Button';

interface EditItemPageProps {
  params: {
    id: string;
  };
}

export default function EditItemPage({ params }: EditItemPageProps) {
  const itemId = parseInt(params.id, 10);
  const router = useRouter();
  const { user, isAuthenticated } = useAuth();
  const [item, setItem] = useState<Item | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isSaving, setIsSaving] = useState(false);
  const [error, setError] = useState('');
  
  // Redirect to login if not authenticated
  useEffect(() => {
    if (!isAuthenticated) {
      router.push('/login');
    }
  }, [isAuthenticated, router]);
  
  // Fetch item data
  useEffect(() => {
    const fetchItem = async () => {
      try {
        // This is a mock implementation since we don't have a direct endpoint to get a single item
        // In a real application, we would have a getItem(id) API call
        const response = await api.items.getFeed(0, 100);
        const foundItem = response.items.find(i => i.id === itemId);
        
        if (!foundItem) {
          setError('Item not found');
          return;
        }
        
        // Check if current user is the owner
        if (foundItem.username !== user?.username) {
          setError('You are not authorized to edit this item');
          return;
        }
        
        setItem(foundItem);
      } catch (error) {
        console.error('Error fetching item:', error);
        setError('Failed to load item data');
      } finally {
        setIsLoading(false);
      }
    };
    
    if (itemId && isAuthenticated) {
      fetchItem();
    }
  }, [itemId, isAuthenticated, user?.username]);
  
  const handleSubmit = async (data: ItemRequest) => {
    setIsSaving(true);
    setError('');
    
    try {
      await api.items.updateItem(itemId, data);
      router.push('/');
    } catch (error) {
      console.error('Error updating item:', error);
      setError('Failed to update item. Please try again.');
    } finally {
      setIsSaving(false);
    }
  };
  
  // Convert the Item to ItemRequest to use in the form
  const itemRequest: ItemRequest | undefined = item
    ? {
        name: item.name,
        brand: item.brand,
        condition: item.condition,
        price: item.price,
        size: item.size,
      }
    : undefined;
  
  if (isLoading) {
    return (
      <div className="flex justify-center py-12">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"></div>
      </div>
    );
  }
  
  if (error) {
    return (
      <div className="max-w-2xl mx-auto">
        <Card>
          <CardContent className="py-10">
            <div className="text-center">
              <div className="text-red-600 mb-4">{error}</div>
              <Button onClick={() => router.push('/')}>Back to Home</Button>
            </div>
          </CardContent>
        </Card>
      </div>
    );
  }
  
  return (
    <div className="max-w-2xl mx-auto">
      <Card>
        <CardHeader>
          <h1 className="text-2xl font-bold">Edit Item</h1>
        </CardHeader>
        <CardContent>
          {itemRequest && (
            <ItemForm
              initialData={itemRequest}
              onSubmit={handleSubmit}
              submitButtonText="Update Listing"
              isLoading={isSaving}
            />
          )}
        </CardContent>
      </Card>
    </div>
  );
} 