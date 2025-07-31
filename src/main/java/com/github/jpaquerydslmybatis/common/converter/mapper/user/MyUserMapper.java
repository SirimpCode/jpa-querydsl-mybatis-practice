package com.github.jpaquerydslmybatis.common.converter.mapper.user;

import com.github.jpaquerydslmybatis.repository.db1.jpa.user.MyUser;
import com.github.jpaquerydslmybatis.web.dto.auth.UserInfoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface MyUserMapper {
    MyUserMapper INSTANCE = Mappers.getMapper(MyUserMapper.class);
    @Mapping(source = "role", target = "role")
    UserInfoResponse myUserToUserInfoDto(MyUser myUser);
    List<UserInfoResponse> myUserToUserInfoDtoList(List<MyUser> myUsers);
}
