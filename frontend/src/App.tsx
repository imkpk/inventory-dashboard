import { Alert, Box, Container, CssBaseline, Stack, ThemeProvider, Typography, createTheme } from '@mui/material';
import { QueryClient, QueryClientProvider, useQuery } from '@tanstack/react-query';
import dayjs from 'dayjs';
import { fetchMovements, getCsvExportUrl } from './api/movementsApi';
import { Charts } from './components/Charts';
import { FiltersPanel } from './components/FiltersPanel';
import { MovementTable } from './components/MovementTable';
import type { MovementFilters } from './types/movement';
import { useState } from 'react';

const queryClient = new QueryClient();

const theme = createTheme({
  palette: {
    mode: 'light'
  },
  shape: {
    borderRadius: 10
  }
});

const defaultFilters: MovementFilters = {
  from: '2026-03-01',
  to: dayjs().format('YYYY-MM-DD'),
  type: 'ALL'
};

function DashboardPage() {
  const [filters, setFilters] = useState<MovementFilters>(defaultFilters);

  const isDateRangeValid = Boolean(filters.from && filters.to) && !dayjs(filters.from).isAfter(dayjs(filters.to));

  const {
    data: movements = [],
    isFetching,
    isError,
    error
  } = useQuery({
    queryKey: ['movements', filters],
    queryFn: () => fetchMovements(filters),
    enabled: isDateRangeValid
  });

  const handleExport = () => {
    window.location.href = getCsvExportUrl(filters);
  };

  return (
    <Container maxWidth="xl" sx={{ py: 4 }}>
      <Stack spacing={3}>
        <Box>
          <Typography variant="h4" fontWeight={700}>
            Inventory Movement Dashboard
          </Typography>
          <Typography variant="body1" color="text.secondary">
            Filter warehouse stock movements, view charts, and export the full filtered dataset.
          </Typography>
        </Box>

        <FiltersPanel
          filters={filters}
          onChange={setFilters}
          onExport={handleExport}
          isExportDisabled={!isDateRangeValid || movements.length === 0}
        />

        {!isDateRangeValid && (
          <Alert severity="warning">Please select a valid date range. From date cannot be after To date.</Alert>
        )}

        {isError && (
          <Alert severity="error">
            {error instanceof Error ? error.message : 'Failed to load stock movements'}
          </Alert>
        )}

        <Charts movements={movements} />

        <MovementTable rows={movements} isLoading={isFetching} />
      </Stack>
    </Container>
  );
}

export default function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <ThemeProvider theme={theme}>
        <CssBaseline />
        <DashboardPage />
      </ThemeProvider>
    </QueryClientProvider>
  );
}
