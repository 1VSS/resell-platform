import React from 'react';
import Link from 'next/link';

export const Footer: React.FC = () => {
  return (
    <footer className="bg-gray-100 mt-auto">
      <div className="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8">
        <div className="flex flex-col md:flex-row justify-between items-center space-y-4 md:space-y-0">
          <div className="flex items-center">
            <span className="text-lg font-semibold text-blue-600">Resell</span>
            <span className="ml-2 text-sm text-gray-500">© {new Date().getFullYear()} All rights reserved.</span>
          </div>
          <div className="flex space-x-6">
            <Link href="/about" className="text-sm text-gray-500 hover:text-gray-800">
              About
            </Link>
            <Link href="/terms" className="text-sm text-gray-500 hover:text-gray-800">
              Terms
            </Link>
            <Link href="/privacy" className="text-sm text-gray-500 hover:text-gray-800">
              Privacy
            </Link>
          </div>
        </div>
      </div>
    </footer>
  );
}; 