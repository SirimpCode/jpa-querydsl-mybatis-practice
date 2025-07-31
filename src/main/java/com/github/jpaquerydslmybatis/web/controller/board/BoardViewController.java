package com.github.jpaquerydslmybatis.web.controller.board;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jpaquerydslmybatis.common.exception.CustomViewException;
import com.github.jpaquerydslmybatis.common.myenum.RoleEnum;
import com.github.jpaquerydslmybatis.service.board.BoardService;
import com.github.jpaquerydslmybatis.web.dto.auth.UserInfoResponse;
import com.github.jpaquerydslmybatis.web.dto.board.BoardListResponse;
import com.github.jpaquerydslmybatis.web.dto.board.BoardResponse;
import com.github.jpaquerydslmybatis.web.dto.board.BoardSearchRequest;
import com.github.jpaquerydslmybatis.web.dto.board.CountIncrement;
import com.github.jpaquerydslmybatis.web.dto.response.PaginationResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardViewController {
    private final BoardService boardService;


    @GetMapping({"/write", "/write/{rootId}/{parentId}"})
    public String writePageViewRequiredLogin(HttpServletRequest request,
                                             HttpServletResponse response,
                                             ModelAndView modelAndView,
                                             @PathVariable(required = false) Long parentId,
                                             @PathVariable(required = false) Long rootId
    ) {
        //답변달기 일때는 운영자인지 확인
        UserInfoResponse loginUser = (UserInfoResponse) request.getSession().getAttribute("loginuser");
        if (parentId != null && loginUser.getRole() != RoleEnum.ROLE_ADMIN)
            throw new CustomViewException("운영자만 접근할 수 있습니다.", request.getHeader("Referer"));
        if (rootId == null)
            rootId = parentId; // 기본게시물에는 rootId가 없으므로 부모가 rootId가 된다.
        modelAndView.setViewName("mycontent1/board/write");
        modelAndView.addObject("parentId", parentId);
        modelAndView.addObject("rootId", rootId);
        return "mycontent1/board/write";
    }

    @GetMapping("/comment")
    public String commentPageViewRequiredLogin(HttpServletRequest request,
                                               HttpServletResponse response
    ) {

        return "mycontent1/board/write";
    }

    @GetMapping("/list/season2")
    public ModelAndView listPageViewVersion2(ModelAndView mav,
                                             @RequestParam(required = false, value = "searchWord") String searchValue,
                                             @RequestParam(required = false) String searchType,
                                             @RequestParam(required = false) String searchSort,
                                             @RequestParam(defaultValue = "1") long page,
                                             @RequestParam(defaultValue = "10") long size
    ) {

//        List<BoardResponse> boardList = boardService.getAllList();
        //List<BoardListResponse> boardList = boardService.getAllListSeason2();

        BoardSearchRequest searchRequest = BoardSearchRequest.of(searchValue, searchType, searchSort);
        PaginationResponse<BoardListResponse> responseList = boardService.getBoardList(searchRequest, page - 1, size);

        mav.addObject("boardList", responseList);
        mav.setViewName("mycontent1/board/listVersion2");
        return mav;

    }

    @GetMapping("/list")
    public ModelAndView listPageView(ModelAndView mav,
                                     @RequestParam(required = false, value = "searchWord") String searchValue,
                                     @RequestParam(required = false) String searchType,
                                     @RequestParam(required = false) String searchSort,
                                     @RequestParam(defaultValue = "1") long page,
                                     @RequestParam(defaultValue = "10") long size
    ) {

//        List<BoardResponse> boardList = boardService.getAllList();
        //List<BoardListResponse> boardList = boardService.getAllListSeason2();

        BoardSearchRequest searchRequest = BoardSearchRequest.of(searchValue, searchType, searchSort);
        PaginationResponse<BoardListResponse> responseList = boardService.getBoardList(searchRequest, page - 1, size);

        mav.addObject("boardList", responseList);
        mav.setViewName("mycontent1/board/list");
        return mav;
    }

    private Set<Long> parseViewCountList(Object attr) {
        if (!(attr instanceof Set<?>))
            return new HashSet<>();
        return ((Set<?>) attr).stream()
                .filter(e -> e instanceof Long)
                .map(e -> (Long) e)
                .collect(Collectors.toSet());
    }

    private boolean isViewCountIncremented(HttpSession session, Long boardId, String loginUserId) {
        String viewListId = loginUserId != null ? "viewCountList_" + loginUserId : "viewCountList_anonymous";
        Set<Long> viewCountList = parseViewCountList(session.getAttribute(viewListId));

        boolean isIncremented = !viewCountList.contains(boardId);
        if (isIncremented) {
            viewCountList.add(boardId);
            session.setAttribute(viewListId, viewCountList);
        }
        return isIncremented;
    }

    //글조회수 증가 dml update 문 때문에 post 방식으로 처리한다.
    //POST-Redirect-GET(PRG) 패턴 을 적용 해 보겠음
    //조회수 증가 여부만 담아서 redirect 시켜줄예정
    @PostMapping({"/view/{boardId}", "/view/{rootId}/{parentId}/{boardId}"})
    public String viewPageView(
            @PathVariable Long boardId,
            @PathVariable(required = false) Long parentId,
            @PathVariable(required = false) Long rootId,
            HttpSession httpSession,
            RedirectAttributes redirectAttributes,
            @RequestParam(required = false, value = "searchWord") String searchValue,
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String searchSort,
            @RequestParam(required = false) String returnUrl
    ) {

        String loginUserId = httpSession.getAttribute("loginuser") != null ?
                ((UserInfoResponse) httpSession.getAttribute("loginuser")).getUserId() :
                null;
        boolean countIncrement = isViewCountIncremented(httpSession, boardId, loginUserId);

        redirectAttributes.addFlashAttribute("countIncrement", CountIncrement.of(countIncrement));
        redirectAttributes.addAttribute("searchWord", searchValue);
        redirectAttributes.addAttribute("searchType", searchType);
        redirectAttributes.addAttribute("searchSort", searchSort);
        redirectAttributes.addAttribute("returnUrl", returnUrl);

        return (parentId != null && rootId != null)
                ? String.format("redirect:/board/view/%d/%d/%d", rootId, parentId, boardId)
                : String.format("redirect:/board/view/%d", boardId);
    }


    @GetMapping({"/view/{boardId}", "/view/{rootId}/{parentId}/{boardId}"})
    public ModelAndView viewPageViewGet(ModelAndView mav,
                                        @PathVariable Long boardId,
                                        @PathVariable(required = false) Long parentId,
                                        @PathVariable(required = false) Long rootId,
                                        HttpSession httpSession,
                                        @ModelAttribute("countIncrement") CountIncrement countIncrement,
                                        @RequestParam(required = false, value = "searchWord") String searchValue,
                                        @RequestParam(required = false) String searchType,
                                        @RequestParam(required = false) String searchSort,
                                        @RequestParam(required = false) String returnUrl
    ) {
        BoardSearchRequest searchRequest = BoardSearchRequest.of(searchValue, searchType, searchSort);

        String loginUserId = httpSession.getAttribute("loginuser") != null ?
                ((UserInfoResponse) httpSession.getAttribute("loginuser")).getUserId() :
                null;
        // FlashAttribute 에서 countIncrement 꺼내서 사용(없으면 false)
        BoardResponse boardResponse = boardService.getBoardById(boardId, loginUserId, countIncrement.isCountIncrement(), searchRequest);
        //사실 응답에 parentId 와 rootId 가 포함되어있다.
        mav.addObject("board", boardResponse);
        mav.addObject("parentId", parentId);
        mav.addObject("rootId", rootId);
        mav.addObject("returnUrl", returnUrl);
        mav.setViewName("mycontent1/board/view");
        return mav;
    }


    // ==== redirect(GET방식임) 시 데이터를 넘길때 GET 방식이 아닌
    // POST 방식처럼 데이터를 넘기려면 RedirectAttributes 를 사용하면 된다. 시작 ==== 예시//
    @GetMapping("/temp")
    public String tests1(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("test", "a");
        redirectAttributes.addFlashAttribute("test2", "b");
        //addFlashAttribute 는 한개만 담을 수 있다..? 아님 두개 다담김
        //
        // 만약 맵을 추가한다면
        Map<String, String> map = Map.of(
                "test3", "c",
                "test4", "d"
        );
        //맵을 넣고 보냈더니  그냥똑같이 키 벨류 형식으로 벨류에 맵이 들어갔음
        redirectAttributes.addFlashAttribute("map", map);
        return "redirect:/board/temp2";
    }

    @GetMapping("/temp2")
    public String tests2(HttpServletRequest request
    ) {
        // redirectAttributes 에 담긴 값은 request 에 담겨서 넘어온다.
        Map<String, ?> tests = RequestContextUtils.getInputFlashMap(request);
        return "";
    }

    @GetMapping("/modify/{boardId}")
    public ModelAndView modifyPageView(ModelAndView mav,
                                       @PathVariable Long boardId,
                                       HttpSession httpSession,
                                       HttpServletRequest request
    ) {
        String prevUrl = request.getHeader("Referer"); // 이전 페이지 전체 URL
        UserInfoResponse userInfoResponse = verifyLoginUser(httpSession, prevUrl);

        BoardResponse response = boardService.getOnlyBoardById(boardId, prevUrl, userInfoResponse.getUserId());
        mav.addObject("board", response);
        mav.setViewName("mycontent1/board/modify");
        return mav;
    }

    private UserInfoResponse verifyLoginUser(HttpSession httpSession, String redirectUrl) {
        UserInfoResponse loginUser = (UserInfoResponse) httpSession.getAttribute("loginuser");
        if (loginUser == null) {
            throw new CustomViewException("로그인 후 이용해주세요.", redirectUrl);
        }
        return loginUser;
    }
}
