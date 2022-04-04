package com.cyg.gulimall.order.dao;

import com.cyg.gulimall.order.entity.OrderItemEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单项信息
 *
 * @author CuiYuangeng
 * @email 1021716024@qq.com
 * @date 2022-03-12 16:47:50
 */
@Mapper
public interface OrderItemDao extends BaseMapper<OrderItemEntity> {

}
