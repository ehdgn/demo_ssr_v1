package org.example.demo_ssr_v1.board;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.demo_ssr_v1._core.errors.exception.Exception401;
import org.example.demo_ssr_v1._core.errors.exception.Exception403;
import org.example.demo_ssr_v1._core.errors.exception.Exception404;
import org.example.demo_ssr_v1._core.errors.exception.Exception500;
import org.example.demo_ssr_v1.user.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@RequiredArgsConstructor // DI
@Controller // IoC
public class BoardController {

    private final BoardService boardService;

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
     * 게시글 목록 화면 요청
     * @param model
     * @return
     */
    @GetMapping({"/board/list", "/"})
    public String boardList(Model model) {
        List<BoardResponse.ListDTO> boardList = boardService.게시글목록조회();

        model.addAttribute("boardList", boardList);
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
     * 게시글 상세보기 화면 요청
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/board/{id}")
    public String detail(@PathVariable Long id, Model model, HttpSession session) {

        BoardResponse.DetailDTO board = boardService.게시글상세조회(id);

        // 세션에 로그인 사용자 정보 조회(없을 수도 있음)
        User sessionUser = (User) session.getAttribute("sessionUser");
        boolean isOwner = false;

        if (sessionUser != null && board.getUserId() != null) {
            isOwner = board.getUserId().equals(sessionUser.getId());
        }

        model.addAttribute("isOwner", isOwner);
        model.addAttribute("board", board);
        return "/board/detail";
    }
}