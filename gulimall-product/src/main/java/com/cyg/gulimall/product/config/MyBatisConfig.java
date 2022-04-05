package com.cyg.gulimall.product.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author CuiYuangeng
 * @create 2022-04-05 12:59
 */
@EnableTransactionManagement // 开启事务
@Configuration
@MapperScan("com.cyg.gulimall.product.dao")
public class MyBatisConfig {


    /**
     * 引入分页插件
     * @return
     */
    @Bean
    public PaginationInterceptor paginationInterceptor(){
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        // 设置请求的页面大于最大页后操作，true返回首页，false继续请求 默认（false）
        paginationInterceptor.setOverflow(true);
        // 设置最大但也限制数量，默认500条， -1 不受限制
        paginationInterceptor.setLimit(1000);
        return paginationInterceptor;
    }
}
