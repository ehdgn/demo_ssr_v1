package org.example.demo_ssr_v1.reply;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.demo_ssr_v1.user.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class ReplyController {

    private final ReplyService replyService;

    /**
     * 댓글 작성 기능 요청
     * @param saveDTO
     * @param session
     * @return
     */
    @PostMapping("/reply/save")
    public String saveProc(ReplyRequest.SaveDTO saveDTO, HttpSession session) {
        // 1. 인증 검사 () -> 로그인 인터셉터가 인증검사 함 -> /reply/** 설정
        // 2. 유효성 검사(형식 검사)
        // 3. 댓글 작성 요청 (서비스단)
        // 4. 게시글 상세보기 화면으로 리다이렉트 처리

        User sessionUser = (User) session.getAttribute("sessionUser");
        saveDTO.validate();
        replyService.댓글작성(saveDTO, sessionUser.getId());
        return "redirect:/board/" + saveDTO.getBoardId();
    }

    @PostMapping("/reply/{id}/delete")
    public String deleteProc(@PathVariable(name = "id") Long replyId, HttpSession session) {
        User sessionUser = (User) session.getAttribute("sessionUser");

        Long boardId = replyService.댓글삭제(replyId, sessionUser.getId());

        // 댓글 삭제 후 게시글 상세보기 리다이렉트 처리
        return "redirect:/board/" + boardId;

    }
}
