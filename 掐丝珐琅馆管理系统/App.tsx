
import React, { useState, useCallback } from 'react';
import { Page } from './types';
import { APP_NAME } from './constants';
import { HomeIcon, CalendarDaysIcon, CoffeeIcon, ShoppingCartIcon } from './components/Icons';
import OverviewPage from './pages/OverviewPage';
import SchedulingPage from './pages/SchedulingPage';
import CoffeeShopPage from './pages/CoffeeShopPage';
import POSPage from './pages/POSPage';

const SidebarLink: React.FC<{
  icon: React.ReactNode;
  label: Page;
  isActive: boolean;
  onClick: () => void;
}> = ({ icon, label, isActive, onClick }) => {
  return (
    <button
      onClick={onClick}
      className={`flex items-center w-full px-4 py-3 text-sm font-medium rounded-lg transition-colors duration-150
                  ${isActive ? 'bg-indigo-700 text-white' : 'text-slate-200 hover:bg-indigo-500 hover:text-white'}`}
    >
      <span className="mr-3 h-5 w-5">{icon}</span>
      {label}
    </button>
  );
};

const App: React.FC = () => {
  const [currentPage, setCurrentPage] = useState<Page>(Page.Overview);

  const renderPage = useCallback(() => {
    switch (currentPage) {
      case Page.Overview:
        return <OverviewPage />;
      case Page.Scheduling:
        return <SchedulingPage />;
      case Page.CoffeeShop:
        return <CoffeeShopPage />;
      case Page.POS:
        return <POSPage />;
      default:
        return <OverviewPage />;
    }
  }, [currentPage]);

  const navItems = [
    { label: Page.Overview, icon: <HomeIcon className="h-5 w-5" /> },
    { label: Page.Scheduling, icon: <CalendarDaysIcon className="h-5 w-5" /> },
    { label: Page.CoffeeShop, icon: <CoffeeIcon className="h-5 w-5" /> },
    { label: Page.POS, icon: <ShoppingCartIcon className="h-5 w-5" /> },
  ];

  return (
    <div className="flex h-screen bg-slate-100">
      {/* Sidebar */}
      <aside className="w-64 bg-indigo-800 text-white flex flex-col p-4 space-y-2 shadow-lg">
        <div className="text-2xl font-bold p-4 text-center border-b border-indigo-700 mb-4">
          {APP_NAME}
        </div>
        <nav className="flex-grow">
          {navItems.map((item) => (
            <SidebarLink
              key={item.label}
              icon={item.icon}
              label={item.label}
              isActive={currentPage === item.label}
              onClick={() => setCurrentPage(item.label)}
            />
          ))}
        </nav>
        <div className="text-xs text-indigo-300 p-2 text-center">
            © {new Date().getFullYear()} 珐琅艺术体验
        </div>
      </aside>

      {/* Main Content */}
      <main className="flex-1 p-6 overflow-y-auto">
        {renderPage()}
      </main>
    </div>
  );
};

export default App;
