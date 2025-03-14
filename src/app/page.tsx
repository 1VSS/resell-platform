'use client';

import React, { useEffect, useState } from 'react';
import { api } from '@/services/api';
import { Item } from '@/types/item';
import { ItemCard } from '@/components/items/ItemCard';
import { Button } from '@/components/ui/Button';

export default function Home() {
  const [items, setItems] = useState<Item[]>([]);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [isLoading, setIsLoading] = useState(true);
  const pageSize = 12;
  
  const fetchItems = async (page: number) => {
    setIsLoading(true);
    
    try {
      const response = await api.items.getFeed(page, pageSize);
      setItems(response.items);
      setTotalPages(response.totalPages);
    } catch (error) {
      console.error('Error fetching items:', error);
    } finally {
      setIsLoading(false);
    }
  };
  
  useEffect(() => {
    fetchItems(currentPage);
  }, [currentPage]);
  
  const handleItemDeleted = () => {
    fetchItems(currentPage);
  };
  
  const handlePreviousPage = () => {
    if (currentPage > 0) {
      setCurrentPage(currentPage - 1);
    }
  };
  
  const handleNextPage = () => {
    if (currentPage < totalPages - 1) {
      setCurrentPage(currentPage + 1);
    }
  };
  
  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h1 className="text-2xl font-bold">Latest Items</h1>
      </div>
      
      {isLoading ? (
        <div className="flex justify-center py-12">
          <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"></div>
        </div>
      ) : (
        <>
          {items.length === 0 ? (
            <div className="text-center py-12">
              <p className="text-gray-500">No items available at the moment.</p>
            </div>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {items.map((item) => (
                <ItemCard
                  key={item.id}
                  item={item}
                  onDelete={handleItemDeleted}
                />
              ))}
            </div>
          )}
          
          {items.length > 0 && (
            <div className="flex justify-center space-x-4 pt-6">
              <Button
                variant="secondary"
                onClick={handlePreviousPage}
                disabled={currentPage === 0}
              >
                Previous
              </Button>
              <span className="flex items-center px-3 py-1 rounded-md bg-gray-100">
                Page {currentPage + 1} of {totalPages}
              </span>
              <Button
                variant="secondary"
                onClick={handleNextPage}
                disabled={currentPage === totalPages - 1}
              >
                Next
              </Button>
            </div>
          )}
        </>
      )}
    </div>
  );
} 