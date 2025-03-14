'use client';

import React, { forwardRef } from 'react';

interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  label?: string;
  error?: string;
  fullWidth?: boolean;
}

export const Input = forwardRef<HTMLInputElement, InputProps>(
  ({ label, error, fullWidth = true, className = '', ...props }, ref) => {
    return (
      <div className={`${fullWidth ? 'w-full' : ''} space-y-2`}>
        {label && (
          <label
            htmlFor={props.id}
            className="block text-sm font-medium text-gray-700"
          >
            {label}
          </label>
        )}
        <input
          ref={ref}
          className={`
            px-3 py-2 bg-white border shadow-sm border-gray-300 placeholder-gray-400 
            focus:outline-none focus:border-blue-500 focus:ring-blue-500 block rounded-md sm:text-sm 
            focus:ring-1 ${error ? 'border-red-500' : ''} ${fullWidth ? 'w-full' : ''} ${className}
          `}
          {...props}
        />
        {error && <p className="mt-1 text-sm text-red-600">{error}</p>}
      </div>
    );
  }
); 