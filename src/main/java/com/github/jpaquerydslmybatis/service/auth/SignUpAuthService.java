package com.github.jpaquerydslmybatis.service.auth;

import com.github.jpaquerydslmybatis.common.converter.mapper.user.MyUserMapper;
import com.github.jpaquerydslmybatis.common.exception.CustomBadCredentialsException;
import com.github.jpaquerydslmybatis.common.exception.CustomNotFoundException;
import com.github.jpaquerydslmybatis.repository.db1.jpa.user.LoginHistory;
import com.github.jpaquerydslmybatis.repository.db1.jpa.user.LoginHistoryRepository;
import com.github.jpaquerydslmybatis.repository.db1.jpa.user.MyUser;
import com.github.jpaquerydslmybatis.repository.db1.jpa.user.MyUserRepository;
import com.github.jpaquerydslmybatis.web.dto.auth.LoginRequest;
import com.github.jpaquerydslmybatis.web.dto.auth.UserInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class SignUpAuthService {
    private final MyUserRepository myUserRepository;
    private final LoginHistoryRepository loginHistoryRepository;

    private final @Qualifier("sha256Encrypt") Function<String, String> sha256Encrypt;
    private final @Qualifier("aes256Encrypt") Function<String, String> aes256Encrypt;
    private final @Qualifier("aes256Decrypt") Function<String, String> aes256Decrypt;


    public List<UserInfoResponse> getAllUserInfo() {
        List<MyUser> users = myUserRepository.findAll();
        return MyUserMapper.INSTANCE.myUserToUserInfoDtoList(users);
    }

    @Transactional("db1TransactionManager")
    public UserInfoResponse loginLogic(LoginRequest loginRequest, String remoteAddr) {
        String encryptedPassword = sha256Encrypt.apply(loginRequest.getPassword());

        MyUser myUser = myUserRepository.findByIdJoinLastLoginHistory(loginRequest.getUserId())
                .orElseThrow(() -> CustomNotFoundException.of()
                        .customMessage("존재하지 않는 아이디입니다.")
                        .request(loginRequest.getUserId())
                        .build());

        if (!myUser.getPassword().equals(encryptedPassword))
            throw CustomBadCredentialsException.of()
                    .customMessage("비밀번호 오류 다시 입력해주세요.")
                    .request(loginRequest.getPassword())
                    .build();
        loginValidation(myUser);

        loginHistoryRepository.save(LoginHistory.fromUserAndIp(myUser, remoteAddr));
        UserInfoResponse userInfoResponse = MyUserMapper.INSTANCE.myUserToUserInfoDto(myUser);
        userInfoResponse.decryptionFields(aes256Decrypt);
        return userInfoResponse;
    }

    private void loginValidation(MyUser myUser) {
        if (myUser.isDormancy())
            throw CustomBadCredentialsException.of()
                    .customMessage("휴면 계정 입니다. 관리자에게 문의하세요.")
                    .build();
        if (myUser.isStatus())
            throw CustomBadCredentialsException.of()
                    .customMessage("정지된 계정입니다. 관리자에게 문의하세요.")
                    .build();
        // 비밀변호를 변경한지 3개월이 지났는지 확인
//        if (myUser.getLastChangeAt().isBefore(LocalDateTime.now().minusMonths(3)))
//            throw CustomNotAcceptException.of()
//                    .customMessage("비밀번호 변경한지 3개월이 지났습니다. 비밀번호를 변경해주세요.")
//                    .build();

    }
}
