# jshERP 系统二次开发指南

## 目录
- [核心开发资料](#核心开发资料)
- [数据库设计详解](#数据库设计详解)
- [API 接口文档](#api-接口文档)
- [功能清单分析](#功能清单分析)
- [用户手册参考](#用户手册参考)
- [系统架构分析](#系统架构分析)
- [二次开发实践](#二次开发实践)
- [开发环境配置](#开发环境配置)
- [常见开发场景](#常见开发场景)
- [技术支持与联系](#技术支持与联系)

## 核心开发资料

### 必备文档资源
1. **数据库设计汇总**
   - 文件位置：`file:///Users/macmini/Desktop/jshERP-master/jshERP-boot/docs/管伊佳ERP数据库设计汇总.xlsx`
   - 内容：完整的数据库表结构、字段说明、关系图
   - 用途：数据库扩展、表结构理解、字段含义查询

2. **API 接口文档**
   - 在线地址：https://apifox.com/apidoc/shared/088347e3-c579-40ec-bc8d-3fbb4cd48c2b
   - 内容：完整的 REST API 接口文档
   - 用途：接口调用、参数说明、返回值格式

3. **功能清单**
   - 文件位置：`file:///Users/macmini/Desktop/jshERP-0612-Cursor/jshERP-boot/docs/fun-list (1).xlsx`
   - 内容：系统功能模块详细清单
   - 用途：功能理解、模块划分、开发规划

4. **用户手册**
   - 在线地址：https://www.kdocs.cn/l/ciWZFoGoCvU1
   - 内容：系统使用说明、操作流程
   - 用途：业务流程理解、用户需求分析

### 本地开发资源
- **源码位置**：`/Users/macmini/Desktop/jshERP-0612-Cursor`
- **数据库脚本**：`jshERP-boot/docs/jsh_erp.sql`
- **更新记录**：`jshERP-boot/docs/数据库更新记录-首次安装请勿使用.txt`

## 数据库设计详解

### 数据库架构概述
基于 `管伊佳ERP数据库设计汇总.xlsx` 的分析，jshERP 采用多租户架构设计，核心特点：

- **多租户隔离**：每张业务表都包含 `tenant_id` 字段
- **软删除机制**：使用 `delete_flag` 字段标记删除状态
- **审计字段**：包含创建时间、更新时间、创建人等审计信息
- **编码规范**：统一的字段命名和数据类型规范

### 核心表结构分析

#### 1. 用户权限体系
```sql
-- 用户表
jsh_user: 用户基本信息
├── id: 主键ID
├── username: 用户名
├── login_name: 登录名
├── password: 密码（加密存储）
├── position: 职位
├── department: 部门
├── email: 邮箱
├── phone_number: 手机号
├── is_manager: 是否经理
├── is_system: 是否系统用户
├── status: 状态（0禁用 1启用）
├── description: 描述
├── remark: 备注
├── tenant_id: 租户ID
└── delete_flag: 删除标记

-- 角色表
jsh_role: 角色定义
├── id: 主键ID
├── name: 角色名称
├── type: 角色类型
├── value: 角色值
├── description: 描述
├── tenant_id: 租户ID
└── delete_flag: 删除标记

-- 用户角色关系表
jsh_user_business: 用户业务关系
├── id: 主键ID
├── type: 业务类型（UserRole/UserCustomer/UserDepot等）
├── key_id: 关联ID（用户ID/角色ID等）
├── value: 关联值
├── btn_str: 按钮权限字符串
├── tenant_id: 租户ID
└── delete_flag: 删除标记
```

#### 2. 商品管理体系
```sql
-- 商品表
jsh_material: 商品基本信息
├── id: 主键ID
├── category_id: 商品类别ID
├── name: 商品名称
├── mfrs: 制造商
├── other_field1-3: 自定义字段
├── unit_id: 基本单位ID
├── safe_stock: 安全库存
├── enabled: 启用状态
├── tenant_id: 租户ID
└── delete_flag: 删除标记

-- 商品类别表
jsh_material_category: 商品分类
├── id: 主键ID
├── name: 类别名称
├── category_level: 类别级别
├── parent_id: 父类别ID
├── sort: 排序
├── status: 状态
├── tenant_id: 租户ID
└── delete_flag: 删除标记

-- 商品扩展表
jsh_material_extend: 商品扩展信息
├── id: 主键ID
├── material_id: 商品ID
├── bar_code: 条码
├── commodity_unit: 商品单位
├── sku: SKU编码
├── purchase_decimal: 采购价小数位
├── commodity_decimal: 零售价小数位
├── wholesale_decimal: 批发价小数位
├── low_decimal: 最低售价小数位
├── tenant_id: 租户ID
└── delete_flag: 删除标记
```

#### 3. 单据管理体系
```sql
-- 单据主表
jsh_depot_head: 单据主表
├── id: 主键ID
├── type: 单据类型（入库/出库/调拨等）
├── sub_type: 单据子类型
├── default_number: 单据编号
├── number: 单据号
├── create_time: 创建时间
├── oper_time: 操作时间
├── organ_id: 机构ID
├── creator: 创建人
├── account_id_list: 账户ID列表
├── change_amount: 变动金额
├── all_change_amount: 总变动金额
├── total_price: 总价
├── pay_type: 付款类型
├── remark: 备注
├── file_name: 附件名称
├── status: 状态（0未审核 1已审核）
├── purchase_status: 采购状态
├── source: 单据来源
├── link_number: 关联单号
├── tenant_id: 租户ID
└── delete_flag: 删除标记

-- 单据明细表
jsh_depot_item: 单据明细
├── id: 主键ID
├── header_id: 主表ID
├── material_id: 商品ID
├── material_extend_id: 商品扩展ID
├── material_unit: 商品单位
├── sku: SKU
├── oper_number: 数量
├── basic_number: 基本数量
├── unit_price: 单价
├── tax_unit_price: 含税单价
├── all_price: 总价
├── remark: 备注
├── img: 图片
├── tenant_id: 租户ID
└── delete_flag: 删除标记
```

#### 4. 财务管理体系
```sql
-- 财务主表
jsh_account_head: 财务主表
├── id: 主键ID
├── type: 类型（收入/支出/收款/付款/转账）
├── bill_no: 单据号
├── bill_time: 单据时间
├── organ_id: 往来单位ID
├── hands_person_id: 经手人ID
├── change_amount: 变动金额
├── total_price: 总金额
├── remark: 备注
├── file_name: 附件
├── status: 状态
├── tenant_id: 租户ID
└── delete_flag: 删除标记

-- 财务明细表
jsh_account_item: 财务明细
├── id: 主键ID
├── header_id: 主表ID
├── account_id: 账户ID
├── in_out_item_id: 收支项目ID
├── bill_id: 关联单据ID
├── need_debt: 应收应付
├── finish_debt: 已收已付
├── each_amount: 单项金额
├── remark: 备注
├── tenant_id: 租户ID
└── delete_flag: 删除标记
```

### 多租户设计原理
```sql
-- 多租户字段设计
tenant_id BIGINT(20) DEFAULT NULL COMMENT '租户id'

-- 多租户查询示例
SELECT * FROM jsh_material
WHERE tenant_id = #{tenantId}
AND delete_flag = '0'
AND enabled = 1
```

## 功能清单分析

### 基于 fun-list.xlsx 的功能模块划分

#### 1. 零售管理模块
- **零售出库**
  - 功能：POS销售、会员销售、散客销售
  - 涉及表：jsh_depot_head, jsh_depot_item, jsh_material
  - 业务流程：选择商品 → 计算金额 → 选择付款方式 → 生成出库单

- **零售退货**
  - 功能：退货处理、退款管理
  - 涉及表：jsh_depot_head, jsh_depot_item
  - 业务流程：选择原单 → 选择退货商品 → 计算退款金额 → 生成退货单

#### 2. 采购管理模块
- **采购入库**
  - 功能：采购商品入库、供应商管理
  - 涉及表：jsh_depot_head, jsh_depot_item, jsh_supplier
  - 业务流程：选择供应商 → 添加商品 → 确认价格 → 生成入库单

- **采购退货**
  - 功能：采购商品退货
  - 涉及表：jsh_depot_head, jsh_depot_item
  - 业务流程：选择原采购单 → 选择退货商品 → 生成退货单

#### 3. 销售管理模块
- **销售出库**
  - 功能：批发销售、客户管理
  - 涉及表：jsh_depot_head, jsh_depot_item, jsh_supplier
  - 业务流程：选择客户 → 添加商品 → 确认价格 → 生成出库单

- **销售退货**
  - 功能：销售商品退货
  - 涉及表：jsh_depot_head, jsh_depot_item
  - 业务流程：选择原销售单 → 选择退货商品 → 生成退货单

#### 4. 仓库管理模块
- **其它入库**
  - 功能：盘盈入库、调拨入库、组装入库
  - 涉及表：jsh_depot_head, jsh_depot_item, jsh_depot
  - 业务流程：选择仓库 → 添加商品 → 填写入库原因 → 生成入库单

- **其它出库**
  - 功能：盘亏出库、调拨出库、拆卸出库
  - 涉及表：jsh_depot_head, jsh_depot_item, jsh_depot
  - 业务流程：选择仓库 → 选择商品 → 填写出库原因 → 生成出库单

#### 5. 财务管理模块
- **收款管理**
  - 功能：应收款管理、收款单据
  - 涉及表：jsh_account_head, jsh_account_item, jsh_account
  - 业务流程：选择客户 → 选择应收单据 → 填写收款金额 → 选择收款账户

- **付款管理**
  - 功能：应付款管理、付款单据
  - 涉及表：jsh_account_head, jsh_account_item, jsh_account
  - 业务流程：选择供应商 → 选择应付单据 → 填写付款金额 → 选择付款账户

## 用户手册参考

### 在线用户手册
- **访问地址**：https://www.kdocs.cn/l/ciWZFoGoCvU1
- **内容概述**：
  - 系统操作流程说明
  - 各功能模块使用指南
  - 常见问题解答
  - 业务场景操作示例

### 业务流程理解
通过用户手册可以深入理解：
- **业务逻辑**：各模块间的业务关联关系
- **操作流程**：标准的业务操作步骤
- **数据流转**：数据在各模块间的流转过程
- **权限控制**：不同角色的功能权限范围

## 系统架构分析

### 技术架构
```
前端层：Vue.js + Ant Design Vue
├── 路由管理：Vue Router
├── 状态管理：Vuex
├── HTTP通信：Axios
└── 构建工具：Vue CLI + Webpack

业务层：Spring Boot + MyBatis Plus
├── 控制器层：Controller
├── 服务层：Service
├── 数据访问层：Mapper
├── 实体层：Entity
└── 配置层：Configuration

数据层：MySQL + Redis
├── 主数据库：MySQL 5.7+
├── 缓存层：Redis 6.x
├── 多租户：tenant_id 字段隔离
└── 软删除：delete_flag 标记
```

### 项目结构
```
jshERP-boot/                    # 后端项目
├── src/main/java/com/jsh/erp/
│   ├── controller/             # 控制器层
│   │   ├── UserController.java
│   │   ├── MaterialController.java
│   │   └── DepotController.java
│   ├── service/                # 服务层
│   │   ├── user/
│   │   ├── material/
│   │   └── depot/
│   ├── datasource/             # 数据访问层
│   │   ├── entities/           # 实体类
│   │   ├── mappers/            # Mapper接口
│   │   └── vo/                 # 视图对象
│   ├── utils/                  # 工具类
│   └── config/                 # 配置类
├── src/main/resources/
│   ├── mapper_xml/             # MyBatis映射文件
│   ├── application.properties  # 配置文件
│   └── static/                 # 静态资源
└── docs/                       # 文档目录

jshERP-web/                     # 前端项目
├── src/
│   ├── views/                  # 页面组件
│   │   ├── user/
│   │   ├── material/
│   │   └── depot/
│   ├── components/             # 公共组件
│   ├── api/                    # API接口
│   ├── store/                  # Vuex状态管理
│   ├── router/                 # 路由配置
│   └── utils/                  # 工具函数
├── public/                     # 公共资源
└── package.json                # 依赖配置
```

## 二次开发实践

### 开发环境准备

#### 1. 基础环境要求
```bash
# Java 环境
Java JDK 1.8+
Maven 3.6+

# 前端环境
Node.js 14+
npm 6+ 或 yarn 1.22+

# 数据库环境
MySQL 5.7+
Redis 6.0+

# 开发工具推荐
IDE: IntelliJ IDEA / Eclipse
前端: VS Code
数据库: Navicat / DBeaver
API测试: Postman / Apifox
```

#### 2. 项目启动步骤
```bash
# 1. 克隆项目（如果从远程获取）
git clone https://gitee.com/jishenghua/JSH_ERP.git

# 2. 数据库初始化
mysql -u root -p < jshERP-boot/docs/jsh_erp.sql

# 3. 修改配置文件
vim jshERP-boot/src/main/resources/application.properties
# 配置数据库连接、Redis连接等

# 4. 启动后端
cd jshERP-boot
mvn clean install
mvn spring-boot:run

# 5. 启动前端
cd jshERP-web
npm install
npm run serve

# 6. 访问系统
# 前端：http://localhost:8080
# 后端：http://localhost:9999
# API文档：http://localhost:9999/doc.html
```

### 开发规范与最佳实践

#### 1. 代码规范
```java
// 控制器层示例
@RestController
@RequestMapping("/api/custom")
public class CustomController {

    @Autowired
    private CustomService customService;

    @PostMapping("/add")
    public ResponseEntity<?> addCustom(@RequestBody CustomVO customVO,
                                       HttpServletRequest request) {
        try {
            // 获取当前用户信息
            User user = userService.getCurrentUser();

            // 业务逻辑处理
            customService.addCustom(customVO, user);

            // 记录操作日志
            logService.insertLog("自定义模块", "新增操作", request);

            return ResponseEntity.ok("操作成功");
        } catch (Exception e) {
            logger.error("新增失败", e);
            return ResponseEntity.status(500).body("操作失败");
        }
    }
}

// 服务层示例
@Service
public class CustomService {

    @Autowired
    private CustomMapper customMapper;

    @Transactional(rollbackFor = Exception.class)
    public void addCustom(CustomVO customVO, User user) throws Exception {
        // 数据验证
        validateCustomData(customVO);

        // 构建实体对象
        Custom custom = new Custom();
        BeanUtils.copyProperties(customVO, custom);
        custom.setTenantId(user.getTenantId());
        custom.setCreateTime(new Date());
        custom.setCreator(user.getId());

        // 保存数据
        customMapper.insertSelective(custom);
    }
}
```

#### 2. 数据库操作规范
```xml
<!-- MyBatis映射文件示例 -->
<mapper namespace="com.jsh.erp.datasource.mappers.CustomMapper">

    <!-- 基础查询 -->
    <select id="selectByExample" resultType="com.jsh.erp.datasource.entities.Custom">
        SELECT * FROM jsh_custom
        WHERE tenant_id = #{tenantId}
        AND delete_flag = '0'
        <if test="status != null">
            AND status = #{status}
        </if>
        ORDER BY create_time DESC
    </select>

    <!-- 分页查询 -->
    <select id="selectByExampleWithPage" resultType="com.jsh.erp.datasource.entities.Custom">
        SELECT * FROM jsh_custom
        WHERE tenant_id = #{tenantId}
        AND delete_flag = '0'
        <if test="keyword != null and keyword != ''">
            AND (name LIKE CONCAT('%', #{keyword}, '%')
                 OR code LIKE CONCAT('%', #{keyword}, '%'))
        </if>
        ORDER BY create_time DESC
        LIMIT #{offset}, #{limit}
    </select>

</mapper>
```

#### 3. 前端开发规范
```javascript
// API接口定义
import { axios } from '@/utils/request'

const api = {
  customList: '/api/custom/list',
  customAdd: '/api/custom/add',
  customUpdate: '/api/custom/update',
  customDelete: '/api/custom/delete'
}

export default api

export function getCustomList(parameter) {
  return axios({
    url: api.customList,
    method: 'get',
    params: parameter
  })
}

export function addCustom(parameter) {
  return axios({
    url: api.customAdd,
    method: 'post',
    data: parameter
  })
}

// Vue组件示例
<template>
  <div class="custom-list">
    <a-card :bordered="false">
      <!-- 查询表单 -->
      <div class="table-page-search-wrapper">
        <a-form layout="inline">
          <a-row :gutter="48">
            <a-col :md="8" :sm="24">
              <a-form-item label="名称">
                <a-input v-model="queryParam.name" placeholder="请输入名称"/>
              </a-form-item>
            </a-col>
            <a-col :md="8" :sm="24">
              <a-form-item label="状态">
                <a-select v-model="queryParam.status" placeholder="请选择状态">
                  <a-select-option value="1">启用</a-select-option>
                  <a-select-option value="0">禁用</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :md="8" :sm="24">
              <span class="table-page-search-submitButtons">
                <a-button type="primary" @click="$refs.table.refresh(true)">查询</a-button>
                <a-button style="margin-left: 8px" @click="() => this.queryParam = {}">重置</a-button>
              </span>
            </a-col>
          </a-row>
        </a-form>
      </div>

      <!-- 表格 -->
      <s-table
        ref="table"
        size="default"
        rowKey="id"
        :columns="columns"
        :data="loadData"
        :alert="true"
        :rowSelection="rowSelection"
        showPagination="auto"
      >
        <!-- 操作列 -->
        <span slot="action" slot-scope="text, record">
          <template>
            <a @click="handleEdit(record)">编辑</a>
            <a-divider type="vertical" />
            <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
              <a>删除</a>
            </a-popconfirm>
          </template>
        </span>
      </s-table>
    </a-card>
  </div>
</template>

<script>
import { STable } from '@/components'
import { getCustomList, deleteCustom } from '@/api/custom'

export default {
  name: 'CustomList',
  components: {
    STable
  },
  data () {
    return {
      queryParam: {},
      columns: [
        {
          title: '名称',
          dataIndex: 'name'
        },
        {
          title: '编码',
          dataIndex: 'code'
        },
        {
          title: '状态',
          dataIndex: 'status',
          customRender: (text) => {
            return text === '1' ? '启用' : '禁用'
          }
        },
        {
          title: '操作',
          dataIndex: 'action',
          width: '150px',
          scopedSlots: { customRender: 'action' }
        }
      ],
      loadData: parameter => {
        const requestParameters = Object.assign({}, parameter, this.queryParam)
        return getCustomList(requestParameters)
          .then(res => {
            return res.data
          })
      },
      selectedRowKeys: [],
      selectedRows: []
    }
  },
  methods: {
    handleEdit (record) {
      this.$router.push({ path: '/custom/edit', query: { id: record.id } })
    },
    handleDelete (id) {
      deleteCustom({ id: id }).then(res => {
        if (res.code === 200) {
          this.$message.success('删除成功')
          this.$refs.table.refresh()
        }
      })
    }
  }
}
</script>
```

## 常见开发场景

### 场景1：新增业务模块

#### 需求描述
为系统新增一个"设备管理"模块，包括设备信息维护、设备状态跟踪等功能。

#### 实现步骤

**1. 数据库设计**
```sql
-- 创建设备表
CREATE TABLE `jsh_equipment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `code` varchar(50) DEFAULT NULL COMMENT '设备编码',
  `name` varchar(100) NOT NULL COMMENT '设备名称',
  `type` varchar(50) DEFAULT NULL COMMENT '设备类型',
  `model` varchar(100) DEFAULT NULL COMMENT '设备型号',
  `manufacturer` varchar(100) DEFAULT NULL COMMENT '制造商',
  `purchase_date` datetime DEFAULT NULL COMMENT '采购日期',
  `warranty_date` datetime DEFAULT NULL COMMENT '保修期至',
  `status` varchar(20) DEFAULT '1' COMMENT '状态：1正常 2维修 3报废',
  `location` varchar(200) DEFAULT NULL COMMENT '存放位置',
  `responsible_person` varchar(50) DEFAULT NULL COMMENT '责任人',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户id',
  `delete_flag` varchar(1) DEFAULT '0' COMMENT '删除标记，0未删除，1删除',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `creator` bigint(20) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `updater` bigint(20) DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`),
  KEY `idx_tenant_id` (`tenant_id`),
  KEY `idx_delete_flag` (`delete_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='设备信息表';
```

**2. 后端实现**
```java
// 实体类
@Entity
@Table(name = "jsh_equipment")
public class Equipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;
    private String name;
    private String type;
    private String model;
    private String manufacturer;
    private Date purchaseDate;
    private Date warrantyDate;
    private String status;
    private String location;
    private String responsiblePerson;
    private String remark;
    private Long tenantId;
    private String deleteFlag;
    private Date createTime;
    private Long creator;
    private Date updateTime;
    private Long updater;

    // getter/setter 省略
}

// Mapper接口
@Mapper
public interface EquipmentMapper extends BaseMapper<Equipment> {
    List<Equipment> selectByExample(@Param("example") EquipmentExample example);
    int countByExample(@Param("example") EquipmentExample example);
}

// Service层
@Service
public class EquipmentService {

    @Autowired
    private EquipmentMapper equipmentMapper;

    @Autowired
    private LogService logService;

    public List<Equipment> getEquipmentList(String name, String type, Integer currentPage, Integer pageSize) throws Exception {
        EquipmentExample example = new EquipmentExample();
        example.createCriteria()
                .andDeleteFlagEqualTo("0")
                .andTenantIdEqualTo(getCurrentTenantId());

        if (StringUtils.isNotEmpty(name)) {
            example.createCriteria().andNameLike("%" + name + "%");
        }
        if (StringUtils.isNotEmpty(type)) {
            example.createCriteria().andTypeEqualTo(type);
        }

        PageHelper.startPage(currentPage, pageSize);
        return equipmentMapper.selectByExample(example);
    }

    @Transactional(rollbackFor = Exception.class)
    public int insertEquipment(Equipment equipment, HttpServletRequest request) throws Exception {
        equipment.setTenantId(getCurrentTenantId());
        equipment.setCreateTime(new Date());
        equipment.setCreator(getCurrentUserId());
        equipment.setDeleteFlag("0");

        int result = equipmentMapper.insertSelective(equipment);
        logService.insertLog("设备管理", "新增设备：" + equipment.getName(), request);
        return result;
    }
}

// Controller层
@RestController
@RequestMapping("/equipment")
public class EquipmentController {

    @Autowired
    private EquipmentService equipmentService;

    @GetMapping("/list")
    public ResponseEntity<?> getEquipmentList(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "currentPage", defaultValue = "1") Integer currentPage,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        try {
            List<Equipment> list = equipmentService.getEquipmentList(name, type, currentPage, pageSize);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("查询失败");
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> addEquipment(@RequestBody Equipment equipment, HttpServletRequest request) {
        try {
            equipmentService.insertEquipment(equipment, request);
            return ResponseEntity.ok("新增成功");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("新增失败");
        }
    }
}
```

**3. 前端实现**
```javascript
// API接口定义 (src/api/equipment.js)
import { axios } from '@/utils/request'

const api = {
  equipmentList: '/equipment/list',
  equipmentAdd: '/equipment/add',
  equipmentUpdate: '/equipment/update',
  equipmentDelete: '/equipment/delete'
}

export function getEquipmentList(parameter) {
  return axios({
    url: api.equipmentList,
    method: 'get',
    params: parameter
  })
}

export function addEquipment(parameter) {
  return axios({
    url: api.equipmentAdd,
    method: 'post',
    data: parameter
  })
}

// 路由配置 (src/router/index.js)
{
  path: '/equipment',
  name: 'equipment',
  component: () => import('@/views/equipment/EquipmentList'),
  meta: { title: '设备管理', icon: 'tool', permission: ['equipment:list'] }
}

// 页面组件 (src/views/equipment/EquipmentList.vue)
<template>
  <div class="equipment-list">
    <a-card :bordered="false">
      <!-- 查询表单 -->
      <div class="table-page-search-wrapper">
        <a-form layout="inline">
          <a-row :gutter="48">
            <a-col :md="8" :sm="24">
              <a-form-item label="设备名称">
                <a-input v-model="queryParam.name" placeholder="请输入设备名称"/>
              </a-form-item>
            </a-col>
            <a-col :md="8" :sm="24">
              <a-form-item label="设备类型">
                <a-select v-model="queryParam.type" placeholder="请选择设备类型">
                  <a-select-option value="生产设备">生产设备</a-select-option>
                  <a-select-option value="办公设备">办公设备</a-select-option>
                  <a-select-option value="检测设备">检测设备</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :md="8" :sm="24">
              <span class="table-page-search-submitButtons">
                <a-button type="primary" @click="$refs.table.refresh(true)">查询</a-button>
                <a-button style="margin-left: 8px" @click="() => this.queryParam = {}">重置</a-button>
              </span>
            </a-col>
          </a-row>
        </a-form>
      </div>

      <!-- 操作按钮 -->
      <div class="table-operator">
        <a-button type="primary" icon="plus" @click="handleAdd">新建</a-button>
      </div>

      <!-- 表格 -->
      <s-table
        ref="table"
        size="default"
        rowKey="id"
        :columns="columns"
        :data="loadData"
        showPagination="auto"
      >
        <span slot="status" slot-scope="text">
          <a-badge :status="text === '1' ? 'success' : text === '2' ? 'warning' : 'error'"
                   :text="text === '1' ? '正常' : text === '2' ? '维修' : '报废'" />
        </span>

        <span slot="action" slot-scope="text, record">
          <template>
            <a @click="handleEdit(record)">编辑</a>
            <a-divider type="vertical" />
            <a @click="handleView(record)">查看</a>
            <a-divider type="vertical" />
            <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
              <a>删除</a>
            </a-popconfirm>
          </template>
        </span>
      </s-table>
    </a-card>
  </div>
</template>
```

### 场景2：扩展现有单据功能

#### 需求描述
在采购入库单中增加"质检状态"字段，支持质检流程管理。

#### 实现步骤

**1. 数据库扩展**
```sql
-- 在单据主表中增加质检相关字段
ALTER TABLE jsh_depot_head ADD COLUMN quality_status VARCHAR(20) DEFAULT '0' COMMENT '质检状态：0待质检 1质检合格 2质检不合格';
ALTER TABLE jsh_depot_head ADD COLUMN quality_person VARCHAR(50) DEFAULT NULL COMMENT '质检人员';
ALTER TABLE jsh_depot_head ADD COLUMN quality_time DATETIME DEFAULT NULL COMMENT '质检时间';
ALTER TABLE jsh_depot_head ADD COLUMN quality_remark VARCHAR(500) DEFAULT NULL COMMENT '质检备注';
```

**2. 实体类扩展**
```java
// 在 DepotHead 实体类中增加字段
public class DepotHead {
    // 原有字段...

    private String qualityStatus;
    private String qualityPerson;
    private Date qualityTime;
    private String qualityRemark;

    // getter/setter 方法
}
```

**3. 业务逻辑扩展**
```java
@Service
public class DepotService {

    // 质检审核方法
    @Transactional(rollbackFor = Exception.class)
    public void qualityCheck(Long headerId, String qualityStatus, String qualityRemark, HttpServletRequest request) throws Exception {
        DepotHead depotHead = depotHeadMapper.selectByPrimaryKey(headerId);
        if (depotHead == null) {
            throw new BusinessException("单据不存在");
        }

        // 更新质检状态
        depotHead.setQualityStatus(qualityStatus);
        depotHead.setQualityPerson(getCurrentUser().getUsername());
        depotHead.setQualityTime(new Date());
        depotHead.setQualityRemark(qualityRemark);

        depotHeadMapper.updateByPrimaryKeySelective(depotHead);

        // 记录日志
        String operation = "1".equals(qualityStatus) ? "质检合格" : "质检不合格";
        logService.insertLog("采购管理", operation + "：" + depotHead.getNumber(), request);

        // 如果质检不合格，可以触发退货流程
        if ("2".equals(qualityStatus)) {
            // 处理质检不合格逻辑
            handleQualityReject(depotHead);
        }
    }
}
```

### 场景3：自定义报表开发

#### 需求描述
开发一个"销售趋势分析报表"，按月统计销售数据并生成图表。

#### 实现步骤

**1. 数据查询SQL**
```sql
-- 销售趋势统计查询
SELECT
    DATE_FORMAT(dh.oper_time, '%Y-%m') as month,
    SUM(dh.total_price) as total_amount,
    COUNT(dh.id) as order_count,
    AVG(dh.total_price) as avg_amount
FROM jsh_depot_head dh
WHERE dh.type = '出库'
    AND dh.sub_type = '销售'
    AND dh.status = '1'
    AND dh.delete_flag = '0'
    AND dh.tenant_id = #{tenantId}
    AND dh.oper_time >= #{startDate}
    AND dh.oper_time <= #{endDate}
GROUP BY DATE_FORMAT(dh.oper_time, '%Y-%m')
ORDER BY month;
```

**2. 后端实现**
```java
// 报表VO类
public class SalesTrendVO {
    private String month;
    private BigDecimal totalAmount;
    private Integer orderCount;
    private BigDecimal avgAmount;

    // getter/setter
}

// 报表Service
@Service
public class ReportService {

    @Autowired
    private DepotHeadMapperEx depotHeadMapperEx;

    public List<SalesTrendVO> getSalesTrend(String startDate, String endDate) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("tenantId", getCurrentTenantId());
        params.put("startDate", startDate);
        params.put("endDate", endDate);

        return depotHeadMapperEx.getSalesTrend(params);
    }
}

// 报表Controller
@RestController
@RequestMapping("/report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/sales-trend")
    public ResponseEntity<?> getSalesTrend(
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate) {
        try {
            List<SalesTrendVO> data = reportService.getSalesTrend(startDate, endDate);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("查询失败");
        }
    }
}
```

**3. 前端图表实现**
```vue
<template>
  <div class="sales-trend-report">
    <a-card title="销售趋势分析">
      <!-- 查询条件 -->
      <div class="search-form">
        <a-range-picker v-model="dateRange" @change="onDateChange" />
        <a-button type="primary" @click="loadData">查询</a-button>
      </div>

      <!-- 图表 -->
      <div ref="chart" style="height: 400px;"></div>
    </a-card>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { getSalesTrend } from '@/api/report'

export default {
  name: 'SalesTrendReport',
  data() {
    return {
      dateRange: [],
      chartInstance: null
    }
  },
  mounted() {
    this.initChart()
    this.loadData()
  },
  methods: {
    initChart() {
      this.chartInstance = echarts.init(this.$refs.chart)
    },

    async loadData() {
      if (!this.dateRange || this.dateRange.length !== 2) {
        this.$message.warning('请选择日期范围')
        return
      }

      try {
        const params = {
          startDate: this.dateRange[0].format('YYYY-MM-DD'),
          endDate: this.dateRange[1].format('YYYY-MM-DD')
        }

        const response = await getSalesTrend(params)
        this.renderChart(response.data)
      } catch (error) {
        this.$message.error('数据加载失败')
      }
    },

    renderChart(data) {
      const option = {
        title: {
          text: '销售趋势分析'
        },
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: ['销售金额', '订单数量']
        },
        xAxis: {
          type: 'category',
          data: data.map(item => item.month)
        },
        yAxis: [
          {
            type: 'value',
            name: '销售金额',
            position: 'left'
          },
          {
            type: 'value',
            name: '订单数量',
            position: 'right'
          }
        ],
        series: [
          {
            name: '销售金额',
            type: 'line',
            data: data.map(item => item.totalAmount),
            yAxisIndex: 0
          },
          {
            name: '订单数量',
            type: 'bar',
            data: data.map(item => item.orderCount),
            yAxisIndex: 1
          }
        ]
      }

      this.chartInstance.setOption(option)
    }
  }
}
</script>
```

## 二次开发建议

### 1. 开发环境搭建
```bash
# 1. 克隆项目
git clone https://gitee.com/jishenghua/JSH_ERP.git

# 2. 后端环境
cd jshERP-boot
mvn clean install

# 3. 前端环境
cd jshERP-web
npm install
npm run serve

# 4. 数据库初始化
mysql -u root -p < docs/jsh_erp.sql
```

### 2. 代码结构分析
```
jshERP-boot/
├── src/main/java/com/jsh/erp/
│   ├── controller/     # 控制器层
│   ├── service/        # 业务逻辑层
│   ├── datasource/     # 数据访问层
│   ├── utils/          # 工具类
│   └── config/         # 配置类
├── src/main/resources/
│   ├── mapper_xml/     # MyBatis 映射文件
│   └── application.properties  # 配置文件
```

### 3. 开发规范
- **命名规范**：遵循 Java 命名规范
- **代码注释**：关键业务逻辑必须注释
- **异常处理**：统一异常处理机制
- **日志记录**：重要操作记录日志
- **事务管理**：合理使用事务注解

### 4. 扩展建议
- **新增模块**：参考现有模块结构
- **数据库扩展**：遵循多租户设计原则
- **API 扩展**：遵循 RESTful 设计规范
- **前端扩展**：遵循 Vue.js 组件化开发

## 定制服务案例

### 成功案例
1. **光普森科ERP系统**：http://wh.gpthink.com/
2. **布草管理系统**：https://egg.jkfsjc.com/
3. **明途云掌柜**：http://erp.zhangzhixiang.cn/
4. **OMS系统**：http://oms.diwgroup.com.cn/
5. **协同平台**：https://cw.wjxtzd.com/

### 定制服务流程
1. **需求分析**：详细需求文档
2. **技术评估**：复杂度和周期评估
3. **方案设计**：技术方案设计
4. **开发实施**：按计划开发
5. **测试部署**：测试和上线
6. **维护支持**：后期维护服务

### 联系方式
- **邮箱**：752718920@qq.com
- **QQ**：752718920
- **微信**：shenhua861584
- **淘宝店铺**：https://shop104070207.taobao.com

## 开发环境搭建

### 系统要求
- **JDK**：1.8+
- **Maven**：3.6+
- **Node.js**：14+
- **MySQL**：5.7+
- **Redis**：6.0+

### 快速启动
```bash
# 1. 启动 MySQL 和 Redis
docker-compose up -d jsherp-mysql-dev jsherp-redis-dev

# 2. 启动后端
cd jshERP-boot
mvn spring-boot:run

# 3. 启动前端
cd jshERP-web
npm run serve

# 4. 访问系统
# 前端：http://localhost:8080
# 后端：http://localhost:9999
# 接口文档：http://localhost:9999/doc.html
```

### 默认账号
- **超管账户**：admin / 123456
- **租户账户**：jsh / 123456

## API 接口文档详解

### 接口文档地址
- **在线文档**：https://apifox.com/apidoc/shared-088347e3-c579-40ec-bc8d-3fbb4cd48c2b
- **本地访问**：http://localhost:9999/jshERP-boot/doc.html

### 核心接口模块

#### 1. 用户管理接口
```
POST /user/login          # 用户登录
GET  /user/randomImage     # 获取验证码
POST /user/register        # 用户注册
PUT  /user/updatePassword  # 修改密码
GET  /user/getUserSession  # 获取用户会话信息
```

#### 2. 商品管理接口
```
GET  /material/list        # 商品列表查询
POST /material/add         # 新增商品
PUT  /material/update      # 更新商品
DELETE /material/delete    # 删除商品
GET  /material/findByBarCode # 根据条码查询商品
```

#### 3. 单据管理接口
```
GET  /depotHead/list       # 单据列表查询
POST /depotHead/add        # 新增单据
PUT  /depotHead/update     # 更新单据
DELETE /depotHead/delete   # 删除单据
POST /depotHead/batchSetStatus # 批量审核
```

### 接口调用示例
```javascript
// 登录接口调用
const loginData = {
  loginName: 'admin',
  password: 'e10adc3949ba59abbe56e057f20f883e', // MD5加密
  code: '1234',
  uuid: 'uuid-string'
}

axios.post('/user/login', loginData)
  .then(response => {
    const token = response.data.data.token
    localStorage.setItem('Access-Token', token)
  })
```

## 技术支持与联系

### 官方支持渠道
- **官方网站**：https://www.gyjerp.com
- **技术咨询**：QQ 752718920，微信 shenhua861584
- **定制开发**：752718920@qq.com
- **插件购买**：https://shop104070207.taobao.com

### 核心开发资料
1. **数据库设计**：管伊佳ERP数据库设计汇总.xlsx
2. **功能清单**：fun-list.xlsx
3. **API 文档**：https://apifox.com/apidoc/shared-088347e3-c579-40ec-bc8d-3fbb4cd48c2b
4. **用户手册**：https://www.kdocs.cn/l/ciWZFoGoCvU1

### 学习建议
1. **先理解业务**：通过用户手册了解业务流程
2. **熟悉数据库**：研读数据库设计文档
3. **掌握接口**：熟练使用 API 接口文档
4. **参考案例**：学习现有的定制开发案例

---

**文档版本**：v2.0
**最后更新**：2025年6月13日
**基于版本**：jshERP 最新版本
**核心资料**：基于官方提供的数据库设计、API文档、功能清单、用户手册整理
