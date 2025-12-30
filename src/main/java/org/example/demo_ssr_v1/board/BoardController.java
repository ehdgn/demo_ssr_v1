package org.example.demo_ssr_v1.board;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.demo_ssr_v1.purchase.PurchaseService;
import org.example.demo_ssr_v1.reply.ReplyResponse;
import org.example.demo_ssr_v1.reply.ReplyService;
import org.example.demo_ssr_v1.user.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor // DI
@Controller // IoC
public class BoardController {

    private final BoardService boardService;
    private final ReplyService replyService;
    private final PurchaseService purchaseService;

    /**
     * 게시글 수정 화면 요청
     * @param id
     * @param model
     * @param session
     * @return
     */
    @GetMapping("/board/{id}/update")
    public String updateForm(@PathVariable Long id, Model model, HttpSession session) {

        // 1. 인증 검사 (o)
        User sessionUser = (User) session.getAttribute("sessionUser"); // sessionUser -> 상수
        // LoginInterceptor가 알아서 처리 해줌

        // 2. 인가 검사 (o)
        BoardResponse.UpdateFormDTO board = boardService.게시글수정화면(id, sessionUser.getId());

        model.addAttribute("board", board);
        return "board/update-form";
    }

    /**
     * 게시글 수정 요청 기능
     * @param id
     * @param updateDTO
     * @param session
     * @return
     */
    @PostMapping("/board/{id}/update")
    public String updateProc(@PathVariable Long id, BoardRequest.UpdateDTO updateDTO, HttpSession session) {

        // 1. 인증 처리 ()
        User sessionUser = (User) session.getAttribute("sessionUser");
        updateDTO.validate();
        boardService.게시글수정(updateDTO, id, sessionUser.getId());

        return "redirect:/board/list";
    }

    /**
     * 게시글 목록 페이징 처리 기능 추가
     * @param model
     * @return
     * // 예시: /board/list?page=1&size=5&keyword="사용자입력값"
     */
    @GetMapping({"/board/list", "/"})
//    @ResponseBody // 뷰 리졸브 (x) 데이터를 반환
    public String boardList(Model model,
                                 @RequestParam(defaultValue = "1") int page,
                                 @RequestParam(defaultValue = "3") int size,
                                 @RequestParam(required = false) String keyword) {

        int pageIndex = Math.max(0, page - 1);
        BoardResponse.PageDTO boardPage = boardService.게시글목록조회(pageIndex, size, keyword);
        model.addAttribute("boardPage", boardPage);
        model.addAttribute("keyword", keyword);
        return "board/list";
    }

    /**
     * 게시글 작성 화면 요청
     * @param session
     * @return
     */
    @GetMapping("/board/save")
    public String saveForm(HttpSession session) {
        User sessionUser = (User) session.getAttribute("sessionUser");

        return "board/save-form";
    }

    /**
     * 게시글 작성 요청 기능
     * @param saveDTO
     * @param session
     * @return
     */
    @PostMapping("/board/save")
    public String saveProc(BoardRequest.SaveDTO saveDTO, HttpSession session) {
        // 1. 인증 검사 - 인터셉터
        // 2. 유효성 검사 (형식), 논리적인 검사는 (서비스단)
        User sessionUser = (User) session.getAttribute("sessionUser");
        boardService.게시글작성(saveDTO, sessionUser);

        return "redirect:/";
    }

    /**
     * 게시글 삭제 요청 기능
     * @param id
     * @param session
     * @return
     */
    @PostMapping("/board/{id}/delete")
    public String delete(@PathVariable Long id, HttpSession session) {
        // 1. 인증 처리 (o)
        User sessionUser = (User) session.getAttribute("sessionUser");

        boardService.게시글삭제(id, sessionUser.getId());

        return "redirect:/";
    }

    /**
     * 게시글 상세 보기 화면 요청
     * @param boardId
     * @param model
     * @return
     */
    @GetMapping("board/{id}")
    public String detail(@PathVariable(name = "id") Long boardId, Model model, HttpSession session) {

        // 세션에 로그인 사용자 정보 조회(없을 수도 있음)
        User sessionUser = (User)  session.getAttribute("sessionUser");
        Long sessionUserId = sessionUser != null ? sessionUser.getId() : null;

        BoardResponse.DetailDTO board = boardService.게시글상세조회(boardId, sessionUserId);
        boolean isOwner = false;
        // 힌트 - 만약 응답 DTO 에 담겨 있는 정보과
        // SessionUser 담겨 정보를 확인하여 처리 가능
        if(sessionUser != null && board.getUserId() != null) {
            isOwner = board.getUserId().equals(sessionUser.getId());
        }
        // 댓글 목록 조회 (추가)
        // 로그인 안 한 상태에서 댓글 목록 요청시에 sessionUserId 는 null 값이다.
        List<ReplyResponse.ListDTO> replyList = replyService.댓글목록조회(boardId, sessionUserId);

        model.addAttribute("isOwner", isOwner);
        model.addAttribute("board", board);
        model.addAttribute("replyList", replyList);

        return "board/detail";
    }

    // /board/{{board.id}}/purchase
    @PostMapping("/board/{boardId}/purchase")
    public String purchase(@PathVariable Long boardId, HttpSession session) {
        // 1. 인증 검사 - 로그인 인터셉터가 동작
        User sessionUser = (User) session.getAttribute("sessionUser");
        // 포인트 차감
        // 구매 내역 저장
        purchaseService.구매하기(sessionUser.getId(), boardId);

        return "redirect:/board/" + boardId;
        // 2.
    }
}