package com.github.jpaquerydslmybatis.web.dto.employees;

import lombok.AccessLevel;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class EmpListResponse {
    private Long employeeId;
    private Long departmentId;
    private String departmentName;
    private String firstName;
    private String lastName;
    @Getter(AccessLevel.NONE)
    private Double salary;
    @Getter(AccessLevel.NONE)
    private Double commissionPct;
    @Getter(AccessLevel.NONE)
    private String rrn;
    @Getter(AccessLevel.NONE)
    private LocalDate hireDate;

    public long getAge() {
        boolean isBirthDayOver = LocalDateTime.now().getMonthValue() > hireDate.getMonthValue() ||
                (LocalDateTime.now().getMonthValue() == hireDate.getMonthValue() &&
                        LocalDateTime.now().getDayOfMonth() >= hireDate.getDayOfMonth());
        // 주민등록번호로 나이 계산
        // 예시: 9001011234567 형식의 주민등록번호에서 90년생이면 33세, 00년생이면 23세
        boolean isYoung = this.rrn.charAt(6) == '3' || this.rrn.charAt(6) == '4';
        int birthYear = isYoung ?
                Integer.parseInt(this.rrn.substring(0, 2)) + 2000 :
                Integer.parseInt(this.rrn.substring(0, 2)) + 1900;
        int age = LocalDateTime.now().getYear() - birthYear;
        return isBirthDayOver ? age : age-1;
    }
    public String getGender(){
        char genderCode = this.rrn.charAt(6);
        return switch (genderCode) {
            case '1', '3' -> "남성";
            case '2', '4' -> "여성";
            default -> "반반섞임";
        };
    }
    public String getRefinedHireDate(){
        return String.format("%d년 %d월 %d일", hireDate.getYear(), hireDate.getMonthValue(), hireDate.getDayOfMonth());
    }
    public String getRefinedSalary(){
        if (this.salary == null) return null;
        if (this.commissionPct == null)
            //3자리마다 , 찍기 소수점 2자리까지 표시
            return String.format("$%,.2f", this.salary);
        double refinedSal = this.salary * this.commissionPct + this.salary;
        return String.format("$%,.2f", refinedSal);
    }
    public Double getSalaryNumberType(){
        if (this.salary == null) return null;
        if (this.commissionPct == null)
            return this.salary;
        return this.salary * this.commissionPct + this.salary;
    }

}
