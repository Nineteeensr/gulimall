package com.cyg.gulimall.member.dao;

import com.cyg.gulimall.member.entity.MemberLoginLogEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员登录记录
 * 
 * @author CuiYuangeng
 * @email 1021716024@qq.com
 * @date 2022-03-12 16:42:09
 */
@Mapper
public interface MemberLoginLogDao extends BaseMapper<MemberLoginLogEntity> {
	
}
