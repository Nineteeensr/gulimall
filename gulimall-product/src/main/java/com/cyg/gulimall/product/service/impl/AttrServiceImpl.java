package com.cyg.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cyg.common.constant.ProductConstant;
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
import com.cyg.gulimall.product.service.CategoryService;
import com.cyg.gulimall.product.vo.AttrGroupRelationVo;
import com.cyg.gulimall.product.vo.AttrRespVo;
import com.cyg.gulimall.product.vo.AttrVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Resource
    AttrAttrgroupRelationDao relationDao;

    @Resource
    AttrGroupDao attrGroupDao;

    @Resource
    CategoryDao categoryDao;

    @Autowired
    CategoryService categoryService;

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
        if (attr.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            // 关联表relation的 attr_group_id 和
            relationEntity.setAttrGroupId(attr.getAttrGroupId());
            relationEntity.setAttrId(attrEntity.getAttrId());
            relationEntity.setAttrSort(0);
            relationDao.insert(relationEntity);
        }
    }

    @Override
    public PageUtils queryBaseAttr(Map<String, Object> params, Long catelogId, String attrType) {

        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<AttrEntity>()
                .eq("attr_type", "base".equalsIgnoreCase(attrType)
                        ? ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() : ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode());

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

            if ("base".equalsIgnoreCase(attrType)) {
                // 1、设置分类和分组的名字
                AttrAttrgroupRelationEntity attrId = relationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>()
                        .eq("attr_id", attrEntity.getAttrId()));
                if (attrId != null && attrId.getAttrGroupId() != null) {
                    AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrId.getAttrGroupId());
                    attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }
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

    /**
     * 查询规格参数详细信息
     *
     * @param attrId
     * @return
     */
    @Override
    public AttrRespVo getAttrInfo(Long attrId) {
        AttrRespVo respVo = new AttrRespVo();
        // 1、查询主表（attr）中的信息
        AttrEntity attrEntity = this.getById(attrId);
        BeanUtils.copyProperties(attrEntity, respVo);

        if (attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            // 根据attrId 去关联表查询 关联对象 然后获取关联表的attrGroupId
            AttrAttrgroupRelationEntity attrgroupRelation = relationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>()
                    .eq("attr_id", attrId));
            // 2、设置分组(attr_group)信息
            if (attrgroupRelation != null && attrgroupRelation.getAttrGroupId() != null) {
                respVo.setAttrGroupId(attrgroupRelation.getAttrGroupId());

                // 根据分组id 去attr_group（属性分组）表中 获取attrgroup_name
                AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrgroupRelation.getAttrGroupId());
                respVo.setGroupName(attrGroupEntity.getAttrGroupName());
            }
        }

        // 3、设置分类(category)信息
        Long catelogId = attrEntity.getCatelogId();
        Long[] catelogPath = categoryService.findCatelogPath(catelogId);
        respVo.setCatelogPath(catelogPath);
        CategoryEntity categoryEntity = categoryDao.selectById(catelogId);

        if (categoryEntity != null) {
            respVo.setCatelogName(categoryEntity.getName());
        }

        return respVo;
    }

    /**
     * 修改规格参数
     * 多表修改:
     * 1) pms_attr
     * 2) attr_attrgroup_relation
     *
     * @param attrVo
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateAttr(AttrVo attrVo) {
        // 1、修改pms_attr表
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attrVo, attrEntity);
        this.updateById(attrEntity);
        if (attrVo.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            // 1、修改分组关联 attr_attrgroup_relation 表
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();

            relationEntity.setAttrGroupId(attrVo.getAttrGroupId());
            relationEntity.setAttrId(attrVo.getAttrId());

            // 根据attr_id查询数据记录数
            Integer count = relationDao.selectCount(new QueryWrapper<AttrAttrgroupRelationEntity>()
                    .eq("attr_id", attrVo.getAttrId()));
            if (count > 0) {
                relationDao.update(relationEntity, new UpdateWrapper<AttrAttrgroupRelationEntity>()
                        .eq("attr_id", attrVo.getAttrId()));
            } else {
                relationDao.insert(relationEntity);
            }
        }
    }

    /**
     * 删除规格参数 TODO
     * 操作 1) pms_attr
     * 2) attr_attrgroup_relation
     *
     * @param longs
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void removeAttr(List<Long> longs) {
        // 1) pms_attr
        this.removeByIds(longs);

        // 2) attr_attrgroup_relation
        List<Long> list = new ArrayList<>();
        for (Long attrId : longs) {
            AttrAttrgroupRelationEntity relationEntity = relationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>()
                    .eq("attr_id", attrId));
            // 如果为空说明是销售属性：销售属性无需在关联表中关联
            if (relationEntity == null) {
                return;
            }
            list.add(relationEntity.getId());
        }
        // 根据Id删除attr_attrgroup_relation
        relationDao.deleteBatchIds(list);
    }


    /**
     * 根据分组Id查找关联的所有基本属性
     *
     * @param attrgroupId
     * @return
     */
    @Override
    public List<AttrEntity> getRelationAttr(Long attrgroupId) {
        // 根据attr_group_id 去关联表（attr_attrgroup_relation）中查attr_id
        List<AttrAttrgroupRelationEntity> entities = relationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>()
                .eq("attr_group_id", attrgroupId));
        if (!entities.isEmpty()) {
            List<Long> list = entities.stream().map(attr -> attr.getAttrId()).collect(Collectors.toList());
            // 再根据attr_id 去（attr）表查基本属性
            Collection<AttrEntity> attrEntities = this.listByIds(list);
            return (List<AttrEntity>) attrEntities;
        }
        return null;
    }

    /**
     * 批量删除（attr_attrgroup_relation）
     *
     * @param vos
     */
    @Override
    public void deleteRelation(AttrGroupRelationVo[] vos) {
        List<AttrAttrgroupRelationEntity> entities = Arrays.asList(vos).stream().map(item -> {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(item, relationEntity);
            return relationEntity;
        }).collect(Collectors.toList());
        // 批量删除
        relationDao.deleteBatchRelation(entities);
    }

    /**
     * 获取当前分组没有关联的数据
     *
     * @param params
     * @param attrgroupId
     * @return
     */
    @Override
    public PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId) {
        // 1、当前分组只能关联自己所属分类的所有属性
        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrgroupId);
        Long catelogId = attrGroupEntity.getCatelogId();

        // 2、只能关联别的分组没有引用的属性
        //  1) 当前分类下的其他分组
        List<AttrGroupEntity> groupEntities = attrGroupDao.selectList(new QueryWrapper<AttrGroupEntity>()
                .eq("catelog_id", catelogId));
        List<Long> collect = groupEntities.stream().map(item -> item.getAttrGroupId()).collect(Collectors.toList());

        //  2) 这些分组关联的属性
        List<AttrAttrgroupRelationEntity> groupId = relationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>()
                .in("attr_group_id", collect));
        List<Long> attrIds = groupId.stream().map(item -> item.getAttrId()).collect(Collectors.toList());

        //  3) 从当前分类的所有属性中移除这些属性 【注意这里只查询基本属性】
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<AttrEntity>()
                .eq("catelog_id", catelogId).eq("attr_type", ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode());
        if (!attrIds.isEmpty()) {
            wrapper.notIn("attr_id", attrIds);
        }
        // 关键字模糊查询
        String key = (String) params.get("key");
        if (StringUtils.isEmpty(key)) {
            wrapper.and(w -> w.eq("attr_id", key).or().like("attr_name", key).or().like("value_select", key));
        }
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), wrapper);
        return new PageUtils(page);
    }

}