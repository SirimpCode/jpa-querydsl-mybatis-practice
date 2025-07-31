package com.github.jpaquerydslmybatis.repository.db1.jpa.board;

import com.github.jpaquerydslmybatis.repository.db1.jpa.comment.QMyComment;
import com.github.jpaquerydslmybatis.repository.db1.jpa.user.QMyUser;
import com.github.jpaquerydslmybatis.web.dto.board.*;
import com.github.jpaquerydslmybatis.web.dto.response.PaginationResponse;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.github.jpaquerydslmybatis.repository.db1.jpa.board.QBoard.board;

@RequiredArgsConstructor
public class BoardRepositoryCustomImpl implements BoardRepositoryCustom {
    private final @Qualifier("db1QueryFactory") JPAQueryFactory queryFactory;


    private BooleanExpression getPrevNextCondition(
            boolean isAsc,
            LocalDateTime currentCreatedAt,
            Long currentBoardId,
            BooleanExpression searchCondition,
            boolean isPrev // true: 이전글, false: 다음글
    ) {
        BooleanExpression baseCondition;
        if (isPrev) {
            baseCondition = isAsc
                    ? board.createdAt.lt(currentCreatedAt)
                    .or(board.createdAt.eq(currentCreatedAt).and(board.boardId.lt(currentBoardId)))
                    : board.createdAt.gt(currentCreatedAt)
                    .or(board.createdAt.eq(currentCreatedAt).and(board.boardId.gt(currentBoardId)));
        } else {
            baseCondition = isAsc
                    ? board.createdAt.gt(currentCreatedAt)
                    .or(board.createdAt.eq(currentCreatedAt).and(board.boardId.gt(currentBoardId)))
                    : board.createdAt.lt(currentCreatedAt)
                    .or(board.createdAt.eq(currentCreatedAt).and(board.boardId.lt(currentBoardId)));
        }
        return searchCondition != null ? searchCondition.and(baseCondition) : baseCondition;
    }

    // 기존의 정렬을 반대로 바꾸는 메소드
    private OrderSpecifier<?>[] reverseOrderSpecifiers(OrderSpecifier<?>[] specs) {
        return Arrays.stream(specs)
                .map(spec -> new OrderSpecifier<>(
                        spec.getOrder() == Order.ASC ? Order.DESC : Order.ASC,
                        spec.getTarget()
                ))
                .toArray(OrderSpecifier[]::new);
    }


    @Override
    public BoardPrevNextValue findPrevNextById(Board currentBoard, BoardSearchRequest searchRequest) {
        //사전에 사용된 검색 조건
        BooleanExpression priorSearchCondition = createWhereCondition(searchRequest);
        //이전 게시글 을 찾기 위한 조건
        BooleanExpression prevCondition = createPrevNextCondition(searchRequest, currentBoard, true);
        //다음 게시글 을 찾기 위한 조건
        BooleanExpression nextCondition = createPrevNextCondition(searchRequest, currentBoard, false);
        //사전에 사용된 정렬 조건
        OrderSpecifier<?>[] priorOrderSpecifier = createOrderByCondition(searchRequest);
        // 정렬조건에 오름차인지 내림차인지 확인할수있다. 쓸일없어서 주석처리
        //boolean isAsc = priorOrderSpecifier[0].isAscending();


        // 이전 게시글 정보
        BoardSimpleValue prevValue = queryFactory
                .select(
                        Projections.fields(
                                BoardSimpleValue.class,
                                board.boardId,
                                board.title
                        ))
                .from(board)
                .where(
                        priorSearchCondition,
                        prevCondition
                )
                .orderBy(reverseOrderSpecifiers(priorOrderSpecifier))
                .limit(1)
                .fetchOne();
        //이후 게시글 정보
        BoardSimpleValue nextValue = queryFactory
                .select(
                        Projections.fields(
                                BoardSimpleValue.class,
                                board.boardId,
                                board.title
                        ))
                .from(board)
                .where(
                        priorSearchCondition,
                        nextCondition
                )
                .orderBy(priorOrderSpecifier)
                .limit(1)
                .fetchOne();


        return BoardPrevNextValue.fromPrevNext(prevValue, nextValue);
    }

    private BooleanExpression createPrevNextCondition(BoardSearchRequest searchRequest, Board currentBoard, boolean isPrev) {
        return switch (searchRequest.getSearchSort()) {
            case CREATED_AT_ASC -> isPrev
                    ? board.createdAt.lt(currentBoard.getCreatedAt())
                    .or(board.createdAt.eq(currentBoard.getCreatedAt()).and(board.boardId.lt(currentBoard.getBoardId())))
                    : board.createdAt.gt(currentBoard.getCreatedAt())
                    .or(board.createdAt.eq(currentBoard.getCreatedAt()).and(board.boardId.gt(currentBoard.getBoardId())));
            case CREATED_AT_DESC -> isPrev
                    ? board.createdAt.gt(currentBoard.getCreatedAt())
                    .or(board.createdAt.eq(currentBoard.getCreatedAt()).and(board.boardId.gt(currentBoard.getBoardId())))
                    : board.createdAt.lt(currentBoard.getCreatedAt())
                    .or(board.createdAt.eq(currentBoard.getCreatedAt()).and(board.boardId.lt(currentBoard.getBoardId())));
            case TITLE_ASC -> isPrev
                    ? board.title.lt(currentBoard.getTitle())
                    .or(board.title.eq(currentBoard.getTitle()).and(board.boardId.lt(currentBoard.getBoardId())))
                    : board.title.gt(currentBoard.getTitle())
                    .or(board.title.eq(currentBoard.getTitle()).and(board.boardId.gt(currentBoard.getBoardId())));
            case TITLE_DESC -> isPrev
                    ? board.title.gt(currentBoard.getTitle())
                    .or(board.title.eq(currentBoard.getTitle()).and(board.boardId.gt(currentBoard.getBoardId())))
                    : board.title.lt(currentBoard.getTitle())
                    .or(board.title.eq(currentBoard.getTitle()).and(board.boardId.lt(currentBoard.getBoardId())));
        };
    }

    @Override
    public PaginationResponse<BoardListResponse> findBoardListByCondition(BoardSearchRequest searchRequest, long page, long size) {
        BooleanExpression searchCondition = createWhereCondition(searchRequest);
        OrderSpecifier<?>[] orderSpecifier = createOrderByCondition(searchRequest);

        List<Long> parentBoardIds = queryFactory
                .select(board.boardId)
                .from(board)
                .where(board.parentBoard.isNull(), searchCondition)
                .orderBy(orderSpecifier)
                .offset(page * size)
                .limit(size)
                .fetch();//일반 게시물들의 아이디들
        if (parentBoardIds.isEmpty())
            return PaginationResponse.of(0L, Collections.emptyList(), page, size);

        Long totalParentCount = queryFactory.select(board.count())
                .from(board)
                .where(board.parentBoard.isNull(), searchCondition)
                .fetchOne();
        long safeTotalParentCount = totalParentCount != null ? totalParentCount : 0L;

        QBoard parentBoard = new QBoard("parentBoard");
        // 부모 게시글과 자식 게시글을 모두 가져오는 쿼리 rootBoard 컬럼을 이용하여 해당게시물의 모든 연관 게시글을 가져온다.
        List<BoardListResponse> response = queryFactory.select(
                        Projections.fields(BoardListResponse.class,
                                board.boardId,
                                board.title,
                                board.myUser.username.as("writer"),
                                board.parentBoard.boardId.as("parentBoardId"),
                                board.createdAt,
                                board.viewCount,
                                board.content,
                                // 댓글 수 서브쿼리
                                Expressions.as(
                                        JPAExpressions.select(QMyComment.myComment.count())
                                                .from(QMyComment.myComment)
                                                .where(QMyComment.myComment.board.boardId.eq(board.boardId)),
                                        "commentCount"
                                ),
                                // 파일 첨부 여부
                                board.fileName.isNotNull().and(board.fileSize.gt(0)).as("isFileAttached")
                        )
                )
                .from(board)
                .where(board.boardId.in(parentBoardIds).or(board.rootBoard.boardId.in(parentBoardIds)))
                .leftJoin(board.parentBoard, parentBoard)
                .orderBy(orderSpecifier)
                .fetch();


//        JPAQuery<BoardListResponse> query = queryFactory.select(
//                        Projections.fields(
//                                BoardListResponse.class,
//                                board.boardId,
//                                board.title,
//                                board.myUser.username.as("writer"),
//                                board.createdAt,
//                                board.viewCount,
//                                board.content,
//                                // 댓글 수 서브쿼리
//                                Expressions.as(
//                                        JPAExpressions.select(QMyComment.myComment.count())
//                                                .from(QMyComment.myComment)
//                                                .where(QMyComment.myComment.board.boardId.eq(board.boardId)),
//                                        "commentCount"
//                                )
//
//                        )
//                ).from(board)
//                .where(searchCondition)
//                .join(board.myUser, QMyUser.myUser)
//                .orderBy(orderSpecifier);
//
//        // 전체 개수 쿼리 (count)
//        Long totalCount = queryFactory
//                .select(board.count())
//                .from(board)
//                .where(searchCondition)
//                .fetchOne();
//        long safeTotalCount = totalCount != null ? totalCount : 0L;
//
//        //페이징 처리
//        if (size > 0 && page > -1)
//            query.offset(page * size).limit(size);
//
//
//        List<BoardListResponse> boardListResponses = query.fetch();


        return PaginationResponse.of(safeTotalParentCount, response, page, size);


    }

    private OrderSpecifier<?>[] createOrderByCondition(BoardSearchRequest searchRequest) {
        return switch (searchRequest.getSearchSort()) {
            case CREATED_AT_ASC -> new OrderSpecifier<?>[]{board.createdAt.asc(), board.boardId.asc()};
            case CREATED_AT_DESC -> new OrderSpecifier<?>[]{board.createdAt.desc(), board.boardId.desc()};
            case TITLE_ASC -> new OrderSpecifier<?>[]{board.title.asc(), board.boardId.asc()};
            case TITLE_DESC -> new OrderSpecifier<?>[]{board.title.desc(), board.boardId.desc()};
        };
    }

    private BooleanExpression createWhereCondition(BoardSearchRequest searchRequest) {
        if (searchRequest.getSearchValue() == null) return null;

        return switch (searchRequest.getSearchType()) {
            case TITLE -> board.title.containsIgnoreCase(searchRequest.getSearchValue());
            case CONTENT -> board.content.containsIgnoreCase(searchRequest.getSearchValue());
            case WRITER -> board.myUser.username.containsIgnoreCase(searchRequest.getSearchValue());
            case ALL -> board.title.containsIgnoreCase(searchRequest.getSearchValue())
                    .or(board.content.containsIgnoreCase(searchRequest.getSearchValue()));
        };
    }
}
