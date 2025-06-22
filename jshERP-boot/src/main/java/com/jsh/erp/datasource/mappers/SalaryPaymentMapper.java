package com.jsh.erp.datasource.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsh.erp.datasource.entities.SalaryPayment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 薪酬发放记录Mapper接口
 * 
 * @author jshERP
 * @date 2025-06-22
 */
@Mapper
public interface SalaryPaymentMapper extends BaseMapper<SalaryPayment> {

}
