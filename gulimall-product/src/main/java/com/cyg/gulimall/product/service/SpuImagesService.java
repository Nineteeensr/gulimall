package com.cyg.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cyg.common.utils.PageUtils;
import com.cyg.gulimall.product.entity.SpuImagesEntity;

import java.util.Map;

/**
 * spu图片
 *
 * @author CuiYuangeng
 * @email 1021716024@qq.com
 * @date 2022-03-12 15:10:36
 */
public interface SpuImagesService extends IService<SpuImagesEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

