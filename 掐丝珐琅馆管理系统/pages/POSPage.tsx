
import React, { useState, useMemo } from 'react';
import { Card, Button, Modal, Select, Input } from '../components/ui_elements';
import { POSItem, CartItem, POSSaleTransaction } from '../types';
import { MOCK_POS_ITEMS, PAYMENT_METHODS } from '../constants';
import { ShoppingCartIcon, PlusIcon, TrashIcon, DollarSignIcon, XMarkIcon } from '../components/Icons';

const PageHeader: React.FC<{ title: string; actions?: React.ReactNode }> = ({ title, actions }) => (
  <div className="mb-6 flex justify-between items-center">
    <h1 className="text-3xl font-bold text-slate-800">{title}</h1>
    {actions && <div>{actions}</div>}
  </div>
);

interface ProductCardProps {
  item: POSItem;
  onAddToCart: (item: POSItem) => void;
}

const ProductCard: React.FC<ProductCardProps> = ({ item, onAddToCart }) => {
  return (
    <Card className="flex flex-col">
      <img src={item.imageUrl || `https://picsum.photos/seed/${item.id}/300/200`} alt={item.name} className="w-full h-40 object-cover" />
      <div className="p-4 flex-grow flex flex-col">
        <h4 className="text-md font-semibold text-slate-800 truncate">{item.name}</h4>
        <p className="text-sm text-slate-500">{item.category}</p>
        <p className="text-lg font-bold text-indigo-600 mt-1">¥{item.price.toFixed(2)}</p>
        <p className={`text-xs mt-1 ${item.stock > 10 ? 'text-green-600' : 'text-amber-600'}`}>库存: {item.stock}</p>
        <Button onClick={() => onAddToCart(item)} size="sm" className="mt-auto w-full" leftIcon={<PlusIcon className="w-4 h-4" />} disabled={item.stock === 0}>
          {item.stock > 0 ? '加入购物车' : '已售罄'}
        </Button>
      </div>
    </Card>
  );
};

interface CartPanelProps {
  cartItems: CartItem[];
  onUpdateQuantity: (itemId: string, quantity: number) => void;
  onRemoveItem: (itemId: string) => void;
  onCheckout: () => void;
  totalAmount: number;
}

const CartPanel: React.FC<CartPanelProps> = ({ cartItems, onUpdateQuantity, onRemoveItem, onCheckout, totalAmount }) => {
  return (
    <Card title="购物车" className="sticky top-6">
      {cartItems.length === 0 ? (
        <p className="text-slate-500 text-center py-8">购物车是空的</p>
      ) : (
        <>
          <div className="max-h-96 overflow-y-auto divide-y divide-slate-200 pr-2">
            {cartItems.map(item => (
              <div key={item.id} className="py-3 flex items-center space-x-3">
                <img src={item.imageUrl || `https://picsum.photos/seed/${item.id}/100/100`} alt={item.name} className="h-12 w-12 rounded object-cover" />
                <div className="flex-grow">
                  <p className="text-sm font-medium text-slate-700 truncate">{item.name}</p>
                  <p className="text-xs text-slate-500">¥{item.price.toFixed(2)}</p>
                </div>
                <div className="flex items-center space-x-1">
                  <Input 
                    type="number" 
                    min="1" 
                    max={item.stock}
                    value={item.quantity} 
                    onChange={e => onUpdateQuantity(item.id, parseInt(e.target.value))}
                    className="w-16 text-sm p-1 text-center"
                  />
                </div>
                <Button variant="ghost" size="sm" onClick={() => onRemoveItem(item.id)} className="text-red-500 hover:bg-red-100 p-1">
                  <TrashIcon className="w-4 h-4" />
                </Button>
              </div>
            ))}
          </div>
          <div className="mt-6 pt-4 border-t border-slate-200">
            <div className="flex justify-between items-center text-lg font-semibold">
              <span className="text-slate-700">总计:</span>
              <span className="text-indigo-600">¥{totalAmount.toFixed(2)}</span>
            </div>
            <Button onClick={onCheckout} className="w-full mt-4" size="lg" leftIcon={<DollarSignIcon className="w-5 h-5" />}>
              去结算
            </Button>
          </div>
        </>
      )}
    </Card>
  );
};


interface CheckoutModalProps {
  isOpen: boolean;
  onClose: () => void;
  onConfirmCheckout: (paymentMethod: POSSaleTransaction['paymentMethod']) => void;
  totalAmount: number;
}

const CheckoutModal: React.FC<CheckoutModalProps> = ({ isOpen, onClose, onConfirmCheckout, totalAmount }) => {
  const [paymentMethod, setPaymentMethod] = useState<POSSaleTransaction['paymentMethod']>(PAYMENT_METHODS[0]);
  
  const paymentOptions = PAYMENT_METHODS.map(p => ({ value: p, label: p }));

  const handleConfirm = () => {
    onConfirmCheckout(paymentMethod);
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} title="确认订单并支付"
      footer={
        <>
          <Button variant="primary" onClick={handleConfirm} leftIcon={<DollarSignIcon className="w-5 h-5" />}>确认支付 ¥{totalAmount.toFixed(2)}</Button>
          <Button variant="secondary" onClick={onClose} className="ml-2">取消</Button>
        </>
      }
    >
      <div className="space-y-4">
        <p className="text-sm text-slate-600">请确认订单总金额并选择支付方式。</p>
        <div className="text-3xl font-bold text-center text-indigo-600 py-4 border-y border-slate-200">
          总金额: ¥{totalAmount.toFixed(2)}
        </div>
        <Select 
          label="支付方式" 
          options={paymentOptions} 
          value={paymentMethod}
          onChange={e => setPaymentMethod(e.target.value as POSSaleTransaction['paymentMethod'])}
        />
        {/* Potentially add more payment details here based on method */}
      </div>
    </Modal>
  );
};


const POSPage: React.FC = () => {
  const [products] = useState<POSItem[]>(MOCK_POS_ITEMS);
  const [cartItems, setCartItems] = useState<CartItem[]>([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedCategory, setSelectedCategory] = useState<string>('all');
  const [isCheckoutModalOpen, setIsCheckoutModalOpen] = useState(false);
  const [transactions, setTransactions] = useState<POSSaleTransaction[]>([]); // To store completed sales

  const categories = useMemo(() => ['all', ...Array.from(new Set(products.map(p => p.category)))], [products]);

  const filteredProducts = useMemo(() => {
    return products.filter(product => {
      const matchesSearch = product.name.toLowerCase().includes(searchTerm.toLowerCase());
      const matchesCategory = selectedCategory === 'all' || product.category === selectedCategory;
      return matchesSearch && matchesCategory;
    });
  }, [products, searchTerm, selectedCategory]);

  const handleAddToCart = (item: POSItem) => {
    setCartItems(prevCart => {
      const existingItem = prevCart.find(ci => ci.id === item.id);
      if (existingItem) {
        if (existingItem.quantity < item.stock) {
            return prevCart.map(ci => 
                ci.id === item.id ? { ...ci, quantity: ci.quantity + 1 } : ci
            );
        }
        return prevCart; // Quantity limit reached
      } else {
        if (item.stock > 0) {
            return [...prevCart, { ...item, quantity: 1 }];
        }
        return prevCart; // Out of stock
      }
    });
  };

  const handleUpdateQuantity = (itemId: string, quantity: number) => {
    setCartItems(prevCart => 
      prevCart.map(item => {
        if (item.id === itemId) {
          const newQuantity = Math.max(1, Math.min(quantity, item.stock)); // Ensure quantity is between 1 and stock
          return { ...item, quantity: newQuantity };
        }
        return item;
      }).filter(item => item.quantity > 0) // Remove if quantity becomes 0 (though UI prevents this for now)
    );
  };

  const handleRemoveItem = (itemId: string) => {
    setCartItems(prevCart => prevCart.filter(item => item.id !== itemId));
  };

  const totalAmount = useMemo(() => {
    return cartItems.reduce((sum, item) => sum + item.price * item.quantity, 0);
  }, [cartItems]);

  const handleCheckout = () => {
    if (cartItems.length > 0) {
      setIsCheckoutModalOpen(true);
    }
  };

  const handleConfirmCheckout = (paymentMethod: POSSaleTransaction['paymentMethod']) => {
    // Simulate transaction
    const newTransaction: POSSaleTransaction = {
      id: `txn-${Date.now()}`,
      items: [...cartItems],
      totalAmount,
      paymentMethod,
      timestamp: new Date().toISOString(),
    };
    setTransactions(prev => [newTransaction, ...prev]);
    
    // Update stock (important!)
    // For this demo, we'll update the MOCK_POS_ITEMS. In a real app, this would be an API call.
    cartItems.forEach(cartItem => {
        const productIndex = products.findIndex(p => p.id === cartItem.id);
        if (productIndex !== -1) {
            products[productIndex].stock -= cartItem.quantity;
        }
    });

    setCartItems([]); // Clear cart
    setIsCheckoutModalOpen(false);
    alert(`订单已完成! 支付方式: ${paymentMethod}. 总金额: ¥${totalAmount.toFixed(2)}`);
  };

  return (
    <div>
      <PageHeader title="POS 销售系统" />
      <div className="flex flex-col lg:flex-row gap-6">
        {/* Products Grid */}
        <div className="lg:w-2/3">
          <Card className="mb-6">
            <div className="p-4 flex flex-col sm:flex-row gap-4">
              <Input 
                type="text" 
                placeholder="搜索商品..." 
                value={searchTerm}
                onChange={e => setSearchTerm(e.target.value)}
                className="flex-grow"
              />
              <Select 
                options={categories.map(c => ({ value: c, label: c === 'all' ? '全部分类' : c }))}
                value={selectedCategory}
                onChange={e => setSelectedCategory(e.target.value)}
                className="sm:w-48"
              />
            </div>
          </Card>
          
          {filteredProducts.length > 0 ? (
            <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-3 gap-6">
              {filteredProducts.map(item => (
                <ProductCard key={item.id} item={item} onAddToCart={handleAddToCart} />
              ))}
            </div>
          ) : (
            <Card>
              <p className="text-center text-slate-500 py-10">未找到商品。请尝试调整搜索条件或分类。</p>
            </Card>
          )}
        </div>

        {/* Cart Panel */}
        <div className="lg:w-1/3">
          <CartPanel 
            cartItems={cartItems}
            onUpdateQuantity={handleUpdateQuantity}
            onRemoveItem={handleRemoveItem}
            onCheckout={handleCheckout}
            totalAmount={totalAmount}
          />
        </div>
      </div>
      
      <CheckoutModal 
        isOpen={isCheckoutModalOpen}
        onClose={() => setIsCheckoutModalOpen(false)}
        onConfirmCheckout={handleConfirmCheckout}
        totalAmount={totalAmount}
      />

      {/* Optionally display recent transactions */}
      {transactions.length > 0 && (
          <Card title="最近交易记录" className="mt-8">
              <ul className="divide-y divide-slate-200 max-h-96 overflow-y-auto">
                  {transactions.slice(0, 5).map(txn => (
                      <li key={txn.id} className="py-3">
                          <p className="text-sm font-medium text-slate-700">订单号: {txn.id.slice(-6)}</p>
                          <p className="text-xs text-slate-500">时间: {new Date(txn.timestamp).toLocaleString()}</p>
                          <p className="text-xs text-slate-500">金额: ¥{txn.totalAmount.toFixed(2)} ({txn.paymentMethod})</p>
                          <p className="text-xs text-slate-500">商品数: {txn.items.reduce((acc, item) => acc + item.quantity, 0)}</p>
                      </li>
                  ))}
              </ul>
          </Card>
      )}
    </div>
  );
};

export default POSPage;
