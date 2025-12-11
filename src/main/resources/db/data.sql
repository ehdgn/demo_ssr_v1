insert into board_tb(title, content, username, created_at) values('제목1','내용1','ssar',now());
insert into board_tb(title, content, username, created_at) values('제목2','내용2','ssar',now());
insert into board_tb(title, content, username, created_at) values('제목3','내용3','cos',now());
insert into board_tb(title, content, username, created_at) values('제목4','내용4','love',now());

-- User 테이블 데이터 (5명의 사용자)
INSERT INTO user_tb (username, password, email, created_at) VALUES
                                                                ('admin', '1234', 'admin@blog.com', NOW()),
                                                                ('ssar', '1234', 'ssar@nate.com', NOW()),
                                                                ('cos', '1234', 'cos@gmail.com', NOW()),
                                                                ('hong', '1234', 'hong@naver.com', NOW()),
                                                                ('kim', '1234', 'kim@daum.net', NOW());