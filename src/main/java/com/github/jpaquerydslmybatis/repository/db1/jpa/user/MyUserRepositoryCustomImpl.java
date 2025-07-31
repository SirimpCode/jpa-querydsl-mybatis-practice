package com.github.jpaquerydslmybatis.repository.db1.jpa.user;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Optional;

@RequiredArgsConstructor
public class MyUserRepositoryCustomImpl implements MyUserRepositoryCustom {

    private final @Qualifier("db1QueryFactory") JPAQueryFactory queryFactory;


    @Override
    public Optional<MyUser> findByIdJoinLastLoginHistory(String userId) {
        //마지막 로그인 기록만 조인해서 가져오기
        MyUser user = queryFactory
                .selectFrom(QMyUser.myUser)
                .leftJoin(QMyUser.myUser.loginHistories, QLoginHistory.loginHistory)
                .fetchJoin()
                .where(
                        QMyUser.myUser.userId.eq(userId)
                                .and(
                                        QLoginHistory.loginHistory.loginDate.eq(
                                                        JPAExpressions
                                                                .select(QLoginHistory.loginHistory.loginDate.max())
                                                                .from(QLoginHistory.loginHistory)
                                                                .where(QLoginHistory.loginHistory.myUser.eq(QMyUser.myUser))
                                                )
                                                .or(QLoginHistory.loginHistory.loginDate.isNull()) // 로그인 이력이 없는 경우도 포함
                                )
                )
                .fetchOne();

        return Optional.ofNullable(user);
    }

    @Override
    public long updateCommentPoint(String userId) {

        return queryFactory.update(QMyUser.myUser)
                .set(QMyUser.myUser.point, QMyUser.myUser.point.add(50))
                .where(QMyUser.myUser.userId.eq(userId))
                .execute();
    }

    @Override
    public long updatePointPlus(String userId, Long plusPoint) {

        return queryFactory.update(QMyUser.myUser)
                .set(QMyUser.myUser.point, QMyUser.myUser.point.add(plusPoint))
                .where(QMyUser.myUser.userId.eq(userId))
                .execute();
    }
}
