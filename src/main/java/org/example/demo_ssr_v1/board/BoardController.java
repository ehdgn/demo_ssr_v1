package org.example.demo_ssr_v1.board;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller // IoC
public class BoardController {

    @Autowired
    private BoardPersistRepository boardPersistRepository;

    // 게시글 화면 요청 - 자원요청 get
    // http://localhost:8080/board/save-form
    @GetMapping("/board/save-form")
    public String saveForm() {

        return "board/save-form";
    }

    // 게시글 작성 기능
    // post - http://localhost:8080/board/save-form
    @PostMapping("/board/save-form")
    public String saveFormProc(
            @RequestParam("username") String username,
            @RequestParam("title") String title,
            @RequestParam("content") String content
    ) {
        System.out.println("username: " + username);
        System.out.println("title" + title);
        System.out.println("content" + content);

        Board board = new Board();
        board.setUsername(username);
        board.setTitle(title);
        board.setContent(content);

        boardPersistRepository.save(board);

        return "redirect:/board/list";
    }

    // 게시글 목록 보기
    // get - http://localhost:8080/board/list
    @GetMapping("/board/list")
    public String list(Model model) {

        List<Board> boardList = boardPersistRepository.findAll();
        model.addAttribute("boardList", boardList);
        System.out.println(boardList.stream().toList());
        return "board/list";
    }
}