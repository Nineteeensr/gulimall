package com.cyg.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cyg.common.utils.PageUtils;
import com.cyg.gulimall.coupon.entity.CouponHistoryEntity;

import java.util.Map;

/**
 * 优惠券领取历史记录
 *
 * @author CuiYuangeng
 * @email 1021716024@qq.com
 * @date 2022-03-12 16:35:03
 */
public interface CouponHistoryService extends IService<CouponHistoryEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

