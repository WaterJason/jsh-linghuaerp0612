# jshERP二次开发技术参考手册

## 目录
1. [项目架构概览](#1-项目架构概览)
2. [核心架构模式](#2-核心架构模式)
3. [关键业务模块实现模板](#3-关键业务模块实现模板)
4. [可复用组件和工具类](#4-可复用组件和工具类)
5. [编码规范和开发标准](#5-编码规范和开发标准)
6. [安全机制实现](#6-安全机制实现)
7. [开发模板和最佳实践](#7-开发模板和最佳实践)
8. [集成指导](#8-集成指导)

---

## 1. 项目架构概览

### 1.1 整体架构设计

jshERP采用前后端分离的B/S架构模式：

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   前端应用层     │    │   后端应用层     │    │    数据存储层    │
│  (jshERP-web)   │ => │ (jshERP-boot)   │ => │   (MySQL/Redis) │
│                 │    │                 │    │                 │
│ Vue.js + Ant    │    │ Spring Boot +   │    │ MySQL 5.7+     │
│ Design Vue      │    │ MyBatis        │    │ Redis 缓存      │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### 1.2 目录结构

#### 后端目录结构 (jshERP-boot/)
```
jshERP-boot/
├── src/main/java/com/jsh/erp/
│   ├── base/                   # 基础类
│   │   ├── BaseController.java # Controller基类
│   │   ├── AjaxResult.java     # 统一响应格式
│   │   └── TableDataInfo.java  # 分页响应格式
│   ├── config/                 # 配置类
│   │   ├── TenantConfig.java   # 多租户配置
│   │   └── Swagger2Config.java # API文档配置
│   ├── controller/             # 控制层
│   ├── service/                # 服务层
│   ├── datasource/             # 数据源层
│   │   ├── entities/           # 实体类
│   │   ├── mappers/            # Mapper接口
│   │   └── vo/                # 视图对象
│   ├── utils/                  # 工具类
│   │   ├── Tools.java          # 通用工具
│   │   ├── StringUtil.java     # 字符串工具
│   │   └── ResponseJsonUtil.java # 响应工具
│   └── exception/              # 异常处理
└── src/main/resources/
    ├── application.properties  # 主配置文件
    ├── mapper_xml/             # MyBatis映射文件
    └── logback-spring.xml      # 日志配置
```

#### 前端目录结构 (jshERP-web/)
```
jshERP-web/
├── src/
│   ├── api/                    # API接口定义
│   ├── components/             # 通用组件
│   │   ├── jeecg/             # JEECG组件库
│   │   ├── layouts/           # 布局组件
│   │   └── tools/             # 工具组件
│   ├── views/                  # 页面视图
│   │   ├── material/          # 商品管理
│   │   ├── system/            # 系统管理
│   │   └── bill/              # 单据管理
│   ├── store/                  # Vuex状态管理
│   ├── utils/                  # 工具函数
│   │   ├── request.js         # HTTP请求封装
│   │   ├── auth.js            # 权限工具
│   │   └── util.js            # 通用工具
│   └── router/                 # 路由配置
├── public/                     # 静态资源
└── package.json               # 依赖配置
```

---

## 2. 核心架构模式

### 2.1 后端三层架构实现

#### 2.1.1 Controller层标准实现

**文件位置**: `jshERP-boot/src/main/java/com/jsh/erp/controller/MaterialController.java`

```java
@RestController
@RequestMapping(value = "/material")
@Api(tags = {"商品管理"})
public class MaterialController extends BaseController {
    private Logger logger = LoggerFactory.getLogger(MaterialController.class);

    @Resource
    private MaterialService materialService;

    // 标准CRUD接口模式
    
    @GetMapping(value = "/list")
    @ApiOperation(value = "获取信息列表")
    public TableDataInfo getList(@RequestParam(value = Constants.SEARCH, required = false) String search,
                                 HttpServletRequest request) throws Exception {
        // 1. 参数解析
        String categoryId = StringUtil.getInfo(search, "categoryId");
        String materialParam = StringUtil.getInfo(search, "materialParam");
        
        // 2. 业务调用
        List<MaterialVo4Unit> list = materialService.select(materialParam, /* 其他参数 */);
        
        // 3. 统一响应
        return getDataTable(list);
    }

    @PostMapping(value = "/add")
    @ApiOperation(value = "新增")
    public String addResource(@RequestBody JSONObject obj, HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        int insert = materialService.insertMaterial(obj, request);
        return returnStr(objectMap, insert);
    }

    @PutMapping(value = "/update")
    @ApiOperation(value = "修改")
    public String updateResource(@RequestBody JSONObject obj, HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        int update = materialService.updateMaterial(obj, request);
        return returnStr(objectMap, update);
    }

    @DeleteMapping(value = "/delete")
    @ApiOperation(value = "删除")
    public String deleteResource(@RequestParam("id") Long id, HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        int delete = materialService.deleteMaterial(id, request);
        return returnStr(objectMap, delete);
    }
}
```

**Controller层开发规范**：
1. 继承`BaseController`获得基础功能
2. 使用`@RestController`和`@RequestMapping`注解
3. 统一使用`@ApiOperation`添加接口文档
4. 标准RESTful接口命名：`/list`、`/add`、`/update`、`/delete`
5. 统一响应格式：`returnStr(objectMap, result)`或`getDataTable(list)`

#### 2.1.2 Service层标准实现

**文件位置**: `jshERP-boot/src/main/java/com/jsh/erp/service/MaterialService.java`

```java
@Service
public class MaterialService {
    private Logger logger = LoggerFactory.getLogger(MaterialService.class);

    @Resource
    private MaterialMapper materialMapper;
    @Resource
    private MaterialMapperEx materialMapperEx;
    @Resource
    private LogService logService;

    // 查询方法示例
    public List<MaterialVo4Unit> select(String materialParam, String standard, 
                                       String model, /* 其他参数 */) throws Exception {
        List<MaterialVo4Unit> list = new ArrayList<>();
        try {
            // 1. 分页设置
            PageUtils.startPage();
            
            // 2. 数据查询
            list = materialMapperEx.selectByConditionMaterial(materialParam, standard, /* 其他参数 */);
            
            // 3. 数据处理
            if (null != list && list.size() > 0) {
                for (MaterialVo4Unit m : list) {
                    // 扩展信息处理
                    m.setMaterialOther(getMaterialOtherByParam(mpArr, m));
                }
            }
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return list;
    }

    // 新增方法示例
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int insertMaterial(JSONObject obj, HttpServletRequest request) throws Exception {
        Material m = JSONObject.parseObject(obj.toJSONString(), Material.class);
        try {
            // 1. 数据处理
            m.setEnabled(true);
            
            // 2. 数据库操作
            materialMapperEx.insertSelectiveEx(m);
            
            // 3. 扩展处理
            materialExtendService.saveDetials(obj, obj.getString("sortList"), m.getId(), "insert");
            
            // 4. 日志记录
            logService.insertLog("商品", 
                new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_ADD).append(m.getName()).toString(), 
                request);
            return 1;
        } catch (BusinessRunTimeException ex) {
            throw new BusinessRunTimeException(ex.getCode(), ex.getMessage());
        } catch (Exception e) {
            JshException.writeFail(logger, e);
            return 0;
        }
    }
}
```

**Service层开发规范**：
1. 使用`@Service`注解
2. 重要方法添加`@Transactional`事务控制
3. 统一异常处理：`JshException`封装
4. 操作日志记录：调用`logService.insertLog()`
5. 分页处理：使用`PageUtils.startPage()`

#### 2.1.3 Mapper层标准实现

**接口文件**: `MaterialMapper.java` (MyBatis Generator生成)
**扩展接口**: `MaterialMapperEx.java` (手动编写复杂查询)
**映射文件**: `mapper_xml/MaterialMapperEx.xml`

```xml
<mapper namespace="com.jsh.erp.datasource.mappers.MaterialMapperEx">
    <resultMap extends="com.jsh.erp.datasource.mappers.MaterialMapper.BaseResultMap" 
               id="ResultMapList" type="com.jsh.erp.datasource.entities.MaterialVo4Unit">
        <result column="unitName" jdbcType="VARCHAR" property="unitName" />
        <result column="categoryName" jdbcType="VARCHAR" property="categoryName" />
        <result column="bar_code" jdbcType="VARCHAR" property="mBarCode" />
    </resultMap>

    <select id="selectByConditionMaterial" resultMap="ResultMapList">
        select jm.*, u.name unitName, mc.name categoryName, jme.bar_code
        from jsh_material jm
        left join jsh_unit u on jm.unit_id = u.id
        left join jsh_material_category mc on jm.category_id = mc.id
        left join jsh_material_extend jme on jm.id = jme.material_id
        where 1=1
        <if test="materialParam != null and materialParam !=''">
            <bind name="bindKey" value="'%'+materialParam+'%'"/>
            and (jme.bar_code like #{bindKey} or jm.name like #{bindKey} 
                 or jm.mnemonic like #{bindKey})
        </if>
        and ifnull(jm.delete_Flag,'0') !='1'
        order by jm.id desc
    </select>
</mapper>
```

### 2.2 前端组件化架构

#### 2.2.1 Vue页面组件标准结构

**文件位置**: `jshERP-web/src/views/material/MaterialList.vue`

```vue
<template>
  <a-row :gutter="24">
    <a-col :md="24">
      <a-card :style="cardStyle" :bordered="false">
        <!-- 查询区域 -->
        <div class="table-page-search-wrapper">
          <a-form layout="inline" @keyup.enter.native="searchQuery">
            <a-row :gutter="24">
              <a-col :md="6" :sm="24">
                <a-form-item label="类别" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-tree-select v-model="queryParam.categoryId" 
                                 :treeData="categoryTree" 
                                 placeholder="请选择类别">
                  </a-tree-select>
                </a-form-item>
              </a-col>
              <!-- 更多查询条件 -->
            </a-row>
          </a-form>
        </div>

        <!-- 操作按钮区域 -->
        <div class="table-operator">
          <a-button type="primary" icon="plus" @click="handleAdd">新增</a-button>
          <a-button type="primary" icon="edit" :disabled="!hasSelected" @click="handleEdit">编辑</a-button>
          <a-button type="danger" icon="delete" :disabled="!hasSelected" @click="handleDelete">删除</a-button>
        </div>

        <!-- 数据表格 -->
        <a-table ref="table"
                 size="middle"
                 bordered
                 rowKey="id"
                 :columns="columns"
                 :dataSource="dataSource"
                 :pagination="ipagination"
                 :loading="loading"
                 :rowSelection="rowSelection"
                 @change="handleTableChange">
        </a-table>
      </a-card>
    </a-col>
  </a-row>
</template>

<script>
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
import MaterialModal from './modules/MaterialModal'

export default {
  name: "MaterialList",
  mixins: [JeecgListMixin],
  components: {
    MaterialModal
  },
  data() {
    return {
      // 表格列定义
      columns: [
        {title: '名称', align: "center", dataIndex: 'name'},
        {title: '规格', align: "center", dataIndex: 'standard'},
        {title: '型号', align: "center", dataIndex: 'model'},
        {title: '操作', dataIndex: 'action', width: 150, scopedSlots: {customRender: 'action'}}
      ],
      // API配置
      url: {
        list: "/material/list",
        delete: "/material/delete",
        deleteBatch: "/material/deleteBatch"
      }
    }
  },
  created() {
    this.getSuperFieldList();
  },
  methods: {
    // 获取扩展字段配置
    getSuperFieldList() {
      // 实现扩展字段逻辑
    },
    
    // 新增
    handleAdd() {
      this.$refs.modalForm.add();
      this.$refs.modalForm.title = "新增";
    },
    
    // 编辑
    handleEdit() {
      this.$refs.modalForm.edit(this.selectedRowKeys[0]);
      this.$refs.modalForm.title = "编辑";
    }
  }
}
</script>
```

#### 2.2.2 Vuex状态管理

**文件位置**: `jshERP-web/src/store/modules/user.js`

```javascript
const user = {
  state: {
    token: '',
    username: '',
    permissionList: [],
    info: {}
  },

  mutations: {
    SET_TOKEN: (state, token) => {
      state.token = token
    },
    SET_PERMISSIONLIST: (state, permissionList) => {
      state.permissionList = permissionList
    }
  },

  actions: {
    // 登录
    Login({ commit }, userInfo) {
      return new Promise((resolve, reject) => {
        login(userInfo).then(response => {
          if(response.code === 200) {
            const result = response.data
            // 保存token和用户信息
            Vue.ls.set(ACCESS_TOKEN, result.token, 7 * 24 * 60 * 60 * 1000)
            Vue.ls.set(USER_INFO, result.user, 7 * 24 * 60 * 60 * 1000)
            commit('SET_TOKEN', result.token)
          }
          resolve(response)
        }).catch(error => {
          reject(error)
        })
      })
    },

    // 获取权限列表
    GetPermissionList({ commit }) {
      return new Promise((resolve, reject) => {
        let params = {pNumber:0, userId: Vue.ls.get(USER_ID)};
        queryPermissionsByUser(params).then(response => {
          if (response && response.length > 0) {
            commit('SET_PERMISSIONLIST', response)
          }
          resolve(response)
        }).catch(error => {
          reject(error)
        })
      })
    }
  }
}
```

### 2.3 数据库设计规范

#### 2.3.1 表命名规范

所有表名以`jsh_`为前缀，采用下划线命名法：

```sql
-- 商品主表
CREATE TABLE `jsh_material` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `category_id` bigint(20) DEFAULT NULL COMMENT '商品类别id',
  `name` varchar(100) NOT NULL COMMENT '名称',
  `standard` varchar(100) DEFAULT NULL COMMENT '规格',
  `model` varchar(100) DEFAULT NULL COMMENT '型号',
  `color` varchar(50) DEFAULT NULL COMMENT '颜色',
  `unit` varchar(50) DEFAULT NULL COMMENT '单位',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户id',
  `delete_flag` varchar(1) DEFAULT '0' COMMENT '删除标记，0未删除，1删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商品表';

-- 商品扩展表（价格、条码等）
CREATE TABLE `jsh_material_extend` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `material_id` bigint(20) NOT NULL COMMENT '商品id',
  `bar_code` varchar(50) NOT NULL COMMENT '条码',
  `commodity_unit` varchar(50) DEFAULT NULL COMMENT '商品单位',
  `purchase_decimal` decimal(24,6) DEFAULT NULL COMMENT '采购价格',
  `commodity_decimal` decimal(24,6) DEFAULT NULL COMMENT '零售价格',
  `wholesale_decimal` decimal(24,6) DEFAULT NULL COMMENT '销售价格',
  `low_decimal` decimal(24,6) DEFAULT NULL COMMENT '最低售价',
  `default_flag` varchar(1) DEFAULT NULL COMMENT '是否为默认单位，1是，0否',
  `sku` varchar(100) DEFAULT NULL COMMENT 'SKU',
  `delete_flag` varchar(1) DEFAULT '0' COMMENT '删除标记',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商品价格扩展';
```

#### 2.3.2 多租户机制

**核心字段**: 每个业务表都包含`tenant_id`字段实现数据隔离

```java
// 实体类中的租户字段
public class Material {
    private Long tenantId;  // 租户ID，实现数据隔离
    private String deleteFlag; // 逻辑删除标记
}
```

**Service层自动注入租户ID**:
```java
// 在Service层的增删改查方法中自动处理tenant_id
public Material getMaterial(long id) throws Exception {
    Material result = null;
    try {
        // 查询时自动过滤租户数据
        result = materialMapper.selectByPrimaryKey(id);
    } catch(Exception e) {
        JshException.readFail(logger, e);
    }
    return result;
}
```

---

## 3. 关键业务模块实现模板

### 3.1 商品管理模块（标准参考模板）

商品管理模块作为jshERP的核心模块，展示了完整的CRUD操作和业务逻辑处理标准。

#### 3.1.1 实体设计模式

**主实体**: `Material.java` - 商品基础信息
**扩展实体**: `MaterialExtend.java` - 价格和条码信息
**视图对象**: `MaterialVo4Unit.java` - 查询结果封装

```java
// 主实体：包含基础字段和多租户字段
public class Material {
    private Long id;
    private String name;
    private String standard;
    private String model;
    private Long tenantId;      // 多租户字段
    private String deleteFlag;  // 逻辑删除字段
    // ... 其他业务字段
}

// 视图对象：用于查询结果展示
public class MaterialVo4Unit extends Material {
    private String unitName;        // 关联单位名称
    private String categoryName;    // 关联类别名称
    private String mBarCode;        // 条码
    private BigDecimal stock;       // 库存
    private String materialOther;   // 扩展信息
}
```

#### 3.1.2 复杂业务逻辑处理

**多单位处理**:
```java
// 服务层处理多单位逻辑
public String getBigUnitStock(BigDecimal stock, Long unitId) throws Exception {
    String bigUnitStock = "";
    if(null != unitId) {
        Unit unit = unitService.getUnit(unitId);
        if(unit.getRatio() != null && unit.getRatio().compareTo(BigDecimal.ZERO) != 0 && stock != null) {
            bigUnitStock = stock.divide(unit.getRatio(), 2, BigDecimal.ROUND_HALF_UP) + unit.getOtherUnit();
        }
    }
    return bigUnitStock;
}
```

**库存处理**:
```java
// 初始库存和当前库存的计算
public Map<Long,BigDecimal> getInitialStockMapByMaterialList(List<MaterialVo4Unit> list) {
    Map<Long,BigDecimal> map = new HashMap<>();
    List<Long> materialIdList = new ArrayList<>();
    for(MaterialVo4Unit materialVo4Unit: list) {
        materialIdList.add(materialVo4Unit.getId());
    }
    List<MaterialInitialStock> mcsList = materialInitialStockMapperEx.getInitialStockMapByIdList(materialIdList);
    for(MaterialInitialStock materialInitialStock: mcsList) {
        map.put(materialInitialStock.getMaterialId(), materialInitialStock.getNumber());
    }
    return map;
}
```

### 3.2 用户权限模块

#### 3.2.1 JWT认证流程

**后端Token生成**:
```java
// 登录成功后生成JWT Token
@PostMapping("/login")
public BaseResponseInfo login(@RequestBody User user, HttpServletRequest request) throws Exception {
    BaseResponseInfo res = new BaseResponseInfo();
    try {
        // 1. 用户验证
        User userInfo = userService.validateUser(user.getLoginName(), user.getPassword());
        
        // 2. 生成Token
        String token = JwtUtil.generateToken(userInfo);
        
        // 3. 返回结果
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", userInfo);
        res.code = 200;
        res.data = data;
    } catch (Exception e) {
        res.code = 500;
        res.data = "登录失败";
    }
    return res;
}
```

**前端Token使用**:
```javascript
// 请求拦截器自动添加Token
service.interceptors.request.use(config => {
  const token = Vue.ls.get(ACCESS_TOKEN)
  if (token) {
    config.headers['X-Access-Token'] = token
  }
  return config
})

// 响应拦截器处理Token过期
service.interceptors.response.use((response) => {
  return response.data
}, err => {
  if (err.response.status === 401) {
    // Token过期，跳转登录页
    store.dispatch('Logout').then(() => {
      window.location.reload()
    })
  }
})
```

#### 3.2.2 权限控制机制

**后端权限验证**:
```java
// 基于注解的权限控制
@PreAuthorize("hasPermission('material:list')")
@GetMapping("/list")
public TableDataInfo getList() {
    // 方法实现
}
```

**前端权限指令**:
```vue
<!-- 按钮权限控制 -->
<a-button v-has="'material:add'" type="primary" @click="handleAdd">新增</a-button>

<!-- 路由权限控制在router/index.js中配置 -->
```

---

## 4. 可复用组件和工具类

### 4.1 后端工具类

#### 4.1.1 BaseController基础类

**文件位置**: `com.jsh.erp.base.BaseController`

```java
public class BaseController {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    // 分页处理
    protected void startPage() {
        PageUtils.startPage();
    }

    // 统一分页响应
    protected TableDataInfo getDataTable(List<?> list) {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(200);
        Map<String, Object> data = new HashMap<>();
        data.put("rows", list);
        data.put("total", new PageInfo(list).getTotal());
        rspData.setData(data);
        return rspData;
    }

    // 统一操作结果响应
    protected AjaxResult toAjax(int rows) {
        return rows > 0 ? success() : error();
    }
}
```

**使用方法**:
```java
@RestController
public class YourController extends BaseController {
    @GetMapping("/list")
    public TableDataInfo getList() {
        startPage(); // 开启分页
        List<YourEntity> list = yourService.getList();
        return getDataTable(list); // 返回分页结果
    }
}
```

#### 4.1.2 ResponseJsonUtil响应工具类

```java
public class ResponseJsonUtil {
    // 统一成功响应
    public static String returnJson(Map<String, Object> map, String message, int code) {
        map.put("message", message);
        return backJson(new ResponseCode(code, map));
    }

    // 根据操作结果返回响应
    public static String returnStr(Map<String, Object> objectMap, int res) {
        if(res > 0) {
            return returnJson(objectMap, ErpInfo.OK.name, ErpInfo.OK.code);
        } else {
            return returnJson(objectMap, ErpInfo.ERROR.name, ErpInfo.ERROR.code);
        }
    }
}
```

#### 4.1.3 StringUtil字符串工具类

```java
public class StringUtil {
    // 字符串非空判断
    public static boolean isNotEmpty(String str) {
        return str != null && !"".equals(str.trim());
    }

    // 从JSON字符串中获取指定字段值
    public static String getInfo(String search, String name) {
        if(isNotEmpty(search) && isNotEmpty(name)) {
            JSONObject obj = JSONObject.parseObject(search);
            if(obj.containsKey(name)) {
                return obj.getString(name);
            }
        }
        return "";
    }

    // 字符串转Long列表
    public static List<Long> strToLongList(String str) {
        List<Long> list = new ArrayList<>();
        if(isNotEmpty(str)) {
            String[] arr = str.split(",");
            for(String s : arr) {
                if(isNotEmpty(s)) {
                    list.add(Long.parseLong(s));
                }
            }
        }
        return list;
    }
}
```

### 4.2 前端通用组件

#### 4.2.1 JEditableTable可编辑表格

**使用方法**:
```vue
<template>
  <j-editable-table
    :ref="refKeys[0]"
    :rowNumber="true"
    :dragSort="true"
    :columns="columns"
    v-model="dataSource"
    :maxHeight="300">
  </j-editable-table>
</template>

<script>
export default {
  data() {
    return {
      columns: [
        {
          title: '商品名称',
          key: 'materialName',
          type: JEditableTableUtil.cellType.popup,
          options: {
            url: '/material/findBySelect',
            showField: 'materialName',
            idField: 'id'
          }
        },
        {
          title: '数量',
          key: 'operNumber',
          type: JEditableTableUtil.cellType.inputNumber
        }
      ]
    }
  }
}
</script>
```

#### 4.2.2 JCategorySelect分类选择组件

```vue
<j-category-select
  v-model="form.categoryId"
  :url="categoryUrl"
  placeholder="请选择分类"
  :multiple="false">
</j-category-select>
```

#### 4.2.3 JeecgListMixin列表页面混入

**文件位置**: `src/mixins/JeecgListMixin.js`

```javascript
export const JeecgListMixin = {
  data() {
    return {
      // 分页配置
      ipagination: {
        current: 1,
        pageSize: 10,
        total: 0,
        showTotal: (total, range) => `显示 ${range[0]}-${range[1]} 共 ${total} 条`,
        showSizeChanger: true,
        showQuickJumper: true
      },
      // 表格数据
      dataSource: [],
      loading: false,
      // 选择行
      selectedRowKeys: [],
      selectionRows: []
    }
  },
  
  computed: {
    hasSelected() {
      return this.selectedRowKeys.length > 0
    },
    
    rowSelection() {
      return {
        selectedRowKeys: this.selectedRowKeys,
        onChange: this.onSelectChange
      }
    }
  },
  
  created() {
    this.loadData();
  },
  
  methods: {
    // 加载数据
    loadData(arg) {
      if(!this.url.list) {
        console.error("请设置url.list属性!");
        return;
      }
      
      // 参数处理
      let params = this.getQueryParams();
      this.loading = true;
      
      getAction(this.url.list, params).then(res => {
        if(res.success) {
          this.dataSource = res.result.records || res.result;
          this.ipagination.total = res.result.total;
        }
        this.loading = false;
      });
    },
    
    // 获取查询参数
    getQueryParams() {
      let param = Object.assign({}, this.queryParam, this.isorter);
      param.current = this.ipagination.current;
      param.size = this.ipagination.pageSize;
      return filterObj(param);
    },
    
    // 查询
    searchQuery() {
      this.loadData(1);
    },
    
    // 重置
    searchReset() {
      this.queryParam = {};
      this.loadData();
    }
  }
}
```

---

## 5. 编码规范和开发标准

### 5.1 API设计规范

#### 5.1.1 RESTful接口规范

**标准URL命名**:
```
GET    /module/list        # 查询列表
GET    /module/info        # 根据ID查询详情
POST   /module/add         # 新增
PUT    /module/update      # 修改
DELETE /module/delete      # 删除单个
DELETE /module/deleteBatch # 批量删除
```

**统一响应格式**:
```json
{
  "code": 200,
  "message": "操作成功", 
  "data": {
    "rows": [...],
    "total": 100
  }
}
```

#### 5.1.2 参数验证规范

**后端参数校验**:
```java
@PostMapping("/add")
public String add(@RequestBody @Valid MaterialDto dto, HttpServletRequest request) {
    // @Valid自动触发参数校验
}

// DTO中的校验注解
public class MaterialDto {
    @NotBlank(message = "商品名称不能为空")
    @Size(max = 100, message = "商品名称长度不能超过100字符")
    private String name;
    
    @DecimalMin(value = "0", message = "价格不能为负数")
    private BigDecimal price;
}
```

**前端表单验证**:
```vue
<a-form-model ref="form" :model="form" :rules="validatorRules">
  <a-form-model-item label="商品名称" prop="name">
    <a-input v-model="form.name" placeholder="请输入商品名称"></a-input>
  </a-form-model-item>
</a-form-model>

<script>
export default {
  data() {
    return {
      validatorRules: {
        name: [
          { required: true, message: '请输入商品名称!' },
          { max: 100, message: '商品名称长度不能超过100字符!' }
        ]
      }
    }
  }
}
</script>
```

### 5.2 数据库操作规范

#### 5.2.1 事务处理规范

```java
// Service层方法添加事务注解
@Transactional(value = "transactionManager", rollbackFor = Exception.class)
public int insertMaterial(JSONObject obj, HttpServletRequest request) throws Exception {
    try {
        // 1. 主表操作
        materialMapper.insertSelective(material);
        
        // 2. 子表操作
        materialExtendService.saveDetails(obj, material.getId());
        
        // 3. 库存初始化
        stockService.initStock(material.getId());
        
        // 4. 日志记录
        logService.insertLog("商品", "新增商品:" + material.getName(), request);
        
        return 1;
    } catch (Exception e) {
        // 事务会自动回滚
        logger.error("新增商品失败", e);
        throw e;
    }
}
```

#### 5.2.2 分页查询规范

```java
// Service层分页处理
public List<MaterialVo4Unit> select(String materialParam, /* 其他参数 */) throws Exception {
    try {
        // 1. 启动分页
        PageUtils.startPage();
        
        // 2. 执行查询
        List<MaterialVo4Unit> list = materialMapperEx.selectByConditionMaterial(materialParam);
        
        // 3. 数据处理
        if (list != null && list.size() > 0) {
            for (MaterialVo4Unit m : list) {
                // 补充扩展信息
                m.setMaterialOther(getMaterialOtherByParam(mpArr, m));
            }
        }
        return list;
    } catch (Exception e) {
        JshException.readFail(logger, e);
        return new ArrayList<>();
    }
}
```

### 5.3 前端开发规范

#### 5.3.1 组件命名规范

**文件命名**: 使用PascalCase
- `MaterialList.vue` - 列表页面
- `MaterialModal.vue` - 弹窗组件  
- `MaterialForm.vue` - 表单组件

**组件注册**:
```javascript
export default {
  name: "MaterialList",  // 组件名使用PascalCase
  components: {
    MaterialModal        // 子组件注册
  }
}
```

#### 5.3.2 API调用封装

**统一API调用方式**:
```javascript
// 使用封装的getAction、postAction等方法
import { getAction, postAction, putAction, deleteAction } from '@/api/manage'

export default {
  data() {
    return {
      url: {
        list: "/material/list",
        add: "/material/add", 
        edit: "/material/update",
        delete: "/material/delete"
      }
    }
  },
  
  methods: {
    // 查询列表
    loadData() {
      getAction(this.url.list, this.queryParam).then(res => {
        if(res.success) {
          this.dataSource = res.result.records;
        }
      });
    },
    
    // 新增
    handleAdd() {
      postAction(this.url.add, this.form).then(res => {
        if(res.success) {
          this.$message.success("添加成功!");
          this.loadData();
        }
      });
    }
  }
}
```

---

## 6. 安全机制实现

### 6.1 JWT认证机制

#### 6.1.1 Token生成和验证

**后端JWT工具类**:
```java
public class JwtUtil {
    private static final String SECRET = "jshERP_secret_key";
    private static final long EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000; // 7天

    // 生成Token
    public static String generateToken(User user) {
        try {
            Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            return JWT.create()
                    .withClaim("userId", user.getId())
                    .withClaim("loginName", user.getLoginName())
                    .withExpiresAt(date)
                    .sign(algorithm);
        } catch (Exception e) {
            return null;
        }
    }

    // 验证Token
    public static boolean verify(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            JWTVerifier verifier = JWT.require(algorithm).build();
            verifier.verify(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
```

#### 6.1.2 权限拦截器

```java
@Component
public class AuthInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, 
                           Object handler) throws Exception {
        
        String token = request.getHeader("X-Access-Token");
        
        if (StringUtils.isEmpty(token)) {
            response.setStatus(401);
            return false;
        }
        
        if (!JwtUtil.verify(token)) {
            response.setStatus(401);
            return false;
        }
        
        return true;
    }
}
```

### 6.2 数据权限控制

#### 6.2.1 多租户数据隔离

**自动注入租户ID**:
```java
@Aspect
@Component
public class TenantAspect {
    
    @Around("execution(* com.jsh.erp.service.*.*(..))")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取当前用户的租户ID
        Long tenantId = getCurrentUserTenantId();
        
        // 设置租户上下文
        TenantContextHolder.setTenantId(tenantId);
        
        try {
            return joinPoint.proceed();
        } finally {
            TenantContextHolder.clear();
        }
    }
}
```

**MyBatis拦截器自动添加租户条件**:
```java
@Intercepts({@Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})})
public class TenantInterceptor implements Interceptor {
    
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        Object parameter = invocation.getArgs()[1];
        
        // 获取SQL
        BoundSql boundSql = mappedStatement.getBoundSql(parameter);
        String sql = boundSql.getSql();
        
        // 添加租户条件
        Long tenantId = TenantContextHolder.getTenantId();
        if (tenantId != null) {
            sql = addTenantCondition(sql, tenantId);
        }
        
        // 创建新的BoundSql
        BoundSql newBoundSql = new BoundSql(mappedStatement.getConfiguration(), sql, 
                                          boundSql.getParameterMappings(), parameter);
        
        return invocation.proceed();
    }
}
```

### 6.3 前端安全措施

#### 6.3.1 路由权限守卫

```javascript
// router/index.js
router.beforeEach((to, from, next) => {
  const token = Vue.ls.get(ACCESS_TOKEN)
  
  if (to.path === '/user/login') {
    next()
  } else if (!token) {
    next('/user/login')
  } else {
    // 验证权限
    if (hasPermission(to.meta.permission)) {
      next()
    } else {
      next('/403')
    }
  }
})
```

#### 6.3.2 按钮权限控制

```javascript
// 权限指令
Vue.directive('has', {
  inserted: function (el, binding) {
    const permission = binding.value
    const hasPermission = checkPermission(permission)
    
    if (!hasPermission) {
      el.parentNode && el.parentNode.removeChild(el)
    }
  }
})

// 使用方式
<a-button v-has="'material:add'" type="primary">新增</a-button>
```

---

## 7. 开发模板和最佳实践

### 7.1 新增业务模块开发模板

#### 7.1.1 后端开发步骤

**第1步：创建数据库表**
```sql
-- 1. 主表
CREATE TABLE `jsh_your_module` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(100) NOT NULL COMMENT '名称',
  `code` varchar(50) COMMENT '编码',
  `remark` varchar(200) COMMENT '备注',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户id',
  `delete_flag` varchar(1) DEFAULT '0' COMMENT '删除标记',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='你的模块表';
```

**第2步：生成基础代码**
使用MyBatis Generator生成：
- `YourModule.java` - 实体类
- `YourModuleExample.java` - 查询条件类  
- `YourModuleMapper.java` - 基础Mapper接口
- `YourModuleMapper.xml` - 基础映射文件

**第3步：创建扩展类**
```java
// 扩展Mapper接口
public interface YourModuleMapperEx {
    List<YourModuleVo> selectByCondition(@Param("name") String name);
}

// 视图对象
public class YourModuleVo extends YourModule {
    private String createTimeStr; // 格式化时间
    // 其他扩展字段
}
```

**第4步：实现Service层**
```java
@Service
public class YourModuleService {
    
    @Resource
    private YourModuleMapper yourModuleMapper;
    @Resource 
    private YourModuleMapperEx yourModuleMapperEx;
    @Resource
    private LogService logService;

    public List<YourModuleVo> select(String name) throws Exception {
        try {
            PageUtils.startPage();
            return yourModuleMapperEx.selectByCondition(name);
        } catch (Exception e) {
            JshException.readFail(logger, e);
            return new ArrayList<>();
        }
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int insert(YourModule yourModule, HttpServletRequest request) throws Exception {
        try {
            yourModuleMapper.insertSelective(yourModule);
            logService.insertLog("你的模块", "新增:" + yourModule.getName(), request);
            return 1;
        } catch (Exception e) {
            JshException.writeFail(logger, e);
            return 0;
        }
    }
}
```

**第5步：实现Controller层**
```java
@RestController
@RequestMapping(value = "/yourModule")
@Api(tags = {"你的模块管理"})
public class YourModuleController extends BaseController {

    @Resource
    private YourModuleService yourModuleService;

    @GetMapping(value = "/list")
    @ApiOperation(value = "获取列表")
    public TableDataInfo getList(@RequestParam(value = "search", required = false) String search,
                                 HttpServletRequest request) throws Exception {
        String name = StringUtil.getInfo(search, "name");
        List<YourModuleVo> list = yourModuleService.select(name);
        return getDataTable(list);
    }

    @PostMapping(value = "/add")
    @ApiOperation(value = "新增")
    public String add(@RequestBody YourModule yourModule, HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        int result = yourModuleService.insert(yourModule, request);
        return returnStr(objectMap, result);
    }
}
```

#### 7.1.2 前端开发步骤

**第1步：创建列表页面**
```vue
<!-- YourModuleList.vue -->
<template>
  <a-row :gutter="24">
    <a-col :md="24">
      <a-card :bordered="false">
        <!-- 查询区域 -->
        <div class="table-page-search-wrapper">
          <a-form layout="inline" @keyup.enter.native="searchQuery">
            <a-row :gutter="24">
              <a-col :md="6" :sm="24">
                <a-form-item label="名称">
                  <a-input v-model="queryParam.name" placeholder="请输入名称"></a-input>
                </a-form-item>
              </a-col>
              <a-col :md="6" :sm="24">
                <a-button type="primary" @click="searchQuery">查询</a-button>
                <a-button style="margin-left: 8px" @click="searchReset">重置</a-button>
              </a-col>
            </a-row>
          </a-form>
        </div>

        <!-- 操作按钮 -->
        <div class="table-operator">
          <a-button type="primary" icon="plus" @click="handleAdd">新增</a-button>
          <a-button type="primary" icon="edit" :disabled="!hasSelected" @click="handleEdit">编辑</a-button>
          <a-button type="danger" icon="delete" :disabled="!hasSelected" @click="handleDelete">删除</a-button>
        </div>

        <!-- 数据表格 -->
        <a-table ref="table"
                 size="middle" 
                 bordered
                 rowKey="id"
                 :columns="columns"
                 :dataSource="dataSource"
                 :pagination="ipagination"
                 :loading="loading"
                 :rowSelection="rowSelection"
                 @change="handleTableChange">
          <template slot="action" slot-scope="text, record">
            <a @click="handleEdit(record)">编辑</a>
            <a-divider type="vertical" />
            <a @click="handleDelete(record)">删除</a>
          </template>
        </a-table>
      </a-card>
    </a-col>

    <!-- 弹窗 -->
    <your-module-modal ref="modalForm" @ok="modalFormOk"></your-module-modal>
  </a-row>
</template>

<script>
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
import YourModuleModal from './modules/YourModuleModal'

export default {
  name: "YourModuleList",
  mixins: [JeecgListMixin],
  components: {
    YourModuleModal
  },
  data() {
    return {
      columns: [
        {title: '#', dataIndex: '', key:'rowIndex', width:60, align:"center", customRender:function (t,r,i) {return parseInt(i)+1;}},
        {title: '名称', align: "center", dataIndex: 'name'},
        {title: '编码', align: "center", dataIndex: 'code'},
        {title: '备注', align: "center", dataIndex: 'remark'},
        {title: '操作', dataIndex: 'action', width: 150, scopedSlots: {customRender: 'action'}}
      ],
      url: {
        list: "/yourModule/list",
        delete: "/yourModule/delete",
        deleteBatch: "/yourModule/deleteBatch"
      }
    }
  },
  methods: {
    handleAdd() {
      this.$refs.modalForm.add();
      this.$refs.modalForm.title = "新增";
    },
    
    handleEdit(record) {
      this.$refs.modalForm.edit(record);
      this.$refs.modalForm.title = "编辑";
    }
  }
}
</script>
```

**第2步：创建弹窗组件**
```vue
<!-- modules/YourModuleModal.vue -->
<template>
  <a-modal
    :title="title"
    :width="800"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭">

    <a-spin :spinning="confirmLoading">
      <a-form-model ref="form" :model="model" :rules="validatorRules">
        <a-row :gutter="24">
          <a-col :md="12" :sm="24">
            <a-form-model-item label="名称" prop="name">
              <a-input v-model="model.name" placeholder="请输入名称"></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :md="12" :sm="24">
            <a-form-model-item label="编码" prop="code">
              <a-input v-model="model.code" placeholder="请输入编码"></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :md="24" :sm="24">
            <a-form-model-item label="备注" prop="remark">
              <a-textarea v-model="model.remark" placeholder="请输入备注" :rows="3"></a-textarea>
            </a-form-model-item>
          </a-col>
        </a-row>
      </a-form-model>
    </a-spin>
  </a-modal>
</template>

<script>
import { httpAction } from '@/api/manage'
import { validateDuplicateValue } from '@/utils/util'

export default {
  name: "YourModuleModal",
  data() {
    return {
      title: "操作",
      visible: false,
      model: {},
      confirmLoading: false,
      validatorRules: {
        name: [
          { required: true, message: '请输入名称!' },
          { max: 100, message: '名称长度不能超过100字符!' }
        ],
        code: [
          { required: true, message: '请输入编码!' },
          { max: 50, message: '编码长度不能超过50字符!' }
        ]
      },
      url: {
        add: "/yourModule/add",
        edit: "/yourModule/update"
      }
    }
  },
  
  methods: {
    add() {
      this.edit({});
    },
    
    edit(record) {
      this.model = Object.assign({}, record);
      this.visible = true;
    },
    
    close() {
      this.$emit('close');
      this.visible = false;
    },
    
    handleOk() {
      const that = this;
      this.$refs.form.validate(valid => {
        if (valid) {
          that.confirmLoading = true;
          let httpurl = '';
          let method = '';
          if (!this.model.id) {
            httpurl = this.url.add;
            method = 'post';
          } else {
            httpurl = this.url.edit;
            method = 'put';
          }
          httpAction(httpurl, this.model, method).then((res) => {
            if (res.success) {
              that.$message.success(res.message);
              that.$emit('ok');
              that.close();
            } else {
              that.$message.warning(res.message);
            }
          }).finally(() => {
            that.confirmLoading = false;
          })
        }
      })
    },
    
    handleCancel() {
      this.close();
    }
  }
}
</script>
```

### 7.2 开发最佳实践

#### 7.2.1 代码质量保证

**1. 异常处理标准**
```java
// Service层统一异常处理
try {
    // 业务逻辑
    return result;
} catch (BusinessRunTimeException ex) {
    // 业务异常直接抛出
    throw ex;
} catch (Exception e) {
    // 系统异常记录日志并封装
    logger.error("操作失败", e);
    JshException.writeFail(logger, e);
    return 0;
}
```

**2. 日志记录规范**
```java
// 重要操作记录日志
logService.insertLog("模块名", 
    new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_ADD)
        .append(entity.getName()).toString(), 
    request);
```

**3. 性能优化要点**
- 使用分页查询避免大量数据加载
- 合理使用缓存机制
- 数据库查询优化，避免N+1问题
- 前端虚拟滚动处理大列表

#### 7.2.2 测试策略

**单元测试示例**:
```java
@SpringBootTest
@RunWith(SpringRunner.class)
public class MaterialServiceTest {
    
    @Autowired
    private MaterialService materialService;
    
    @Test
    public void testInsertMaterial() throws Exception {
        // 准备测试数据
        JSONObject obj = new JSONObject();
        obj.put("name", "测试商品");
        obj.put("standard", "规格1");
        
        // 执行测试
        int result = materialService.insertMaterial(obj, mockRequest);
        
        // 验证结果
        Assert.assertEquals(1, result);
    }
}
```

---

## 8. 集成指导

### 8.1 开发环境搭建

#### 8.1.1 后端环境要求

**必要软件**:
- JDK 1.8+
- Maven 3.6+
- MySQL 5.7+
- Redis 3.0+

**配置文件设置**:
```properties
# application.properties
server.port=9999
spring.datasource.url=jdbc:mysql://localhost:3306/jsh_erp?useUnicode=true&characterEncoding=utf8
spring.datasource.username=root
spring.datasource.password=123456

# Redis配置
spring.redis.host=127.0.0.1
spring.redis.port=6379

# 文件上传配置
file.uploadType=1
file.path=/opt/jshERP/upload
```

#### 8.1.2 前端环境搭建

**环境要求**:
```bash
# Node.js 12+
node -v

# 安装依赖
npm install
# 或
yarn install

# 启动开发服务器
npm run serve
```

**代理配置** (`vue.config.js`):
```javascript
module.exports = {
  devServer: {
    proxy: {
      '/jshERP-boot': {
        target: 'http://localhost:9999',
        ws: false,
        changeOrigin: true
      }
    }
  }
}
```

### 8.2 生产环境部署

#### 8.2.1 Docker部署方式

**docker-compose.yml配置**:
```yaml
version: '3'
services:
  mysql:
    image: mysql:5.7
    environment:
      MYSQL_ROOT_PASSWORD: 123456
      MYSQL_DATABASE: jsh_erp
    ports:
      - "3306:3306"
    volumes:
      - ./volumes/mysql:/var/lib/mysql

  redis:
    image: redis:5.0
    ports:
      - "6379:6379"
    command: redis-server --requirepass 1234abcd

  backend:
    build: 
      context: ./jshERP-boot
      dockerfile: Dockerfile
    ports:
      - "9999:9999"
    depends_on:
      - mysql
      - redis
    environment:
      - SPRING_PROFILES_ACTIVE=docker

  frontend:
    build:
      context: ./jshERP-web
      dockerfile: Dockerfile
    ports:
      - "80:80"
    depends_on:
      - backend
```

**启动命令**:
```bash
# 构建并启动所有服务
docker-compose up -d

# 查看运行状态
docker-compose ps

# 查看日志
docker-compose logs -f backend
```

#### 8.2.2 传统部署方式

**后端部署**:
```bash
# 1. 打包
mvn clean package -Dmaven.test.skip=true

# 2. 上传jar包到服务器
scp target/jshERP-boot.jar user@server:/opt/jshERP/

# 3. 启动服务
java -jar /opt/jshERP/jshERP-boot.jar --spring.profiles.active=prod
```

**前端部署**:
```bash
# 1. 构建生产版本
npm run build

# 2. 部署到nginx
cp -r dist/* /var/www/html/

# 3. nginx配置
server {
    listen 80;
    server_name yourdomain.com;
    root /var/www/html;
    
    location /jshERP-boot/ {
        proxy_pass http://localhost:9999/jshERP-boot/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

### 8.3 常见问题解决

#### 8.3.1 开发过程中的常见问题

**1. 跨域问题**
```javascript
// 前端代理配置
devServer: {
  proxy: {
    '/jshERP-boot': {
      target: 'http://localhost:9999',
      changeOrigin: true
    }
  }
}
```

**2. 多租户数据隔离问题**
```java
// 确保Service层所有查询都包含租户过滤
public List<Material> getMaterialList() {
    MaterialExample example = new MaterialExample();
    example.createCriteria()
           .andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED)
           .andTenantIdEqualTo(getCurrentTenantId()); // 重要：添加租户过滤
    return materialMapper.selectByExample(example);
}
```

**3. 权限控制问题**
```vue
<!-- 前端权限指令正确使用 -->
<a-button v-has="'material:add'" type="primary">新增</a-button>
<!-- 错误用法：v-has="material:add" 缺少引号 -->
```

#### 8.3.2 性能优化建议

**1. 数据库优化**
```sql
-- 为常用查询字段添加索引
ALTER TABLE jsh_material ADD INDEX idx_name_tenant (name, tenant_id);
ALTER TABLE jsh_material ADD INDEX idx_category_tenant (category_id, tenant_id);
```

**2. 缓存策略**
```java
// 对不经常变动的数据使用缓存
@Cacheable(value = "materialCache", key = "#id")
public Material getMaterial(Long id) {
    return materialMapper.selectByPrimaryKey(id);
}
```

**3. 前端性能优化**
```vue
<!-- 大列表使用虚拟滚动 -->
<a-table :scroll="{ y: 400 }" :pagination="false">
</a-table>

<!-- 图片懒加载 -->
<img v-lazy="imageUrl" />
```

---

## 附录

### A. 常用代码片段

#### A.1 后端代码片段

**标准Controller方法**:
```java
@GetMapping("/list")
@ApiOperation(value = "获取列表")
public TableDataInfo getList(@RequestParam(value = "search", required = false) String search) throws Exception {
    String param = StringUtil.getInfo(search, "param");
    List<Entity> list = service.select(param);
    return getDataTable(list);
}
```

**Service事务方法**:
```java
@Transactional(value = "transactionManager", rollbackFor = Exception.class)
public int insert(Entity entity, HttpServletRequest request) throws Exception {
    try {
        mapper.insertSelective(entity);
        logService.insertLog("模块", "新增:" + entity.getName(), request);
        return 1;
    } catch (Exception e) {
        JshException.writeFail(logger, e);
        return 0;
    }
}
```

#### A.2 前端代码片段

**列表页面基础结构**:
```vue
<template>
  <a-card :bordered="false">
    <!-- 查询表单 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <!-- 查询条件 -->
      </a-form>
    </div>
    
    <!-- 操作按钮 -->
    <div class="table-operator">
      <a-button type="primary" @click="handleAdd">新增</a-button>
    </div>
    
    <!-- 数据表格 -->
    <a-table :columns="columns" :dataSource="dataSource">
    </a-table>
  </a-card>
</template>

<script>
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
export default {
  mixins: [JeecgListMixin],
  data() {
    return {
      columns: [],
      url: {
        list: "/your/list"
      }
    }
  }
}
</script>
```

### B. 数据库表结构参考

#### B.1 基础业务表结构

```sql
-- 标准业务表结构模板
CREATE TABLE `jsh_your_table` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(100) NOT NULL COMMENT '名称',
  `code` varchar(50) COMMENT '编码',
  `status` varchar(1) DEFAULT '1' COMMENT '状态：0禁用，1启用',
  `remark` varchar(500) COMMENT '备注',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户id',
  `delete_flag` varchar(1) DEFAULT '0' COMMENT '删除标记，0未删除，1删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` bigint(20) COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint(20) COMMENT '更新人',
  PRIMARY KEY (`id`),
  KEY `idx_tenant_delete` (`tenant_id`, `delete_flag`),
  KEY `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='你的业务表';
```

---

**手册总结**

本技术参考手册详细介绍了jshERP系统的核心架构、开发规范、最佳实践和集成指导。通过遵循本手册的规范和模板，开发者可以：

1. **快速上手**：理解系统架构和设计理念
2. **规范开发**：遵循统一的编码标准和最佳实践  
3. **高效集成**：使用现有组件和工具类快速开发
4. **质量保证**：通过标准化流程确保代码质量
5. **安全可靠**：实现完整的权限控制和数据隔离

建议开发团队将此手册作为开发标准，确保所有二次开发工作都能与原有系统保持一致性和兼容性。

---

*本手册基于jshERP当前版本编写，如有更新请及时同步修订。*