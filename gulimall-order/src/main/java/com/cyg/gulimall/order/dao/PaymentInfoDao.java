package com.cyg.gulimall.order.dao;

import com.cyg.gulimall.order.entity.PaymentInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付信息表
 * 
 * @author CuiYuangeng
 * @email 1021716024@qq.com
 * @date 2022-03-12 16:47:50
 */
@Mapper
public interface PaymentInfoDao extends BaseMapper<PaymentInfoEntity> {
	
}
