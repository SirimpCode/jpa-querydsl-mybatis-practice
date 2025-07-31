package com.github.jpaquerydslmybatis.web.dto.auth;

import com.github.jpaquerydslmybatis.common.myenum.Gender;
import com.github.jpaquerydslmybatis.common.myenum.RoleEnum;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.function.Function;
//== Lombok 라이브러리에서 제공하는 어노테이션 ==

//@ToString                // 객체를 문자열로 표현할 때 사용
//@RequiredArgsConstructor // final 이거나 @NonNull이 붙은 필드만 포함하는 생성자를 자동으로 생성해준다.
//@AllArgsConstructor      // 모든 필드 값을 파라미터로 받는 생성자를 만들어주는 것
//@NoArgsConstructor       // 파라미터가 없는 기본생성자를 만들어주는 것
//@Data                    // lombok 에서 사용하는 @Data 어노테이션은 @Getter, @Setter, @ToString, @EqualsAndHashCode, @RequiredArgsConstructor 를 모두 합쳐놓은 종합선물세트인 것이다.
@Getter
@Setter
public class UserInfoResponse {
    private String userId;
    private String username;
    private String password;

    private String email;
    private String phoneNumber;
    private String address;
    private String addressDetail;
    private String addressReference;
    private Gender gender;
    private RoleEnum role;
    private LocalDateTime birthDay;
    private long coin;
    private long point;
    private LocalDateTime registerAt;
    private LocalDateTime lastChangeAt;
    private boolean status; // 계정 활성화 여부
    private boolean dormancy;// 휴면계정 여부

    public void decryptionFields(Function<String, String> aes256Decrypt) {
        this.email = aes256Decrypt.apply(this.email);
        this.phoneNumber = aes256Decrypt.apply(this.phoneNumber);
    }
    public boolean isPasswordChange(){
        return lastChangeAt.isBefore(LocalDateTime.now().minusMonths(3));
    }
}
