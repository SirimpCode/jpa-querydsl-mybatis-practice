package com.github.jpaquerydslmybatis.repository.db2.jpa.employees;

import com.github.jpaquerydslmybatis.repository.db2.jpa.department.Departments;
import com.github.jpaquerydslmybatis.repository.db2.jpa.job.Jobs;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@DynamicInsert
public class Employees {
    @Id
    private Long employeeId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private LocalDate hireDate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id")
    private Jobs jobs;
    private Double salary;
    private Double commissionPct;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private Employees manager;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Departments departments;
    @Column(name = "jubun")
    private String rrn;// 주민등록번호 residentRegistrationNumber

//    public long getAge() {
//        boolean isBirthDayOver = LocalDateTime.now().getMonthValue() > hireDate.getMonthValue() ||
//                (LocalDateTime.now().getMonthValue() == hireDate.getMonthValue() &&
//                        LocalDateTime.now().getDayOfMonth() >= hireDate.getDayOfMonth());
//        // 주민등록번호로 나이 계산
//        // 예시: 9001011234567 형식의 주민등록번호에서 90년생이면 33세, 00년생이면 23세
//        boolean isYoung = this.rrn.charAt(6) == '3' || this.rrn.charAt(6) == '4';
//        int birthYear = isYoung ?
//                Integer.parseInt(this.rrn.substring(0, 2)) + 2000 :
//                Integer.parseInt(this.rrn.substring(0, 2)) + 1900;
//        int age = LocalDateTime.now().getYear() - birthYear;
//        return isBirthDayOver ? age : age-1;
//    }
//    public String getGender(){
//        char genderCode = this.rrn.charAt(6);
//        return switch (genderCode) {
//            case '1', '3' -> "남성";
//            case '2', '4' -> "여성";
//            default -> "중성";
//        };
//    }
//    public String getRefinedHireDate(){
//        return String.format("%d년 %d월 %d일", hireDate.getYear(), hireDate.getMonthValue(), hireDate.getDayOfMonth());
//    }

}
