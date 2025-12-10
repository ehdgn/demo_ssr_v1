package org.example.demo_ssr_v1.board;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@RequiredArgsConstructor // DI
@Controller // IoC
public class BoardController {

    private final BoardPersistRepository repository;

    // 게시글 수정 폼 페이지 요청
    // http://localhost:8080/board/1/update
    @GetMapping("/board/{id}/update")
    public String updateForm(@PathVariable Long id, Model model) {

        Board board = repository.findById(id);
        if (board == null) throw new RuntimeException("수정할 게시글을 찾을 수 없어요");

        model.addAttribute("board", board);

        return "board/update-form";
    }

    // 게시글 수정 요청 (기능 요청)
    // http://localhost:8080/board/1/update
    @PostMapping("/board/{id}/update")
    public String updateProc(@PathVariable Long id, BoardRequest.UpdateDTO updateDTO) {
        try {
            repository.updateById(id, updateDTO);
            // 더티 체킹 활용
        } catch (Exception e) {
            throw new RuntimeException("게시글 수정 실패");
        }

        return "redirect:/board/list";
    }

    // 게시글 목록 요청
    @GetMapping({"/board/list", "/"})
    public String boardList(Model model) {
        List<Board> boardList = repository.findAll();
        model.addAttribute("boardList", boardList);
        return "board/list";
    }

    // 게시글 저장 화면 요청
    // http://localhost:8080/board/save
    @GetMapping("/board/save")
    public String saveForm() {

        return "board/save-form";
    }

    // 게시글 저장 요청 (기능 요청)
    // http://localhost:8080/board/save
    @PostMapping("/board/save")
    public String saveProc(BoardRequest.SaveDTO saveDTO) {
        // HTTP 요청: username=값&title=값&content=값
        // 스프링이 처리 : new SaveDto(), setter 메서드 호출해서 값을 넣어줌

        try {
            repository.save(saveDTO.toEntity());
        } catch (Exception e) {
            throw new RuntimeException("저장 실패");
        }

        return "redirect:/";
    }

    // 삭제 @DeleteMapping 이지만 form 태그 활용 없음 get, post (fetch 함수 활용)
    @PostMapping("/board/{id}/delete")
    public String delete(@PathVariable Long id) {
        repository.deleteById(id);
        return "redirect:/";
    }

    // 상세보기 화면
    // http://localhost:8080/board/1
    @GetMapping("/board/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Board board = repository.findById(id);
        if (board == null) throw new RuntimeException("게시글을 찾을 수 없어요: " + id);
        model.addAttribute(board);
        return "/board/detail";
    }
}