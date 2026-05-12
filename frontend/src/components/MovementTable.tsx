import { Card, CardContent, Chip, Typography } from '@mui/material';
import { DataGrid, GridColDef, GridToolbar } from '@mui/x-data-grid';
import dayjs from 'dayjs';
import type { StockMovement } from '../types/movement';

interface MovementTableProps {
  rows: StockMovement[];
  isLoading: boolean;
}

const columns: GridColDef<StockMovement>[] = [
  {
    field: 'timestamp',
    headerName: 'Date/Time',
    flex: 1.5,
    minWidth: 190,
    valueFormatter: (value) => dayjs(value as string).format('YYYY-MM-DD HH:mm')
  },
  {
    field: 'sku',
    headerName: 'SKU',
    flex: 1,
    minWidth: 120
  },
  {
    field: 'movementType',
    headerName: 'Movement Type',
    flex: 1,
    minWidth: 150,
    renderCell: (params) => (
      <Chip
        label={params.value}
        size="small"
        color={params.value === 'IN' ? 'success' : 'warning'}
        variant="outlined"
      />
    )
  },
  {
    field: 'quantity',
    headerName: 'Quantity',
    flex: 1,
    minWidth: 120,
    type: 'number'
  }
];

export function MovementTable({ rows, isLoading }: MovementTableProps) {
  return (
    <Card elevation={1}>
      <CardContent>
        <Typography variant="h6" sx={{ mb: 2 }}>
          Stock Movements
        </Typography>

        <DataGrid
          rows={rows}
          columns={columns}
          loading={isLoading}
          getRowId={(row) => row.id}
          checkboxSelection
          sortingOrder={['asc', 'desc']}
          slots={{
            toolbar: GridToolbar
          }}
          slotProps={{
            toolbar: {
              showQuickFilter: true,
              csvOptions: {
                fileName: 'stock-movements'
              },
              printOptions: {
                disableToolbarButton: true
              }
            }
          }}
          pageSizeOptions={[10]}
          initialState={{
            pagination: {
              paginationModel: {
                page: 0,
                pageSize: 10
              }
            }
          }}
          disableRowSelectionOnClick
          autoHeight
        />
      </CardContent>
    </Card>
  );
}
