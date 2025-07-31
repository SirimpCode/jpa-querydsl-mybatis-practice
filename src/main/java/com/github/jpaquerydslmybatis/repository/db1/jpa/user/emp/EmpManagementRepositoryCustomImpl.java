package com.github.jpaquerydslmybatis.repository.db1.jpa.user.emp;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
public class EmpManagementRepositoryCustomImpl implements EmpManagementRepositoryCustom {
    private final @Qualifier("db1QueryFactory") JPAQueryFactory queryFactory;
}
