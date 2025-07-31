package com.github.jpaquerydslmybatis.repository.db1.jpa.comment;

import com.github.jpaquerydslmybatis.repository.db1.jpa.user.QMyUser;
import com.github.jpaquerydslmybatis.web.dto.comment.CommentChildren;
import com.github.jpaquerydslmybatis.web.dto.comment.CommentParent;
import com.github.jpaquerydslmybatis.web.dto.comment.CommentTreeStructureResponse;
import com.github.jpaquerydslmybatis.web.dto.response.PaginationResponse;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Collections;
import java.util.List;

import static com.github.jpaquerydslmybatis.repository.db1.jpa.comment.QMyComment.myComment;
import static com.querydsl.core.group.GroupBy.groupBy;

@RequiredArgsConstructor
public class CommentRepositoryCustomImpl implements CommentRepositoryCustom {

    private @Qualifier("db1QueryFactory")
    final JPAQueryFactory queryFactory;

    @Override
    public PaginationResponse<CommentParent> findCommentListByBoardId(Long boardId, long page, long size) {
        Long totalElements = queryFactory
                .select(myComment.count())
                .from(myComment)
                .where(myComment.board.boardId.eq(boardId)
                        .and(myComment.parentMyComment.isNull())) // 부모 댓글만 세야 정확해!
                .fetchOne();

        QMyComment parentComment = new QMyComment("parentComment");
        QMyComment childComment = new QMyComment("childComment");
        QMyUser parentUser = new QMyUser("parentUser");
        QMyUser childUser = new QMyUser("childUser");

//
//        List<CommentDetail> commentList = queryFactory
//                .select(Projections.fields(CommentDetail.class,
//                        parentComment.commentId,
//                        parentComment.content,
//                        parentComment.createdAt,
//                        parentComment.status,
//                        parentComment.myUser.userId,
//                        parentComment.myUser.username
//                        ))
//                .from(parentComment)
//                .where(parentComment.board.boardId.eq(boardId)
//                        .and(parentComment.parentMyComment.isNull()))
//                .join(parentComment.myUser, QMyUser.myUser)
//                .orderBy(parentComment.createdAt.desc())
//                .offset(page * size)
//                .limit(size)
//                .fetch();

        // 1단계: 부모 댓글만 먼저 조회
        List<Long> parentIds = queryFactory
                .select(parentComment.commentId)
                .from(parentComment)
                .where(parentComment.board.boardId.eq(boardId)
                        .and(parentComment.parentMyComment.isNull()))
                .orderBy(parentComment.createdAt.desc())
                .offset(page * size)
                .limit(size)
                .fetch();

        if (parentIds.isEmpty()) {
            return PaginationResponse.of(0L, Collections.emptyList(), page, size);
        }
        // 2단계: 위에서 구한 parentIds를 기준으로 부모+자식 join fetch
        List<CommentParent> result = queryFactory
                .from(parentComment)
                .leftJoin(parentComment.childrenComments, childComment)
                .join(parentComment.myUser, parentUser)
                .leftJoin(childComment.myUser, childUser)
                .where(parentComment.commentId.in(parentIds))
                .orderBy(parentComment.createdAt.desc(), childComment.createdAt.asc())
                .transform(
                        groupBy(parentComment.commentId).list(
                                Projections.fields(CommentParent.class,
                                        parentComment.commentId,
                                        parentComment.content,
                                        parentComment.status,
                                        parentComment.createdAt,
                                        parentComment.fileName,
                                        parentComment.filePath,
                                        parentComment.fileSize,
                                        parentUser.userId.as("userId"),
                                        parentUser.username.as("username"),
                                        GroupBy.list(
                                                Projections.fields(CommentChildren.class,
                                                        childComment.commentId,
                                                        childComment.parentMyComment.commentId.as("parentCommentId"),
                                                        childComment.content,
                                                        childComment.status,
                                                        childComment.createdAt,
                                                        childComment.fileName,
                                                        childComment.filePath,
                                                        childComment.fileSize,
                                                        childUser.userId.as("userIdChild"),
                                                        childUser.username.as("usernameChild")
                                                )
                                        ).as("childrenComments")
                                )
                        )
                );


        totalElements = totalElements != null ? totalElements : 0L;

        //null 처리
        result.forEach(parent -> {
            //자식댓글중 id가 null 인경우 제외 시키기
            parent.getChildrenComments().removeIf(child -> child.getCommentId() == null);
        });

        return PaginationResponse.of(totalElements, result, page, size);
    }

    @Override
    public List<CommentTreeStructureResponse> findCommentListTreeByBoardId(Long boardId) {
        QMyComment parentComment = new QMyComment("parentComment");
        return queryFactory
                .select(
                        Projections.fields(CommentTreeStructureResponse.class,
                                myComment.commentId,
                                myComment.content,
                                myComment.status,
                                myComment.createdAt,
                                parentComment.commentId.as("parentCommentId"),
                                myComment.myUser.userId.as("userId"),
                                myComment.myUser.username.as("username")
                        )
                )
                .from(myComment)
                .leftJoin(myComment.parentMyComment, parentComment)
                .where(myComment.board.boardId.eq(boardId))
                .orderBy(myComment.createdAt.desc())
                .fetch();
    }

}
