package com.github.jpaquerydslmybatis.config.database.jpa;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuerydslConfigDb1 {
    @PersistenceContext(unitName = "db1PU")
    private EntityManager em;

    @Bean
    public JPAQueryFactory db1QueryFactory() {
        return new JPAQueryFactory(em);
    }
    /*
    private final JPAQueryFactory db1QueryFactory;

    public MemberRepositoryCustomImpl(@Qualifier("db1QueryFactory") JPAQueryFactory db1QueryFactory) {
        this.db1QueryFactory = db1QueryFactory;
    }
    */
}
