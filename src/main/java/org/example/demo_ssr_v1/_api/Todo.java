package org.example.demo_ssr_v1._api;

import lombok.Data;

// 응답 Todo에 대한 DTO 설계
@Data
public class Todo {
    // { "userId": 1, "id": 1, "title": "delectus aut autem", "completed": false }
    private Integer userId;
    private Integer id;
    private String title;
    private boolean completed;
}
