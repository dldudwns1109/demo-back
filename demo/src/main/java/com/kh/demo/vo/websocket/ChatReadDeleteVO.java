package com.kh.demo.vo.websocket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatReadDeleteVO {
	private Long chatRoomNo;
	private Long unreadMemberNo;
}
