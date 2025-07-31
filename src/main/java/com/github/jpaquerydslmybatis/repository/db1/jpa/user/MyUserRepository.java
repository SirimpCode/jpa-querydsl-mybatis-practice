package com.github.jpaquerydslmybatis.repository.db1.jpa.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MyUserRepository extends JpaRepository<MyUser,String>, MyUserRepositoryCustom {


}
