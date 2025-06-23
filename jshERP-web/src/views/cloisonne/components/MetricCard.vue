<template>
  <a-card :bordered="false" :loading="loading" class="metric-card">
    <div class="metric-content">
      <!-- 图标区域 -->
      <div class="metric-icon" :style="{ backgroundColor: color }">
        <a-icon :type="icon" />
      </div>
      
      <!-- 数据区域 -->
      <div class="metric-data">
        <div class="metric-title">{{ title }}</div>
        <div class="metric-value">
          <span v-if="prefix" class="prefix">{{ prefix }}</span>
          <span class="value">{{ formattedValue }}</span>
          <span v-if="suffix" class="suffix">{{ suffix }}</span>
        </div>
        
        <!-- 趋势指示器 -->
        <div v-if="trend" class="metric-trend" :class="trendClass">
          <a-icon :type="trendIcon" />
          <span>{{ trendText }}</span>
        </div>
      </div>
      
      <!-- 额外操作区域 -->
      <div v-if="$slots.extra" class="metric-extra">
        <slot name="extra"></slot>
      </div>
    </div>
    
    <!-- 底部描述 -->
    <div v-if="description" class="metric-description">
      {{ description }}
    </div>
  </a-card>
</template>

<script>
export default {
  name: 'MetricCard',
  
  props: {
    title: {
      type: String,
      required: true
    },
    value: {
      type: [Number, String],
      required: true
    },
    prefix: {
      type: String,
      default: ''
    },
    suffix: {
      type: String,
      default: ''
    },
    icon: {
      type: String,
      required: true
    },
    color: {
      type: String,
      default: '#1890ff'
    },
    loading: {
      type: Boolean,
      default: false
    },
    description: {
      type: String,
      default: ''
    },
    trend: {
      type: Object,
      default: null
      // 格式: { type: 'up|down|flat', value: 12.5, text: '较昨日' }
    },
    precision: {
      type: Number,
      default: 0
    }
  },
  
  computed: {
    formattedValue() {
      if (typeof this.value === 'number') {
        return this.value.toLocaleString('zh-CN', {
          minimumFractionDigits: this.precision,
          maximumFractionDigits: this.precision
        })
      }
      return this.value
    },
    
    trendClass() {
      if (!this.trend) return ''
      return {
        'trend-up': this.trend.type === 'up',
        'trend-down': this.trend.type === 'down',
        'trend-flat': this.trend.type === 'flat'
      }
    },
    
    trendIcon() {
      if (!this.trend) return ''
      switch (this.trend.type) {
        case 'up': return 'arrow-up'
        case 'down': return 'arrow-down'
        case 'flat': return 'minus'
        default: return 'minus'
      }
    },
    
    trendText() {
      if (!this.trend) return ''
      const sign = this.trend.type === 'up' ? '+' : this.trend.type === 'down' ? '-' : ''
      return `${sign}${this.trend.value}% ${this.trend.text || ''}`
    }
  }
}
</script>

<style lang="less" scoped>
.metric-card {
  height: 100%;
  
  .ant-card-body {
    padding: 24px;
  }
  
  .metric-content {
    display: flex;
    align-items: flex-start;
    gap: 16px;
    
    .metric-icon {
      width: 48px;
      height: 48px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #fff;
      font-size: 20px;
      flex-shrink: 0;
    }
    
    .metric-data {
      flex: 1;
      
      .metric-title {
        font-size: 14px;
        color: #666;
        margin-bottom: 8px;
        line-height: 1.4;
      }
      
      .metric-value {
        font-size: 28px;
        font-weight: 600;
        color: #333;
        line-height: 1.2;
        margin-bottom: 8px;
        
        .prefix,
        .suffix {
          font-size: 18px;
          font-weight: 400;
          color: #666;
        }
        
        .value {
          margin: 0 2px;
        }
      }
      
      .metric-trend {
        font-size: 12px;
        display: flex;
        align-items: center;
        gap: 4px;
        
        &.trend-up {
          color: #52c41a;
        }
        
        &.trend-down {
          color: #ff4d4f;
        }
        
        &.trend-flat {
          color: #666;
        }
      }
    }
    
    .metric-extra {
      flex-shrink: 0;
      align-self: flex-start;
    }
  }
  
  .metric-description {
    margin-top: 16px;
    padding-top: 16px;
    border-top: 1px solid #f0f0f0;
    font-size: 12px;
    color: #666;
    line-height: 1.4;
  }
}

// 悬停效果
.metric-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
  transition: all 0.3s ease;
}

// 响应式设计
@media (max-width: 768px) {
  .metric-card {
    .metric-content {
      .metric-icon {
        width: 40px;
        height: 40px;
        font-size: 18px;
      }
      
      .metric-data {
        .metric-value {
          font-size: 24px;
          
          .prefix,
          .suffix {
            font-size: 16px;
          }
        }
      }
    }
  }
}
</style>
