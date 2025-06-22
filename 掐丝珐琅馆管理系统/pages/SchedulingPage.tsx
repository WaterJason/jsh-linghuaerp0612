
import React, { useState, useMemo, useCallback, useEffect } from 'react';
import { Card, Button, Modal, Select, Input } from '../components/ui_elements';
import { ScheduleEntry, StaffMember } from '../types';
import { MOCK_SCHEDULE_ENTRIES, MOCK_STAFF_MEMBERS, SHIFT_OPTIONS } from '../constants';
import { CalendarDaysIcon, ListIcon, PlusIcon, ChevronLeftIcon, ChevronRightIcon, TrashIcon, EditIcon, UsersIcon } from '../components/Icons';

const PageHeader: React.FC<{ title: string; actions?: React.ReactNode }> = ({ title, actions }) => (
  <div className="mb-6 flex justify-between items-center">
    <h1 className="text-3xl font-bold text-slate-800">{title}</h1>
    {actions && <div className="flex items-center space-x-2">{actions}</div>}
  </div>
);

// Date utilities (simplified)
const getDaysInMonth = (year: number, month: number): number => new Date(year, month + 1, 0).getDate();
const getFirstDayOfMonth = (year: number, month: number): number => new Date(year, month, 1).getDay(); // 0 for Sunday, 1 for Monday...
const formatDate = (date: Date): string => date.toISOString().split('T')[0];
const getMonthName = (month: number): string => 
  ['一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月'][month];

interface CalendarViewProps {
  schedules: ScheduleEntry[];
  currentDate: Date;
  onDateClick: (date: string) => void;
  onEditSchedule: (schedule: ScheduleEntry) => void;
}

const CalendarView: React.FC<CalendarViewProps> = ({ schedules, currentDate, onDateClick, onEditSchedule }) => {
  const year = currentDate.getFullYear();
  const month = currentDate.getMonth();

  const daysInMonth = getDaysInMonth(year, month);
  let firstDay = getFirstDayOfMonth(year, month);
  firstDay = firstDay === 0 ? 6 : firstDay -1; // Adjust: Monday as first day (0), Sunday as last (6)


  const calendarDays = useMemo(() => {
    const days: (Date | null)[] = [];
    for (let i = 0; i < firstDay; i++) {
      days.push(null);
    }
    for (let i = 1; i <= daysInMonth; i++) {
      days.push(new Date(year, month, i));
    }
    return days;
  }, [year, month, daysInMonth, firstDay]);

  const todayStr = formatDate(new Date());

  return (
    <Card>
      <div className="grid grid-cols-7 gap-px border border-slate-200 bg-slate-200">
        {['一', '二', '三', '四', '五', '六', '日'].map(day => (
          <div key={day} className="py-2 text-center font-semibold text-slate-700 bg-slate-100 text-sm">
            {day}
          </div>
        ))}
        {calendarDays.map((day, index) => {
          const dayStr = day ? formatDate(day) : '';
          const daySchedules = day ? schedules.filter(s => s.date === dayStr) : [];
          const isToday = dayStr === todayStr;
          
          return (
            <div
              key={index}
              className={`p-2 min-h-[100px] bg-white hover:bg-slate-50 transition-colors cursor-pointer ${day ? '' : 'bg-slate-50'}`}
              onClick={() => day && onDateClick(dayStr)}
            >
              {day && (
                <>
                  <div className={`text-sm font-medium ${isToday ? 'text-indigo-600 font-bold' : 'text-slate-600'}`}>
                    {day.getDate()}
                  </div>
                  <ul className="mt-1 space-y-1 text-xs">
                    {daySchedules.map(schedule => (
                      <li key={schedule.id} 
                          className="bg-indigo-100 text-indigo-700 p-1 rounded truncate hover:bg-indigo-200"
                          onClick={(e) => { e.stopPropagation(); onEditSchedule(schedule); }}>
                        {schedule.staffName} ({schedule.shift})
                      </li>
                    ))}
                  </ul>
                </>
              )}
            </div>
          );
        })}
      </div>
    </Card>
  );
};


interface ScheduleListViewProps {
  schedules: ScheduleEntry[];
  onEdit: (schedule: ScheduleEntry) => void;
  onDelete: (scheduleId: string) => void;
}

const ScheduleListView: React.FC<ScheduleListViewProps> = ({ schedules, onEdit, onDelete }) => {
  const sortedSchedules = [...schedules].sort((a,b) => new Date(a.date).getTime() - new Date(b.date).getTime());
  return (
    <Card>
      <div className="overflow-x-auto">
        <table className="min-w-full divide-y divide-slate-200">
          <thead className="bg-slate-50">
            <tr>
              <th className="px-4 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider">日期</th>
              <th className="px-4 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider">员工</th>
              <th className="px-4 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider">班次</th>
              <th className="px-4 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider">操作</th>
            </tr>
          </thead>
          <tbody className="bg-white divide-y divide-slate-200">
            {sortedSchedules.length === 0 && (
              <tr><td colSpan={4} className="p-4 text-center text-slate-500">暂无排班记录</td></tr>
            )}
            {sortedSchedules.map(schedule => (
              <tr key={schedule.id} className="hover:bg-slate-50">
                <td className="px-4 py-3 whitespace-nowrap text-sm text-slate-700">{schedule.date}</td>
                <td className="px-4 py-3 whitespace-nowrap text-sm text-slate-700">{schedule.staffName}</td>
                <td className="px-4 py-3 whitespace-nowrap text-sm text-slate-700">{schedule.shift}</td>
                <td className="px-4 py-3 whitespace-nowrap text-sm space-x-2">
                  <Button variant="ghost" size="sm" onClick={() => onEdit(schedule)} leftIcon={<EditIcon className="w-4 h-4" />}>编辑</Button>
                  <Button variant="ghost" size="sm" className="text-red-500 hover:bg-red-100" onClick={() => onDelete(schedule.id)} leftIcon={<TrashIcon className="w-4 h-4" />}>删除</Button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </Card>
  );
};

interface MonthlyStaffSummaryProps {
  schedules: ScheduleEntry[];
  staffMembers: StaffMember[];
  currentDate: Date;
}
const MonthlyStaffSummary: React.FC<MonthlyStaffSummaryProps> = ({ schedules, staffMembers, currentDate}) => {
    const year = currentDate.getFullYear();
    const month = currentDate.getMonth();

    const staffWorkDays = useMemo(() => {
        const summary: Record<string, { name: string; days: number; shifts: Record<string, number>}> = {};
        staffMembers.forEach(staff => {
            summary[staff.id] = { name: staff.name, days: 0, shifts: {} };
        });

        schedules.forEach(schedule => {
            const scheduleDate = new Date(schedule.date);
            if(scheduleDate.getFullYear() === year && scheduleDate.getMonth() === month) {
                if(summary[schedule.staffId]) {
                    summary[schedule.staffId].days++;
                    summary[schedule.staffId].shifts[schedule.shift] = (summary[schedule.staffId].shifts[schedule.shift] || 0) + 1;
                }
            }
        });
        return Object.values(summary).filter(s => s.days > 0).sort((a,b) => b.days - a.days);
    }, [schedules, staffMembers, year, month]);
    
    return (
        <Card title={`${getMonthName(month)} ${year} 员工排班统计`}>
            {staffWorkDays.length === 0 ? (
                 <p className="text-slate-500">本月暂无员工排班。</p>
            ) : (
            <ul className="space-y-2">
                {staffWorkDays.map(staff => (
                    <li key={staff.name} className="p-3 bg-slate-50 rounded-md flex justify-between items-center">
                        <span className="font-medium text-slate-700">{staff.name}</span>
                        <span className="text-sm text-slate-600">
                            {staff.days} 天
                            ({Object.entries(staff.shifts).map(([shift, count]) => `${shift}:${count}`).join(', ')})
                        </span>
                    </li>
                ))}
            </ul>
            )}
        </Card>
    );
};


interface ScheduleFormModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSave: (schedule: Omit<ScheduleEntry, 'id' | 'staffName'> & {id?: string}) => void;
  staffMembers: StaffMember[];
  initialData?: ScheduleEntry | { date: string };
}

const ScheduleFormModal: React.FC<ScheduleFormModalProps> = ({ isOpen, onClose, onSave, staffMembers, initialData }) => {
  const isEditing = initialData && 'id' in initialData;
  const [staffId, setStaffId] = useState(isEditing ? (initialData as ScheduleEntry).staffId || '' : '');
  const [date, setDate] = useState(initialData?.date || formatDate(new Date()));
  const [shift, setShift] = useState<ScheduleEntry['shift']>(isEditing ? (initialData as ScheduleEntry).shift : '早班');
  const [errors, setErrors] = useState<Record<string, string>>({});

  useEffect(() => {
    if (isOpen) {
        if (initialData) {
            const isFullEntry = 'id' in initialData;
            setStaffId(isFullEntry ? (initialData as ScheduleEntry).staffId || '' : '');
            setDate(initialData.date); // date is common, so initialData.date is safe here
            setShift(isFullEntry ? (initialData as ScheduleEntry).shift : '早班');
        } else {
            // Reset for new entry (initialData is undefined)
            setStaffId('');
            setDate(formatDate(new Date()));
            setShift('早班');
        }
        setErrors({});
    }
  }, [initialData, isOpen]);


  const handleSubmit = () => {
    const newErrors: Record<string, string> = {};
    if (!staffId) newErrors.staffId = '请选择员工';
    if (!date) newErrors.date = '请选择日期';
    if (!shift) newErrors.shift = '请选择班次';

    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors);
      return;
    }
    
    onSave({ id: (initialData && 'id' in initialData) ? (initialData as ScheduleEntry).id : undefined, staffId, date, shift });
    onClose();
  };

  const staffOptions = staffMembers.map(s => ({ value: s.id, label: s.name }));
  const shiftOptionsConst = SHIFT_OPTIONS.map(s => ({value: s, label: s}));

  return (
    <Modal isOpen={isOpen} onClose={onClose} title={(initialData && 'id' in initialData) ? "编辑排班" : "新增排班"}
      footer={
        <>
          <Button variant="primary" onClick={handleSubmit}>保存</Button>
          <Button variant="secondary" onClick={onClose} className="ml-2">取消</Button>
        </>
      }>
      <div className="space-y-4">
        <Select label="员工" id="staffId" value={staffId} onChange={e => setStaffId(e.target.value)} options={staffOptions} placeholder="选择员工" error={errors.staffId} />
        <Input label="日期" id="date" type="date" value={date} onChange={e => setDate(e.target.value)} error={errors.date} />
        <Select label="班次" id="shift" value={shift} onChange={e => setShift(e.target.value as ScheduleEntry['shift'])} options={shiftOptionsConst} error={errors.shift} />
      </div>
    </Modal>
  );
};

interface BatchScheduleModalProps {
    isOpen: boolean;
    onClose: () => void;
    onBatchSave: (staffIds: string[], startDate: string, endDate: string, shift: ScheduleEntry['shift']) => void;
    staffMembers: StaffMember[];
}

const BatchScheduleModal: React.FC<BatchScheduleModalProps> = ({isOpen, onClose, onBatchSave, staffMembers}) => {
    const [selectedStaffIds, setSelectedStaffIds] = useState<string[]>([]);
    const [startDate, setStartDate] = useState(formatDate(new Date()));
    const [endDate, setEndDate] = useState(formatDate(new Date()));
    const [shift, setShift] = useState<ScheduleEntry['shift']>('早班');
    const [errors, setErrors] = useState<Record<string, string>>({});

    const handleStaffSelection = (staffId: string) => {
        setSelectedStaffIds(prev => prev.includes(staffId) ? prev.filter(id => id !== staffId) : [...prev, staffId]);
    };
    
    const handleSubmit = () => {
        const newErrors: Record<string, string> = {};
        if (selectedStaffIds.length === 0) newErrors.staff = "至少选择一名员工";
        if (!startDate) newErrors.startDate = "请选择开始日期";
        if (!endDate) newErrors.endDate = "请选择结束日期";
        if (new Date(startDate) > new Date(endDate)) newErrors.dateRange = "结束日期不能早于开始日期";

        if (Object.keys(newErrors).length > 0) {
            setErrors(newErrors);
            return;
        }
        onBatchSave(selectedStaffIds, startDate, endDate, shift);
        onClose();
    };
    
    useEffect(() => {
      if(!isOpen) { // Reset on close
        setSelectedStaffIds([]);
        setStartDate(formatDate(new Date()));
        setEndDate(formatDate(new Date()));
        setShift('早班');
        setErrors({});
      }
    }, [isOpen]);

    const shiftOptionsConst = SHIFT_OPTIONS.map(s => ({value: s, label: s}));

    return (
        <Modal isOpen={isOpen} onClose={onClose} title="批量排班" size="lg"
            footer={
                <>
                  <Button variant="primary" onClick={handleSubmit}>确认批量排班</Button>
                  <Button variant="secondary" onClick={onClose} className="ml-2">取消</Button>
                </>
            }>
            <div className="space-y-4">
                <div>
                    <label className="block text-sm font-medium text-slate-700 mb-1">选择员工 (可多选)</label>
                    <div className="grid grid-cols-2 sm:grid-cols-3 gap-2 max-h-40 overflow-y-auto p-2 border rounded-md">
                        {staffMembers.map(staff => (
                            <label key={staff.id} className={`flex items-center space-x-2 p-2 rounded-md cursor-pointer ${selectedStaffIds.includes(staff.id) ? 'bg-indigo-100 text-indigo-700' : 'hover:bg-slate-50'}`}>
                                <input type="checkbox" checked={selectedStaffIds.includes(staff.id)} onChange={() => handleStaffSelection(staff.id)} className="form-checkbox h-4 w-4 text-indigo-600 border-slate-300 rounded focus:ring-indigo-500" />
                                <span>{staff.name}</span>
                            </label>
                        ))}
                    </div>
                    {errors.staff && <p className="text-red-500 text-sm mt-1">{errors.staff}</p>}
                </div>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <Input label="开始日期" type="date" value={startDate} onChange={e => setStartDate(e.target.value)} error={errors.startDate}/>
                    <Input label="结束日期" type="date" value={endDate} onChange={e => setEndDate(e.target.value)} error={errors.endDate}/>
                </div>
                {errors.dateRange && <p className="text-red-500 text-sm mt-1">{errors.dateRange}</p>}
                <Select label="班次" value={shift} onChange={e => setShift(e.target.value as ScheduleEntry['shift'])} options={shiftOptionsConst} />
            </div>
        </Modal>
    );
};


const SchedulingPage: React.FC = () => {
  const [viewMode, setViewMode] = useState<'calendar' | 'list' | 'summary'>('calendar');
  const [currentDate, setCurrentDate] = useState(new Date());
  const [schedules, setSchedules] = useState<ScheduleEntry[]>(MOCK_SCHEDULE_ENTRIES);
  const [isFormModalOpen, setIsFormModalOpen] = useState(false);
  const [isBatchModalOpen, setIsBatchModalOpen] = useState(false);
  const [editingSchedule, setEditingSchedule] = useState<ScheduleEntry | {date: string} | undefined>(undefined);

  const staffMembers = MOCK_STAFF_MEMBERS;

  const handlePrevMonth = () => setCurrentDate(prev => new Date(prev.getFullYear(), prev.getMonth() - 1, 1));
  const handleNextMonth = () => setCurrentDate(prev => new Date(prev.getFullYear(), prev.getMonth() + 1, 1));
  const handleToday = () => setCurrentDate(new Date());

  const handleAddSchedule = (dateStr?: string) => {
    setEditingSchedule(dateStr ? {date: dateStr} : {date: formatDate(new Date())});
    setIsFormModalOpen(true);
  };
  
  const handleEditSchedule = (schedule: ScheduleEntry) => {
    setEditingSchedule(schedule);
    setIsFormModalOpen(true);
  };

  const handleDeleteSchedule = (scheduleId: string) => {
    if(window.confirm('确定要删除这条排班记录吗？')) {
        setSchedules(prev => prev.filter(s => s.id !== scheduleId));
    }
  };

  const handleSaveSchedule = (scheduleData: Omit<ScheduleEntry, 'id' | 'staffName'> & {id?: string}) => {
    const staffMember = staffMembers.find(s => s.id === scheduleData.staffId);
    if (!staffMember) return; // Should not happen if UI is correct

    if (scheduleData.id) { // Editing existing
      setSchedules(prev => prev.map(s => s.id === scheduleData.id ? { ...s, ...scheduleData, staffName: staffMember.name } : s));
    } else { // Adding new
      const newSchedule: ScheduleEntry = {
        ...scheduleData,
        id: `sch-${Date.now()}-${Math.random().toString(16).slice(2)}`,
        staffName: staffMember.name
      };
      setSchedules(prev => [...prev, newSchedule]);
    }
  };

  const handleBatchSave = (staffIds: string[], startDateStr: string, endDateStr: string, shift: ScheduleEntry['shift']) => {
    const newSchedules: ScheduleEntry[] = [];
    const startDate = new Date(startDateStr);
    const endDate = new Date(endDateStr);

    for (const staffId of staffIds) {
        const staffMember = staffMembers.find(s => s.id === staffId);
        if (!staffMember) continue;

        for (let d = new Date(startDate); d <= endDate; d.setDate(d.getDate() + 1)) {
            const date = formatDate(new Date(d)); // Ensure new Date object for loop
            // Avoid duplicates if a schedule for this staff, date, and shift already exists
            const existing = schedules.find(s => s.staffId === staffId && s.date === date && s.shift === shift);
            if (!existing) {
                 newSchedules.push({
                    id: `sch-${Date.now()}-${Math.random().toString(16).slice(2)}-${staffId}-${date}`,
                    staffId,
                    staffName: staffMember.name,
                    date,
                    shift,
                });
            }
        }
    }
    setSchedules(prev => [...prev, ...newSchedules]);
  };


  return (
    <div>
      <PageHeader title="排班管理" actions={
        <>
            <Button onClick={() => setIsBatchModalOpen(true)} leftIcon={<UsersIcon className="w-4 h-4"/>} className="hidden sm:flex">批量排班</Button>
            <Button onClick={() => handleAddSchedule()} leftIcon={<PlusIcon className="w-4 h-4"/>}>新增排班</Button>
        </>
      }/>
      
      <Card className="mb-6">
        <div className="flex flex-col sm:flex-row justify-between items-center p-4">
          <div className="flex items-center space-x-2 mb-4 sm:mb-0">
            <Button variant="secondary" size="sm" onClick={handlePrevMonth} leftIcon={<ChevronLeftIcon className="w-4 h-4" />}>上个月</Button>
            <Button variant="secondary" size="sm" onClick={handleToday}>今天</Button>
            <Button variant="secondary" size="sm" onClick={handleNextMonth} rightIcon={<ChevronRightIcon className="w-4 h-4" />}>下个月</Button>
            <h2 className="text-xl font-semibold text-slate-700 ml-4 hidden md:block">
              {currentDate.getFullYear()}年 {getMonthName(currentDate.getMonth())}
            </h2>
          </div>
          <div className="flex items-center space-x-1 p-1 bg-slate-200 rounded-lg">
            <Button size="sm" variant={viewMode === 'calendar' ? 'primary' : 'ghost'} onClick={() => setViewMode('calendar')} leftIcon={<CalendarDaysIcon className="w-4 h-4" />}>日历</Button>
            <Button size="sm" variant={viewMode === 'list' ? 'primary' : 'ghost'} onClick={() => setViewMode('list')} leftIcon={<ListIcon className="w-4 h-4" />}>列表</Button>
            <Button size="sm" variant={viewMode === 'summary' ? 'primary' : 'ghost'} onClick={() => setViewMode('summary')} leftIcon={<UsersIcon className="w-4 h-4" />}>统计</Button>
          </div>
        </div>
        <h2 className="text-xl font-semibold text-slate-700 text-center mb-4 block md:hidden">
          {currentDate.getFullYear()}年 {getMonthName(currentDate.getMonth())}
        </h2>
      </Card>

      {viewMode === 'calendar' && <CalendarView schedules={schedules} currentDate={currentDate} onDateClick={(dateStr) => handleAddSchedule(dateStr)} onEditSchedule={handleEditSchedule} />}
      {viewMode === 'list' && <ScheduleListView schedules={schedules.filter(s => {
          const scheduleDate = new Date(s.date);
          return scheduleDate.getFullYear() === currentDate.getFullYear() && scheduleDate.getMonth() === currentDate.getMonth();
      })} onEdit={handleEditSchedule} onDelete={handleDeleteSchedule} />}
      {viewMode === 'summary' && <MonthlyStaffSummary schedules={schedules} staffMembers={staffMembers} currentDate={currentDate} />}
      
      <ScheduleFormModal 
        isOpen={isFormModalOpen} 
        onClose={() => setIsFormModalOpen(false)}
        onSave={handleSaveSchedule}
        staffMembers={staffMembers}
        initialData={editingSchedule}
      />
      <BatchScheduleModal
        isOpen={isBatchModalOpen}
        onClose={() => setIsBatchModalOpen(false)}
        onBatchSave={handleBatchSave}
        staffMembers={staffMembers}
      />
    </div>
  );
};

export default SchedulingPage;
