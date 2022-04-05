package com.cyg.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cyg.common.utils.PageUtils;
import com.cyg.common.utils.Query;
import com.cyg.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.cyg.gulimall.product.dao.AttrDao;
import com.cyg.gulimall.product.dao.AttrGroupDao;
import com.cyg.gulimall.product.dao.CategoryDao;
import com.cyg.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.cyg.gulimall.product.entity.AttrEntity;
import com.cyg.gulimall.product.entity.AttrGroupEntity;
import com.cyg.gulimall.product.entity.CategoryEntity;
import com.cyg.gulimall.product.service.AttrService;
import com.cyg.gulimall.product.vo.AttrRespVo;
import com.cyg.gulimall.product.vo.AttrVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Resource
    AttrAttrgroupRelationDao relationDao;

    @Resource
    AttrGroupDao attrGroupDao;

    @Resource
    CategoryDao categoryDao;

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
     *
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
        relationEntity.setAttrSort(0);
        relationDao.insert(relationEntity);
    }

    @Override
    public PageUtils queryBaseAttr(Map<String, Object> params, Long catelogId) {
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (catelogId != 0) {
            wrapper.eq("catelog_id", catelogId);
        }
        if (!StringUtils.isEmpty(key)) {
            wrapper.and((obj) -> {
                obj.eq("attr_id", key).or().like("attr_name", key).or().like("value_select", key);
            });
        }
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), wrapper);
        PageUtils pageUtils = new PageUtils(page);
        List<AttrEntity> records = page.getRecords();

        // stream流映射结果集为respVO对象
        List<AttrRespVo> respVos = records.stream().map(attrEntity -> {

            AttrRespVo attrRespVo = new AttrRespVo();
            BeanUtils.copyProperties(attrEntity, attrRespVo);
            // 1、设置分类和分组的名字
            AttrAttrgroupRelationEntity attrId = relationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>()
                    .eq("attr_id", attrEntity.getAttrId()));
            if (attrId != null && attrId.getAttrGroupId() != null) {
                AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrId.getAttrGroupId());
                attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
            }

            // 分类名
            CategoryEntity categoryEntity = categoryDao.selectById(attrEntity.getCatelogId());
            if (categoryEntity != null) {
                attrRespVo.setCatelogName(categoryEntity.getName());
            }
            return attrRespVo;

        }).collect(Collectors.toList());
        pageUtils.setList(respVos);
        return pageUtils;
    }

}