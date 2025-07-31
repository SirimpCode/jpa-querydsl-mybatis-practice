package com.github.jpaquerydslmybatis.repository.db1.jpa.test;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringTestRepository extends JpaRepository<SpringTest, Long>, SpringTestQueryRepository {

}
