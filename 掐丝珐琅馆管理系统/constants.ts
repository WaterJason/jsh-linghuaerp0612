import { StaffMember, POSItem, Task, ScheduleEntry, CoffeeShopSale, POSSaleTransaction } from './types';

export const APP_NAME = "掐丝珐琅馆管理系统";

export const MOCK_STAFF_MEMBERS: StaffMember[] = [
  { id: 's1', name: '张三', role: '管理员', avatar: 'https://picsum.photos/seed/zhangsan/100/100' },
  { id: 's2', name: '李四', role: '咖啡师', avatar: 'https://picsum.photos/seed/lisi/100/100' },
  { id: 's3', name: '王五', role: '展馆服务', avatar: 'https://picsum.photos/seed/wangwu/100/100' },
  { id: 's4', name: '赵六', role: '收银员', avatar: 'https://picsum.photos/seed/zhaoliu/100/100' },
  { id: 's5', name: '孙七', role: '咖啡师', avatar: 'https://picsum.photos/seed/sunqi/100/100' },
];

export const MOCK_POS_ITEMS: POSItem[] = [
  { id: 'p1', name: '拿铁咖啡', price: 28, category: '饮品', imageUrl: 'https://picsum.photos/seed/latte/200/200', stock: 50 },
  { id: 'p2', name: '可颂面包', price: 15, category: '小食', imageUrl: 'https://picsum.photos/seed/croissant/200/200', stock: 30 },
  { id: 'p3', name: '珐琅钥匙扣', price: 68, category: '纪念品', imageUrl: 'https://picsum.photos/seed/keychain/200/200', stock: 100 },
  { id: 'p4', name: '迷你珐琅花瓶', price: 288, category: '珐琅制品', imageUrl: 'https://picsum.photos/seed/vase/200/200', stock: 20 },
  { id: 'p5', name: '美式咖啡', price: 22, category: '饮品', imageUrl: 'https://picsum.photos/seed/americano/200/200', stock: 40 },
  { id: 'p6', name: '巧克力蛋糕', price: 35, category: '小食', imageUrl: 'https://picsum.photos/seed/cake/200/200', stock: 25 },
];

export const MOCK_TASKS: Task[] = [
  { id: 't1', description: '检查展厅设备', completed: false, dueDate: new Date().toISOString().split('T')[0] },
  { id: 't2', description: '补充咖啡豆库存', completed: true },
  { id: 't3', description: '整理纪念品区', completed: false },
  { id: 't4', description: '更新今日优惠信息', completed: false, dueDate: new Date().toISOString().split('T')[0] },
];

const today = new Date();
const tomorrow = new Date(today);
tomorrow.setDate(today.getDate() + 1);

export const MOCK_SCHEDULE_ENTRIES: ScheduleEntry[] = [
  { id: 'se1', date: today.toISOString().split('T')[0], staffId: 's2', staffName: '李四', shift: '早班' },
  { id: 'se2', date: today.toISOString().split('T')[0], staffId: 's3', staffName: '王五', shift: '全天' },
  { id: 'se3', date: tomorrow.toISOString().split('T')[0], staffId: 's5', staffName: '孙七', shift: '晚班' },
  { id: 'se4', date: tomorrow.toISOString().split('T')[0], staffId: 's4', staffName: '赵六', shift: '早班' },
];

export const MOCK_COFFEE_SHOP_SALES: CoffeeShopSale[] = [
    { id: 'cs1', date: new Date(Date.now() - 86400000).toISOString().split('T')[0], totalAmount: 1250, notes: '昨日销售额' },
];

export const SHIFT_OPTIONS: ScheduleEntry['shift'][] = ['早班', '晚班', '全天'];
export const PAYMENT_METHODS: POSSaleTransaction['paymentMethod'][] = ['现金', '刷卡', '微信支付', '支付宝'];