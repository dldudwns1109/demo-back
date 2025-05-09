package com.kh.demo.websocket;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

import com.kh.demo.dao.MemberDao;
import com.kh.demo.dto.MemberDto;
import com.kh.demo.service.TokenService;
import com.kh.demo.vo.websocket.MemberChatResponseVO;
import com.kh.demo.vo.websocket.MemberChatVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class MemberChatController {
	
	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	
	@Autowired
	private TokenService tokenService;
	
	@Autowired
	private MemberDao memberDao;

	@MessageMapping("/member/chat")
	public void memberChat(Message<MemberChatVO> message) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		String accessToken = accessor.getFirstNativeHeader("accessToken");
//		log.debug("accessToken = {}", accessToken);
		
		if (accessToken == null || !accessToken.startsWith("Bearer ")) return;
		long memberNo = tokenService.parseBearerToken(accessToken);
		
		MemberDto memberDto = memberDao.findMemberByNo(memberNo);
		
		MemberChatVO vo = message.getPayload();
		log.debug("content = {}", vo.getContent());
		
		MemberChatResponseVO response = MemberChatResponseVO.builder()
				.accountNo(memberDto.getMemberNo())
				.accountNickname(memberDto.getMemberNickname())
				.content(vo.getContent())
				.time(LocalDateTime.now())
				.build();
		
		messagingTemplate.convertAndSend("/public/member/chat", response);
	}
}
