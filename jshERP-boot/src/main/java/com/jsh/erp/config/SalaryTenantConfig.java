package com.jsh.erp.config;

import com.baomidou.mybatisplus.extension.plugins.tenant.TenantHandler;
import com.jsh.erp.utils.Tools;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * 薪酬管理模块多租户配置
 * 
 * @author jshERP
 * @date 2025-06-22
 */
@Configuration
public class SalaryTenantConfig {

    /**
     * 薪酬管理相关表名列表
     */
    private static final List<String> SALARY_TABLES = Arrays.asList(
        "jsh_salary_profile",
        "jsh_salary_item", 
        "jsh_salary_calculation",
        "jsh_salary_payment",
        "jsh_salary_detail",
        "jsh_employee_salary_item"
    );

    /**
     * 配置薪酬管理模块的多租户处理器
     * 注意：在MyBatis Plus 3.0.7.1版本中，多租户配置已在TenantConfig中统一处理
     * 这里提供薪酬模块专用的租户处理器实现
     */
    public TenantHandler createSalaryTenantHandler() {
        return new TenantHandler() {

            @Override
            public Expression getTenantId() {
                // 获取当前请求的租户ID
                Long tenantId = getCurrentTenantId();
                return new LongValue(tenantId != null ? tenantId : 0L);
            }

            @Override
            public String getTenantIdColumn() {
                return "tenant_id";
            }

            @Override
            public boolean doTableFilter(String tableName) {
                // 只对薪酬管理相关表进行多租户处理
                return SALARY_TABLES.contains(tableName.toLowerCase());
            }
        };
    }

    /**
     * 获取当前请求的租户ID
     */
    private Long getCurrentTenantId() {
        try {
            ServletRequestAttributes attributes = 
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String token = request.getHeader("X-Access-Token");
                
                if (token != null && !token.isEmpty()) {
                    return Tools.getTenantIdByToken(token);
                }
            }
            
            // 如果无法获取租户ID，返回默认值0（系统租户）
            return 0L;
            
        } catch (Exception e) {
            // 异常情况下返回默认租户ID
            return 0L;
        }
    }
}
