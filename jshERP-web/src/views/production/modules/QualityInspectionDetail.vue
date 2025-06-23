<template>
  <a-modal
    title="质检详情"
    :width="1200"
    :visible="visible"
    :footer="null"
    @cancel="handleCancel">
    
    <div class="inspection-detail" v-if="inspectionData">
      <!-- 基本信息 -->
      <div class="detail-section">
        <h3 class="section-title">
          <a-icon type="info-circle" />
          基本信息
        </h3>
        
        <a-row :gutter="24">
          <a-col :span="8">
            <div class="info-item">
              <span class="label">质检编号：</span>
              <span class="value">{{ inspectionData.inspectionNumber }}</span>
            </div>
          </a-col>
          <a-col :span="8">
            <div class="info-item">
              <span class="label">任务编号：</span>
              <span class="value">{{ inspectionData.taskNumber }}</span>
            </div>
          </a-col>
          <a-col :span="8">
            <div class="info-item">
              <span class="label">质检类型：</span>
              <a-tag :color="getInspectionTypeColor(inspectionData.inspectionType)">
                {{ getInspectionTypeText(inspectionData.inspectionType) }}
              </a-tag>
            </div>
          </a-col>
        </a-row>
        
        <a-row :gutter="24" style="margin-top: 16px;">
          <a-col :span="8">
            <div class="info-item">
              <span class="label">产品名称：</span>
              <span class="value">{{ inspectionData.productName }}</span>
            </div>
          </a-col>
          <a-col :span="8">
            <div class="info-item">
              <span class="label">质检员：</span>
              <span class="value">{{ inspectionData.inspectorName }}</span>
            </div>
          </a-col>
          <a-col :span="8">
            <div class="info-item">
              <span class="label">质检时间：</span>
              <span class="value">{{ formatTime(inspectionData.inspectionTime) }}</span>
            </div>
          </a-col>
        </a-row>
      </div>

      <!-- 数量统计 -->
      <div class="detail-section">
        <h3 class="section-title">
          <a-icon type="number" />
          数量统计
        </h3>
        
        <a-row :gutter="24">
          <a-col :span="6">
            <div class="stat-card">
              <div class="stat-title">质检数量</div>
              <div class="stat-value">{{ inspectionData.inspectionQuantity || 0 }}</div>
              <div class="stat-unit">{{ inspectionData.unitName || '个' }}</div>
            </div>
          </a-col>
          <a-col :span="6">
            <div class="stat-card success">
              <div class="stat-title">合格数量</div>
              <div class="stat-value">{{ inspectionData.qualifiedQuantity || 0 }}</div>
              <div class="stat-unit">{{ inspectionData.unitName || '个' }}</div>
            </div>
          </a-col>
          <a-col :span="6">
            <div class="stat-card error">
              <div class="stat-title">不合格数量</div>
              <div class="stat-value">{{ inspectionData.defectiveQuantity || 0 }}</div>
              <div class="stat-unit">{{ inspectionData.unitName || '个' }}</div>
            </div>
          </a-col>
          <a-col :span="6">
            <div class="stat-card">
              <div class="stat-title">合格率</div>
              <div class="stat-value">{{ inspectionData.qualificationRate || 0 }}%</div>
              <div class="stat-progress">
                <a-progress 
                  :percent="inspectionData.qualificationRate || 0" 
                  :status="getProgressStatus(inspectionData.qualificationRate)"
                  size="small" />
              </div>
            </div>
          </a-col>
        </a-row>
      </div>

      <!-- 质量评分 -->
      <div class="detail-section">
        <h3 class="section-title">
          <a-icon type="star" />
          质量评分
        </h3>
        
        <a-row :gutter="24">
          <a-col :span="12">
            <div class="score-grid">
              <div class="score-item">
                <span class="score-label">外观质量：</span>
                <a-rate :value="inspectionData.appearanceScore || 0" disabled allowHalf />
                <span class="score-value">{{ inspectionData.appearanceScore || 0 }}/5</span>
              </div>
              <div class="score-item">
                <span class="score-label">尺寸精度：</span>
                <a-rate :value="inspectionData.sizeScore || 0" disabled allowHalf />
                <span class="score-value">{{ inspectionData.sizeScore || 0 }}/5</span>
              </div>
              <div class="score-item">
                <span class="score-label">颜色效果：</span>
                <a-rate :value="inspectionData.colorScore || 0" disabled allowHalf />
                <span class="score-value">{{ inspectionData.colorScore || 0 }}/5</span>
              </div>
            </div>
          </a-col>
          <a-col :span="12">
            <div class="score-grid">
              <div class="score-item">
                <span class="score-label">表面质感：</span>
                <a-rate :value="inspectionData.textureScore || 0" disabled allowHalf />
                <span class="score-value">{{ inspectionData.textureScore || 0 }}/5</span>
              </div>
              <div class="score-item">
                <span class="score-label">细节处理：</span>
                <a-rate :value="inspectionData.detailScore || 0" disabled allowHalf />
                <span class="score-value">{{ inspectionData.detailScore || 0 }}/5</span>
              </div>
              <div class="score-item">
                <span class="score-label">整体效果：</span>
                <a-rate :value="inspectionData.overallEffectScore || 0" disabled allowHalf />
                <span class="score-value">{{ inspectionData.overallEffectScore || 0 }}/5</span>
              </div>
            </div>
          </a-col>
        </a-row>
        
        <div class="overall-score-section">
          <div class="overall-score-card">
            <div class="overall-title">综合评分</div>
            <div class="overall-rate">
              <a-rate :value="inspectionData.overallScore || 0" disabled allowHalf />
            </div>
            <div class="overall-value">{{ inspectionData.overallScore || 0 }}/5</div>
          </div>
          <div class="grade-card">
            <div class="grade-title">质量等级</div>
            <div class="grade-value">
              <a-tag :color="getGradeColor(inspectionData.qualityGrade)" size="large">
                {{ inspectionData.qualityGrade }}
              </a-tag>
            </div>
            <div class="grade-desc">{{ getGradeDescription(inspectionData.qualityGrade) }}</div>
          </div>
        </div>
      </div>

      <!-- 质检结果 -->
      <div class="detail-section">
        <h3 class="section-title">
          <a-icon type="check-circle" />
          质检结果
        </h3>
        
        <a-row :gutter="24">
          <a-col :span="8">
            <div class="result-card">
              <div class="result-title">总体结果</div>
              <div class="result-value">
                <a-tag :color="getResultColor(inspectionData.overallResult)">
                  <a-icon :type="getResultIcon(inspectionData.overallResult)" />
                  {{ getResultText(inspectionData.overallResult) }}
                </a-tag>
              </div>
            </div>
          </a-col>
          <a-col :span="8">
            <div class="result-card">
              <div class="result-title">质检标准</div>
              <div class="result-value">{{ inspectionData.inspectionStandard || '未设置' }}</div>
            </div>
          </a-col>
          <a-col :span="8">
            <div class="result-card">
              <div class="result-title">创建时间</div>
              <div class="result-value">{{ formatTime(inspectionData.createTime) }}</div>
            </div>
          </a-col>
        </a-row>
      </div>

      <!-- 质检照片 -->
      <div class="detail-section" v-if="inspectionData.qualityPhotos">
        <h3 class="section-title">
          <a-icon type="camera" />
          质检照片
        </h3>
        
        <div class="photo-gallery">
          <div 
            v-for="(photo, index) in photoList" 
            :key="index" 
            class="photo-item"
            @click="previewPhoto(index)">
            <img :src="photo" :alt="`质检照片${index + 1}`" />
            <div class="photo-overlay">
              <a-icon type="eye" />
            </div>
          </div>
        </div>
      </div>

      <!-- 问题描述和改进建议 -->
      <div class="detail-section" v-if="inspectionData.problemDescription || inspectionData.improvementSuggestion">
        <h3 class="section-title">
          <a-icon type="file-text" />
          问题描述和改进建议
        </h3>
        
        <div class="text-content">
          <div v-if="inspectionData.problemDescription" class="text-item">
            <h4>问题描述：</h4>
            <p>{{ inspectionData.problemDescription }}</p>
          </div>
          <div v-if="inspectionData.improvementSuggestion" class="text-item">
            <h4>改进建议：</h4>
            <p>{{ inspectionData.improvementSuggestion }}</p>
          </div>
        </div>
      </div>

      <!-- 备注信息 -->
      <div class="detail-section" v-if="inspectionData.remark">
        <h3 class="section-title">
          <a-icon type="message" />
          备注信息
        </h3>
        
        <div class="remark-content">
          {{ inspectionData.remark }}
        </div>
      </div>
    </div>
  </a-modal>
</template>

<script>
import dayjs from 'dayjs'

export default {
  name: "QualityInspectionDetail",
  data() {
    return {
      visible: false,
      inspectionData: null
    }
  },
  
  computed: {
    // 照片列表
    photoList() {
      if (!this.inspectionData || !this.inspectionData.qualityPhotos) {
        return [];
      }
      
      try {
        return JSON.parse(this.inspectionData.qualityPhotos);
      } catch (e) {
        return this.inspectionData.qualityPhotos.split(',').filter(url => url.trim());
      }
    }
  },
  
  methods: {
    // 显示详情
    show(record) {
      this.inspectionData = record;
      this.visible = true;
    },
    
    // 关闭
    handleCancel() {
      this.visible = false;
      this.inspectionData = null;
    },
    
    // 预览照片
    previewPhoto(index) {
      // TODO: 实现照片预览功能
      this.$message.info('照片预览功能开发中...');
    },
    
    // 获取质检类型文本
    getInspectionTypeText(type) {
      const typeMap = {
        'INCOMING': '来料质检',
        'PROCESS': '过程质检',
        'FINAL': '最终质检',
        'REWORK': '返工质检'
      };
      return typeMap[type] || type;
    },
    
    // 获取质检类型颜色
    getInspectionTypeColor(type) {
      const colorMap = {
        'INCOMING': 'blue',
        'PROCESS': 'orange',
        'FINAL': 'green',
        'REWORK': 'purple'
      };
      return colorMap[type] || 'default';
    },
    
    // 获取结果文本
    getResultText(result) {
      const resultMap = {
        'PASS': '合格',
        'FAIL': '不合格',
        'REWORK': '需返工'
      };
      return resultMap[result] || result;
    },
    
    // 获取结果颜色
    getResultColor(result) {
      const colorMap = {
        'PASS': 'success',
        'FAIL': 'error',
        'REWORK': 'warning'
      };
      return colorMap[result] || 'default';
    },
    
    // 获取结果图标
    getResultIcon(result) {
      const iconMap = {
        'PASS': 'check-circle',
        'FAIL': 'close-circle',
        'REWORK': 'exclamation-circle'
      };
      return iconMap[result] || 'question-circle';
    },
    
    // 获取等级颜色
    getGradeColor(grade) {
      const colorMap = {
        'A+': 'red',
        'A': 'green',
        'B': 'blue',
        'C': 'orange',
        'D': 'default'
      };
      return colorMap[grade] || 'default';
    },
    
    // 获取等级描述
    getGradeDescription(grade) {
      const descMap = {
        'A+': '优秀',
        'A': '良好',
        'B': '合格',
        'C': '一般',
        'D': '不合格'
      };
      return descMap[grade] || '';
    },
    
    // 获取进度状态
    getProgressStatus(rate) {
      if (rate >= 100) return 'success';
      if (rate >= 95) return 'normal';
      if (rate >= 80) return 'active';
      return 'exception';
    },
    
    // 格式化时间
    formatTime(time) {
      if (!time) return '未设置';
      return dayjs(time).format('YYYY-MM-DD HH:mm:ss');
    }
  }
}
</script>

<style scoped>
.inspection-detail {
  padding: 0;
}

.detail-section {
  margin-bottom: 32px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #262626;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  border-bottom: 1px solid #e8e8e8;
  padding-bottom: 8px;
}

.section-title .anticon {
  margin-right: 8px;
  color: #1890ff;
}

.info-item {
  margin-bottom: 8px;
}

.info-item .label {
  color: #8c8c8c;
  margin-right: 8px;
}

.info-item .value {
  color: #262626;
  font-weight: 500;
}

/* 统计卡片样式 */
.stat-card {
  background: #fafafa;
  border-radius: 6px;
  padding: 16px;
  text-align: center;
  border: 1px solid #e8e8e8;
}

.stat-card.success {
  background: #f6ffed;
  border-color: #b7eb8f;
}

.stat-card.error {
  background: #fff2f0;
  border-color: #ffccc7;
}

.stat-title {
  color: #8c8c8c;
  font-size: 12px;
  margin-bottom: 8px;
}

.stat-value {
  color: #262626;
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 4px;
}

.stat-unit {
  color: #8c8c8c;
  font-size: 12px;
}

.stat-progress {
  margin-top: 8px;
}

/* 评分样式 */
.score-grid {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.score-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.score-label {
  width: 80px;
  color: #8c8c8c;
  font-size: 14px;
}

.score-value {
  color: #8c8c8c;
  font-size: 12px;
  margin-left: 8px;
}

.overall-score-section {
  margin-top: 24px;
  display: flex;
  gap: 24px;
  justify-content: center;
}

.overall-score-card, .grade-card {
  background: #fafafa;
  border-radius: 8px;
  padding: 20px;
  text-align: center;
  border: 1px solid #e8e8e8;
  min-width: 200px;
}

.overall-title, .grade-title {
  color: #8c8c8c;
  font-size: 14px;
  margin-bottom: 12px;
}

.overall-rate {
  margin-bottom: 8px;
}

.overall-value {
  color: #262626;
  font-size: 18px;
  font-weight: 600;
}

.grade-value {
  margin-bottom: 8px;
}

.grade-desc {
  color: #8c8c8c;
  font-size: 12px;
}

/* 结果卡片样式 */
.result-card {
  background: #fafafa;
  border-radius: 6px;
  padding: 16px;
  text-align: center;
  border: 1px solid #e8e8e8;
}

.result-title {
  color: #8c8c8c;
  font-size: 12px;
  margin-bottom: 8px;
}

.result-value {
  color: #262626;
  font-weight: 500;
}

/* 照片画廊样式 */
.photo-gallery {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.photo-item {
  position: relative;
  width: 120px;
  height: 120px;
  border-radius: 6px;
  overflow: hidden;
  cursor: pointer;
  border: 1px solid #e8e8e8;
}

.photo-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.photo-overlay {
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

.photo-item:hover .photo-overlay {
  opacity: 1;
}

.photo-overlay .anticon {
  color: white;
  font-size: 24px;
}

/* 文本内容样式 */
.text-content {
  background: #fafafa;
  border-radius: 6px;
  padding: 16px;
}

.text-item {
  margin-bottom: 16px;
}

.text-item:last-child {
  margin-bottom: 0;
}

.text-item h4 {
  color: #262626;
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 8px;
}

.text-item p {
  color: #595959;
  line-height: 1.6;
  margin: 0;
}

.remark-content {
  background: #fafafa;
  border-radius: 6px;
  padding: 16px;
  color: #595959;
  line-height: 1.6;
}
</style>
