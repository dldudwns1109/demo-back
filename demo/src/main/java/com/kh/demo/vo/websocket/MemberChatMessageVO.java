package com.kh.demo.vo.websocket;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberChatMessageVO {
	private Long roomNo;
	private Long senderNo;
	private String senderNickname;
	private Long receiverNo;
	private String type; // 'DM' / 'CREW' / 'SYSTEM'
	private String content;
	private LocalDateTime time;
}
