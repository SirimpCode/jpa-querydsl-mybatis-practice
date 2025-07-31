//package com.github.jpaquerydslmybatis.config;
//
//import org.apache.ibatis.session.SqlSessionFactory;
//import org.mybatis.spring.SqlSessionFactoryBean;
//import org.mybatis.spring.annotation.MapperScan;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
//
//import javax.sql.DataSource;
//
//@Configuration
//@MapperScan(basePackages={"com.example.mybatismixjpa.mapper"}, sqlSessionFactoryRef="sqlSessionFactory",sqlSessionTemplateRef = "sqlSessionTemplate")
//public class MybatisConfig {
//
//    @Bean(name="sqlSessionFactory")//yml 프로퍼티를 통해 자동생성된 jpa dataSource 이다.
//    public SqlSessionFactory sqlSessionFactory(@Qualifier("dataSource") DataSource dataSource) throws Exception {
//        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
//        sqlSessionFactoryBean.setDataSource(dataSource);
//        sqlSessionFactoryBean.setTypeAliasesPackage("com.example.mybatismixjpa");
//        sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:/mybatis/*.xml"));
//        return sqlSessionFactoryBean.getObject();
//    }
//
//}
