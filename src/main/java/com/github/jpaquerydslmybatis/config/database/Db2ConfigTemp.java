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
//@EnableJpaRepositories(
//        basePackages = "com.github.jpaquerydslmybatis.repository.db2",
//        entityManagerFactoryRef = "db2EntityManagerFactory",
//        transactionManagerRef = "db2TransactionManager"
//)
//@MapperScan(
//        basePackages = "com.github.jpaquerydslmybatis.repository.db2.mybatis",
//        sqlSessionFactoryRef = "db2SqlSessionFactory"
//)
//public class Db2Config {
//
//    @Bean
//    @ConfigurationProperties("spring.datasource.db2")
//    public DataSourceProperties db2DataSourceProperties() {
//        return new DataSourceProperties();
//    }
//
//    @Bean
//    public DataSource db2DataSource() {
//        return db2DataSourceProperties().initializeDataSourceBuilder().build();
//    }
//
//    @Bean
//    public LocalContainerEntityManagerFactoryBean db2EntityManagerFactory(
//            EntityManagerFactoryBuilder builder) {
//        return builder
//                .dataSource(db2DataSource())
//                .packages("com.github.jpaquerydslmybatis.web.domain.db2")
//                .persistenceUnit("db2PU")
//                .build();
//    }
//
//    @Bean
//    public PlatformTransactionManager db2TransactionManager(
//            @Qualifier("db2EntityManagerFactory") EntityManagerFactory emf) {
//        return new JpaTransactionManager(emf);
//    }
//
//    @Bean
//    public SqlSessionFactory db2SqlSessionFactory(
//            @Qualifier("db2DataSource") DataSource ds) throws Exception {
//        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
//        factory.setDataSource(ds);
//        factory.setMapperLocations(
//                new PathMatchingResourcePatternResolver()
//                        .getResources("classpath:/mappers/db2/**/*.xml"));
//        return factory.getObject();
//    }
//}
//
