package com.kh.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatReadDto {
	private Long chatReadNo;
	private Long chatNo;
	private Long chatRoomNo;
	private Long unreadMemberNo;
}
