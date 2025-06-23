# 掐丝珐琅馆POS销售页面详细设计

## 📋 设计概述

POS销售页面是掐丝珐琅馆综合管理模块的核心销售功能，采用现代化的电商界面设计，支持商品选择、购物车管理、支付处理等完整的销售流程，为线下零售提供数字化解决方案。

---

## 🎨 整体布局设计

### 页面结构 (桌面端双栏布局)
```
┌─────────────────────────────────────────────────────────────┐
│ 页面标题: POS 销售系统                                       │
├─────────────────┬───────────────────────────────────────────┤
│   商品选择区     │              购物车区域                    │
│                │                                           │
│ ┌─────────────┐ │ ┌───────────────────────────────────────┐ │
│ │ 搜索+筛选   │ │ │ 购物车标题 (3)        [清空] [结算]   │ │
│ └─────────────┘ │ ├───────────────────────────────────────┤ │
│                │ │ 商品1  x2  ¥50.00    [+] [-] [删除]   │ │
│ ┌─────────────┐ │ │ 商品2  x1  ¥30.00    [+] [-] [删除]   │ │
│ │   商品网格   │ │ │ 商品3  x3  ¥90.00    [+] [-] [删除]   │ │
│ │             │ │ ├───────────────────────────────────────┤ │
│ │ [商品卡片]   │ │ │ 小计:                    ¥170.00     │ │
│ │ [商品卡片]   │ │ │ 优惠:                     ¥0.00      │ │
│ │ [商品卡片]   │ │ │ 总计:                    ¥170.00     │ │
│ │ [商品卡片]   │ │ ├───────────────────────────────────────┤ │
│ └─────────────┘ │ │ [选择支付方式] [确认结算]              │ │
│                │ └───────────────────────────────────────┘ │
└─────────────────┴───────────────────────────────────────────┘
```

### Vue.js主组件实现
```vue
<template>
  <div class="pos-sales">
    <!-- 页面头部 -->
    <pos-header 
      title="POS 销售系统"
      :cart-count="cartItemCount"
      @toggle-cart="handleToggleCart" />
    
    <!-- 主要内容区域 -->
    <div class="pos-content">
      <a-row :gutter="24" v-if="!isMobile">
        <!-- 桌面端：双栏布局 -->
        <!-- 商品选择区域 -->
        <a-col :span="16" class="products-section">
          <product-selection
            :products="products"
            :categories="categories"
            :loading="productsLoading"
            @add-to-cart="handleAddToCart"
            @search="handleProductSearch"
            @filter="handleProductFilter" />
        </a-col>
        
        <!-- 购物车区域 -->
        <a-col :span="8" class="cart-section">
          <shopping-cart
            :items="cartItems"
            :total="cartTotal"
            :loading="cartLoading"
            @update-quantity="handleUpdateQuantity"
            @remove-item="handleRemoveItem"
            @clear-cart="handleClearCart"
            @checkout="handleCheckout" />
        </a-col>
      </a-row>
      
      <!-- 移动端：Tab切换布局 -->
      <div v-else class="mobile-layout">
        <a-tabs v-model="activeTab" animated>
          <a-tab-pane key="products" tab="商品选择">
            <product-selection
              :products="products"
              :categories="categories"
              :loading="productsLoading"
              @add-to-cart="handleAddToCart"
              @search="handleProductSearch"
              @filter="handleProductFilter" />
          </a-tab-pane>
          
          <a-tab-pane key="cart" :tab="`购物车(${cartItemCount})`">
            <shopping-cart
              :items="cartItems"
              :total="cartTotal"
              :loading="cartLoading"
              @update-quantity="handleUpdateQuantity"
              @remove-item="handleRemoveItem"
              @clear-cart="handleClearCart"
              @checkout="handleCheckout" />
          </a-tab-pane>
        </a-tabs>
      </div>
    </div>
    
    <!-- 支付弹窗 -->
    <payment-modal
      ref="paymentModal"
      :visible="paymentVisible"
      :order-data="currentOrder"
      :payment-methods="paymentMethods"
      @confirm="handlePaymentConfirm"
      @cancel="handlePaymentCancel" />
      
    <!-- 订单成功弹窗 -->
    <order-success-modal
      ref="successModal"
      :visible="successVisible"
      :order="completedOrder"
      @print="handlePrintReceipt"
      @new-order="handleNewOrder"
      @close="handleSuccessClose" />
  </div>
</template>

<script>
import { mapState, mapActions } from 'vuex'
import { responsiveMixin } from '@/mixins/responsiveMixin'
import PosHeader from './components/PosHeader.vue'
import ProductSelection from './components/ProductSelection.vue'
import ShoppingCart from './components/ShoppingCart.vue'
import PaymentModal from './components/PaymentModal.vue'
import OrderSuccessModal from './components/OrderSuccessModal.vue'

export default {
  name: 'POSSales',
  
  components: {
    PosHeader,
    ProductSelection,
    ShoppingCart,
    PaymentModal,
    OrderSuccessModal
  },
  
  mixins: [responsiveMixin],
  
  data() {
    return {
      activeTab: 'products',
      productsLoading: false,
      cartLoading: false,
      paymentVisible: false,
      successVisible: false,
      currentOrder: null,
      completedOrder: null
    }
  },
  
  computed: {
    ...mapState('cloisonne', [
      'products',
      'categories',
      'cartItems',
      'paymentMethods'
    ]),
    
    cartItemCount() {
      return this.cartItems.reduce((sum, item) => sum + item.quantity, 0)
    },
    
    cartTotal() {
      return this.cartItems.reduce((sum, item) => sum + (item.price * item.quantity), 0)
    }
  },
  
  async created() {
    await this.loadPOSData()
  },
  
  methods: {
    ...mapActions('cloisonne', [
      'fetchProducts',
      'fetchCategories',
      'addToCart',
      'updateCartItem',
      'removeFromCart',
      'clearCart',
      'createOrder',
      'fetchPaymentMethods'
    ]),
    
    async loadPOSData() {
      this.productsLoading = true
      try {
        await Promise.all([
          this.fetchProducts(),
          this.fetchCategories(),
          this.fetchPaymentMethods()
        ])
      } catch (error) {
        this.$message.error('加载POS数据失败')
      } finally {
        this.productsLoading = false
      }
    },
    
    // 商品操作
    handleAddToCart(product) {
      // 检查库存
      if (product.stock <= 0) {
        this.$message.warning('商品库存不足')
        return
      }
      
      // 检查购物车中是否已存在
      const existingItem = this.cartItems.find(item => item.productId === product.id)
      if (existingItem && existingItem.quantity >= product.stock) {
        this.$message.warning('商品库存不足')
        return
      }
      
      this.addToCart({
        productId: product.id,
        name: product.name,
        price: product.price,
        image: product.image,
        quantity: 1,
        maxStock: product.stock
      })
      
      this.$message.success(`${product.name} 已添加到购物车`)
      
      // 移动端自动切换到购物车
      if (this.isMobile) {
        this.activeTab = 'cart'
      }
    },
    
    handleProductSearch(keyword) {
      this.fetchProducts({ search: keyword })
    },
    
    handleProductFilter(filters) {
      this.fetchProducts(filters)
    },
    
    // 购物车操作
    handleUpdateQuantity(item, quantity) {
      if (quantity <= 0) {
        this.handleRemoveItem(item)
        return
      }
      
      if (quantity > item.maxStock) {
        this.$message.warning('超出库存数量')
        return
      }
      
      this.updateCartItem({
        productId: item.productId,
        quantity
      })
    },
    
    handleRemoveItem(item) {
      this.removeFromCart(item.productId)
      this.$message.success('商品已从购物车移除')
    },
    
    handleClearCart() {
      this.$confirm({
        title: '确认清空',
        content: '确定要清空购物车吗？',
        onOk: () => {
          this.clearCart()
          this.$message.success('购物车已清空')
        }
      })
    },
    
    handleToggleCart() {
      if (this.isMobile) {
        this.activeTab = this.activeTab === 'cart' ? 'products' : 'cart'
      }
    },
    
    // 结算流程
    handleCheckout() {
      if (this.cartItems.length === 0) {
        this.$message.warning('购物车为空')
        return
      }
      
      // 创建订单数据
      this.currentOrder = {
        items: [...this.cartItems],
        subtotal: this.cartTotal,
        discount: 0,
        total: this.cartTotal,
        createTime: new Date().toISOString()
      }
      
      this.paymentVisible = true
    },
    
    async handlePaymentConfirm(paymentData) {
      this.cartLoading = true
      try {
        const orderData = {
          ...this.currentOrder,
          ...paymentData,
          cashierId: this.$store.getters['user/userInfo'].id
        }
        
        const response = await this.createOrder(orderData)
        this.completedOrder = response.data
        
        // 清空购物车
        this.clearCart()
        
        // 显示成功页面
        this.paymentVisible = false
        this.successVisible = true
        
        this.$message.success('订单创建成功')
        
      } catch (error) {
        this.$message.error('订单创建失败')
      } finally {
        this.cartLoading = false
      }
    },
    
    handlePaymentCancel() {
      this.paymentVisible = false
      this.currentOrder = null
    },
    
    // 订单完成处理
    handlePrintReceipt() {
      // 打印小票功能
      this.$refs.successModal.printReceipt()
    },
    
    handleNewOrder() {
      this.successVisible = false
      this.completedOrder = null
      
      // 移动端切换到商品选择
      if (this.isMobile) {
        this.activeTab = 'products'
      }
    },
    
    handleSuccessClose() {
      this.successVisible = false
      this.completedOrder = null
    }
  }
}
</script>

<style lang="less" scoped>
.pos-sales {
  height: 100vh;
  display: flex;
  flex-direction: column;
  
  .pos-content {
    flex: 1;
    padding: 24px;
    overflow: hidden;
    
    .products-section,
    .cart-section {
      height: 100%;
    }
    
    .mobile-layout {
      height: 100%;
      
      .ant-tabs {
        height: 100%;
        
        .ant-tabs-content {
          height: calc(100% - 44px);
          overflow-y: auto;
        }
      }
    }
  }
}
</style>
```

---

## 🛍️ 商品选择组件

### ProductSelection 商品选择组件
```vue
<template>
  <div class="product-selection">
    <!-- 搜索和筛选区域 -->
    <a-card class="search-filters" :bordered="false">
      <a-row :gutter="16">
        <a-col :xs="24" :sm="12" :md="8">
          <a-input-search
            v-model="searchKeyword"
            placeholder="搜索商品名称、编码..."
            @search="handleSearch"
            @pressEnter="handleSearch"
            enter-button />
        </a-col>
        
        <a-col :xs="24" :sm="12" :md="8">
          <a-select
            v-model="selectedCategory"
            placeholder="选择分类"
            style="width: 100%"
            allow-clear
            @change="handleCategoryChange">
            <a-select-option value="">全部分类</a-select-option>
            <a-select-option 
              v-for="category in categories"
              :key="category.id"
              :value="category.id">
              {{ category.name }}
            </a-select-option>
          </a-select>
        </a-col>
        
        <a-col :xs="24" :sm="24" :md="8">
          <a-space>
            <a-button @click="handleReset">
              <a-icon type="reload" />
              重置
            </a-button>
            <a-button @click="handleSort">
              <a-icon type="sort-ascending" />
              排序
            </a-button>
          </a-space>
        </a-col>
      </a-row>
    </a-card>
    
    <!-- 商品网格 -->
    <div class="products-grid">
      <a-spin :spinning="loading">
        <a-empty 
          v-if="filteredProducts.length === 0 && !loading"
          description="暂无商品"
          :image="emptyImage" />
        
        <a-row v-else :gutter="[16, 16]">
          <a-col 
            v-for="product in filteredProducts"
            :key="product.id"
            :xs="12" :sm="8" :md="6" :lg="4" :xl="4">
            <product-card 
              :product="product"
              @add-to-cart="handleAddToCart"
              @view-detail="handleViewDetail" />
          </a-col>
        </a-row>
      </a-spin>
    </div>
    
    <!-- 分页 -->
    <div class="pagination-wrapper" v-if="filteredProducts.length > 0">
      <a-pagination
        v-model="pagination.current"
        :total="pagination.total"
        :page-size="pagination.pageSize"
        :show-size-changer="true"
        :show-quick-jumper="true"
        :show-total="(total, range) => `第 ${range[0]}-${range[1]} 条，共 ${total} 条`"
        @change="handlePageChange"
        @showSizeChange="handlePageSizeChange" />
    </div>
  </div>
</template>

<script>
import ProductCard from './ProductCard.vue'

export default {
  name: 'ProductSelection',
  
  components: {
    ProductCard
  },
  
  props: {
    products: { type: Array, default: () => [] },
    categories: { type: Array, default: () => [] },
    loading: { type: Boolean, default: false }
  },
  
  data() {
    return {
      searchKeyword: '',
      selectedCategory: '',
      sortBy: 'name',
      sortOrder: 'asc',
      
      pagination: {
        current: 1,
        pageSize: 20,
        total: 0
      },
      
      emptyImage: require('@/assets/images/empty-products.svg')
    }
  },
  
  computed: {
    filteredProducts() {
      let filtered = [...this.products]
      
      // 搜索过滤
      if (this.searchKeyword) {
        const keyword = this.searchKeyword.toLowerCase()
        filtered = filtered.filter(product => 
          product.name.toLowerCase().includes(keyword) ||
          product.code?.toLowerCase().includes(keyword)
        )
      }
      
      // 分类过滤
      if (this.selectedCategory) {
        filtered = filtered.filter(product => 
          product.categoryId === this.selectedCategory
        )
      }
      
      // 排序
      filtered.sort((a, b) => {
        let aValue = a[this.sortBy]
        let bValue = b[this.sortBy]
        
        if (typeof aValue === 'string') {
          aValue = aValue.toLowerCase()
          bValue = bValue.toLowerCase()
        }
        
        if (this.sortOrder === 'asc') {
          return aValue > bValue ? 1 : -1
        } else {
          return aValue < bValue ? 1 : -1
        }
      })
      
      // 更新分页总数
      this.pagination.total = filtered.length
      
      // 分页
      const start = (this.pagination.current - 1) * this.pagination.pageSize
      const end = start + this.pagination.pageSize
      
      return filtered.slice(start, end)
    }
  },
  
  methods: {
    handleSearch() {
      this.pagination.current = 1
      this.$emit('search', this.searchKeyword)
    },
    
    handleCategoryChange() {
      this.pagination.current = 1
      this.$emit('filter', {
        category: this.selectedCategory,
        search: this.searchKeyword
      })
    },
    
    handleReset() {
      this.searchKeyword = ''
      this.selectedCategory = ''
      this.pagination.current = 1
      this.$emit('filter', {})
    },
    
    handleSort() {
      // 切换排序方式
      if (this.sortBy === 'name') {
        this.sortBy = 'price'
      } else if (this.sortBy === 'price') {
        this.sortBy = 'stock'
      } else {
        this.sortBy = 'name'
      }
      
      this.sortOrder = this.sortOrder === 'asc' ? 'desc' : 'asc'
    },
    
    handleAddToCart(product) {
      this.$emit('add-to-cart', product)
    },
    
    handleViewDetail(product) {
      this.$emit('view-detail', product)
    },
    
    handlePageChange(page) {
      this.pagination.current = page
    },
    
    handlePageSizeChange(current, size) {
      this.pagination.current = 1
      this.pagination.pageSize = size
    }
  }
}
</script>

<style lang="less" scoped>
.product-selection {
  height: 100%;
  display: flex;
  flex-direction: column;
  
  .search-filters {
    margin-bottom: 16px;
    flex-shrink: 0;
  }
  
  .products-grid {
    flex: 1;
    overflow-y: auto;
    padding: 0 4px;
  }
  
  .pagination-wrapper {
    margin-top: 16px;
    text-align: center;
    flex-shrink: 0;
  }
}
</style>
```

---

## 🛒 商品卡片组件

### ProductCard 商品卡片组件
```vue
<template>
  <a-card
    :hoverable="true"
    class="product-card"
    :bordered="false"
    @click="handleCardClick">

    <!-- 商品图片 -->
    <template #cover>
      <div class="product-image">
        <img
          :src="product.image || defaultImage"
          :alt="product.name"
          @error="handleImageError" />

        <!-- 库存状态覆盖层 -->
        <div v-if="product.stock <= 0" class="stock-overlay">
          <span class="out-of-stock">缺货</span>
        </div>

        <!-- 快速添加按钮 -->
        <div class="quick-add-overlay">
          <a-button
            type="primary"
            shape="circle"
            icon="plus"
            size="large"
            :disabled="product.stock <= 0"
            @click.stop="handleQuickAdd" />
        </div>
      </div>
    </template>

    <!-- 商品信息 -->
    <a-card-meta>
      <template #title>
        <div class="product-title">
          <span class="product-name">{{ product.name }}</span>
          <a-tag
            v-if="product.isHot"
            color="red"
            size="small"
            class="hot-tag">
            热销
          </a-tag>
        </div>
      </template>

      <template #description>
        <div class="product-info">
          <!-- 商品编码 -->
          <div class="product-code" v-if="product.code">
            编码: {{ product.code }}
          </div>

          <!-- 价格信息 -->
          <div class="product-price">
            <span class="current-price">¥{{ product.price.toFixed(2) }}</span>
            <span
              v-if="product.originalPrice && product.originalPrice > product.price"
              class="original-price">
              ¥{{ product.originalPrice.toFixed(2) }}
            </span>
          </div>

          <!-- 库存信息 -->
          <div class="product-stock">
            <span :class="['stock-text', getStockClass(product.stock)]">
              库存: {{ product.stock }}
            </span>
            <div class="stock-bar">
              <div
                class="stock-progress"
                :style="{
                  width: `${getStockPercentage(product.stock, product.maxStock)}%`,
                  backgroundColor: getStockColor(product.stock)
                }"></div>
            </div>
          </div>

          <!-- 分类标签 -->
          <div class="product-category" v-if="product.categoryName">
            <a-tag size="small">{{ product.categoryName }}</a-tag>
          </div>
        </div>
      </template>
    </a-card-meta>

    <!-- 操作按钮 -->
    <template #actions>
      <a-tooltip title="查看详情">
        <a-icon type="eye" @click.stop="handleViewDetail" />
      </a-tooltip>

      <a-tooltip title="添加到购物车">
        <a-icon
          type="shopping-cart"
          :class="{ disabled: product.stock <= 0 }"
          @click.stop="handleAddToCart" />
      </a-tooltip>

      <a-tooltip title="收藏商品">
        <a-icon
          :type="product.isFavorite ? 'heart' : 'heart-o'"
          :class="{ favorite: product.isFavorite }"
          @click.stop="handleToggleFavorite" />
      </a-tooltip>
    </template>
  </a-card>
</template>

<script>
export default {
  name: 'ProductCard',

  props: {
    product: { type: Object, required: true }
  },

  data() {
    return {
      defaultImage: require('@/assets/images/default-product.png')
    }
  },

  methods: {
    handleCardClick() {
      if (this.product.stock > 0) {
        this.handleAddToCart()
      } else {
        this.handleViewDetail()
      }
    },

    handleQuickAdd() {
      this.handleAddToCart()
    },

    handleAddToCart() {
      if (this.product.stock <= 0) {
        this.$message.warning('商品库存不足')
        return
      }

      this.$emit('add-to-cart', this.product)
    },

    handleViewDetail() {
      this.$emit('view-detail', this.product)
    },

    handleToggleFavorite() {
      this.$emit('toggle-favorite', this.product)
    },

    handleImageError(event) {
      event.target.src = this.defaultImage
    },

    getStockClass(stock) {
      if (stock <= 0) return 'out-of-stock'
      if (stock <= 10) return 'low-stock'
      return 'normal-stock'
    },

    getStockColor(stock) {
      if (stock <= 0) return '#ff4d4f'
      if (stock <= 10) return '#fa8c16'
      return '#52c41a'
    },

    getStockPercentage(current, max) {
      if (!max || max <= 0) return 0
      return Math.min((current / max) * 100, 100)
    }
  }
}
</script>

<style lang="less" scoped>
.product-card {
  position: relative;
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);

    .quick-add-overlay {
      opacity: 1;
    }
  }

  .product-image {
    position: relative;
    overflow: hidden;
    height: 200px;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      transition: transform 0.3s ease;
    }

    &:hover img {
      transform: scale(1.05);
    }

    .stock-overlay {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: rgba(0, 0, 0, 0.6);
      display: flex;
      align-items: center;
      justify-content: center;

      .out-of-stock {
        color: white;
        font-size: 18px;
        font-weight: bold;
      }
    }

    .quick-add-overlay {
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      opacity: 0;
      transition: opacity 0.3s ease;
    }
  }

  .product-title {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 8px;

    .product-name {
      font-size: 14px;
      font-weight: 600;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      flex: 1;
    }

    .hot-tag {
      margin-left: 8px;
      flex-shrink: 0;
    }
  }

  .product-info {
    .product-code {
      font-size: 12px;
      color: #999;
      margin-bottom: 6px;
    }

    .product-price {
      margin-bottom: 8px;

      .current-price {
        font-size: 16px;
        font-weight: bold;
        color: #ff4d4f;
      }

      .original-price {
        font-size: 12px;
        color: #999;
        text-decoration: line-through;
        margin-left: 8px;
      }
    }

    .product-stock {
      margin-bottom: 8px;

      .stock-text {
        font-size: 12px;

        &.normal-stock {
          color: #52c41a;
        }

        &.low-stock {
          color: #fa8c16;
        }

        &.out-of-stock {
          color: #ff4d4f;
        }
      }

      .stock-bar {
        height: 4px;
        background: #f0f0f0;
        border-radius: 2px;
        margin-top: 4px;
        overflow: hidden;

        .stock-progress {
          height: 100%;
          transition: width 0.3s ease;
        }
      }
    }

    .product-category {
      .ant-tag {
        margin: 0;
      }
    }
  }

  .ant-card-actions {
    .anticon {
      font-size: 16px;
      transition: color 0.3s ease;

      &:hover {
        color: #3B82F6;
      }

      &.disabled {
        color: #d9d9d9;
        cursor: not-allowed;

        &:hover {
          color: #d9d9d9;
        }
      }

      &.favorite {
        color: #ff4d4f;
      }
    }
  }
}
</style>
```

---

## 🛒 购物车组件

### ShoppingCart 购物车组件
```vue
<template>
  <a-card
    class="shopping-cart"
    :bordered="false">

    <!-- 购物车头部 -->
    <template #title>
      <div class="cart-header">
        <span>
          <a-icon type="shopping-cart" />
          购物车 ({{ items.length }})
        </span>
        <a-button
          type="link"
          size="small"
          :disabled="items.length === 0"
          @click="handleClearCart">
          清空
        </a-button>
      </div>
    </template>

    <!-- 购物车内容 -->
    <div class="cart-content">
      <!-- 空购物车状态 -->
      <a-empty
        v-if="items.length === 0"
        description="购物车为空"
        :image="emptyImage">
        <a-button type="primary" @click="handleContinueShopping">
          继续购物
        </a-button>
      </a-empty>

      <!-- 购物车商品列表 -->
      <div v-else class="cart-items">
        <div
          v-for="item in items"
          :key="item.productId"
          class="cart-item">

          <!-- 商品图片 -->
          <div class="item-image">
            <img :src="item.image || defaultImage" :alt="item.name" />
          </div>

          <!-- 商品信息 -->
          <div class="item-info">
            <div class="item-name">{{ item.name }}</div>
            <div class="item-price">¥{{ item.price.toFixed(2) }}</div>
            <div class="item-stock" v-if="item.maxStock">
              库存: {{ item.maxStock }}
            </div>
          </div>

          <!-- 数量控制 -->
          <div class="item-controls">
            <a-input-number
              :value="item.quantity"
              :min="1"
              :max="item.maxStock"
              size="small"
              @change="(value) => handleQuantityChange(item, value)" />

            <a-button
              type="link"
              size="small"
              icon="delete"
              @click="handleRemoveItem(item)" />
          </div>

          <!-- 小计 -->
          <div class="item-subtotal">
            ¥{{ (item.price * item.quantity).toFixed(2) }}
          </div>
        </div>
      </div>
    </div>

    <!-- 购物车底部 -->
    <div v-if="items.length > 0" class="cart-footer">
      <!-- 优惠券选择 -->
      <div class="coupon-section">
        <a-select
          v-model="selectedCoupon"
          placeholder="选择优惠券"
          style="width: 100%"
          allow-clear>
          <a-select-option
            v-for="coupon in availableCoupons"
            :key="coupon.id"
            :value="coupon.id">
            {{ coupon.name }} - {{ coupon.discount }}
          </a-select-option>
        </a-select>
      </div>

      <!-- 金额汇总 -->
      <div class="amount-summary">
        <div class="summary-item">
          <span>小计:</span>
          <span>¥{{ subtotal.toFixed(2) }}</span>
        </div>

        <div class="summary-item" v-if="discount > 0">
          <span>优惠:</span>
          <span class="discount-amount">-¥{{ discount.toFixed(2) }}</span>
        </div>

        <div class="summary-item total">
          <span>总计:</span>
          <span class="total-amount">¥{{ total.toFixed(2) }}</span>
        </div>
      </div>

      <!-- 结算按钮 -->
      <div class="checkout-section">
        <a-button
          type="primary"
          size="large"
          block
          :loading="loading"
          @click="handleCheckout">
          <a-icon type="credit-card" />
          立即结算 ({{ totalQuantity }}件)
        </a-button>
      </div>
    </div>
  </a-card>
</template>

<script>
export default {
  name: 'ShoppingCart',

  props: {
    items: { type: Array, default: () => [] },
    loading: { type: Boolean, default: false }
  },

  data() {
    return {
      selectedCoupon: null,
      availableCoupons: [
        { id: 1, name: '满100减10', discount: '10元' },
        { id: 2, name: '新用户9折', discount: '9折' },
        { id: 3, name: '会员8.8折', discount: '8.8折' }
      ],
      defaultImage: require('@/assets/images/default-product.png'),
      emptyImage: require('@/assets/images/empty-cart.svg')
    }
  },

  computed: {
    subtotal() {
      return this.items.reduce((sum, item) => sum + (item.price * item.quantity), 0)
    },

    discount() {
      // 根据选择的优惠券计算折扣
      if (!this.selectedCoupon) return 0

      const coupon = this.availableCoupons.find(c => c.id === this.selectedCoupon)
      if (!coupon) return 0

      // 简单的折扣计算逻辑
      if (coupon.id === 1 && this.subtotal >= 100) return 10
      if (coupon.id === 2) return this.subtotal * 0.1
      if (coupon.id === 3) return this.subtotal * 0.12

      return 0
    },

    total() {
      return Math.max(this.subtotal - this.discount, 0)
    },

    totalQuantity() {
      return this.items.reduce((sum, item) => sum + item.quantity, 0)
    }
  },

  methods: {
    handleQuantityChange(item, quantity) {
      if (quantity && quantity > 0) {
        this.$emit('update-quantity', item, quantity)
      }
    },

    handleRemoveItem(item) {
      this.$emit('remove-item', item)
    },

    handleClearCart() {
      this.$emit('clear-cart')
    },

    handleCheckout() {
      const checkoutData = {
        items: this.items,
        subtotal: this.subtotal,
        discount: this.discount,
        total: this.total,
        couponId: this.selectedCoupon
      }

      this.$emit('checkout', checkoutData)
    },

    handleContinueShopping() {
      this.$emit('continue-shopping')
    }
  }
}
</script>

<style lang="less" scoped>
.shopping-cart {
  height: 100%;
  display: flex;
  flex-direction: column;

  .cart-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .anticon {
      margin-right: 8px;
      color: #3B82F6;
    }
  }

  .cart-content {
    flex: 1;
    overflow-y: auto;

    .cart-items {
      .cart-item {
        display: flex;
        align-items: center;
        padding: 12px 0;
        border-bottom: 1px solid #f0f0f0;

        &:last-child {
          border-bottom: none;
        }

        .item-image {
          width: 60px;
          height: 60px;
          margin-right: 12px;
          border-radius: 6px;
          overflow: hidden;

          img {
            width: 100%;
            height: 100%;
            object-fit: cover;
          }
        }

        .item-info {
          flex: 1;
          margin-right: 12px;

          .item-name {
            font-size: 14px;
            font-weight: 500;
            margin-bottom: 4px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          }

          .item-price {
            font-size: 14px;
            color: #ff4d4f;
            font-weight: bold;
            margin-bottom: 2px;
          }

          .item-stock {
            font-size: 12px;
            color: #999;
          }
        }

        .item-controls {
          display: flex;
          flex-direction: column;
          align-items: center;
          margin-right: 12px;

          .ant-input-number {
            width: 80px;
            margin-bottom: 4px;
          }

          .ant-btn {
            padding: 0;
            width: 24px;
            height: 24px;
            color: #ff4d4f;
          }
        }

        .item-subtotal {
          font-size: 14px;
          font-weight: bold;
          color: #333;
          min-width: 80px;
          text-align: right;
        }
      }
    }
  }

  .cart-footer {
    margin-top: 16px;
    padding-top: 16px;
    border-top: 1px solid #f0f0f0;

    .coupon-section {
      margin-bottom: 16px;
    }

    .amount-summary {
      margin-bottom: 16px;

      .summary-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 4px 0;

        &.total {
          font-size: 16px;
          font-weight: bold;
          color: #333;
          border-top: 1px solid #f0f0f0;
          padding-top: 8px;
          margin-top: 8px;

          .total-amount {
            color: #ff4d4f;
            font-size: 18px;
          }
        }

        .discount-amount {
          color: #52c41a;
        }
      }
    }

    .checkout-section {
      .ant-btn {
        height: 48px;
        font-size: 16px;
        font-weight: bold;
      }
    }
  }
}
</style>
```

---

## 💳 支付弹窗组件

### PaymentModal 支付弹窗组件
```vue
<template>
  <a-modal
    :visible="visible"
    title="订单结算"
    width="600px"
    :footer="null"
    :mask-closable="false"
    @cancel="handleCancel">

    <div class="payment-modal">
      <!-- 订单信息 -->
      <div class="order-summary">
        <h4>订单详情</h4>
        <div class="order-items">
          <div
            v-for="item in orderData.items"
            :key="item.productId"
            class="order-item">
            <span class="item-name">{{ item.name }}</span>
            <span class="item-quantity">x{{ item.quantity }}</span>
            <span class="item-amount">¥{{ (item.price * item.quantity).toFixed(2) }}</span>
          </div>
        </div>

        <div class="order-total">
          <div class="total-line">
            <span>小计:</span>
            <span>¥{{ orderData.subtotal.toFixed(2) }}</span>
          </div>
          <div class="total-line" v-if="orderData.discount > 0">
            <span>优惠:</span>
            <span class="discount">-¥{{ orderData.discount.toFixed(2) }}</span>
          </div>
          <div class="total-line final">
            <span>总计:</span>
            <span class="final-amount">¥{{ orderData.total.toFixed(2) }}</span>
          </div>
        </div>
      </div>

      <!-- 支付方式选择 -->
      <div class="payment-methods">
        <h4>选择支付方式</h4>
        <a-radio-group
          v-model="selectedPaymentMethod"
          class="payment-options">
          <div
            v-for="method in paymentMethods"
            :key="method.id"
            class="payment-option">
            <a-radio :value="method.id">
              <div class="payment-info">
                <a-icon :type="method.icon" class="payment-icon" />
                <div class="payment-details">
                  <div class="payment-name">{{ method.name }}</div>
                  <div class="payment-desc">{{ method.description }}</div>
                </div>
              </div>
            </a-radio>
          </div>
        </a-radio-group>
      </div>

      <!-- 现金支付额外信息 -->
      <div v-if="selectedPaymentMethod === 'cash'" class="cash-payment">
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="收款金额">
              <a-input-number
                v-model="cashReceived"
                :min="orderData.total"
                :precision="2"
                placeholder="请输入收款金额"
                style="width: 100%">
                <template #addonBefore>¥</template>
              </a-input-number>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="找零金额">
              <a-input
                :value="changeAmount"
                readonly
                style="width: 100%">
                <template #addonBefore>¥</template>
              </a-input>
            </a-form-item>
          </a-col>
        </a-row>

        <!-- 快速金额按钮 -->
        <div class="quick-amounts">
          <span class="quick-label">快速选择:</span>
          <a-button-group>
            <a-button
              v-for="amount in quickAmounts"
              :key="amount"
              size="small"
              @click="cashReceived = amount">
              ¥{{ amount }}
            </a-button>
          </a-button-group>
        </div>
      </div>

      <!-- 备注信息 -->
      <div class="order-notes">
        <a-form-item label="订单备注">
          <a-textarea
            v-model="orderNotes"
            :rows="2"
            placeholder="请输入订单备注信息..."
            :maxLength="200"
            show-count />
        </a-form-item>
      </div>

      <!-- 操作按钮 -->
      <div class="payment-actions">
        <a-space size="large">
          <a-button size="large" @click="handleCancel">
            取消
          </a-button>
          <a-button
            type="primary"
            size="large"
            :loading="processing"
            :disabled="!canConfirm"
            @click="handleConfirm">
            <a-icon type="check" />
            确认支付 ¥{{ orderData.total.toFixed(2) }}
          </a-button>
        </a-space>
      </div>
    </div>
  </a-modal>
</template>

<script>
export default {
  name: 'PaymentModal',

  props: {
    visible: { type: Boolean, default: false },
    orderData: { type: Object, default: () => ({}) },
    paymentMethods: { type: Array, default: () => [] }
  },

  data() {
    return {
      selectedPaymentMethod: 'cash',
      cashReceived: 0,
      orderNotes: '',
      processing: false
    }
  },

  computed: {
    changeAmount() {
      if (this.selectedPaymentMethod === 'cash' && this.cashReceived >= this.orderData.total) {
        return (this.cashReceived - this.orderData.total).toFixed(2)
      }
      return '0.00'
    },

    quickAmounts() {
      const total = this.orderData.total || 0
      const amounts = []

      // 添加整数金额
      const roundedUp = Math.ceil(total)
      amounts.push(roundedUp)

      // 添加常用金额
      const commonAmounts = [50, 100, 200, 500]
      commonAmounts.forEach(amount => {
        if (amount > total && !amounts.includes(amount)) {
          amounts.push(amount)
        }
      })

      return amounts.slice(0, 4) // 最多显示4个
    },

    canConfirm() {
      if (!this.selectedPaymentMethod) return false

      if (this.selectedPaymentMethod === 'cash') {
        return this.cashReceived >= this.orderData.total
      }

      return true
    }
  },

  watch: {
    visible(newVal) {
      if (newVal) {
        this.resetForm()
      }
    },

    'orderData.total'(newVal) {
      if (newVal && this.selectedPaymentMethod === 'cash') {
        this.cashReceived = newVal
      }
    }
  },

  methods: {
    resetForm() {
      this.selectedPaymentMethod = 'cash'
      this.cashReceived = this.orderData.total || 0
      this.orderNotes = ''
      this.processing = false
    },

    handleCancel() {
      this.$emit('cancel')
    },

    async handleConfirm() {
      this.processing = true

      try {
        const paymentData = {
          paymentMethod: this.selectedPaymentMethod,
          cashReceived: this.selectedPaymentMethod === 'cash' ? this.cashReceived : null,
          changeAmount: this.selectedPaymentMethod === 'cash' ? parseFloat(this.changeAmount) : null,
          notes: this.orderNotes,
          paymentTime: new Date().toISOString()
        }

        this.$emit('confirm', paymentData)

      } catch (error) {
        this.$message.error('支付处理失败')
      } finally {
        this.processing = false
      }
    }
  }
}
</script>

<style lang="less" scoped>
.payment-modal {
  .order-summary {
    margin-bottom: 24px;
    padding: 16px;
    background: #fafafa;
    border-radius: 6px;

    h4 {
      margin: 0 0 12px 0;
      color: #333;
    }

    .order-items {
      margin-bottom: 12px;

      .order-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 4px 0;

        .item-name {
          flex: 1;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }

        .item-quantity {
          margin: 0 12px;
          color: #666;
        }

        .item-amount {
          font-weight: 500;
          min-width: 80px;
          text-align: right;
        }
      }
    }

    .order-total {
      border-top: 1px solid #e8e8e8;
      padding-top: 12px;

      .total-line {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 2px 0;

        &.final {
          font-size: 16px;
          font-weight: bold;
          color: #333;
          border-top: 1px solid #e8e8e8;
          padding-top: 8px;
          margin-top: 8px;

          .final-amount {
            color: #ff4d4f;
            font-size: 18px;
          }
        }

        .discount {
          color: #52c41a;
        }
      }
    }
  }

  .payment-methods {
    margin-bottom: 24px;

    h4 {
      margin: 0 0 12px 0;
      color: #333;
    }

    .payment-options {
      width: 100%;

      .payment-option {
        display: block;
        margin-bottom: 12px;

        .ant-radio-wrapper {
          width: 100%;
          padding: 12px;
          border: 1px solid #d9d9d9;
          border-radius: 6px;
          transition: all 0.3s;

          &:hover {
            border-color: #3B82F6;
          }
        }

        .ant-radio-wrapper-checked {
          border-color: #3B82F6;
          background: #f6f8ff;
        }

        .payment-info {
          display: flex;
          align-items: center;
          margin-left: 8px;

          .payment-icon {
            font-size: 24px;
            margin-right: 12px;
            color: #3B82F6;
          }

          .payment-details {
            .payment-name {
              font-size: 14px;
              font-weight: 500;
              margin-bottom: 2px;
            }

            .payment-desc {
              font-size: 12px;
              color: #666;
            }
          }
        }
      }
    }
  }

  .cash-payment {
    margin-bottom: 24px;
    padding: 16px;
    background: #f6f8ff;
    border-radius: 6px;

    .quick-amounts {
      margin-top: 12px;

      .quick-label {
        margin-right: 8px;
        color: #666;
        font-size: 12px;
      }
    }
  }

  .order-notes {
    margin-bottom: 24px;
  }

  .payment-actions {
    text-align: center;
    padding-top: 16px;
    border-top: 1px solid #f0f0f0;
  }
}
</style>
```

---

## 🎉 订单成功弹窗组件

### OrderSuccessModal 订单成功弹窗
```vue
<template>
  <a-modal
    :visible="visible"
    title="订单完成"
    width="500px"
    :footer="null"
    :mask-closable="false"
    centered
    @cancel="handleClose">

    <div class="order-success-modal">
      <!-- 成功图标 -->
      <div class="success-icon">
        <a-icon type="check-circle" theme="filled" />
      </div>

      <!-- 成功信息 -->
      <div class="success-message">
        <h2>支付成功！</h2>
        <p>订单已完成，感谢您的购买</p>
      </div>

      <!-- 订单信息 -->
      <div class="order-info" v-if="order">
        <div class="info-item">
          <span class="label">订单编号:</span>
          <span class="value">{{ order.orderNo }}</span>
        </div>

        <div class="info-item">
          <span class="label">支付金额:</span>
          <span class="value amount">¥{{ order.totalAmount.toFixed(2) }}</span>
        </div>

        <div class="info-item">
          <span class="label">支付方式:</span>
          <span class="value">{{ getPaymentMethodName(order.paymentMethod) }}</span>
        </div>

        <div class="info-item" v-if="order.changeAmount > 0">
          <span class="label">找零金额:</span>
          <span class="value change">¥{{ order.changeAmount.toFixed(2) }}</span>
        </div>

        <div class="info-item">
          <span class="label">交易时间:</span>
          <span class="value">{{ formatDateTime(order.createTime) }}</span>
        </div>
      </div>

      <!-- 操作按钮 -->
      <div class="action-buttons">
        <a-space size="large">
          <a-button
            size="large"
            @click="handlePrint">
            <a-icon type="printer" />
            打印小票
          </a-button>

          <a-button
            type="primary"
            size="large"
            @click="handleNewOrder">
            <a-icon type="plus" />
            新订单
          </a-button>
        </a-space>
      </div>

      <!-- 自动关闭倒计时 -->
      <div class="auto-close" v-if="autoCloseCountdown > 0">
        {{ autoCloseCountdown }}秒后自动关闭
      </div>
    </div>
  </a-modal>
</template>

<script>
import moment from 'moment'

export default {
  name: 'OrderSuccessModal',

  props: {
    visible: { type: Boolean, default: false },
    order: { type: Object, default: null }
  },

  data() {
    return {
      autoCloseCountdown: 0,
      countdownTimer: null
    }
  },

  watch: {
    visible(newVal) {
      if (newVal) {
        this.startAutoCloseCountdown()
      } else {
        this.stopAutoCloseCountdown()
      }
    }
  },

  beforeDestroy() {
    this.stopAutoCloseCountdown()
  },

  methods: {
    startAutoCloseCountdown() {
      this.autoCloseCountdown = 10 // 10秒自动关闭
      this.countdownTimer = setInterval(() => {
        this.autoCloseCountdown--
        if (this.autoCloseCountdown <= 0) {
          this.handleClose()
        }
      }, 1000)
    },

    stopAutoCloseCountdown() {
      if (this.countdownTimer) {
        clearInterval(this.countdownTimer)
        this.countdownTimer = null
      }
      this.autoCloseCountdown = 0
    },

    handlePrint() {
      this.stopAutoCloseCountdown()
      this.printReceipt()
      this.$emit('print')
    },

    handleNewOrder() {
      this.stopAutoCloseCountdown()
      this.$emit('new-order')
    },

    handleClose() {
      this.stopAutoCloseCountdown()
      this.$emit('close')
    },

    printReceipt() {
      if (!this.order) return

      // 创建打印内容
      const printContent = this.generateReceiptHTML()

      // 打开新窗口进行打印
      const printWindow = window.open('', '_blank')
      printWindow.document.write(printContent)
      printWindow.document.close()
      printWindow.focus()
      printWindow.print()
      printWindow.close()
    },

    generateReceiptHTML() {
      const order = this.order
      const items = order.items || []

      return `
        <!DOCTYPE html>
        <html>
        <head>
          <title>购物小票</title>
          <style>
            body { font-family: monospace; font-size: 12px; margin: 0; padding: 20px; }
            .receipt { width: 300px; margin: 0 auto; }
            .header { text-align: center; margin-bottom: 20px; }
            .title { font-size: 16px; font-weight: bold; }
            .line { border-bottom: 1px dashed #000; margin: 10px 0; }
            .item { display: flex; justify-content: space-between; margin: 5px 0; }
            .total { font-weight: bold; font-size: 14px; }
            .footer { text-align: center; margin-top: 20px; font-size: 10px; }
          </style>
        </head>
        <body>
          <div class="receipt">
            <div class="header">
              <div class="title">掐丝珐琅馆</div>
              <div>购物小票</div>
            </div>

            <div class="line"></div>

            <div class="item">
              <span>订单编号:</span>
              <span>${order.orderNo}</span>
            </div>

            <div class="item">
              <span>交易时间:</span>
              <span>${this.formatDateTime(order.createTime)}</span>
            </div>

            <div class="line"></div>

            ${items.map(item => `
              <div class="item">
                <span>${item.name}</span>
                <span>${item.quantity}x${item.price.toFixed(2)}</span>
              </div>
            `).join('')}

            <div class="line"></div>

            <div class="item total">
              <span>总计:</span>
              <span>¥${order.totalAmount.toFixed(2)}</span>
            </div>

            <div class="item">
              <span>支付方式:</span>
              <span>${this.getPaymentMethodName(order.paymentMethod)}</span>
            </div>

            ${order.changeAmount > 0 ? `
              <div class="item">
                <span>找零:</span>
                <span>¥${order.changeAmount.toFixed(2)}</span>
              </div>
            ` : ''}

            <div class="footer">
              <div>谢谢惠顾，欢迎再次光临！</div>
              <div>客服电话: 400-123-4567</div>
            </div>
          </div>
        </body>
        </html>
      `
    },

    formatDateTime(datetime) {
      return moment(datetime).format('YYYY-MM-DD HH:mm:ss')
    },

    getPaymentMethodName(method) {
      const methods = {
        cash: '现金支付',
        alipay: '支付宝',
        wechat: '微信支付',
        card: '银行卡'
      }
      return methods[method] || method
    }
  }
}
</script>

<style lang="less" scoped>
.order-success-modal {
  text-align: center;

  .success-icon {
    margin-bottom: 24px;

    .anticon {
      font-size: 64px;
      color: #52c41a;
    }
  }

  .success-message {
    margin-bottom: 32px;

    h2 {
      margin: 0 0 8px 0;
      color: #333;
      font-size: 24px;
    }

    p {
      margin: 0;
      color: #666;
      font-size: 14px;
    }
  }

  .order-info {
    margin-bottom: 32px;
    padding: 20px;
    background: #fafafa;
    border-radius: 8px;
    text-align: left;

    .info-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 8px 0;
      border-bottom: 1px solid #f0f0f0;

      &:last-child {
        border-bottom: none;
      }

      .label {
        color: #666;
        font-size: 14px;
      }

      .value {
        font-weight: 500;
        color: #333;

        &.amount {
          color: #ff4d4f;
          font-size: 16px;
          font-weight: bold;
        }

        &.change {
          color: #52c41a;
          font-weight: bold;
        }
      }
    }
  }

  .action-buttons {
    margin-bottom: 20px;
  }

  .auto-close {
    color: #999;
    font-size: 12px;
  }
}
</style>
```

---

*设计文档版本: v1.0*
*最后更新: 2025-01-22*
