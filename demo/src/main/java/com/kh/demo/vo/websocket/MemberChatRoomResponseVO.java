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
public class MemberChatRoomResponseVO {
	private Long messageNo;
	private Long roomNo;
	private Long accountNo;
	private String accountNickname;
	private String content;
	private String type;
	private LocalDateTime time;
}
