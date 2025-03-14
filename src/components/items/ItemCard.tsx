'use client';

import React from 'react';
import { Card, CardContent, CardFooter } from '../ui/Card';
import { Button } from '../ui/Button';
import { Item } from '@/types/item';
import { useAuth } from '@/contexts/AuthContext';
import { api } from '@/services/api';
import { useRouter } from 'next/navigation';

interface ItemCardProps {
  item: Item;
  onDelete?: () => void;
}

export const ItemCard: React.FC<ItemCardProps> = ({ item, onDelete }) => {
  const { user, isAuthenticated } = useAuth();
  const router = useRouter();
  const [isLoading, setIsLoading] = React.useState(false);
  
  const isOwner = user?.username === item.username;
  
  const formatDate = (dateStr: string) => {
    const date = new Date(dateStr);
    return date.toLocaleDateString();
  };
  
  const handleEdit = () => {
    router.push(`/items/edit/${item.id}`);
  };
  
  const handleDelete = async () => {
    if (!confirm('Are you sure you want to delete this item?')) {
      return;
    }
    
    setIsLoading(true);
    try {
      await api.items.deleteItem(item.id);
      onDelete?.();
    } catch (error) {
      console.error('Error deleting item:', error);
      alert('Failed to delete item');
    } finally {
      setIsLoading(false);
    }
  };
  
  const handlePurchase = async () => {
    if (!isAuthenticated) {
      router.push('/login');
      return;
    }
    
    if (!confirm('Are you sure you want to purchase this item?')) {
      return;
    }
    
    setIsLoading(true);
    try {
      await api.transactions.purchaseItem(item.id);
      alert('Item purchased successfully!');
      router.refresh();
    } catch (error) {
      console.error('Error purchasing item:', error);
      alert('Failed to purchase item');
    } finally {
      setIsLoading(false);
    }
  };
  
  return (
    <Card>
      <CardContent>
        <div className="flex justify-between">
          <h3 className="text-lg font-semibold">{item.name}</h3>
          <span className="font-bold text-lg">${item.price.toFixed(2)}</span>
        </div>
        
        <div className="mt-2 text-sm text-gray-500">
          <p>Brand: {item.brand}</p>
          <p>Condition: {item.condition}</p>
          <p>Size: {item.size}</p>
          <p>Seller: {item.username}</p>
          <p>Listed on: {formatDate(item.listedAt)}</p>
        </div>
        
        <div className="mt-3">
          <span className={`px-2 py-1 rounded-full text-xs font-semibold
            ${item.status === 'AVAILABLE' ? 'bg-green-100 text-green-800' : 
              item.status === 'SOLD' ? 'bg-red-100 text-red-800' : 
              'bg-yellow-100 text-yellow-800'}`}
          >
            {item.status}
          </span>
        </div>
      </CardContent>
      
      <CardFooter className="flex justify-between">
        {isOwner ? (
          <div className="flex space-x-2">
            <Button
              variant="secondary"
              size="sm"
              onClick={handleEdit}
              disabled={item.status !== 'AVAILABLE' || isLoading}
            >
              Edit
            </Button>
            <Button
              variant="danger"
              size="sm"
              onClick={handleDelete}
              isLoading={isLoading}
              disabled={item.status !== 'AVAILABLE'}
            >
              Delete
            </Button>
          </div>
        ) : (
          <Button
            variant="primary"
            onClick={handlePurchase}
            isLoading={isLoading}
            disabled={item.status !== 'AVAILABLE' || isOwner}
          >
            Purchase
          </Button>
        )}
      </CardFooter>
    </Card>
  );
}; 