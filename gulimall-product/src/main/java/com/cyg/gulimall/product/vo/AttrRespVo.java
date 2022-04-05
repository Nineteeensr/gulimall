package com.cyg.gulimall.product.vo;

import lombok.Data;

/**
 * @author CuiYuangeng
 * @create 2022-04-05 17:13
 */
@Data
public class AttrRespVo extends AttrVo{
    /**
     * 所属分类名
     */
   private String catelogName;
    /**
     * 所属主体名
     */
   private String groupName;

    /**
     *  所属分类 [父/子/孙]
     */
   private Long[] catelogPath;
}
