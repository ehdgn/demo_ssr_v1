package org.example.demo_ssr_v1.board;

import lombok.RequiredArgsConstructor;
import org.example.demo_ssr_v1._core.errors.exception.Exception403;
import org.example.demo_ssr_v1._core.errors.exception.Exception404;
import org.example.demo_ssr_v1.reply.ReplyRepository;
import org.example.demo_ssr_v1.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private final BoardRepository boardRepository;
    private final ReplyRepository replyRepository;

    /**
     * 게시글 목록 조회
     * 트랜잭션
     * - 읽기 전용 트랜잭션 - 성능 최적화
     * @return 게시글 목록 (생성일 기준으로 내림차순)
     */
//    public List<BoardResponse.ListDTO> 게시글목록조회() {
//        // 자바 문법
//        // 데이터 타입을 변환해서 맞춰 주어야 한다.
//        // List<Board> --> List<BoardResponse.ListDTO>
//        List<Board> boardList = boardRepository.findAllWithUserOrderByCreatedAtDesc();
//
//        // 1. 반복문
//        // List<BoardResponse.ListDTO> dtoList = new ArrayList<>();
//        // for (Board board: boardList) {
//        // BoardResponse.ListDTO dto = new BoardResponse.ListDTO(board);
//        // dtoList.add(dto);
//        // }
//        // return dtoList;
//
//        // 2. 람다 표현식
//        // return boardList.stream()
//        //         .map(board -> new BoardResponse.ListDTO(board))
//        //         .collect(Collectors.toList());
//
//        // 3. 참조 메서드
//        return boardList.stream()
//                .map(BoardResponse.ListDTO::new)
//                .collect(Collectors.toList());
//
//    }


    /**
     * 게시글 목록 조회 페이징 처리
     * 트랜잭션
     * - 읽기 전용 트랜잭션 - 성능 최적화
     * @return 게시글 목록 (생성일 기준으로 내림차순)
     */
    public BoardResponse.PageDTO 게시글목록조회(int page, int size, String keyword) {


        //** 상한선 제한 **
        // size는 기본값 5, 최소 1, 최대 50으로 제한
        // 페이지 번호가 음수가 되는 것을 막음
        int validPage = Math.max(0, page); // 양수값 보장
        // 최대값 제한   // 최대값 제한 50으로 보장
        // 최소값 제한   // 최소값 제한 1로 보장
        int validSize = Math.max(1, Math.min(50, size));

        // 정렬기준
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(validPage, validSize, sort);
        // [...스프링...] [검색][초기화]
        Page<Board> boardPage;
        if (keyword != null && !keyword.trim().isEmpty()) {
            boardPage = boardRepository.findByTitleContainingOrContentContaining(keyword.trim(), pageable);
        } else {
         boardPage = boardRepository.findAllWithUserOrderByCreatedAtDesc(pageable);
        }

        return new BoardResponse.PageDTO(boardPage);
    }

    public BoardResponse.DetailDTO 게시글상세조회(Long boardId) {

        Board board = boardRepository.findByIdWithUser(boardId)
                .orElseThrow(() -> new Exception404("게시글을 찾을 수 없어요"));

        return new BoardResponse.DetailDTO(board);
    }

    // 1. 트랜잭션 처리
    // 2. repository 저장 처리
    @Transactional
    public Board 게시글작성(BoardRequest.SaveDTO saveDTO, User sessionUser) {
        // DTO 에서 직접 new 해서 생성한 Board 객체일 뿐 아직 영속화된 객체는 아니다
        Board board = saveDTO.toEntity(sessionUser);
        boardRepository.save(board);
        return board;
    }

    // 1. 게시글 조회
    // 2. 인가 처리
    public BoardResponse.UpdateFormDTO 게시글수정화면(Long boardId, Long sessionUserId) {

        // 1
        Board boardEntity = boardRepository.findByIdWithUser(boardId)
                .orElseThrow(() -> new Exception404("게시글을 찾을 수 없습니다."));

        // 2. 인가 처리
        if (!boardEntity.isOwner(sessionUserId))
            throw new Exception403("게시글 수정 권한이 없습니다.");

        return new BoardResponse.UpdateFormDTO(boardEntity);
    }

    // 1. 트랜잭션 처리
    // 2. DB 에서 조회
    // 3. 인가 처리
    // 4. 조회된 board에 상태값 변경 (더티 체킹)
    @Transactional
    public Board 게시글수정(BoardRequest.UpdateDTO updateDTO, Long boardId, Long sessionUserId) {
        // 2.
        Board boardEntity = boardRepository.findById(boardId)
                .orElseThrow(() -> new Exception404("게시글을 찾을 수 없습니다."));

        // 3.
        if (!boardEntity.isOwner(sessionUserId))
            throw new Exception403("게시글 수정 권한이 없습니다.");

        // 4.
        boardEntity.update(updateDTO); // 상태값 변경
        return boardEntity;
    }

    // 1. 트랜잭션 처리
    // 2. 게시글 조회
    // 3. 인가 처리
    // 4. repository 에게 삭제 요청
    @Transactional
    public void 게시글삭제(Long boardId, Long sessionUserId) {
        // 2 (조회부터 해야 DB에 있는 Board에 user_id 값을 확인할 수 있음)
        Board boardEntity = boardRepository.findById(boardId)
                .orElseThrow(() -> new Exception404("게시글을 찾을 수 없습니다."));

        // 3
        if (!boardEntity.isOwner(sessionUserId))
            throw new Exception403("게시글 삭제 권한이 없습니다.");

        // 4
        replyRepository.deleteByBoardId(boardId);

        // 5
        boardRepository.deleteById(boardId);
    }
}