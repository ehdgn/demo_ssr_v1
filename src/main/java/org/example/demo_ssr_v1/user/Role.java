package org.example.demo_ssr_v1.user;

/**
 * 사용자 권한(역할)을 나타내는 enum 타입
 * - ADMIN: 관리자
 * - USER: 일반 사용자
 * - MANAGER..
 * - ...
 * 데이터를 범주화 할 때 사용한다.
 * 레벨 1 ~ 무한대 ...
 * 1 ~ 5 까지만 범주화 하고 싶을 때
 */
public enum Role {
    ADMIN, USER
}

