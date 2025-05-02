package com.kh.demo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrewDetailVO {
	private Long crewNo;
    private String crewName; // 모임 이름
    private String crewCategory; // 모임 카테고리
    private String crewLocation; // 모임 지역
    private long crewLimit; // 최대 인원 수
    private String crewIntro; // 모임 소개글
    private boolean crewIsLiked;
    private long crewMemberCnt;
    private long crewAttachmentNo;
}
