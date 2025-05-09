package com.kh.demo.dto;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class MeetingDto {
	private Long meetingNo;
	private Long meetingCrewNo;
	private Long meetingOwnerNo;
	private String meetingName;
	private Timestamp meetingDate;
	private String meetingLocation;
	private Long meetingPrice;
	private Long meetingLimit;
	private Timestamp meetingCreate;
}
