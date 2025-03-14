export enum Condition {
  NEW = "NEW",
  LIKE_NEW = "LIKE_NEW",
  GOOD = "GOOD",
  FAIR = "FAIR",
  POOR = "POOR",
  USED = "USED"
}

export enum ItemStatus {
  AVAILABLE = "AVAILABLE",
  SOLD = "SOLD",
  RESERVED = "RESERVED"
}

export interface Item {
  id: number;
  name: string;
  brand: string;
  condition: Condition;
  price: number;
  size: string;
  status: ItemStatus;
  listedAt: string;
  username: string; // Seller's username
}

export interface ItemRequest {
  name: string;
  brand: string;
  condition: Condition;
  price: number;
  size: string;
}

export interface ItemResponse {
  id: number;
  name: string;
  brand: string;
  condition: Condition;
  price: number;
  size: string;
  status: ItemStatus;
  listedAt: string;
  username: string;
}

export interface PaginatedItemsResponse {
  items: Item[];
  page: number;
  pageSize: number;
  totalPages: number;
  totalElements: number;
} 