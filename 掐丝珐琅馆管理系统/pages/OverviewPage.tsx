
import React, { useState, useEffect } from 'react';
import { Card, Button } from '../components/ui_elements';
import { Task, StaffMember, ScheduleEntry, DailySalesData } from '../types';
import { MOCK_TASKS, MOCK_STAFF_MEMBERS, MOCK_SCHEDULE_ENTRIES, MOCK_COFFEE_SHOP_SALES } from '../constants';
import { ClipboardListIcon, UsersIcon, DollarSignIcon, PlusIcon, CoffeeIcon, ShoppingCartIcon } from '../components/Icons';

const PageHeader: React.FC<{ title: string; actions?: React.ReactNode }> = ({ title, actions }) => (
  <div className="mb-6 flex justify-between items-center">
    <h1 className="text-3xl font-bold text-slate-800">{title}</h1>
    {actions && <div>{actions}</div>}
  </div>
);

// Helper to get today's date in YYYY-MM-DD format
const getTodayDateString = () => new Date().toISOString().split('T')[0];

const TodaysTasksCard: React.FC = () => {
  const [tasks, setTasks] = useState<Task[]>(MOCK_TASKS.filter(task => !task.dueDate || task.dueDate === getTodayDateString()));

  const toggleTaskCompletion = (id: string) => {
    setTasks(prevTasks => 
      prevTasks.map(task => 
        task.id === id ? { ...task, completed: !task.completed } : task
      )
    );
  };
  
  return (
    <Card title="今日工作" className="h-full" actions={<Button size="sm" variant="ghost" leftIcon={<PlusIcon className="w-4 h-4" />}>新任务</Button>}>
      {tasks.length === 0 ? (
        <p className="text-slate-500">今日无特定任务。</p>
      ) : (
        <ul className="space-y-3">
          {tasks.map(task => (
            <li key={task.id} className="flex items-center justify-between p-2 hover:bg-slate-50 rounded-md">
              <div className="flex items-center">
                <input
                  type="checkbox"
                  checked={task.completed}
                  onChange={() => toggleTaskCompletion(task.id)}
                  className="h-4 w-4 text-indigo-600 border-slate-300 rounded focus:ring-indigo-500 mr-3"
                  aria-labelledby={`task-label-${task.id}`}
                />
                <span id={`task-label-${task.id}`} className={`${task.completed ? 'line-through text-slate-500' : 'text-slate-700'}`}>
                  {task.description}
                </span>
              </div>
              {!task.completed && task.dueDate && <span className="text-xs bg-amber-100 text-amber-700 px-2 py-0.5 rounded-full">截止</span>}
            </li>
          ))}
        </ul>
      )}
    </Card>
  );
};

const SalesSummaryCard: React.FC<{ title: string; amount: number; icon: React.ReactNode; bgColorClass: string }> = ({ title, amount, icon, bgColorClass }) => (
  <Card className={`${bgColorClass} text-white`}>
    <div className="flex items-center space-x-3">
      <div className="p-3 bg-black bg-opacity-20 rounded-lg">
        {icon}
      </div>
      <div>
        <p className="text-sm opacity-90">{title}</p>
        <p className="text-2xl font-bold">¥{amount.toLocaleString(undefined, {minimumFractionDigits: 2, maximumFractionDigits: 2})}</p>
      </div>
    </div>
  </Card>
);

const TodaysOnDutyStaffCard: React.FC = () => {
  const today = getTodayDateString();
  const onDutyEntries = MOCK_SCHEDULE_ENTRIES.filter(entry => entry.date === today);
  
  const staffOnDutyToday = MOCK_STAFF_MEMBERS.filter(staff =>
    onDutyEntries.some(entry => entry.staffId === staff.id)
  );

  return (
    <Card title="今日值班" icon={<UsersIcon className="w-5 h-5 text-indigo-600"/>} className="h-full">
      {staffOnDutyToday.length === 0 ? (
        <p className="text-slate-500">今日无值班人员。</p>
      ) : (
        <ul className="space-y-3">
          {staffOnDutyToday.map(staff => {
            const entry = onDutyEntries.find(e => e.staffId === staff.id);
            return (
            <li key={staff.id} className="flex items-center space-x-3 p-2 hover:bg-slate-50 rounded-md">
              <img src={staff.avatar || `https://ui-avatars.com/api/?name=${encodeURIComponent(staff.name)}&background=random`} alt={staff.name} className="h-10 w-10 rounded-full" />
              <div>
                <p className="font-medium text-slate-700">{staff.name}</p>
                <p className="text-sm text-slate-500">{staff.role} {entry ? `- ${entry.shift}`: ''}</p>
              </div>
            </li>
          );
        })}
        </ul>
      )}
    </Card>
  );
};


const OverviewPage: React.FC = () => {
  const [dailySales, setDailySales] = useState<DailySalesData | null>(null);
  // Add tasks state for the "总任务完成率" card, MOCK_TASKS is used as initial state here for consistency.
  // In a real app, this might be fetched or managed differently.
  const [tasksForSummary, setTasksForSummary] = useState<Task[]>(MOCK_TASKS);


  useEffect(() => {
    const today = getTodayDateString();
    const coffeeSalesToday = MOCK_COFFEE_SHOP_SALES.find(s => s.date === today)?.totalAmount || 0;
    
    // For museum sales, let's sum up POS transactions for today. This requires MOCK_POS_TRANSACTIONS.
    // Assuming MOCK_POS_TRANSACTIONS is available or we use a placeholder.
    // For now, using a placeholder as MOCK_POS_TRANSACTIONS is not in constants.ts imports in OverviewPage.
    const museumSalesToday = 750.00; // Placeholder value

    setDailySales({
        date: today,
        coffeeShopTotal: coffeeSalesToday,
        museumShopTotal: museumSalesToday,
    });
  }, []);
  
  return (
    <div>
      <PageHeader title="总览" actions={
          <Button size="sm" variant="primary" onClick={() => alert('添加新概要项: 功能待实现')} leftIcon={<PlusIcon className="w-4 h-4"/>}>
            添加概要
          </Button>
      } />
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-6">
        <SalesSummaryCard title="咖啡店今日销售" amount={dailySales?.coffeeShopTotal || 0} icon={<CoffeeIcon className="h-6 w-6 text-white" />} bgColorClass="bg-gradient-to-br from-cyan-500 to-blue-600" />
        <SalesSummaryCard title="珐琅馆今日销售" amount={dailySales?.museumShopTotal || 0} icon={<ShoppingCartIcon className="h-6 w-6 text-white" />} bgColorClass="bg-gradient-to-br from-purple-500 to-indigo-600" />
        <Card title="总任务完成率" className="bg-gradient-to-br from-emerald-500 to-green-600 text-white">
            <p className="text-3xl font-bold">
                {tasksForSummary.length > 0 ? ((tasksForSummary.filter(t=>t.completed).length / tasksForSummary.length) * 100).toFixed(0) : 0}%
            </p>
            <p className="text-sm opacity-90">{tasksForSummary.filter(t=>t.completed).length} / {tasksForSummary.length} 项完成</p>
        </Card>
         <Card title="今日值班人数" className="bg-gradient-to-br from-amber-500 to-yellow-600 text-white">
             <p className="text-3xl font-bold">
                {MOCK_SCHEDULE_ENTRIES.filter(e => e.date === getTodayDateString()).length}
             </p>
             <p className="text-sm opacity-90">位员工</p>
        </Card>
      </div>
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2">
          <TodaysTasksCard /> 
        </div>
        <div className="lg:col-span-1">
          <TodaysOnDutyStaffCard />
        </div>
      </div>
    </div>
  );
};

export default OverviewPage;
