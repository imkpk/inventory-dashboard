import { Card, CardContent, Typography, Box } from '@mui/material';
import dayjs from 'dayjs';
import {
  Area,
  AreaChart,
  CartesianGrid,
  Cell,
  Legend,
  Pie,
  PieChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis
} from 'recharts';
import type { StockMovement } from '../types/movement';

interface ChartsProps {
  movements: StockMovement[];
}

interface PieData {
  name: 'IN' | 'OUT';
  value: number;
}

interface TimeSeriesBucket {
  date: string;
  IN: number;
  OUT: number;
}

const COLORS = ['#2e7d32', '#ed6c02'];

function buildPieData(movements: StockMovement[]): PieData[] {
  const totals = movements.reduce(
    (acc, movement) => {
      acc[movement.movementType] += movement.quantity;
      return acc;
    },
    { IN: 0, OUT: 0 }
  );

  const data: PieData[] = [
    { name: 'IN', value: totals.IN },
    { name: 'OUT', value: totals.OUT }
  ];

  return data.filter((item) => item.value > 0);
}

function buildTimeSeriesData(movements: StockMovement[]): TimeSeriesBucket[] {
  const buckets = new Map<string, TimeSeriesBucket>();

  movements.forEach((movement) => {
    const date = dayjs(movement.timestamp).format('YYYY-MM-DD');

    const existing = buckets.get(date) ?? {
      date,
      IN: 0,
      OUT: 0
    };

    existing[movement.movementType] += movement.quantity;
    buckets.set(date, existing);
  });

  return Array.from(buckets.values()).sort((a, b) =>
    a.date.localeCompare(b.date)
  );
}

function EmptyChartState() {
  return (
    <Box
      sx={{
        height: 260,
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center'
      }}>
      <Typography color='text.secondary'>
        No data for selected filters
      </Typography>
    </Box>
  );
}

export function Charts({ movements }: ChartsProps) {
  const pieData = buildPieData(movements);
  const timeSeriesData = buildTimeSeriesData(movements);

  return (
    <Box
      sx={{ display: 'flex', gap: 2, flexWrap: 'wrap', alignItems: 'stretch' }}>
      <Box sx={{ flex: { xs: '1 1 100%', md: '0 0 calc(41.666% - 8px)' } }}>
        <Card elevation={1} sx={{ bgcolor: '#f5f5f5', height: '100%' }}>
          <CardContent>
            <Typography variant='h6' sx={{ mb: 2 }}>
              Quantity IN vs OUT
            </Typography>

            {pieData.length === 0 ? (
              <EmptyChartState />
            ) : (
              <Box sx={{ height: 280 }}>
                <ResponsiveContainer width='100%' height='100%'>
                  <PieChart>
                    <Pie
                      data={pieData}
                      dataKey='value'
                      nameKey='name'
                      outerRadius={90}
                      label>
                      {pieData.map((_, index) => (
                        <Cell
                          key={index}
                          fill={COLORS[index % COLORS.length]}
                        />
                      ))}
                    </Pie>
                    <Tooltip />
                    <Legend />
                  </PieChart>
                </ResponsiveContainer>
              </Box>
            )}
          </CardContent>
        </Card>
      </Box>

      <Box sx={{ flex: { xs: '1 1 100%', md: '0 0 calc(58.333% - 8px)' } }}>
        <Card elevation={1} sx={{ bgcolor: '#f5f5f5', height: '100%' }}>
          <CardContent>
            <Typography variant='h6' sx={{ mb: 2 }}>
              Daily Movement Quantity
            </Typography>

            {timeSeriesData.length === 0 ? (
              <EmptyChartState />
            ) : (
              <Box sx={{ height: 280 }}>
                <ResponsiveContainer width='100%' height='100%'>
                  <AreaChart data={timeSeriesData}>
                    <CartesianGrid strokeDasharray='3 3' />
                    <XAxis dataKey='date' />
                    <YAxis allowDecimals={false} />
                    <Tooltip />
                    <Legend />
                    <Area
                      type='monotone'
                      dataKey='IN'
                      stroke='#2e7d32'
                      fill='#2e7d32'
                      fillOpacity={0.15}
                    />
                    <Area
                      type='monotone'
                      dataKey='OUT'
                      stroke='#ed6c02'
                      fill='#ed6c02'
                      fillOpacity={0.15}
                    />
                  </AreaChart>
                </ResponsiveContainer>
              </Box>
            )}
          </CardContent>
        </Card>
      </Box>
    </Box>
  );
}
