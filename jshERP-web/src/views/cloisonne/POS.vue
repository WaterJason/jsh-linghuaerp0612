<template>
  <div class="pos-container">
    <a-row :gutter="16">
      <!-- 商品选择区域 -->
      <a-col :span="16">
        <a-card title="商品选择" :bordered="false">
          <div class="product-search">
            <a-input-search
              v-model="searchKeyword"
              placeholder="搜索商品名称、条码"
              @search="searchProducts"
              style="margin-bottom: 16px"
            />
          </div>
          
          <div class="product-categories">
            <a-button
              v-for="category in categories"
              :key="category.id"
              :type="selectedCategory === category.id ? 'primary' : 'default'"
              @click="selectCategory(category.id)"
              style="margin-right: 8px; margin-bottom: 8px"
            >
              {{ category.name }}
            </a-button>
          </div>

          <div class="product-grid">
            <a-row :gutter="[16, 16]">
              <a-col :span="6" v-for="product in filteredProducts" :key="product.id">
                <a-card
                  hoverable
                  @click="addToCart(product)"
                  class="product-card"
                >
                  <img
                    slot="cover"
                    :src="product.image || '/img/no-image.png'"
                    :alt="product.name"
                    style="height: 120px; object-fit: cover"
                  />
                  <a-card-meta
                    :title="product.name"
                    :description="`¥${product.price}`"
                  />
                </a-card>
              </a-col>
            </a-row>
          </div>
        </a-card>
      </a-col>

      <!-- 购物车和结算区域 -->
      <a-col :span="8">
        <a-card title="购物车" :bordered="false">
          <div class="cart-items">
            <div
              v-for="item in cartItems"
              :key="item.id"
              class="cart-item"
            >
              <div class="item-info">
                <div class="item-name">{{ item.name }}</div>
                <div class="item-price">¥{{ item.price }}</div>
              </div>
              <div class="item-controls">
                <a-input-number
                  v-model="item.quantity"
                  :min="1"
                  size="small"
                  @change="updateQuantity(item)"
                />
                <a-button
                  type="danger"
                  size="small"
                  icon="delete"
                  @click="removeFromCart(item)"
                  style="margin-left: 8px"
                />
              </div>
            </div>
          </div>

          <a-divider />

          <div class="cart-summary">
            <div class="summary-row">
              <span>商品数量：</span>
              <span>{{ totalQuantity }}</span>
            </div>
            <div class="summary-row">
              <span>小计：</span>
              <span>¥{{ subtotal.toFixed(2) }}</span>
            </div>
            <div class="summary-row">
              <span>折扣：</span>
              <span>¥{{ discount.toFixed(2) }}</span>
            </div>
            <div class="summary-row total">
              <span>总计：</span>
              <span>¥{{ total.toFixed(2) }}</span>
            </div>
          </div>

          <div class="payment-section">
            <a-select
              v-model="paymentMethod"
              placeholder="选择支付方式"
              style="width: 100%; margin-bottom: 16px"
            >
              <a-select-option value="cash">现金</a-select-option>
              <a-select-option value="card">银行卡</a-select-option>
              <a-select-option value="wechat">微信支付</a-select-option>
              <a-select-option value="alipay">支付宝</a-select-option>
            </a-select>

            <a-button
              type="primary"
              size="large"
              block
              @click="checkout"
              :disabled="cartItems.length === 0"
            >
              结算 (¥{{ total.toFixed(2) }})
            </a-button>

            <a-button
              type="default"
              size="large"
              block
              @click="clearCart"
              style="margin-top: 8px"
              :disabled="cartItems.length === 0"
            >
              清空购物车
            </a-button>
          </div>
        </a-card>
      </a-col>
    </a-row>

    <!-- 结算确认弹窗 -->
    <a-modal
      title="确认结算"
      :visible="checkoutVisible"
      @ok="confirmCheckout"
      @cancel="checkoutVisible = false"
      okText="确认支付"
      cancelText="取消"
    >
      <div class="checkout-details">
        <p><strong>支付方式：</strong>{{ paymentMethodText }}</p>
        <p><strong>商品数量：</strong>{{ totalQuantity }}</p>
        <p><strong>应付金额：</strong>¥{{ total.toFixed(2) }}</p>
        
        <a-input
          v-if="paymentMethod === 'cash'"
          v-model="receivedAmount"
          placeholder="实收金额"
          style="margin-top: 16px"
        />
        <p v-if="paymentMethod === 'cash' && receivedAmount">
          <strong>找零：</strong>¥{{ (receivedAmount - total).toFixed(2) }}
        </p>
      </div>
    </a-modal>
  </div>
</template>

<script>
export default {
  name: 'POSSystem',
  data() {
    return {
      searchKeyword: '',
      selectedCategory: null,
      cartItems: [],
      paymentMethod: 'cash',
      checkoutVisible: false,
      receivedAmount: 0,
      categories: [
        { id: null, name: '全部' },
        { id: 1, name: '咖啡' },
        { id: 2, name: '茶饮' },
        { id: 3, name: '甜品' },
        { id: 4, name: '轻食' }
      ],
      products: [
        { id: 1, name: '美式咖啡', price: 25, categoryId: 1, image: '' },
        { id: 2, name: '拿铁', price: 30, categoryId: 1, image: '' },
        { id: 3, name: '卡布奇诺', price: 28, categoryId: 1, image: '' },
        { id: 4, name: '绿茶', price: 20, categoryId: 2, image: '' },
        { id: 5, name: '红茶', price: 20, categoryId: 2, image: '' },
        { id: 6, name: '芝士蛋糕', price: 35, categoryId: 3, image: '' },
        { id: 7, name: '提拉米苏', price: 40, categoryId: 3, image: '' },
        { id: 8, name: '三明治', price: 25, categoryId: 4, image: '' }
      ]
    }
  },
  computed: {
    filteredProducts() {
      let products = this.products;
      
      if (this.selectedCategory) {
        products = products.filter(p => p.categoryId === this.selectedCategory);
      }
      
      if (this.searchKeyword) {
        products = products.filter(p => 
          p.name.toLowerCase().includes(this.searchKeyword.toLowerCase())
        );
      }
      
      return products;
    },
    totalQuantity() {
      return this.cartItems.reduce((sum, item) => sum + item.quantity, 0);
    },
    subtotal() {
      return this.cartItems.reduce((sum, item) => sum + (item.price * item.quantity), 0);
    },
    discount() {
      return 0; // 可以根据业务需求计算折扣
    },
    total() {
      return this.subtotal - this.discount;
    },
    paymentMethodText() {
      const methods = {
        cash: '现金',
        card: '银行卡',
        wechat: '微信支付',
        alipay: '支付宝'
      };
      return methods[this.paymentMethod] || '';
    }
  },
  methods: {
    selectCategory(categoryId) {
      this.selectedCategory = categoryId;
    },
    searchProducts() {
      // 搜索逻辑已在computed中实现
    },
    addToCart(product) {
      const existingItem = this.cartItems.find(item => item.id === product.id);
      if (existingItem) {
        existingItem.quantity += 1;
      } else {
        this.cartItems.push({
          ...product,
          quantity: 1
        });
      }
    },
    updateQuantity(item) {
      if (item.quantity <= 0) {
        this.removeFromCart(item);
      }
    },
    removeFromCart(item) {
      const index = this.cartItems.findIndex(i => i.id === item.id);
      if (index > -1) {
        this.cartItems.splice(index, 1);
      }
    },
    clearCart() {
      this.cartItems = [];
    },
    checkout() {
      if (this.cartItems.length === 0) {
        this.$message.warning('购物车为空');
        return;
      }
      this.receivedAmount = this.total;
      this.checkoutVisible = true;
    },
    confirmCheckout() {
      if (this.paymentMethod === 'cash' && this.receivedAmount < this.total) {
        this.$message.error('实收金额不足');
        return;
      }
      
      // 这里可以调用后端API保存订单
      this.$message.success('支付成功！');
      this.clearCart();
      this.checkoutVisible = false;
      this.receivedAmount = 0;
    }
  }
}
</script>

<style scoped>
.pos-container {
  padding: 24px;
  background: #f0f2f5;
  min-height: calc(100vh - 64px);
}

.product-card {
  cursor: pointer;
  transition: all 0.3s;
}

.product-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}

.cart-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
}

.item-info {
  flex: 1;
}

.item-name {
  font-weight: 500;
}

.item-price {
  color: #666;
  font-size: 12px;
}

.item-controls {
  display: flex;
  align-items: center;
}

.cart-summary {
  margin: 16px 0;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.summary-row.total {
  font-weight: bold;
  font-size: 16px;
  color: #f5222d;
}

.payment-section {
  margin-top: 16px;
}

.checkout-details p {
  margin-bottom: 8px;
}
</style>
