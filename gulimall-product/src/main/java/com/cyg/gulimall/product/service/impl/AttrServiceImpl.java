package com.cyg.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cyg.common.utils.PageUtils;
import com.cyg.common.utils.Query;
import com.cyg.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.cyg.gulimall.product.dao.AttrDao;
import com.cyg.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.cyg.gulimall.product.entity.AttrEntity;
import com.cyg.gulimall.product.service.AttrService;
import com.cyg.gulimall.product.vo.AttrVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Map;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Resource
    AttrAttrgroupRelationDao attrAttrgroupRelationDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 保存规格参数到管理表和主表中
     * @param attr
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveAttr(AttrVo attr) {
        // 1、给attr表保存基本数据
        AttrEntity attrEntity = new AttrEntity();
        // 将vo对象的值 给 PO对象
        BeanUtils.copyProperties(attr, attrEntity);
        this.save(attrEntity);
        // 2、保存关联关系
        AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
        // 关联表relation的 attr_group_id 和
        relationEntity.setAttrGroupId(attr.getAttrGroupId());
        relationEntity.setAttrId(attrEntity.getAttrId());

        attrAttrgroupRelationDao.insert(relationEntity);
    }

}