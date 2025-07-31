package com.github.jpaquerydslmybatis.service.board;

import com.github.jpaquerydslmybatis.common.converter.mapper.board.BoardMapper;
import com.github.jpaquerydslmybatis.common.exception.CustomBadCredentialsException;
import com.github.jpaquerydslmybatis.common.exception.CustomNotFoundException;
import com.github.jpaquerydslmybatis.common.exception.CustomViewException;
import com.github.jpaquerydslmybatis.repository.db1.jpa.board.Board;
import com.github.jpaquerydslmybatis.repository.db1.jpa.board.BoardRepository;
import com.github.jpaquerydslmybatis.repository.db1.jpa.user.MyUser;
import com.github.jpaquerydslmybatis.repository.db1.jpa.user.MyUserRepository;
import com.github.jpaquerydslmybatis.repository.db1.mybatis.board.BoardDao;
import com.github.jpaquerydslmybatis.web.domain.db1.board.BoardSimpleDomain;
import com.github.jpaquerydslmybatis.web.dto.board.*;
import com.github.jpaquerydslmybatis.web.dto.response.PaginationResponse;
import com.github.jpaquerydslmybatis.web.dto.storage.FileInfoUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final BoardDao boardDao;
    private final MyUserRepository myUserRepository;

    @Transactional("db1TransactionManager")
    public long createBoard(BoardWriteRequest request, String userId) {
        Board newBoard = BoardMapper.INSTANCE.writeRequestToBoard(request);
        MyUser loginUser = MyUser.onlyId(userId);
        newBoard.setMyUser(loginUser);
        boardRepository.save(newBoard);
        return newBoard.getBoardId();
    }

    @Transactional(value = "db1TransactionManager", readOnly = true)
    public List<BoardResponse> getAllList() {
        List<Board> boards = boardRepository.findAllFetchJoinUser();
        List<BoardResponse> boardResponses = BoardMapper.INSTANCE.boardsToResponses(boards);

        return boardResponses;
    }
//    @Transactional(value = "db1TransactionManager", readOnly = true)
//    public List<BoardListResponse> getAllListSeason2(){
//        return boardRepository.findBoardListByCondition(BoardSearchRequest.of(null, null, null, null), page, size);
//    }

    @Transactional(value = "db1TransactionManager")
    public BoardResponse getBoardById(Long boardId, String loginUserId, boolean countIncrement, BoardSearchRequest searchRequest) {
        Board board = boardRepository.findByIdFetchJoinUser(boardId)
                .orElseThrow(() -> new IllegalArgumentException("Board not found with id: " + boardId));

        if(countIncrement & (loginUserId == null || !loginUserId.equals(board.getMyUser().getUserId())))
            board.incrementViewCount();

        BoardResponse boardResponse = BoardMapper.INSTANCE.boardToResponse(board);

        BoardPrevNextValue prevNextValue = boardRepository.findPrevNextById(board, searchRequest);
        boardResponse.setBoardPrevNextValue(prevNextValue);

        return boardResponse;
    }

    @Transactional(value = "db1TransactionManager", readOnly = true)
    public PaginationResponse<BoardListResponse> getBoardList(BoardSearchRequest searchRequest, long page, long size) {
        PaginationResponse<BoardListResponse> boardResponses = boardRepository.findBoardListByCondition(searchRequest, page, size);
        List<BoardListResponse> boardListTreeStructure = convertToTreeStructure(boardResponses.getElements());
        return PaginationResponse.of(boardResponses.getTotalElements(), boardListTreeStructure, page, size);

    }
    public List<BoardListResponse> convertToTreeStructure(List<BoardListResponse> boardList){
        //부모 댓글만 추출
        List<BoardListResponse> parentBoards = boardList.stream()
                .filter(board -> board.getParentBoardId() == null)
                .toList();
        //자식 댓글만 추출
        List<BoardListResponse> childBoards = boardList.stream()
                .filter(board -> board.getParentBoardId() != null)
                .toList();
        //아이디를 기준으로 Map 생성
        Map<Long, BoardListResponse> map = boardList.stream()
                .collect(Collectors.toMap(BoardListResponse::getBoardId, board -> board));
        //부모 댓글에 자식 댓글을 추가 자식잿글들을 순회하며
        childBoards.forEach(childBoard -> {
            Long parentId = childBoard.getParentBoardId();
            BoardListResponse parentBoard = map.get(parentId);
            parentBoard.addChildren(childBoard);
        });
        return parentBoards;
    }



    public Set<String> searchTermsToSearchTermList(List<BoardListResponse> searchResponses, BoardSearchRequest searchRequest) {
        return searchResponses.stream()
                .flatMap(response -> searchResponseToSearchTerms(response, searchRequest).stream())
                .collect(Collectors.toUnmodifiableSet());
    }

    private List<String> searchResponseToSearchTerms(BoardListResponse response, BoardSearchRequest searchRequest) {
        String searchValue = searchRequest.getSearchValue();
        return switch (searchRequest.getSearchType()) {
            case TITLE -> List.of(
                    extractSnippet(response.getTitle(), searchValue, 20)
            );
            case CONTENT -> List.of(
                    extractSnippet(response.getContent().replaceAll("<[^>]*>", ""), searchValue, 20)
            );
            case WRITER -> List.of(response.getWriter());
            case ALL -> Stream.of(response.getTitle(), response.getContent().replaceAll("<[^>]*>", "")) // HTML 태그 제거
                    .filter(field -> searchValue != null && field != null && field.contains(searchValue))
                    .map(field -> extractSnippet(field, searchValue, 20))
                    .toList();
        };


    }
    // 문자열 중간 에 키워드가 포함된 부분을 추출하여 길이를 맞추고, 앞 또는 뒤에 '...'을 추가
    private String extractSnippet(String text, String keyword, int length) {
        if (text == null || keyword == null || !text.contains(keyword)) return text;
        int idx = text.indexOf(keyword);
        int start = Math.max(0, idx - (length - keyword.length()) / 2);
        int end = Math.min(text.length(), start + length);
        if (end - start < length && start > 0) {
            start = Math.max(0, end - length);
        }
        String snippet = text.substring(start, end);
        if (start > 0) snippet = "..." + snippet;
        if (end < text.length()) snippet = snippet + "...";
        return snippet;
    }

    @Transactional(value = "db1TransactionManager", readOnly = true)
    public Object getBoardByIdMybatis(Long boardId) {
        BoardSimpleDomain boardResponse = boardDao.findById(boardId);
        return boardResponse;
    }

    @Transactional(value = "db1TransactionManager")
    public List<String> deleteMyPost(Long boardId, String postPassword) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> CustomNotFoundException.of()
                        .customMessage("게시물 존재 하지 않습니다.")
                        .request(boardId).build());
        if (!board.getPassword().equals(postPassword))
            throw CustomBadCredentialsException.of()
                    .request(postPassword)
                    .customMessage("게시물 비밀번호가 일치 하지 않습니다.")
                    .build();
        boardRepository.delete(board);
        return deleteFileInfo(board);
    }
    private List<String> deleteFileInfo(Board board) {
        String content = board.getContent();
        List<String> srcList = new ArrayList<>();
        Pattern pattern = Pattern.compile("<img\\s+[^>]*src=[\"']([^\"']+)[\"']", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            srcList.add(matcher.group(1));
        }
        if(board.getFilePath()!=null)
            srcList.add(board.getFilePath());
        return srcList;
    }

    @Transactional(value = "db1TransactionManager", readOnly = true)
    public Boolean verifyMyPost(Long boardId, String postPassword, String loginId) {
        String realPassword = boardRepository.findPasswordByBoardId(boardId);
        String realWriterId = boardRepository.findWriterIdByBoardId(boardId);


        if(realPassword == null)
            throw CustomNotFoundException.of()
                    .customMessage("게시물 존재 하지 않습니다.")
                    .request(boardId).build();
        if (realWriterId != null && !realWriterId.equals(loginId))
            throw CustomBadCredentialsException.of()
                    .request(loginId)
                    .customMessage("로그인한 사용자와 게시물 작성자가 일치 하지 않습니다.")
                    .build();
        if (!realPassword.equals(postPassword))
            throw CustomBadCredentialsException.of()
                    .request(postPassword)
                    .customMessage("게시물 비밀번호가 일치 하지 않습니다.")
                    .build();
        return true;
    }

    @Transactional(value = "db1TransactionManager", readOnly = true)
    public BoardResponse getOnlyBoardById(Long boardId,String prevUrl, String loginUserId) {
        Board board = boardRepository.findByIdFetchJoinUser(boardId)
                .orElseThrow(() -> new CustomViewException("게시물이 존재하지 않습니다.", prevUrl));
        if (!board.getMyUser().getUserId().equals(loginUserId))
            throw new CustomViewException("게시물 작성자와 로그인한 사용자가 일치하지 않습니다.", prevUrl);

        return BoardMapper.INSTANCE.boardToResponse(board);
    }

    @Transactional(value = "db1TransactionManager")
    public void modifyBoardLogic(BoardWriteRequest writeRequest, String userId) {
        Board board = boardRepository.findByIdFetchJoinUser(writeRequest.getBoardId())
                .orElseThrow(() -> CustomNotFoundException.of().customMessage("게시물이 존재하지 않습니다.")
                        .request(writeRequest.getBoardId()).build());
        if(!board.getMyUser().getUserId().equals(userId)) {
            throw CustomBadCredentialsException.of()
                    .request(userId)
                    .customMessage("로그인한 사용자와 게시물 작성자가 일치 하지 않습니다.")
                    .build();
        }
        board.modifyBoard(writeRequest);

    }
    @Transactional("db1TransactionManager")
    public void updateBoardFileInfo(FileInfoUpdateRequest request) {
        Board board = boardRepository.findById(request.getPrimaryKey())
                .orElseThrow(() -> CustomNotFoundException.of()
                        .customMessage("게시물이 존재하지 않습니다.")
                        .request(request.getPrimaryKey())
                        .build());
        board.fileInfoUpdate(request);
    }
    @Transactional("db1TransactionManager")
    public void userPointPlus(String userId, Long plusPoint) {
        long result = myUserRepository.updatePointPlus(userId, plusPoint);
        if(result != 1)
            throw CustomNotFoundException.of()
                    .customMessage("게시물은 작성 되었으나 사용자 포인트 업데이트에 실패했습니다.")
                    .request(userId)
                    .build();
    }
}
