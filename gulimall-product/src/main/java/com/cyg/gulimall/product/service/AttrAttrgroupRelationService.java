package com.cyg.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cyg.common.utils.PageUtils;
import com.cyg.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.cyg.gulimall.product.vo.AttrGroupRelationVo;

import java.util.List;
import java.util.Map;

/**
 * 属性&属性分组关联
 *
 * @author CuiYuangeng
 * @email 1021716024@qq.com
 * @date 2022-03-12 15:10:36
 */
public interface AttrAttrgroupRelationService extends IService<AttrAttrgroupRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveBatch(List<AttrGroupRelationVo> attrGroupRelationVos);
}

