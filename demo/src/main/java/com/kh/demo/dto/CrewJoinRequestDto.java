package com.kh.demo.dto;

import lombok.Data;
/**
 * 모임 가입 시 인사말을 별도로 받기 위한 Dto
 * - crew_member 테이블에는 직접 저장하지 않음
 * - 채팅(chat) 테이블에 SYSTEM 메시지로 저장
 * 
 * 분리 이유:
 * 1. 가입 정보와 채팅 기능의 책임 분리
 * 2. 채팅과 회원가입 절차의 관심사 분리
 * 3. 확장성 고려 (예: 향후 가입 질문 기능 등 추가 대비)
 * 4. chat 테이블 외에는 저장하지 않기 때문에 별도 테이블 생성 없음
 */
@Data
public class CrewJoinRequestDto {
    private String chatContent;
}
