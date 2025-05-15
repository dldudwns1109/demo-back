package com.kh.demo.vo.websocket;

import lombok.Data;

@Data
public class ChatUserVO {
	private Long chatSender;
	private Long chatReceiver;
}
