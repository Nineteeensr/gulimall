package com.cyg.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cyg.common.utils.PageUtils;
import com.cyg.gulimall.ware.entity.WareSkuEntity;

import java.util.Map;

/**
 * εεεΊε­
 *
 * @author CuiYuangeng
 * @email 1021716024@qq.com
 * @date 2022-03-12 16:49:51
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

