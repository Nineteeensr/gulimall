package com.cyg.gulimall.product.dao;

import com.cyg.gulimall.product.entity.CommentReplayEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品评价回复关系
 * 
 * @author CuiYuangeng
 * @email 1021716024@qq.com
 * @date 2022-03-12 15:10:36
 */
@Mapper
public interface CommentReplayDao extends BaseMapper<CommentReplayEntity> {
	
}
