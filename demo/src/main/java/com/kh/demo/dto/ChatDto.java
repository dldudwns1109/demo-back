package com.kh.demo.dto;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatDto {
	private long chatNo;
	private Long chatRoomNo;
	private Long chatCrewNo;
	private String chatType;
	private String chatContent;
	private Timestamp chatTime;
	private Long chatRead;
	private Long chatSender;
	private Long chatReceiver;
}
