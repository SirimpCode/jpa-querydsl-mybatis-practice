package com.github.jpaquerydslmybatis.repository.db1.jpa.user;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "login_history")
public class LoginHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historyId;
    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private MyUser myUser;
    private LocalDateTime loginDate;
    private String clientIp;

    public static LoginHistory fromUserAndIp(MyUser myUser, String clientIp) {
        LoginHistory loginHistory = new LoginHistory();
        loginHistory.myUser = myUser;
        loginHistory.loginDate = LocalDateTime.now();
        loginHistory.clientIp = clientIp;
        return loginHistory;
    }

}
