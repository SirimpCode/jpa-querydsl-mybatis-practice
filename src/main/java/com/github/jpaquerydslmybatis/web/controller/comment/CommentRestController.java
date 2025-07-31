package com.github.jpaquerydslmybatis.web.controller.comment;

import com.github.jpaquerydslmybatis.common.exception.CustomBadCredentialsException;
import com.github.jpaquerydslmybatis.service.comment.CommentService;
import com.github.jpaquerydslmybatis.web.dto.auth.UserInfoResponse;
import com.github.jpaquerydslmybatis.web.dto.comment.CommentParent;
import com.github.jpaquerydslmybatis.web.dto.comment.CommentRequest;
import com.github.jpaquerydslmybatis.web.dto.comment.CommentTreeStructureResponse;
import com.github.jpaquerydslmybatis.web.dto.response.CustomResponse;
import com.github.jpaquerydslmybatis.web.dto.response.PaginationResponse;
import com.github.jpaquerydslmybatis.web.dto.storage.FileInfoUpdateRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/comment")
public class CommentRestController {
    private final CommentService commentService;
    //@RequestBody Map<Object, Object> test 이렇게 받아와서 키값 확인가능함
    @PostMapping
    public CustomResponse<CommentParent> registrationComment(@RequestBody CommentRequest commentRequest,
                                                             HttpSession httpSession) {
        // 세션에서 로그인한 사용자 정보 가져오기
        UserInfoResponse loginUser = (UserInfoResponse) httpSession.getAttribute("loginuser");
        if (loginUser == null)
            throw CustomBadCredentialsException.of().customMessage("로그인이 필요합니다.").build();
        commentRequest.setUserId(loginUser.getUserId());
        CommentParent response = commentService.registrationCommentLogic(commentRequest);
        return CustomResponse.ofOk("댓글 등록 성공", response);
    }
    @GetMapping
    public CustomResponse<PaginationResponse<CommentParent>> commentListByBoardId(
            @RequestParam Long boardId,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size
    ) {
        PaginationResponse<CommentParent> response = commentService.getCommentListByBoardId(boardId, page - 1, size);

        return CustomResponse.ofOk("댓글목록조회성공", response);
    }

    @GetMapping("/test-batis/{boardId}")
    public Object testBatisMethod(@PathVariable Long boardId) {
        // 테스트용 메소드
        return commentService.getMyTestLogic(boardId);
    }
    @DeleteMapping("/{commentId}")
    public CustomResponse<Void> deleteComment(@PathVariable Long commentId,
                                              HttpSession httpSession) {
        // 세션에서 로그인한 사용자 정보 가져오기
        UserInfoResponse loginUser = (UserInfoResponse) httpSession.getAttribute("loginuser");
        if (loginUser == null) {
            throw  CustomBadCredentialsException.of().customMessage("로그인이 필요합니다.").build();
        }

        commentService.deleteCommentLogic(commentId, loginUser.getUserId());
        return CustomResponse.emptyDataOk("댓글 삭제 성공");
    }
    @PutMapping("/{commentId}")
    public CustomResponse<Void> modifyComment(@PathVariable Long commentId,
                                              @RequestBody CommentRequest commentRequest,
                                              HttpSession httpSession) {
        // 세션에서 로그인한 사용자 정보 가져오기
        UserInfoResponse loginUser = (UserInfoResponse) httpSession.getAttribute("loginuser");
        if (loginUser == null) {
            throw CustomBadCredentialsException.of().customMessage("로그인이 필요합니다.").build();
        }

        commentService.modifyCommentLogic(commentId, commentRequest, loginUser.getUserId());
        return CustomResponse.emptyDataOk("댓글 수정 성공");
    }
    @GetMapping("/tree-test/{boardId}")
    public CustomResponse<List<CommentTreeStructureResponse>> commentTreeStructureTest(@PathVariable Long boardId) {
        // 댓글 트리 구조 테스트용 메소드

        List<CommentTreeStructureResponse> response = commentService.getCommentTreeStructure(boardId);
        return CustomResponse.ofOk("댓글 트리 구조 조회 성공", response);
    }
    @PutMapping("/file")
    public CustomResponse<Void> uploadFile(@RequestBody FileInfoUpdateRequest request) {
        commentService.commentFileInfoUpdateByCommentId(request);

     return CustomResponse.emptyDataOk("댓글에 파일정보 업데이트 성공");
    }
}
