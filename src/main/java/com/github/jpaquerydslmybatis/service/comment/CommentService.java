package com.github.jpaquerydslmybatis.service.comment;

import com.github.jpaquerydslmybatis.common.converter.mapper.comment.CommentMapper;
import com.github.jpaquerydslmybatis.common.exception.CustomBindException;
import com.github.jpaquerydslmybatis.repository.db1.jpa.comment.MyComment;
import com.github.jpaquerydslmybatis.repository.db1.jpa.comment.CommentRepository;
import com.github.jpaquerydslmybatis.repository.db1.jpa.user.MyUser;
import com.github.jpaquerydslmybatis.repository.db1.jpa.user.MyUserRepository;
import com.github.jpaquerydslmybatis.web.dto.comment.CommentParent;
import com.github.jpaquerydslmybatis.web.dto.comment.CommentRequest;
import com.github.jpaquerydslmybatis.web.dto.comment.CommentTreeStructureResponse;
import com.github.jpaquerydslmybatis.web.dto.response.PaginationResponse;
import com.github.jpaquerydslmybatis.web.dto.storage.FileInfoUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final MyUserRepository myUserRepository;

    @Transactional("db1TransactionManager")
    public CommentParent registrationCommentLogic(CommentRequest commentRequest) {
        MyComment myCommentEntity = MyComment.requestToEntity(commentRequest);
        //세이브할때 primary key 만 엔티티에 추가가 되고 나머지 값들은 내가 select 로 다시 가져와야된다.
        commentRepository.save(myCommentEntity);
        long result = myUserRepository.updateCommentPoint(commentRequest.getUserId());
        if (result != 1) throw CustomBindException.of().request(commentRequest).customMessage("포인트 업데이트 실패").build();
        return CommentMapper.INSTANCE.commentToCommentDetail(myCommentEntity);
    }

    @Transactional(value = "db1TransactionManager", readOnly = true)
    public PaginationResponse<CommentParent> getCommentListByBoardId(Long boardId, long page, long size) {
        PaginationResponse<CommentParent> response = commentRepository.findCommentListByBoardId(boardId, page, size);

        return response;
    }

    @Transactional(value = "db1TransactionManager", readOnly = true)
    public Object getMyTestLogic(Long boardId) {

        return null;

    }

    @Transactional("db1TransactionManager")
    public void deleteCommentLogic(Long commentId, String loginUserId) {
        long result = commentRepository.deleteMyCommentByCommentIdAndMyUser(commentId, MyUser.onlyId(loginUserId));
        if (result != 1)
            throw CustomBindException.of()
                    .customMessage("댓글 삭제 실패 - 댓글이 존재하지 않거나, 작성자가 아닙니다.")
                    .request(commentId + " " + loginUserId)
                    .build();

    }

    @Transactional("db1TransactionManager")
    public void modifyCommentLogic(Long commentId, CommentRequest commentRequest, String userId) {
        MyComment myComment = commentRepository.findMyCommentByCommentIdAndMyUser(commentId, MyUser.onlyId(userId))
                .orElseThrow(() -> CustomBindException.of()
                        .customMessage("댓글이 존재하지 않거나, 작성자가 아닙니다.")
                        .request(commentId + " " + userId)
                        .build());
        myComment.modifyContent(commentRequest.getContent());
    }

    /**
     * 댓글 트리 구조 조회
     * @param boardId
     * @return 트리 구조로 정렬된 댓글 목록
     */
    @Transactional(value = "db1TransactionManager", readOnly = true)
    public List<CommentTreeStructureResponse> getCommentTreeStructure(Long boardId) {
        List<CommentTreeStructureResponse> commentResponses =
                commentRepository.findCommentListTreeByBoardId(boardId);
        List<CommentTreeStructureResponse> refinedResponses = groupingCommentTree(commentResponses);

        return refinedResponses;
    }

    private List<CommentTreeStructureResponse> groupingCommentTree(List<CommentTreeStructureResponse> commentResponses) {
        //부모 댓글을 제외한 자식 댓글들을 추출
        List<CommentTreeStructureResponse> roots = commentResponses.stream()
                .filter(comment -> comment.getParentCommentId() == null)
                .toList();
        //부모 댓글을 제외한 자식 댓글들만 추출
        List<CommentTreeStructureResponse> children = commentResponses.stream()
                .filter(comment -> comment.getParentCommentId() != null)
                .toList();
        //아이디를 기준으로 트리 구조로 변경하기 위해 Map 으로 변환
        Map<Long, CommentTreeStructureResponse> map = commentResponses.stream()
                .collect(Collectors.toMap(CommentTreeStructureResponse::getCommentId, c -> c));
        //부모 댓글에 자식 댓글을 추가 부모가 있는 children 댓글들을 순회하면서
        for (CommentTreeStructureResponse comment : children) {
            Long parentId = comment.getParentCommentId();
            CommentTreeStructureResponse parent = map.get(parentId);
            parent.addChild(comment);
        }

        return roots;

    }

    @Transactional("db1TransactionManager")
    public void commentFileInfoUpdateByCommentId(FileInfoUpdateRequest request) {
        MyComment myComment = commentRepository.findById(request.getPrimaryKey())
                .orElseThrow(() -> CustomBindException.of()
                        .customMessage("댓글이 존재하지 않습니다.")
                        .request(request.getPrimaryKey())
                        .build());
        myComment.fileInfoUpdate(request);
    }
}
