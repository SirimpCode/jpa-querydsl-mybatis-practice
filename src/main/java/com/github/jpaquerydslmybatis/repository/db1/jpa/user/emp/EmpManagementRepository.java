package com.github.jpaquerydslmybatis.repository.db1.jpa.user.emp;

import org.springframework.data.jpa.repository.JpaRepository;



public interface EmpManagementRepository extends JpaRepository<EmpManagement, Long>,EmpManagementRepositoryCustom {
    
}
