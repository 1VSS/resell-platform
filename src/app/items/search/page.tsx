'use client';

import React, { useState } from 'react';
import { api } from '@/services/api';
import { Item } from '@/types/item';
import { ItemCard } from '@/components/items/ItemCard';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';

export default function SearchPage() {
  const [searchQuery, setSearchQuery] = useState('');
  const [items, setItems] = useState<Item[]>([]);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [isLoading, setIsLoading] = useState(false);
  const [hasSearched, setHasSearched] = useState(false);
  const pageSize = 12;
  
  const handleSearch = async (page = 0) => {
    if (!searchQuery.trim()) {
      return;
    }
    
    setIsLoading(true);
    setHasSearched(true);
    
    try {
      const response = await api.items.searchItems(searchQuery, page, pageSize);
      setItems(response.items);
      setTotalPages(response.totalPages);
      setCurrentPage(page);
    } catch (error) {
      console.error('Error searching items:', error);
    } finally {
      setIsLoading(false);
    }
  };
  
  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    handleSearch(0);
  };
  
  const handleItemDeleted = () => {
    handleSearch(currentPage);
  };
  
  const handlePreviousPage = () => {
    if (currentPage > 0) {
      handleSearch(currentPage - 1);
    }
  };
  
  const handleNextPage = () => {
    if (currentPage < totalPages - 1) {
      handleSearch(currentPage + 1);
    }
  };
  
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold mb-4">Search Items</h1>
        
        <form onSubmit={handleSubmit} className="flex gap-2 mb-6">
          <Input
            placeholder="Search for items..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="flex-grow"
          />
          <Button
            type="submit"
            disabled={!searchQuery.trim() || isLoading}
            isLoading={isLoading}
          >
            Search
          </Button>
        </form>
      </div>
      
      {isLoading ? (
        <div className="flex justify-center py-12">
          <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"></div>
        </div>
      ) : (
        <>
          {hasSearched && items.length === 0 ? (
            <div className="text-center py-12">
              <p className="text-gray-500">No items found matching "{searchQuery}".</p>
            </div>
          ) : (
            hasSearched && (
              <>
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                  {items.map((item) => (
                    <ItemCard
                      key={item.id}
                      item={item}
                      onDelete={handleItemDeleted}
                    />
                  ))}
                </div>
                
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
                      Page {currentPage + 1} of {totalPages || 1}
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
            )
          )}
        </>
      )}
    </div>
  );
} 