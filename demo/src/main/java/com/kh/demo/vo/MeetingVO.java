package com.kh.demo.vo;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeetingVO {
	private Long meetingNo; //정모 번호
	private Long meetingCrewNo; //정모 모임 번호
	private Long meetingOwnerNo; //정모 소유자
	private String meetingName; //정모 이름
	private Timestamp meetingDate; //정모 날짜+시간
	private String meetingLocation; //정모 장소
	private Long meetingPrice; //정모 비용
	private Long meetingLimit; //정모 최대 인원 수
	private Timestamp meetingCreate; //정모 생성 시각
    private Long attachmentNo; // 정모 대표 이미지 번호
}
