export type MovementType = 'IN' | 'OUT';

export type MovementFilterType = 'ALL' | MovementType;

export interface StockMovement {
  id: string;
  timestamp: string;
  sku: string;
  movementType: MovementType;
  quantity: number;
}

export interface MovementFilters {
  from: string;
  to: string;
  type: MovementFilterType;
}
