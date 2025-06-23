# jshERP掐丝珐琅馆综合管理模块详细UI/UX设计方案

## 📋 设计概述

基于对现有React系统的深度分析，本文档详细描述了jshERP版本的掐丝珐琅馆综合管理模块UI/UX设计方案，将优秀的设计理念转换为Vue.js 2.7.16 + Ant Design Vue 1.5.2的实现。

---

## 🎨 设计系统规范

### 主色调系统
```less
// 主色调 (借鉴现有系统的indigo色系，调整为jshERP标准)
@primary-color: #3B82F6;           // 主蓝色 (jshERP标准)
@primary-color-hover: #2563EB;     // 悬停蓝色
@primary-color-active: #1D4ED8;    // 激活蓝色

// 功能色彩 (保持现有系统的配色理念)
@success-color: #10B981;           // 成功绿色
@warning-color: #F59E0B;           // 警告橙色
@error-color: #EF4444;             // 错误红色
@info-color: #6B7280;              // 信息灰色

// 渐变色系 (借鉴现有系统的渐变卡片设计)
@gradient-cyan: linear-gradient(135deg, #06B6D4 0%, #3B82F6 100%);      // 咖啡店销售
@gradient-purple: linear-gradient(135deg, #8B5CF6 0%, #3B82F6 100%);    // 珐琅馆销售
@gradient-green: linear-gradient(135deg, #10B981 0%, #059669 100%);     // 任务完成率
@gradient-amber: linear-gradient(135deg, #F59E0B 0%, #D97706 100%);     // 值班人数
```

### 组件样式规范
```less
// 卡片设计 (借鉴现有系统的现代化卡片)
.cloisonne-card {
  background: #FFFFFF;
  border-radius: 12px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
  border: none;
  overflow: hidden;
  transition: all 0.3s ease;
  
  &:hover {
    box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
    transform: translateY(-2px);
  }
}

// 渐变数据卡片
.gradient-card {
  color: white;
  position: relative;
  overflow: hidden;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    right: 0;
    width: 100px;
    height: 100px;
    background: rgba(255, 255, 255, 0.1);
    border-radius: 50%;
    transform: translate(30px, -30px);
  }
}
```

---

## 🏠 1. 总览仪表板页面详细设计

### 页面布局结构
```vue
<template>
  <div class="cloisonne-dashboard">
    <!-- 页面头部 -->
    <div class="dashboard-header">
      <h1 class="page-title">掐丝珐琅馆总览</h1>
      <a-button type="primary" icon="plus">
        <a-icon type="plus" />
        添加概要
      </a-button>
    </div>
    
    <!-- 数据卡片区域 -->
    <a-row :gutter="[24, 16]" class="dashboard-metrics">
      <a-col :xs="24" :sm="12" :lg="6">
        <sales-data-card 
          title="咖啡店今日销售"
          :value="coffeeShopSales"
          icon="coffee"
          :gradient="gradientCyan"
          :trend="12.5" />
      </a-col>
      <a-col :xs="24" :sm="12" :lg="6">
        <sales-data-card 
          title="珐琅馆今日销售"
          :value="museumSales"
          icon="shopping-cart"
          :gradient="gradientPurple"
          :trend="8.3" />
      </a-col>
      <a-col :xs="24" :sm="12" :lg="6">
        <task-completion-card 
          title="总任务完成率"
          :completed="completedTasks"
          :total="totalTasks"
          :gradient="gradientGreen" />
      </a-col>
      <a-col :xs="24" :sm="12" :lg="6">
        <staff-count-card 
          title="今日值班人数"
          :count="onDutyStaffCount"
          :gradient="gradientAmber" />
      </a-col>
    </a-row>
    
    <!-- 主要内容区域 -->
    <a-row :gutter="24" class="dashboard-content">
      <a-col :xs="24" :lg="16">
        <today-tasks-card />
      </a-col>
      <a-col :xs="24" :lg="8">
        <on-duty-staff-card />
      </a-col>
    </a-row>
  </div>
</template>
```

### 核心组件设计

#### SalesDataCard 销售数据卡片
```vue
<template>
  <a-card 
    :class="['sales-data-card', 'gradient-card']"
    :style="{ background: gradient }"
    :bordered="false">
    
    <div class="card-content">
      <div class="card-icon">
        <a-icon :type="icon" class="icon-large" />
      </div>
      <div class="card-data">
        <p class="card-title">{{ title }}</p>
        <p class="card-value">¥{{ formattedValue }}</p>
        <div class="card-trend" v-if="trend">
          <a-icon :type="trendIcon" />
          <span>{{ trend }}%</span>
        </div>
      </div>
    </div>
  </a-card>
</template>

<script>
export default {
  name: 'SalesDataCard',
  props: {
    title: { type: String, required: true },
    value: { type: Number, required: true },
    icon: { type: String, required: true },
    gradient: { type: String, required: true },
    trend: { type: Number, default: null }
  },
  computed: {
    formattedValue() {
      return this.value.toLocaleString('zh-CN', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
      })
    },
    trendIcon() {
      return this.trend > 0 ? 'arrow-up' : 'arrow-down'
    }
  }
}
</script>
```

#### TodayTasksCard 今日任务卡片
```vue
<template>
  <a-card 
    title="今日工作"
    class="cloisonne-card"
    :bordered="false">
    
    <template #extra>
      <a-button type="link" icon="plus" @click="handleAddTask">
        新任务
      </a-button>
    </template>
    
    <div class="tasks-list">
      <div 
        v-for="task in todayTasks"
        :key="task.id"
        class="task-item"
        @click="toggleTask(task.id)">
        
        <a-checkbox 
          :checked="task.completed"
          @change="toggleTask(task.id)" />
        
        <span 
          :class="['task-text', { 'completed': task.completed }]">
          {{ task.description }}
        </span>
        
        <a-tag 
          v-if="!task.completed && task.dueDate"
          color="orange"
          size="small">
          截止
        </a-tag>
      </div>
    </div>
  </a-card>
</template>
```

---

## 📅 2. 排班管理页面详细设计

### 多视图切换设计
```vue
<template>
  <div class="schedule-management">
    <!-- 页面头部 -->
    <div class="schedule-header">
      <h1 class="page-title">排班管理</h1>
      <a-space>
        <a-button @click="showBatchModal">
          <a-icon type="team" />
          批量排班
        </a-button>
        <a-button type="primary" @click="showAddModal">
          <a-icon type="plus" />
          新增排班
        </a-button>
      </a-space>
    </div>
    
    <!-- 控制面板 -->
    <a-card class="schedule-controls" :bordered="false">
      <a-row justify="space-between" align="middle">
        <a-col>
          <a-space>
            <a-button @click="prevMonth">
              <a-icon type="left" />
              上个月
            </a-button>
            <a-button @click="goToday">今天</a-button>
            <a-button @click="nextMonth">
              下个月
              <a-icon type="right" />
            </a-button>
            <h2 class="current-month">
              {{ currentYear }}年 {{ currentMonthName }}
            </h2>
          </a-space>
        </a-col>
        
        <a-col>
          <a-radio-group 
            v-model="viewMode" 
            button-style="solid"
            size="small">
            <a-radio-button value="calendar">
              <a-icon type="calendar" />
              日历
            </a-radio-button>
            <a-radio-button value="list">
              <a-icon type="unordered-list" />
              列表
            </a-radio-button>
            <a-radio-button value="statistics">
              <a-icon type="bar-chart" />
              统计
            </a-radio-button>
          </a-radio-group>
        </a-col>
      </a-row>
    </a-card>
    
    <!-- 视图内容区域 -->
    <div class="schedule-content">
      <calendar-view 
        v-if="viewMode === 'calendar'"
        :schedules="schedules"
        :current-date="currentDate"
        @date-click="handleDateClick"
        @schedule-edit="handleScheduleEdit" />
        
      <list-view 
        v-if="viewMode === 'list'"
        :schedules="monthSchedules"
        @edit="handleScheduleEdit"
        @delete="handleScheduleDelete" />
        
      <statistics-view 
        v-if="viewMode === 'statistics'"
        :schedules="schedules"
        :staff-members="staffMembers"
        :current-date="currentDate" />
    </div>
  </div>
</template>
```

### 日历视图组件
```vue
<template>
  <a-card class="calendar-view" :bordered="false">
    <div class="calendar-grid">
      <!-- 星期标题 -->
      <div class="calendar-header">
        <div 
          v-for="day in weekDays"
          :key="day"
          class="week-day">
          {{ day }}
        </div>
      </div>
      
      <!-- 日期格子 -->
      <div class="calendar-body">
        <div 
          v-for="(day, index) in calendarDays"
          :key="index"
          :class="['calendar-day', { 
            'today': isToday(day),
            'other-month': !isCurrentMonth(day),
            'has-schedule': hasSchedule(day)
          }]"
          @click="handleDayClick(day)">
          
          <div class="day-number">{{ day ? day.getDate() : '' }}</div>
          
          <div class="day-schedules" v-if="day">
            <div 
              v-for="schedule in getDaySchedules(day)"
              :key="schedule.id"
              :class="['schedule-item', `shift-${schedule.shift}`]"
              @click.stop="handleScheduleClick(schedule)">
              
              <a-avatar 
                :size="24" 
                :src="getEmployeeAvatar(schedule.staffId)"
                class="schedule-avatar" />
              
              <span class="schedule-text">
                {{ schedule.staffName }} ({{ schedule.shift }})
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </a-card>
</template>

<style lang="less" scoped>
.calendar-grid {
  .calendar-header {
    display: grid;
    grid-template-columns: repeat(7, 1fr);
    gap: 1px;
    background: #f0f0f0;
    margin-bottom: 1px;
    
    .week-day {
      background: #fafafa;
      padding: 12px;
      text-align: center;
      font-weight: 600;
      color: #666;
    }
  }
  
  .calendar-body {
    display: grid;
    grid-template-columns: repeat(7, 1fr);
    gap: 1px;
    background: #f0f0f0;
    
    .calendar-day {
      background: white;
      min-height: 120px;
      padding: 8px;
      cursor: pointer;
      transition: background-color 0.2s;
      
      &:hover {
        background: #f8f9fa;
      }
      
      &.today {
        background: #e6f7ff;
        
        .day-number {
          color: @primary-color;
          font-weight: bold;
        }
      }
      
      &.other-month {
        background: #f5f5f5;
        color: #ccc;
      }
      
      .day-number {
        font-size: 14px;
        font-weight: 500;
        margin-bottom: 4px;
      }
      
      .day-schedules {
        .schedule-item {
          display: flex;
          align-items: center;
          padding: 2px 4px;
          margin-bottom: 2px;
          border-radius: 4px;
          font-size: 12px;
          cursor: pointer;
          
          &.shift-早班 {
            background: #e6f7ff;
            color: #1890ff;
          }
          
          &.shift-晚班 {
            background: #f6ffed;
            color: #52c41a;
          }
          
          &.shift-全天 {
            background: #fff2e8;
            color: #fa8c16;
          }
          
          .schedule-avatar {
            margin-right: 4px;
          }
          
          .schedule-text {
            flex: 1;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          }
        }
      }
    }
  }
}
</style>
```

---

## ☕ 3. 咖啡店管理页面详细设计

### 页面布局
```vue
<template>
  <div class="coffee-shop-management">
    <div class="page-header">
      <h1 class="page-title">咖啡店管理</h1>
    </div>
    
    <a-row :gutter="24">
      <!-- 主要内容区域 -->
      <a-col :xs="24" :lg="16">
        <sales-entry-form @submit="handleSalesSubmit" />
      </a-col>
      
      <!-- 侧边信息区域 -->
      <a-col :xs="24" :lg="8">
        <a-space direction="vertical" size="large" style="width: 100%">
          <on-duty-staff-card />
          <recent-sales-card :sales="recentSales" />
        </a-space>
      </a-col>
    </a-row>
  </div>
</template>
```

### 销售录入表单
```vue
<template>
  <a-card 
    title="日销售数据填写"
    class="cloisonne-card"
    :bordered="false">
    
    <template #extra>
      <a-button 
        type="primary" 
        :loading="submitting"
        @click="handleSubmit">
        <a-icon type="save" />
        保存记录
      </a-button>
    </template>
    
    <a-form 
      :form="form"
      :label-col="{ span: 6 }"
      :wrapper-col="{ span: 18 }">
      
      <a-form-item label="日期">
        <a-date-picker 
          v-decorator="['date', { 
            initialValue: moment(),
            rules: [{ required: true, message: '请选择日期' }]
          }]"
          style="width: 100%" />
      </a-form-item>
      
      <a-form-item label="总销售额">
        <a-input-number
          v-decorator="['amount', {
            rules: [{ required: true, message: '请输入销售额' }]
          }]"
          :min="0"
          :precision="2"
          placeholder="请输入销售额"
          style="width: 100%">
          <template #addonBefore>¥</template>
        </a-input-number>
      </a-form-item>
      
      <a-form-item label="销售凭证">
        <image-uploader 
          v-decorator="['images']"
          :max-count="5"
          accept="image/*"
          @change="handleImageChange" />
      </a-form-item>
      
      <a-form-item label="备注">
        <a-textarea 
          v-decorator="['notes']"
          :rows="3"
          placeholder="例如：今日有促销活动" />
      </a-form-item>
    </a-form>
  </a-card>
</template>
```

---

## 🛒 4. POS销售页面详细设计

### 双栏布局设计
```vue
<template>
  <div class="pos-sales">
    <div class="page-header">
      <h1 class="page-title">POS 销售系统</h1>
    </div>
    
    <a-row :gutter="24" class="pos-content">
      <!-- 商品选择区域 -->
      <a-col :xs="24" :lg="16" class="products-section">
        <!-- 搜索和筛选 -->
        <a-card class="search-filters" :bordered="false">
          <a-row :gutter="16">
            <a-col :xs="24" :sm="16">
              <a-input-search
                v-model="searchKeyword"
                placeholder="搜索商品..."
                @search="handleSearch"
                enter-button />
            </a-col>
            <a-col :xs="24" :sm="8">
              <a-select
                v-model="selectedCategory"
                placeholder="选择分类"
                style="width: 100%"
                @change="handleCategoryChange">
                <a-select-option value="">全部分类</a-select-option>
                <a-select-option 
                  v-for="category in categories"
                  :key="category"
                  :value="category">
                  {{ category }}
                </a-select-option>
              </a-select>
            </a-col>
          </a-row>
        </a-card>
        
        <!-- 商品网格 -->
        <div class="products-grid">
          <a-row :gutter="[16, 16]">
            <a-col 
              v-for="product in filteredProducts"
              :key="product.id"
              :xs="12" :sm="8" :lg="6">
              <product-card 
                :product="product"
                @add-to-cart="handleAddToCart" />
            </a-col>
          </a-row>
        </div>
      </a-col>
      
      <!-- 购物车区域 -->
      <a-col :xs="24" :lg="8" class="cart-section">
        <shopping-cart
          :items="cartItems"
          :total="cartTotal"
          @update-quantity="handleUpdateQuantity"
          @remove-item="handleRemoveItem"
          @checkout="handleCheckout" />
      </a-col>
    </a-row>
  </div>
</template>
```

### 商品卡片组件
```vue
<template>
  <a-card 
    :hoverable="true"
    class="product-card"
    :bordered="false"
    @click="handleAddToCart">
    
    <template #cover>
      <div class="product-image">
        <img 
          :src="product.imageUrl || defaultImage" 
          :alt="product.name" />
        <div class="product-overlay">
          <a-button 
            type="primary" 
            shape="circle" 
            icon="plus"
            size="large" />
        </div>
      </div>
    </template>
    
    <a-card-meta>
      <template #title>
        <div class="product-title">{{ product.name }}</div>
      </template>
      
      <template #description>
        <div class="product-info">
          <div class="product-category">{{ product.category }}</div>
          <div class="product-price">¥{{ product.price.toFixed(2) }}</div>
          <div class="product-stock" :class="stockClass">
            库存: {{ product.stock }}
          </div>
        </div>
      </template>
    </a-card-meta>
  </a-card>
</template>

<style lang="less" scoped>
.product-card {
  .product-image {
    position: relative;
    overflow: hidden;
    
    img {
      width: 100%;
      height: 160px;
      object-fit: cover;
      transition: transform 0.3s;
    }
    
    .product-overlay {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: rgba(0, 0, 0, 0.5);
      display: flex;
      align-items: center;
      justify-content: center;
      opacity: 0;
      transition: opacity 0.3s;
    }
    
    &:hover {
      img {
        transform: scale(1.05);
      }
      
      .product-overlay {
        opacity: 1;
      }
    }
  }
  
  .product-title {
    font-size: 14px;
    font-weight: 600;
    margin-bottom: 8px;
  }
  
  .product-info {
    .product-category {
      font-size: 12px;
      color: #999;
      margin-bottom: 4px;
    }
    
    .product-price {
      font-size: 16px;
      font-weight: bold;
      color: @primary-color;
      margin-bottom: 4px;
    }
    
    .product-stock {
      font-size: 12px;
      
      &.low-stock {
        color: @warning-color;
      }
      
      &.out-of-stock {
        color: @error-color;
      }
    }
  }
}
</style>
```

---

## 📱 响应式设计方案

### 断点策略
```less
// 响应式断点
@screen-xs: 480px;    // 超小屏幕
@screen-sm: 576px;    // 小屏幕
@screen-md: 768px;    // 中等屏幕
@screen-lg: 992px;    // 大屏幕
@screen-xl: 1200px;   // 超大屏幕
@screen-xxl: 1600px;  // 超超大屏幕

// 响应式网格
.responsive-grid {
  @media (max-width: @screen-sm) {
    .ant-col {
      margin-bottom: 16px;
    }
  }
}
```

### 移动端优化
```vue
<!-- 移动端导航 -->
<template v-if="isMobile">
  <a-tabs 
    v-model="activeTab"
    tab-position="bottom"
    class="mobile-tabs">
    <a-tab-pane key="overview" tab="总览">
      <a-icon slot="tab" type="home" />
    </a-tab-pane>
    <a-tab-pane key="schedule" tab="排班">
      <a-icon slot="tab" type="calendar" />
    </a-tab-pane>
    <a-tab-pane key="coffee" tab="咖啡店">
      <a-icon slot="tab" type="coffee" />
    </a-tab-pane>
    <a-tab-pane key="pos" tab="销售">
      <a-icon slot="tab" type="shopping-cart" />
    </a-tab-pane>
  </a-tabs>
</template>
```

---

## 🔧 技术实现细节

### Vue.js组件架构
```javascript
// 主模块入口
const CloisonneManagement = {
  name: 'CloisonneManagement',
  components: {
    DashboardOverview: () => import('./views/Dashboard/DashboardOverview.vue'),
    ScheduleManagement: () => import('./views/Schedule/ScheduleManagement.vue'),
    CoffeeManagement: () => import('./views/Coffee/CoffeeManagement.vue'),
    POSSales: () => import('./views/POS/POSSales.vue')
  },

  data() {
    return {
      currentView: 'dashboard',
      isMobile: false
    }
  },

  mixins: [responsiveMixin, permissionMixin],

  mounted() {
    this.checkDevice()
    window.addEventListener('resize', this.checkDevice)
  }
}
```

### 状态管理设计
```javascript
// Vuex模块
const cloisonneModule = {
  namespaced: true,

  state: {
    // 仪表板数据
    dashboard: {
      todaySales: {
        coffee: 0,
        museum: 0
      },
      tasks: [],
      onDutyStaff: []
    },

    // 排班数据
    schedule: {
      currentMonth: new Date(),
      schedules: [],
      viewMode: 'calendar'
    },

    // 咖啡店数据
    coffee: {
      salesRecords: [],
      currentShift: null
    },

    // POS数据
    pos: {
      products: [],
      cartItems: [],
      categories: []
    }
  },

  mutations: {
    SET_DASHBOARD_DATA(state, data) {
      state.dashboard = { ...state.dashboard, ...data }
    },

    ADD_SCHEDULE(state, schedule) {
      state.schedule.schedules.push(schedule)
    },

    UPDATE_CART(state, items) {
      state.pos.cartItems = items
    }
  },

  actions: {
    async fetchDashboardData({ commit }) {
      try {
        const response = await this.$http.get('/cloisonne/dashboard')
        commit('SET_DASHBOARD_DATA', response.data)
      } catch (error) {
        this.$message.error('获取仪表板数据失败')
      }
    }
  }
}
```

### API接口设计
```javascript
// API服务类
class CloisonneAPI {
  // 仪表板接口
  static getDashboardData() {
    return axios.get('/cloisonne/dashboard/overview')
  }

  static getTodayTasks() {
    return axios.get('/cloisonne/dashboard/tasks')
  }

  static getOnDutyStaff() {
    return axios.get('/cloisonne/dashboard/on-duty')
  }

  // 排班管理接口
  static getSchedules(params) {
    return axios.get('/cloisonne/schedule/list', { params })
  }

  static createSchedule(data) {
    return axios.post('/cloisonne/schedule/create', data)
  }

  static batchCreateSchedule(data) {
    return axios.post('/cloisonne/schedule/batch-create', data)
  }

  // 咖啡店管理接口
  static saveCoffeeSales(data) {
    return axios.post('/cloisonne/coffee/sales', data)
  }

  static uploadSalesImage(file) {
    const formData = new FormData()
    formData.append('file', file)
    return axios.post('/cloisonne/coffee/upload', formData)
  }

  // POS销售接口
  static getProducts(params) {
    return axios.get('/cloisonne/pos/products', { params })
  }

  static createOrder(data) {
    return axios.post('/cloisonne/pos/orders', data)
  }
}
```

### 权限控制集成
```javascript
// 权限混入
const permissionMixin = {
  methods: {
    hasPermission(permission) {
      return this.$store.getters['user/hasPermission'](permission)
    },

    checkCloisonnePermission(action) {
      const permissions = {
        'view': 'cloisonne:view',
        'schedule:edit': 'cloisonne:schedule:edit',
        'coffee:manage': 'cloisonne:coffee:manage',
        'pos:operate': 'cloisonne:pos:operate'
      }
      return this.hasPermission(permissions[action])
    }
  }
}

// 在组件中使用
export default {
  mixins: [permissionMixin],

  computed: {
    canEditSchedule() {
      return this.checkCloisonnePermission('schedule:edit')
    },

    canManageCoffee() {
      return this.checkCloisonnePermission('coffee:manage')
    }
  }
}
```

### 多租户数据隔离
```javascript
// HTTP拦截器
axios.interceptors.request.use(config => {
  // 自动添加租户ID
  const tenantId = store.getters['user/tenantId']
  if (tenantId) {
    config.headers['X-Tenant-ID'] = tenantId
  }
  return config
})

// 数据查询自动过滤
const scheduleService = {
  async getSchedules(params) {
    // 后端会自动根据租户ID过滤数据
    const response = await CloisonneAPI.getSchedules(params)
    return response.data
  }
}
```

---

## 🎯 开发优先级和里程碑

### 第一阶段：基础架构 (3-5天)
1. **通用组件开发**
   - DataCard 数据卡片组件
   - CloisonneHeader 页面头部组件
   - ResponsiveGrid 响应式网格组件

2. **路由和权限配置**
   - 添加掐丝珐琅馆菜单到jsh_function表
   - 配置路由和权限控制
   - 集成现有权限系统

### 第二阶段：核心页面 (7-10天)
1. **总览仪表板** (2-3天)
   - 4个数据卡片组件
   - 任务列表和值班信息
   - ECharts图表集成

2. **排班管理** (3-4天)
   - 日历视图组件
   - 列表视图和统计视图
   - 批量排班功能

3. **咖啡店管理** (2-3天)
   - 销售录入表单
   - 图片上传组件
   - 历史记录查询

### 第三阶段：高级功能 (5-7天)
1. **POS销售系统** (3-4天)
   - 商品选择和搜索
   - 购物车功能
   - 支付流程

2. **响应式优化** (2-3天)
   - 移动端适配
   - 平板端优化
   - 触摸交互支持

### 第四阶段：测试和优化 (3-5天)
1. **功能测试**
2. **性能优化**
3. **用户体验优化**
4. **文档完善**

---

## 📋 质量保证标准

### 代码质量
- ESLint规范检查通过率 100%
- 组件测试覆盖率 ≥ 80%
- 代码注释覆盖率 ≥ 60%

### 性能标准
- 首屏加载时间 ≤ 2秒
- 页面切换响应时间 ≤ 300ms
- 移动端流畅度 ≥ 60fps

### 兼容性要求
- 支持Chrome 70+、Firefox 65+、Safari 12+
- 支持iOS 12+、Android 8+
- 响应式适配1920x1080、1366x768、移动端

---

*设计方案版本: v1.0*
*设计时间: 2025-01-22*
*技术栈: Vue.js 2.7.16 + Ant Design Vue 1.5.2*
