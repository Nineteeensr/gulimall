package com.cyg.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cyg.common.utils.PageUtils;
import com.cyg.common.utils.Query;
import com.cyg.gulimall.product.dao.AttrGroupDao;
import com.cyg.gulimall.product.entity.AttrEntity;
import com.cyg.gulimall.product.entity.AttrGroupEntity;
import com.cyg.gulimall.product.service.AttrGroupService;
import com.cyg.gulimall.product.service.AttrService;
import com.cyg.gulimall.product.vo.AttrGroupWithAttrsVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Resource
    private AttrService attrService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long categlogId) {
        String key = (String) params.get("key");
        // select * from psm_attr_group where catelog_id = ? and (attr_group_id = key or attr_group_name like %key%)
        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<>();
        if (categlogId != 0) {
            wrapper.eq("catelog_id", categlogId);
        }
        if (!StringUtils.isEmpty(key)) {
            wrapper.and((obj) -> {
                obj.eq("attr_group_id", key).or().like("attr_group_name", key);
            });
        }
        IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params)
                , wrapper);
        return new PageUtils(page);
    }

    /**
     * 根据分类 ID 查询所有属性分组以及分组属性信息
     *
     * @param catelogId
     * @return
     */
    @Override
    public List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCatId(Long catelogId) {
        // 1、 查出当前分类下的所有属性分组
        List<AttrGroupEntity> attrGroupEntities = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
        // 2、 查出每个属性分组的所有属性
        List<AttrGroupWithAttrsVo> attrsVos = attrGroupEntities.stream().map(item -> {
            AttrGroupWithAttrsVo attrsVo = new AttrGroupWithAttrsVo();
            BeanUtils.copyProperties(item, attrsVo);
            List<AttrEntity> relationAttr = attrService.getRelationAttr(attrsVo.getAttrGroupId());
            attrsVo.setAttrs(relationAttr);
            return attrsVo;
        }).collect(Collectors.toList());

        return attrsVos;
    }

}