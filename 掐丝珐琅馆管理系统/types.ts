
export enum Page {
  Overview = '总览',
  Scheduling = '排班管理',
  CoffeeShop = '咖啡店管理',
  POS = 'POS销售',
}

export interface StaffMember {
  id: string;
  name: string;
  role: '管理员' | '咖啡师' | '展馆服务' | '收银员';
  avatar?: string;
}

export interface Task {
  id:string;
  description: string;
  completed: boolean;
  dueDate?: string; // Optional due date
}

export interface ScheduleEntry {
  id: string;
  date: string; // YYYY-MM-DD
  staffId: string;
  staffName: string; // Denormalized for easier display
  shift: '早班' | '晚班' | '全天';
}

export interface CoffeeShopSale {
  id: string;
  date: string; // YYYY-MM-DD
  totalAmount: number;
  receiptImage?: string; // base64 encoded image or URL
  notes?: string;
}

export interface POSItem {
  id: string;
  name: string;
  price: number;
  category: '饮品' | '小食' | '纪念品' | '珐琅制品';
  imageUrl?: string;
  stock: number;
}

export interface CartItem extends POSItem {
  quantity: number;
}

export interface POSSaleTransaction {
  id: string;
  items: CartItem[];
  totalAmount: number;
  paymentMethod: '现金' | '刷卡' | '微信支付' | '支付宝';
  timestamp: string; // ISO string
}

export interface DailySalesData {
  date: string; // YYYY-MM-DD
  coffeeShopTotal: number;
  museumShopTotal: number;
}
