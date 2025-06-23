
import React, { useState, useEffect } from 'react';
import { Card, Button, Input, FileUpload, TextArea } from '../components/ui_elements';
import { CoffeeShopSale, ScheduleEntry, StaffMember } from '../types';
import { MOCK_COFFEE_SHOP_SALES, MOCK_SCHEDULE_ENTRIES, MOCK_STAFF_MEMBERS } from '../constants';
import { DollarSignIcon, CameraIcon, UsersIcon, PlusIcon } from '../components/Icons';

const PageHeader: React.FC<{ title: string; actions?: React.ReactNode }> = ({ title, actions }) => (
  <div className="mb-6 flex justify-between items-center">
    <h1 className="text-3xl font-bold text-slate-800">{title}</h1>
    {actions && <div>{actions}</div>}
  </div>
);

// Helper to get today's date in YYYY-MM-DD format
const getTodayDateString = () => new Date().toISOString().split('T')[0];

interface DailySalesReportFormProps {
  onSaveSale: (sale: Omit<CoffeeShopSale, 'id'>) => void;
}

const DailySalesReportForm: React.FC<DailySalesReportFormProps> = ({ onSaveSale }) => {
  const [date, setDate] = useState(getTodayDateString());
  const [totalAmount, setTotalAmount] = useState('');
  const [receiptImage, setReceiptImage] = useState<File | null>(null);
  const [receiptPreview, setReceiptPreview] = useState<string | null>(null);
  const [notes, setNotes] = useState('');
  const [error, setError] = useState('');

  const handleFileChange = (file: File | null) => {
    setReceiptImage(file);
    if (file) {
      const reader = new FileReader();
      reader.onloadend = () => {
        setReceiptPreview(reader.result as string);
      };
      reader.readAsDataURL(file);
    } else {
      setReceiptPreview(null);
    }
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!totalAmount || isNaN(parseFloat(totalAmount)) || parseFloat(totalAmount) <= 0) {
      setError('请输入有效的销售金额');
      return;
    }
    setError('');
    onSaveSale({
      date,
      totalAmount: parseFloat(totalAmount),
      receiptImage: receiptPreview || undefined, // Store base64 preview or actual URL if uploaded
      notes,
    });
    // Reset form
    setDate(getTodayDateString());
    setTotalAmount('');
    setReceiptImage(null);
    setReceiptPreview(null);
    setNotes('');
    alert('销售数据已保存!');
  };

  return (
    <Card title="日销售数据填写" actions={<Button type="submit" form="salesForm" leftIcon={<PlusIcon className="w-4 h-4"/>}>保存记录</Button>}>
      <form id="salesForm" onSubmit={handleSubmit} className="space-y-6">
        <Input label="日期" type="date" value={date} onChange={e => setDate(e.target.value)} required />
        <Input label="总销售额 (元)" type="number" placeholder="例如: 1500.50" value={totalAmount} onChange={e => setTotalAmount(e.target.value)} required error={error}/>
        <FileUpload label="销售凭证截图 (可选)" id="receiptImage" onChange={handleFileChange} accept="image/*" currentFileName={receiptImage?.name} />
        {receiptPreview && (
          <div>
            <p className="text-sm font-medium text-slate-700 mb-1">图片预览:</p>
            <img src={receiptPreview} alt="Receipt Preview" className="max-h-60 rounded-md border border-slate-300" />
          </div>
        )}
        <TextArea label="备注 (可选)" value={notes} onChange={e => setNotes(e.target.value)} placeholder="例如: 今日有促销活动"/>
      </form>
    </Card>
  );
};

const OnDutyCoffeeStaffCard: React.FC = () => {
  const today = getTodayDateString();
  const coffeeShopRoles: StaffMember['role'][] = ['咖啡师', '收银员']; // Assuming these roles work in coffee shop
  
  const onDutyEntries = MOCK_SCHEDULE_ENTRIES.filter(entry => 
    entry.date === today && 
    MOCK_STAFF_MEMBERS.find(staff => staff.id === entry.staffId && coffeeShopRoles.includes(staff.role))
  );

  const staffOnDuty = MOCK_STAFF_MEMBERS.filter(staff => 
    onDutyEntries.some(entry => entry.staffId === staff.id)
  );

  return (
    <Card title="今日咖啡店值班" icon={<UsersIcon className="w-5 h-5 text-indigo-600" />}>
      {staffOnDuty.length === 0 ? (
        <p className="text-slate-500">今日无咖啡店排班人员。</p>
      ) : (
        <ul className="space-y-3">
          {staffOnDuty.map(staff => {
            const entry = onDutyEntries.find(e => e.staffId === staff.id);
            return (
              <li key={staff.id} className="flex items-center space-x-3 p-2 hover:bg-slate-50 rounded-md">
                <img src={staff.avatar || `https://ui-avatars.com/api/?name=${staff.name}&background=random`} alt={staff.name} className="h-10 w-10 rounded-full" />
                <div>
                  <p className="font-medium text-slate-700">{staff.name}</p>
                  <p className="text-sm text-slate-500">{staff.role} - {entry?.shift}</p>
                </div>
              </li>
            );
          })}
        </ul>
      )}
    </Card>
  );
};

const RecentSalesList: React.FC<{sales: CoffeeShopSale[]}> = ({sales}) => {
    const sortedSales = [...sales].sort((a,b) => new Date(b.date).getTime() - new Date(a.date).getTime()).slice(0,5); // Show last 5
    return (
        <Card title="最近销售记录">
            {sortedSales.length === 0 ? (
                <p className="text-slate-500">暂无销售记录。</p>
            ) : (
            <ul className="divide-y divide-slate-200">
                {sortedSales.map(sale => (
                    <li key={sale.id} className="py-3 flex justify-between items-center">
                        <div>
                            <p className="text-sm font-medium text-slate-800">日期: {sale.date}</p>
                            <p className="text-xs text-slate-500">金额: ¥{sale.totalAmount.toLocaleString()}</p>
                            {sale.notes && <p className="text-xs text-slate-500 italic">备注: {sale.notes}</p>}
                        </div>
                        {sale.receiptImage && (
                            <img src={sale.receiptImage} alt={`凭证 ${sale.date}`} className="h-10 w-10 rounded object-cover cursor-pointer" onClick={() => window.open(sale.receiptImage || '')}/>
                        )}
                    </li>
                ))}
            </ul>
            )}
        </Card>
    );
}


const CoffeeShopPage: React.FC = () => {
  const [salesRecords, setSalesRecords] = useState<CoffeeShopSale[]>(MOCK_COFFEE_SHOP_SALES);

  const handleSaveSale = (newSaleData: Omit<CoffeeShopSale, 'id'>) => {
    const newSale: CoffeeShopSale = {
      ...newSaleData,
      id: `cs-${Date.now()}`,
    };
    setSalesRecords(prevSales => [newSale, ...prevSales]);
  };

  return (
    <div>
      <PageHeader title="咖啡店管理" />
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2">
          <DailySalesReportForm onSaveSale={handleSaveSale} />
        </div>
        <div className="lg:col-span-1 space-y-6">
          <OnDutyCoffeeStaffCard />
          <RecentSalesList sales={salesRecords} />
        </div>
      </div>
    </div>
  );
};

export default CoffeeShopPage;
