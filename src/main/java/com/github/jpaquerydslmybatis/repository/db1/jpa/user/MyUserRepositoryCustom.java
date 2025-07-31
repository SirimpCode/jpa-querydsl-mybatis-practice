package com.github.jpaquerydslmybatis.repository.db1.jpa.user;

import java.util.Optional;

public interface MyUserRepositoryCustom {
    Optional<MyUser> findByIdJoinLastLoginHistory(String userId);

    long updateCommentPoint(String userId);

    long updatePointPlus(String userId, Long plusPoint);
}
