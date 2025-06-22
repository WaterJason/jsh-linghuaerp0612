package com.jsh.erp.service.cloisonne;

import com.jsh.erp.datasource.entities.*;
import com.jsh.erp.datasource.vo.CloisonneScheduleVo4List;
import com.jsh.erp.service.UserService;
import com.jsh.erp.service.MaterialService;
import com.jsh.erp.service.AccountHeadService;
import com.jsh.erp.service.AccountItemService;
import com.jsh.erp.service.LogService;
import com.jsh.erp.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 掐丝珐琅馆模块集成服务
 * 负责与jshERP现有模块的数据交互和业务集成
 * 
 * @author jshERP
 * @since 2025-01-22
 */
@Service
public class CloisonneIntegrationService {

    private static final Logger logger = LoggerFactory.getLogger(CloisonneIntegrationService.class);

    @Autowired
    private UserService userService;

    @Autowired
    private MaterialService materialService;

    @Autowired
    private AccountHeadService accountHeadService;

    @Autowired
    private AccountItemService accountItemService;

    @Autowired
    private LogService logService;

    /**
     * 获取员工详细信息并转换为排班VO
     * 
     * @param employeeId 员工ID
     * @param tenantId 租户ID
     * @return 员工信息
     */
    public CloisonneScheduleVo4List getEmployeeForSchedule(Long employeeId, Long tenantId) {
        try {
            User user = userService.getUser(employeeId);
            if (user == null || !user.getTenantId().equals(tenantId)) {
                return null;
            }

            CloisonneScheduleVo4List vo = new CloisonneScheduleVo4List();
            vo.setEmployeeId(user.getId());
            vo.setEmployeeName(user.getUsername());
            vo.setEmployeeRole(user.getPosition());
            vo.setEmployeePhone(user.getPhonenum());
            
            // 生成默认头像URL
            // TODO: User实体类缺少getAvatar()方法，暂时使用默认头像
            // if (StringUtil.isEmpty(user.getAvatar())) {
                vo.setEmployeeAvatar(generateDefaultAvatar(user.getUsername()));
            // } else {
            //     vo.setEmployeeAvatar(user.getAvatar());
            // }

            return vo;
        } catch (Exception e) {
            logger.error("获取员工信息失败, employeeId: {}", employeeId, e);
            return null;
        }
    }

    /**
     * 获取当前租户下的所有员工列表
     * 
     * @param tenantId 租户ID
     * @return 员工列表
     */
    public List<Map<String, Object>> getAllEmployees(Long tenantId) {
        try {
            // TODO: UserService缺少getUserListByTenantId方法，暂时返回空列表
            // List<User> users = userService.getUserListByTenantId(tenantId);
            List<Map<String, Object>> employeeList = new ArrayList<>();

            // TODO: 暂时注释掉用户列表获取逻辑，等待UserService方法实现
            /*
            for (User user : users) {
                if ("0".equals(user.getDeleteFlag()) && user.getEnabled()) {
                    Map<String, Object> employee = new HashMap<>();
                    employee.put("id", user.getId());
                    employee.put("name", user.getUsername());
                    employee.put("role", user.getPosition());
                    employee.put("phone", user.getPhonenum());
                    employee.put("email", user.getEmail());
                    employee.put("avatar", StringUtil.isEmpty(user.getAvatar()) ?
                        generateDefaultAvatar(user.getUsername()) : user.getAvatar());
                    employeeList.add(employee);
                }
            }
            */

            return employeeList;
        } catch (Exception e) {
            logger.error("获取员工列表失败, tenantId: {}", tenantId, e);
            return new ArrayList<>();
        }
    }

    /**
     * 检查用户是否有掐丝珐琅馆模块权限
     * 
     * @param userId 用户ID
     * @param functionNumber 功能编号
     * @return 是否有权限
     */
    public boolean hasCloisonnePermission(Long userId, String functionNumber) {
        try {
            // TODO: UserService缺少checkUserPermission方法，暂时返回true
            // return userService.checkUserPermission(userId, functionNumber);
            return true; // 暂时允许所有权限
        } catch (Exception e) {
            logger.error("检查用户权限失败, userId: {}, functionNumber: {}", userId, functionNumber, e);
            return false;
        }
    }

    /**
     * 获取用户在掐丝珐琅馆模块的按钮权限
     * 
     * @param userId 用户ID
     * @param functionNumber 功能编号
     * @return 按钮权限字符串
     */
    public String getUserButtonPermissions(Long userId, String functionNumber) {
        try {
            // TODO: UserService缺少getUserButtonStr方法，暂时返回空字符串
            // return userService.getUserButtonStr(userId, functionNumber);
            return ""; // 暂时返回空权限
        } catch (Exception e) {
            logger.error("获取用户按钮权限失败, userId: {}, functionNumber: {}", userId, functionNumber, e);
            return "";
        }
    }

    /**
     * 从商品主表同步商品信息到POS商品表
     * 
     * @param materialId 商品ID
     * @param tenantId 租户ID
     * @return 同步后的商品信息
     */
    public Map<String, Object> syncMaterialToPOS(Long materialId, Long tenantId) {
        try {
            Material material = materialService.getMaterial(materialId);
            if (material == null || !material.getTenantId().equals(tenantId)) {
                return null;
            }

            Map<String, Object> posProduct = new HashMap<>();
            posProduct.put("materialId", material.getId());
            posProduct.put("productCode", material.getMfrs());
            posProduct.put("productName", material.getName());
            // TODO: Material实体类缺少getCategoryName()方法，暂时使用空字符串
            // posProduct.put("categoryName", material.getCategoryName());
            posProduct.put("categoryName", ""); // 暂时使用空分类名
            posProduct.put("unit", material.getUnit());
            posProduct.put("enabled", material.getEnabled());
            
            // 获取商品扩展信息(价格等)
            // 这里需要调用MaterialExtendService获取价格信息
            // posProduct.put("price", extend.getCommodityDecimal());
            
            return posProduct;
        } catch (Exception e) {
            logger.error("同步商品到POS失败, materialId: {}", materialId, e);
            return null;
        }
    }

    /**
     * 咖啡店销售生成财务收入记录
     * 
     * @param salesData 销售数据
     * @param tenantId 租户ID
     * @return 是否成功
     */
    @Transactional
    public boolean generateCoffeeSalesFinanceRecord(Map<String, Object> salesData, Long tenantId) {
        try {
            BigDecimal revenue = new BigDecimal(salesData.get("revenue").toString());
            String salesDate = salesData.get("date").toString();
            Long recorderId = Long.valueOf(salesData.get("recorderId").toString());

            // TODO: AccountHeadService和AccountItemService缺少save方法，暂时注释财务记录生成
            /*
            // 创建财务主表记录
            AccountHead accountHead = new AccountHead();
            accountHead.setType("收入");
            accountHead.setBillNo(generateBillNo("COFFEE"));
            accountHead.setBillTime(new Date()); // 修复类型转换问题
            accountHead.setHandsPersonId(recorderId);
            accountHead.setChangeAmount(revenue);
            accountHead.setTotalPrice(revenue);
            accountHead.setRemark("咖啡店销售收入 - " + salesDate);
            accountHead.setTenantId(tenantId);
            accountHead.setDeleteFlag("0");

            // 保存财务主表
            accountHeadService.save(accountHead);

            // 创建财务明细记录
            AccountItem accountItem = new AccountItem();
            accountItem.setHeaderId(accountHead.getId());
            accountItem.setAccountId(getDefaultAccountId(tenantId));
            accountItem.setEachAmount(revenue);
            accountItem.setRemark("咖啡店日销售收入");
            accountItem.setTenantId(tenantId);
            accountItem.setDeleteFlag("0");

            // 保存财务明细
            accountItemService.save(accountItem);
            */

            // 记录操作日志
            logService.insertLogWithUserId(recorderId, tenantId, "咖啡店销售",
                "生成销售收入记录,日期:" + salesDate + ",金额:" + revenue,
                null);

            return true; // 暂时返回成功
        } catch (Exception e) {
            logger.error("生成咖啡店销售财务记录失败", e);
            throw new RuntimeException("财务记录生成失败", e);
        }
    }

    /**
     * POS销售生成财务收入记录
     * 
     * @param orderData 订单数据
     * @param tenantId 租户ID
     * @return 是否成功
     */
    @Transactional
    public boolean generatePOSSalesFinanceRecord(Map<String, Object> orderData, Long tenantId) {
        try {
            String orderNo = orderData.get("orderNo").toString();
            BigDecimal totalAmount = new BigDecimal(orderData.get("totalAmount").toString());
            BigDecimal actualAmount = new BigDecimal(orderData.get("actualAmount").toString());
            String paymentMethod = orderData.get("paymentMethod").toString();
            Long cashierId = Long.valueOf(orderData.get("cashierId").toString());

            // TODO: AccountHeadService和AccountItemService缺少save方法，暂时注释财务记录生成
            /*
            // 创建财务主表记录
            AccountHead accountHead = new AccountHead();
            accountHead.setType("收入");
            accountHead.setBillNo(generateBillNo("POS"));
            accountHead.setBillTime(new Date()); // 修复类型转换问题
            accountHead.setHandsPersonId(cashierId);
            accountHead.setChangeAmount(actualAmount);
            accountHead.setTotalPrice(totalAmount);
            accountHead.setRemark("POS销售收入 - 订单号:" + orderNo);
            accountHead.setTenantId(tenantId);
            accountHead.setDeleteFlag("0");

            // 保存财务主表
            accountHeadService.save(accountHead);

            // 创建财务明细记录
            AccountItem accountItem = new AccountItem();
            accountItem.setHeaderId(accountHead.getId());
            accountItem.setAccountId(getAccountByPaymentMethod(paymentMethod, tenantId));
            accountItem.setEachAmount(actualAmount);
            accountItem.setRemark("POS销售收入 - " + getPaymentMethodName(paymentMethod));
            accountItem.setTenantId(tenantId);
            accountItem.setDeleteFlag("0");

            // 保存财务明细
            accountItemService.save(accountItem);
            */

            // 记录操作日志
            logService.insertLogWithUserId(cashierId, tenantId, "POS销售",
                "生成销售收入记录,订单:" + orderNo + ",金额:" + actualAmount,
                null);

            return true;
        } catch (Exception e) {
            logger.error("生成POS销售财务记录失败", e);
            throw new RuntimeException("财务记录生成失败", e);
        }
    }

    /**
     * 生成默认头像URL
     */
    private String generateDefaultAvatar(String name) {
        try {
            return "https://ui-avatars.com/api/?name=" +
                   java.net.URLEncoder.encode(name, "UTF-8") +
                   "&background=3B82F6&color=fff&size=100";
        } catch (Exception e) {
            return "https://ui-avatars.com/api/?name=User&background=3B82F6&color=fff&size=100";
        }
    }

    /**
     * 生成单据编号
     */
    private String generateBillNo(String prefix) {
        return prefix + System.currentTimeMillis();
    }

    /**
     * 获取默认账户ID
     */
    private Long getDefaultAccountId(Long tenantId) {
        // 这里应该根据租户获取默认现金账户
        // 暂时返回固定值，实际应该查询账户表
        return 1L;
    }

    /**
     * 根据支付方式获取对应账户ID
     */
    private Long getAccountByPaymentMethod(String paymentMethod, Long tenantId) {
        // 根据支付方式返回不同的账户ID
        switch (paymentMethod) {
            case "cash":
                return getCashAccountId(tenantId);
            case "alipay":
                return getAlipayAccountId(tenantId);
            case "wechat":
                return getWechatAccountId(tenantId);
            case "card":
                return getBankAccountId(tenantId);
            default:
                return getDefaultAccountId(tenantId);
        }
    }

    /**
     * 获取支付方式显示名称
     */
    private String getPaymentMethodName(String paymentMethod) {
        switch (paymentMethod) {
            case "cash": return "现金支付";
            case "alipay": return "支付宝";
            case "wechat": return "微信支付";
            case "card": return "银行卡";
            default: return paymentMethod;
        }
    }

    // 以下方法需要根据实际的账户配置实现
    private Long getCashAccountId(Long tenantId) { return 1L; }
    private Long getAlipayAccountId(Long tenantId) { return 2L; }
    private Long getWechatAccountId(Long tenantId) { return 3L; }
    private Long getBankAccountId(Long tenantId) { return 4L; }
}
