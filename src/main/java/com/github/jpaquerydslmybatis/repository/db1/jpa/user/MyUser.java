package com.github.jpaquerydslmybatis.repository.db1.jpa.user;

import com.github.jpaquerydslmybatis.common.converter.custom.GenderConverter;
import com.github.jpaquerydslmybatis.common.converter.custom.RoleEnumConverter;
import com.github.jpaquerydslmybatis.common.myenum.Gender;
import com.github.jpaquerydslmybatis.common.myenum.RoleEnum;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "my_user")
@Getter
public class MyUser {
    @Id
    private String userId;
    @Column(name = "user_name")
    private String username;
    private String password;

    private String email;
    private String phoneNumber;
    private String address;
    private String addressDetail;
    private String addressReference;
    @Convert(converter = GenderConverter.class)
    private Gender gender;
    private LocalDateTime birthDay;
    private long coin;
    private long point;
    @Column(name = "register_at")
    private LocalDateTime registerAt;
    private LocalDateTime lastChangeAt;
    private boolean status; // 계정 활성화 여부
    private boolean dormancy;// 휴면계정 여부
    @Convert(converter = RoleEnumConverter.class)
    private RoleEnum role;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "myUser")
    private List<LoginHistory> loginHistories;

    public static MyUser onlyId(String userId) {
        MyUser myUser = new MyUser();
        myUser.userId = userId;
        return myUser;
    }
}
