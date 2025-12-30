package org.example.demo_ssr_v1.purchase;

import lombok.RequiredArgsConstructor;
import org.example.demo_ssr_v1._core.errors.exception.Exception400;
import org.example.demo_ssr_v1._core.errors.exception.Exception404;
import org.example.demo_ssr_v1.board.Board;
import org.example.demo_ssr_v1.board.BoardRepository;
import org.example.demo_ssr_v1.user.User;
import org.example.demo_ssr_v1.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    // 유료 게시글 구매 기능
    private static final Integer PREMIUM_BOARD_PRICE = 500;

    @Transactional
    public void 구매하기(Long userId, Long boardId) {
        // 1. 게시글 조회 (유료/무료 게시글)
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new Exception404("게시글 찾을 수 없습니다"));
        // 2. 유료 게시글인지 확인
        if(board.getPremium() == null || board.getPremium() == false) {
            throw new Exception400("유료 게시글이 아닙니다");
        }
        // 3. 작성자가 자신의 게시글을 구매하려는 경우 방지
        if(board.isOwner(userId)) {
            throw new Exception400("자신이 작성한 게시글은 구매할 수 없습니다");
        }
        // 4. 일반 사용자가 이미 구매한 게시글인 여부 확인
        if(purchaseRepository.existsByUserIdAndBoardId(userId, boardId)) {
            throw new Exception400("이미 구매한 게시글 입니다");
        }
        // 5. 사용자 정보 (구매 요청자)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception404("사용자를 찾을 수 없습니다"));

        // 6. 해당 하는 사용자의 포인트 차감 처리 (User ) - point
        user.deductPoint(PREMIUM_BOARD_PRICE);

        // 7. 구매 내역 저장
        Purchase purchase = Purchase.builder()
                .user(user)
                .board(board)
                .price(PREMIUM_BOARD_PRICE)
                .build();
        // 엔티티로 저장
        purchaseRepository.save(purchase);

        // 8. 구매 요청자의 포인트 차감 갱신 (user 상태 갱신)
        userRepository.save(user);
    }


    // 게시글 상세보기 화면 들어갈 때 내가 구매한 글 여부 확인
    public boolean 구매여부확인(Long userId, Long boardId) {
        // 비 로그인 시에 false 던져 주기 위함
        if(userId == null) {
            return false;
        }
        return purchaseRepository.existsByUserIdAndBoardId(userId, boardId);
    }

}
