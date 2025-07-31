//package com.github.jpaquerydslmybatis.config.database;
//
//import com.querydsl.jpa.impl.JPAQueryFactory;
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.EntityManagerFactory;
//import jakarta.persistence.PersistenceContext;
//import org.apache.ibatis.session.SqlSessionFactory;
//import org.mybatis.spring.SqlSessionFactoryBean;
//import org.mybatis.spring.annotation.MapperScan;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
//import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
//import org.springframework.orm.jpa.JpaTransactionManager;
//import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
//import org.springframework.transaction.PlatformTransactionManager;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
//
//import javax.sql.DataSource;
//
//@Configuration
//@EnableTransactionManagement
//@EnableJpaAuditing
//@EnableJpaRepositories(
//        basePackages = "com.github.jpaquerydslmybatis.repository.db1",
//        entityManagerFactoryRef = "db1EntityManagerFactory",
//        transactionManagerRef = "db1TransactionManager"
//)
//@MapperScan(
//        basePackages = "com.github.jpaquerydslmybatis.repository.db1.mybatis",
//        sqlSessionFactoryRef = "db1SqlSessionFactory"
//)
//public class Db1Config {
//
//    @Bean
//    @ConfigurationProperties("spring.datasource.db1")
//    public DataSourceProperties db1DataSourceProperties() {
//        return new DataSourceProperties();
//    }
//
//    @Bean
//    public DataSource db1DataSource() {
//        return db1DataSourceProperties().initializeDataSourceBuilder().build();
//    }
//
//    @Bean
//    public LocalContainerEntityManagerFactoryBean db1EntityManagerFactory(
//            EntityManagerFactoryBuilder builder) {
//        return builder
//                .dataSource(db1DataSource())
//                .packages("com.github.jpaquerydslmybatis.web.domain.db1")
//                .persistenceUnit("db1PU")
//                .build();
//    }
//
//    @Bean
//    public PlatformTransactionManager db1TransactionManager(
//            @Qualifier("db1EntityManagerFactory") EntityManagerFactory emf) {
//        return new JpaTransactionManager(emf);
//    }
//
//    @Bean
//    public SqlSessionFactory db1SqlSessionFactory(
//            @Qualifier("db1DataSource") DataSource ds) throws Exception {
//        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
//        factory.setDataSource(ds);
//        factory.setMapperLocations(
//                new PathMatchingResourcePatternResolver()
//                        .getResources("classpath:/mappers/db1/**/*.xml"));
//        return factory.getObject();
//    }
//    //@Transactional(value = "db1TransactionManager", readOnly = true)
//
//
//
//}
