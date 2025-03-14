'use client';

import React, { useState } from 'react';
import { Input } from '../ui/Input';
import { Button } from '../ui/Button';
import { Condition, ItemRequest } from '@/types/item';

interface ItemFormProps {
  initialData?: ItemRequest;
  onSubmit: (data: ItemRequest) => Promise<void>;
  submitButtonText?: string;
  isLoading?: boolean;
}

export const ItemForm: React.FC<ItemFormProps> = ({
  initialData,
  onSubmit,
  submitButtonText = 'Submit',
  isLoading = false,
}) => {
  const [formData, setFormData] = useState<ItemRequest>(
    initialData || {
      name: '',
      brand: '',
      condition: Condition.NEW,
      price: 0,
      size: '',
    }
  );
  
  const [errors, setErrors] = useState<Record<string, string>>({});
  
  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    
    if (name === 'price') {
      setFormData((prev) => ({
        ...prev,
        [name]: parseFloat(value) || 0,
      }));
    } else {
      setFormData((prev) => ({
        ...prev,
        [name]: value,
      }));
    }
    
    // Clear validation error when field is changed
    if (errors[name]) {
      setErrors((prev) => {
        const newErrors = { ...prev };
        delete newErrors[name];
        return newErrors;
      });
    }
  };
  
  const validateForm = (): boolean => {
    const newErrors: Record<string, string> = {};
    
    if (!formData.name.trim()) {
      newErrors.name = 'Name is required';
    }
    
    if (!formData.brand.trim()) {
      newErrors.brand = 'Brand is required';
    }
    
    if (formData.price <= 0) {
      newErrors.price = 'Price must be greater than 0';
    }
    
    if (!formData.size.trim()) {
      newErrors.size = 'Size is required';
    }
    
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };
  
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!validateForm()) {
      return;
    }
    
    try {
      await onSubmit(formData);
    } catch (error) {
      console.error('Form submission error:', error);
    }
  };
  
  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <Input
        label="Item Name"
        name="name"
        value={formData.name}
        onChange={handleChange}
        error={errors.name}
        placeholder="e.g., Nike Air Max 90"
        required
      />
      
      <Input
        label="Brand"
        name="brand"
        value={formData.brand}
        onChange={handleChange}
        error={errors.brand}
        placeholder="e.g., Nike"
        required
      />
      
      <div className="space-y-2">
        <label className="block text-sm font-medium text-gray-700">
          Condition
        </label>
        <select
          name="condition"
          value={formData.condition}
          onChange={handleChange}
          className="mt-1 block w-full pl-3 pr-10 py-2 text-base border-gray-300 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm rounded-md"
        >
          {Object.values(Condition).map((condition) => (
            <option key={condition} value={condition}>
              {condition}
            </option>
          ))}
        </select>
      </div>
      
      <Input
        label="Price ($)"
        name="price"
        type="number"
        value={formData.price}
        onChange={handleChange}
        error={errors.price}
        placeholder="0.00"
        step="0.01"
        min="0"
        required
      />
      
      <Input
        label="Size"
        name="size"
        value={formData.size}
        onChange={handleChange}
        error={errors.size}
        placeholder="e.g., M, 42, 10, etc."
        required
      />
      
      <div className="pt-4">
        <Button
          type="submit"
          isLoading={isLoading}
          fullWidth
        >
          {submitButtonText}
        </Button>
      </div>
    </form>
  );
}; 