package com.github.jpaquerydslmybatis.repository.db1.jpa.user.emp;

import com.github.jpaquerydslmybatis.repository.db1.jpa.user.MyUser;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "emp_management")
public class EmpManagement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long empManagementId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private MyUser myUser;

    private String clientIp;
    private LocalDateTime accessTime;
    private String pageUrl;

    public static EmpManagement of(MyUser myUser, String clientIp,  String pageUrl) {
        EmpManagement empManagement = new EmpManagement();
        empManagement.myUser = myUser;
        empManagement.clientIp = clientIp;
        empManagement.accessTime = LocalDateTime.now();
        empManagement.pageUrl = pageUrl;
        return empManagement;
    }
}
