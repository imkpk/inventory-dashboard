import { Button, Card, CardContent, FormControl, InputLabel, MenuItem, Select, Stack, TextField } from '@mui/material';
import DownloadIcon from '@mui/icons-material/Download';
import type { MovementFilterType, MovementFilters } from '../types/movement';

interface FiltersPanelProps {
  filters: MovementFilters;
  onChange: (filters: MovementFilters) => void;
  onExport: () => void;
  isExportDisabled: boolean;
}

export function FiltersPanel({ filters, onChange, onExport, isExportDisabled }: FiltersPanelProps) {
  return (
    <Card elevation={1}>
      <CardContent>
        <Stack direction={{ xs: 'column', md: 'row' }} spacing={2} alignItems={{ xs: 'stretch', md: 'center' }}>
          <TextField
            label="From"
            type="date"
            value={filters.from}
            onChange={(event) => onChange({ ...filters, from: event.target.value })}
            InputLabelProps={{ shrink: true }}
            required
            fullWidth
          />

          <TextField
            label="To"
            type="date"
            value={filters.to}
            onChange={(event) => onChange({ ...filters, to: event.target.value })}
            InputLabelProps={{ shrink: true }}
            required
            fullWidth
          />

          <FormControl fullWidth>
            <InputLabel id="movement-type-label">Movement Type</InputLabel>
            <Select
              labelId="movement-type-label"
              label="Movement Type"
              value={filters.type}
              onChange={(event) =>
                onChange({ ...filters, type: event.target.value as MovementFilterType })
              }
            >
              <MenuItem value="ALL">All</MenuItem>
              <MenuItem value="IN">IN</MenuItem>
              <MenuItem value="OUT">OUT</MenuItem>
            </Select>
          </FormControl>

          <Button
            variant="contained"
            startIcon={<DownloadIcon />}
            onClick={onExport}
            disabled={isExportDisabled}
            sx={{ minWidth: 160 }}
          >
            Export CSV
          </Button>
        </Stack>
      </CardContent>
    </Card>
  );
}
