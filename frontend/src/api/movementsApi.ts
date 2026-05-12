import axios from 'axios';
import type { MovementFilters, StockMovement } from '../types/movement';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080';

function buildQueryParams(filters: MovementFilters, exportCsv = false): URLSearchParams {
  const params = new URLSearchParams();

  params.set('from', filters.from);
  params.set('to', filters.to);

  if (filters.type !== 'ALL') {
    params.set('type', filters.type);
  }

  if (exportCsv) {
    params.set('export', 'true');
  }

  return params;
}

export async function fetchMovements(filters: MovementFilters): Promise<StockMovement[]> {
  const params = buildQueryParams(filters);

  const response = await axios.get<StockMovement[]>(`${API_BASE_URL}/api/movements?${params.toString()}`);

  return response.data;
}

export function getCsvExportUrl(filters: MovementFilters): string {
  const params = buildQueryParams(filters, true);
  return `${API_BASE_URL}/api/movements?${params.toString()}`;
}
