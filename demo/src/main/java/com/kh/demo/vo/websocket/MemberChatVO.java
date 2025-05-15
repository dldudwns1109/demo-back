package com.kh.demo.vo.websocket;

import lombok.Data;

@Data
public class MemberChatVO {
	private Long target;
	private String content;
	private Long crewNo;
}
