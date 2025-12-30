package org.example.demo_ssr_v1.board;

import lombok.Data;
import org.example.demo_ssr_v1._core.utils.MyDateUtil;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

/**
 * 응답 DTO
 */
public class BoardResponse {

    /**
     * 게시글 목록 응답 DTO
     */
    @Data
    public static class ListDTO {
        private Long id;
        private String title;
        private String username; // 작성자명 {{board.user.username}} --(평탄화)--> {{board.username}}
        private String createdAt;

        public ListDTO(Board board) {
            this.id = board.getId();
            this.title = board.getTitle();
            // 쿼리 --> JOIN FETCH로 가져오면 문제 없음
            if (board.getUser() != null) {
                this.username = board.getUser().getUsername();
            }
            // 날짜 포맷팅
            if (board.getCreatedAt() != null) {
                this.createdAt = MyDateUtil.timestampFormat(board.getCreatedAt());
            }
        }
    } // end of static inner class

    /**
     * 게시글 상세 응답 DTO
     */
    @Data
    public static class DetailDTO {
        private Long id;
        private String title;
        private String content;
        private Long userId;
        private String username;
        private String createdAt;
        private Boolean premium;
        private Boolean isPurchased;
        // 로그인한 사용자가 이 게시글을 구매 했는지 확인 여부 (단, 작성자는 제외)

        public DetailDTO(Board board, Boolean isPurchased) {
            this.id = board.getId();
            this.title = board.getTitle();
            this.content = board.getContent();


            // JOIN FETCH 활용 (한번에 JOIN 에서 Repository 에서 가지고 올 예정)
            if(board.getUser() != null) {
                this.userId = board.getUser().getId();
                this.username = board.getUser().getUsername();
            }
            // 날짜 포맷팅
            if(board.getCreatedAt() != null) {
                this.createdAt = MyDateUtil.timestampFormat(board.getCreatedAt());
            }
            // 유료 게시글 여부 추가
            this.premium = board.getPremium();
            // 구매 여부 추가
            this.isPurchased = isPurchased;
        }
    } // end of class

    /**
     * 게시글 수정 화면 응답 DTO
     */
    @Data
    public static class UpdateFormDTO {
        private Long id;
        private String title;
        private String content;

        public UpdateFormDTO(Board board) {
            this.id = board.getId();
            this.title = board.getTitle();
            this.content = board.getContent();
        }
    }

    @Data
    public static class PageDTO {
        private List<ListDTO> content;
        private int number; // 현재 페이지 번호 (0 부터 시작)
        private int size; // 한 페이지의 크기 (보여줄 개수)
        private int totalPages; // 전체 페이지 수
        private Long totalElements; // 전체 게시글 수
        private boolean first; // 첫 페이지 여부
        private boolean last; // 마지막 페이지 여부
        private boolean hasNext; // 다음 페이지 존재 여부
        private boolean hasPrev; // 이전 페이지 존재 여부

        private Integer previousPageNumber; // 이전 페이지 번호(없으면 null)
        private Integer nextPageNumber;     // 다음 페이지 번호(없으면 null)

        private List<PageLink> pageLinks; // 페이지 번호 링크 목록

        public PageDTO(Page<Board> page) {

            this.content = page.getContent().stream()
                    .map(ListDTO::new)
                    .toList();

            this.number = page.getNumber();
            this.size = page.getSize();
            this.totalPages = page.getTotalPages();
            this.totalElements = page.getTotalElements();
            this.first = page.isFirst();
            this.last = page.isLast();
            this.hasNext = page.hasNext();
            this.hasPrev = page.hasPrevious();

            // 현재 페이지 인덱스 0 부터 시작을 함
            // 화면 -> N + 1
            // 이전 페이지를 - 1

            this.previousPageNumber = page.hasPrevious() ? page.getNumber() : null;
            this.nextPageNumber = page.hasNext() ? page.getNumber() + 2 : null;

            // 페이지 링크 생성 (현재 기준으로 앞뒤 2 페이지 씩 표시)
             this.pageLinks = generatePageLinks(page);

        } // end of constructor

        private List<PageLink> generatePageLinks(Page<Board> page) {
            List<PageLink> links = new ArrayList<>();

            int currentPage = page.getNumber() + 1;
            int totalPages = page.getTotalPages();

            // 현재 페이지 번호 5인 상태
            //      3 4 [5] 6 7
            int startPage = Math.max(1, currentPage - 2);
            // 토탈 페이지 2
            int endPage = Math.min(totalPages, currentPage + 2);

            for (int i = startPage; i <= endPage; i++) {
                PageLink link = new PageLink();
                link.setDisplayNumber(i);
                link.setActive(i == currentPage);
                links.add(link);
            }

            return links;
        }
    }

    // 페이지 링크 클래스 설계
    @Data
    public static class PageLink {
        private int displayNumber; // 표시할 페이지 번호(1)
        private boolean active;
    }

} // end of outer class
