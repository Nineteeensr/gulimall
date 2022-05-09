package com.cyg.gulimall.product.vo;

import com.cyg.gulimall.product.entity.AttrEntity;
import lombok.Data;

import java.util.List;

/**
 * 属性分组及属性信息 VO
 * @author CuiYuangeng
 * @create 2022-04-26 16:46
 */
@Data
public class AttrGroupWithAttrsVo {
    /**
     * 分组id
     */
    private Long attrGroupId;
    /**
     * 组名
     */
    private String attrGroupName;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 描述
     */
    private String descript;
    /**
     * 组图标
     */
    private String icon;
    /**
     * 所属分类id
     */
    private Long catelogId;

    private List<AttrEntity> attrs;
}
