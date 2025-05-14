package com.kh.demo.websocket;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

import com.kh.demo.dao.ChatDao;
import com.kh.demo.dao.MemberDao;
import com.kh.demo.dto.ChatDto;
import com.kh.demo.dto.MemberDto;
import com.kh.demo.service.TokenService;
import com.kh.demo.vo.websocket.MemberChatResponseVO;
import com.kh.demo.vo.websocket.MemberChatRoomResponseVO;
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
	
	@Autowired
	private ChatDao chatDao;
	
	@MessageMapping("/member/room")
	public void memberRoom(Message<?> message) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		String accessToken = accessor.getFirstNativeHeader("accessToken");
		
		if (accessToken == null || !accessToken.startsWith("Bearer ")) return;
		long memberNo = tokenService.parseBearerToken(accessToken);
		
		List<MemberChatRoomResponseVO> list = new ArrayList<>();
		for (Long roomNo: chatDao.selectList(memberNo)) {
			for (ChatDto chatDto : chatDao.selectChatList(roomNo)) {
				long targetNo = memberNo != chatDto.getChatReceiver() ?
								chatDto.getChatReceiver() :
								chatDto.getChatSender();		
				MemberDto memberDto = memberDao.findMemberByNo(targetNo);
				
				list.add(
					MemberChatRoomResponseVO.builder()
						.roomNo(roomNo)
						.accountNo(targetNo)
						.accountNickname(memberDto.getMemberNickname())
						.content(chatDto.getChatContent())
						.time(chatDto.getChatTime().toLocalDateTime())
					.build()
				);
			}
		}
		
		messagingTemplate.convertAndSend("/private/member/rooms/" 
				+ memberNo, list);
	}

	@MessageMapping("/member/chat")
	public void memberChat(Message<MemberChatVO> message) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		String accessToken = accessor.getFirstNativeHeader("accessToken");
//		log.debug("accessToken = {}", accessToken);
		
		if (accessToken == null || !accessToken.startsWith("Bearer ")) return;
		long memberNo = tokenService.parseBearerToken(accessToken);
		
		MemberDto memberDto = memberDao.findMemberByNo(memberNo);
		
		MemberChatVO vo = message.getPayload();
		log.debug("target = {}", vo.getTarget());
		log.debug("content = {}", vo.getContent());
		
		MemberChatResponseVO response = MemberChatResponseVO.builder()
				.targetNo(vo.getTarget())
				.accountNo(memberDto.getMemberNo())
				.accountNickname(memberDto.getMemberNickname())
				.content(vo.getContent())
				.time(LocalDateTime.now())
				.build();
		
		messagingTemplate.convertAndSend("/private/member/chat/" 
				+ String.valueOf(vo.getTarget()), response);
		
		long targetNo = -1;
		Set<Long> set = new HashSet<>(chatDao.selectChatSender(vo.getTarget()));
		if (set.size() > 1) {
			for (long senderNo : set) {
				if (senderNo != memberNo) targetNo = senderNo; 
			}
		} else targetNo = set.iterator().next();
		
		// 그룹 모임 시 조건처리하여 crewNo 넣어야함
		chatDao.insert(
			ChatDto.builder()
//				.chatCrewNo(null)
				.chatRoomNo(vo.getTarget())
				.chatType("DM")
				.chatContent(vo.getContent())
				.chatTime(Timestamp.valueOf(response.getTime()))
				.chatSender(memberNo)
				.chatReceiver(targetNo)
			.build()
		);
	}
}
