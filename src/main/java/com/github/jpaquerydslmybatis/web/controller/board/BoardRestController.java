package com.github.jpaquerydslmybatis.web.controller.board;

import com.github.jpaquerydslmybatis.common.exception.CustomAccessDenied;
import com.github.jpaquerydslmybatis.service.board.BoardService;
import com.github.jpaquerydslmybatis.service.storage.StorageService;
import com.github.jpaquerydslmybatis.web.dto.auth.UserInfoResponse;
import com.github.jpaquerydslmybatis.web.dto.board.BoardListResponse;
import com.github.jpaquerydslmybatis.web.dto.board.BoardSearchRequest;
import com.github.jpaquerydslmybatis.web.dto.board.BoardWriteRequest;
import com.github.jpaquerydslmybatis.web.dto.response.CustomResponse;
import com.github.jpaquerydslmybatis.web.dto.response.PaginationResponse;
import com.github.jpaquerydslmybatis.web.dto.storage.FileInfoUpdateRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Delete;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
public class BoardRestController {
    private final BoardService boardService;
    private final StorageService storageService;

    @PostMapping//AfterReturning 을 적용해서 포인트를 올려줄거야
    public CustomResponse<Long> writeBoardRequiredLoginPointPlus(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestBody BoardWriteRequest writeRequest) {

        UserInfoResponse loginUser = (UserInfoResponse) request.getSession().getAttribute("loginuser");
        request.setAttribute("userId", loginUser.getUserId());
        request.setAttribute("plusPoint", 100L);
        long newBoardId = boardService.createBoard(writeRequest, loginUser.getUserId());
        return CustomResponse.ofOk("게시글이 작성되었습니다.", newBoardId);

    }
    @PutMapping
    public CustomResponse<Long> modifyBoardRequiredLogin(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestBody BoardWriteRequest writeRequest
    ){
        UserInfoResponse loginUser = (UserInfoResponse) request.getSession().getAttribute("loginuser");
        if (loginUser == null) {
            throw CustomAccessDenied.of().customMessage("로그인이 필요합니다.").build();
        }
        boardService.modifyBoardLogic(writeRequest, loginUser.getUserId());

        return CustomResponse.ofOk("게시글이 작성되었습니다.", writeRequest.getBoardId());
    }
    @GetMapping("/test/batis/{boardId}")
    public Object testBatisMethod(Long boardId) {
        // 테스트용 메소드
        return boardService.getBoardByIdMybatis(boardId);
    }


    @GetMapping("/test/{boardId}")
    public Object testMethod(Long boardId) {
        BoardSearchRequest temp = BoardSearchRequest.of(null, null, null);

        // 테스트용 메소드
        return boardService.getBoardById(boardId, null, false, temp);
    }
    @GetMapping("/list")
    public CustomResponse<PaginationResponse<BoardListResponse>> lookupBoardList(
            @RequestParam(required = false) String searchValue,
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String searchSort,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        BoardSearchRequest searchRequest = BoardSearchRequest.of(searchValue,searchType, searchSort);
        PaginationResponse<BoardListResponse> responseList = boardService.getBoardList(searchRequest, page-1, size);
        return CustomResponse.ofOk("게시글 목록 조회 성공", responseList);
    }
    @GetMapping("/search-helper")
    public CustomResponse<Set<String>> searchTermAutomaticRelocation(
            @RequestParam(required = false, value = "searchWord") String searchValue,
            @RequestParam(required = false) String searchType
    ){
        BoardSearchRequest searchRequest = BoardSearchRequest.of(searchValue,searchType, null);
        PaginationResponse<BoardListResponse> responseList = boardService.getBoardList(searchRequest, -1, 0);
        Set<String> searchTerms = boardService.searchTermsToSearchTermList(responseList.getElements(), searchRequest);
        return CustomResponse.ofOk("게시글 목록 조회 성공", searchTerms);
    }
    @DeleteMapping("/{boardId}")
    public CustomResponse<Void> deleteMyPost(@PathVariable Long boardId, @RequestBody String postPassword) {
        List<String> pavilionPathToDelete = boardService.deleteMyPost(boardId, postPassword);
        if(!pavilionPathToDelete.isEmpty())
            storageService.deleteFile(pavilionPathToDelete);
        return CustomResponse.emptyDataOk("게시글이 삭제되었습니다.");
    }

    @GetMapping("/verify/{boardId}")
    public CustomResponse<Boolean> verifyMyPost(
            @PathVariable Long boardId,
            @RequestParam String postPassword,
            HttpSession session,
            Model model
    ){
        Object modelObj = model.getAttribute("board");
        Object loginUserObj = session.getAttribute("loginuser");
        if(loginUserObj == null)
            throw CustomAccessDenied.of().customMessage("접근이 거부되었습니다.").build();
        String loginUserId = ((UserInfoResponse) loginUserObj).getUserId();
        return CustomResponse.ofOk("게시글 비밀번호 확인", boardService.verifyMyPost(boardId, postPassword, loginUserId));
    }

    @PutMapping("/file")
    public CustomResponse<Void> modifyBoardFile(
            @RequestBody FileInfoUpdateRequest request){
        boardService.updateBoardFileInfo(request);
        return CustomResponse.emptyDataOk("파일 정보가 업데이트 되었습니다.");
    }


      // 아래의 메소드는 BoardService 에서 처리하도록 변경하였습니다.
//    private Set<String> searchTermsToSearchTermList(List<BoardListResponse> searchResponses, BoardSearchRequest searchRequest) {
//        return searchResponses.stream()
//                .flatMap(response -> searchResponseToSearchTerms(response, searchRequest).stream())
//                .collect(Collectors.toUnmodifiableSet());
//    }
//
//    private List<String> searchResponseToSearchTerms(BoardListResponse response, BoardSearchRequest searchRequest) {
//        String searchValue = searchRequest.getSearchValue();
//        return switch (searchRequest.getSearchType()) {
//            case TITLE -> List.of(
//                    extractSnippet(response.getTitle(), searchValue, 20)
//            );
//            case CONTENT -> List.of(
//                    extractSnippet(response.getContent().replaceAll("<[^>]*>", ""), searchValue, 20)
//            );
//            case WRITER -> List.of(response.getWriter());
//            case ALL -> Stream.of(response.getTitle(), response.getContent().replaceAll("<[^>]*>", "")) // HTML 태그 제거
//                    .filter(field -> searchValue != null && field != null && field.contains(searchValue))
//                    .map(field -> extractSnippet(field, searchValue, 20))
//                    .toList();
//        };
//
//
//    }
//    // 문자열 중간 에 키워드가 포함된 부분을 추출하여 길이를 맞추고, 앞 또는 뒤에 '...'을 추가
//    private String extractSnippet(String text, String keyword, int length) {
//        if (text == null || keyword == null || !text.contains(keyword)) return text;
//        int idx = text.indexOf(keyword);
//        int start = Math.max(0, idx - (length - keyword.length()) / 2);
//        int end = Math.min(text.length(), start + length);
//        if (end - start < length && start > 0) {
//            start = Math.max(0, end - length);
//        }
//        String snippet = text.substring(start, end);
//        if (start > 0) snippet = "..." + snippet;
//        if (end < text.length()) snippet = snippet + "...";
//        return snippet;
//    }


}
