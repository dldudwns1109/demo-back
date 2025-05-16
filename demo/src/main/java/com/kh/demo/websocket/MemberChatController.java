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
import com.kh.demo.dao.ChatReadDao;
import com.kh.demo.dao.CrewMemberDao;
import com.kh.demo.dao.MemberDao;
import com.kh.demo.dto.ChatDto;
import com.kh.demo.dto.ChatReadDto;
import com.kh.demo.dto.MemberDto;
import com.kh.demo.service.TokenService;
import com.kh.demo.vo.websocket.ChatUserVO;
import com.kh.demo.vo.websocket.MemberChatMessageVO;
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
	
	@Autowired
	private ChatReadDao chatReadDao;
	
	@Autowired
	private CrewMemberDao crewMemberDao;

	
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
						.chatRead(
							chatReadDao.countChatRoomUnread(
								ChatReadDto.builder()
									.chatRoomNo(roomNo)
									.unreadMemberNo(memberNo)
								.build()
							)
						)
					.build()
				);
			}
		}
		
		messagingTemplate.convertAndSend("/private/member/rooms/" 
				+ memberNo, list);
	}
	
	@MessageMapping("/member/read")
	public void memberRead(Message<MemberChatVO> message) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		String accessToken = accessor.getFirstNativeHeader("accessToken");
		
		if (accessToken == null || !accessToken.startsWith("Bearer ")) return;
		long memberNo = tokenService.parseBearerToken(accessToken);
		
		chatReadDao.delete(
			ChatReadDto.builder()
				.chatRoomNo(message.getPayload().getTarget())
				.unreadMemberNo(memberNo)
			.build()
		);
		
		List<Long> chatNoList =  chatDao.selectChatByRoomNo(message.getPayload().getTarget());
		for (Long chatNo: chatNoList) {
			chatDao.updateChatRead(chatNo);
		}
		
		List<ChatDto> list = chatDao.selectChatMessageList(message.getPayload().getTarget());
		
		long targetNo = -1;
		Set<ChatUserVO> set = new HashSet<>(chatDao.selectChatTarget(message.getPayload().getTarget()));
		ChatUserVO chatUser = set.iterator().next();
		
		if (chatUser.getChatSender() != memberNo) 
			targetNo = chatUser.getChatSender();
		else targetNo = chatUser.getChatReceiver();
		
		log.debug("member = {}", memberNo);
		List<MemberChatResponseVO> chatList = new ArrayList<>();
		for (ChatDto chat : list) {
			MemberDto memberDto = memberDao.findMemberByNo(chat.getChatSender());
			chatList.add(
				MemberChatResponseVO.builder()
					.messageNo(chat.getChatNo())
					.targetNo(chat.getChatReceiver())
					.accountNo(chat.getChatSender())
					.accountNickname(memberDto.getMemberNickname())
					.content(chat.getChatContent())
					.time(chat.getChatTime().toLocalDateTime())
					.chatRead(chat.getChatRead())
				.build()
			);
		}
		
		messagingTemplate.convertAndSend("/private/member/read/" 
				+ message.getPayload().getTarget(), chatList);
	}

	@MessageMapping("/member/chat")
	public void memberChat(Message<MemberChatVO> message) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		String accessToken = accessor.getFirstNativeHeader("accessToken");
//		log.debug("accessToken = {}", accessToken);
		
		if (accessToken == null || !accessToken.startsWith("Bearer ")) return;
		long memberNo = tokenService.parseBearerToken(accessToken);
		
		MemberDto memberDto = memberDao.findMemberByNo(memberNo);
		
		long chatNo = chatDao.sequence();
		MemberChatVO vo = message.getPayload();
		
		MemberChatResponseVO response = MemberChatResponseVO.builder()
				.messageNo(chatNo)
				.targetNo(vo.getTarget())
				.accountNo(memberDto.getMemberNo())
				.accountNickname(memberDto.getMemberNickname())
				.content(vo.getContent())
				.time(LocalDateTime.now())
				.chatRead(1L)
				.build();
		
		messagingTemplate.convertAndSend("/private/member/chat/" 
				+ String.valueOf(vo.getTarget()), response);
		
		long targetNo = -1;
		Set<ChatUserVO> set = new HashSet<>(chatDao.selectChatTarget(vo.getTarget()));
		ChatUserVO chatUser = set.iterator().next();
		
		if (chatUser.getChatSender() != memberNo) 
			targetNo = chatUser.getChatSender();
		else targetNo = chatUser.getChatReceiver();
		
		// 그룹 모임 시 조건처리하여 crewNo 넣어야함
		chatDao.insert(
			ChatDto.builder()
				.chatNo(chatNo)
//				.chatCrewNo(null)
				.chatRoomNo(vo.getTarget())
				.chatType("DM")
				.chatContent(vo.getContent())
				.chatTime(Timestamp.valueOf(response.getTime()))
				.chatRead(1L)
				.chatSender(memberNo)
				.chatReceiver(targetNo)
			.build()
		);
		
		chatReadDao.insert(
			ChatReadDto.builder()
				.chatNo(chatNo)
				.chatRoomNo(vo.getTarget())
				.unreadMemberNo(targetNo)
			.build()
		);
	}
	
	public void sendJoinWelcomeMessage(long crewNo, long memberNo, String chatContent) {
	    MemberDto memberDto = memberDao.findMemberByNo(memberNo);
	    String memberName = memberDto.getMemberNickname();

	    Long chatRoomNo = chatDao.findRoomByCrewNo(crewNo);
	    log.debug("chatRoomNo = {}", chatRoomNo);
	    
	    if (chatRoomNo != null) {
	        long systemChatNo = chatDao.sequence(); // 💡 시퀀스 할당

	        ChatDto welcomeMessage = ChatDto.builder()
	                .chatNo(systemChatNo)
	                .chatRoomNo(chatRoomNo)
	                .chatCrewNo(crewNo)
	                .chatType("SYSTEM")
	                .chatContent(memberName + "님이 들어오셨습니다!\n")
	                .chatTime(new Timestamp(System.currentTimeMillis()))
	                .chatSender(memberNo)
	                .chatRead(0L)
	                .build();

	        chatDao.insert(welcomeMessage);

	        MemberChatMessageVO vo = MemberChatMessageVO.builder()
	                .roomNo(chatRoomNo)
	                .senderNo(memberNo)
	                .receiverNo(null)
	                .senderNickname(memberName)
	                .content(welcomeMessage.getChatContent())
	                .type("SYSTEM")
	                .time(LocalDateTime.now())
	                .build();

	        messagingTemplate.convertAndSend("/private/member/chat/" + chatRoomNo, vo);
	    }

	    long leaderNo = crewMemberDao.findLeaderMemberNo(crewNo);
	    if (leaderNo != memberNo) {
	        Long dmRoomNo = chatDao.findDmRoom(memberNo, leaderNo);
	        if (dmRoomNo == null) {
	            dmRoomNo = chatDao.roomSequence();
	        }

	        long dmChatNo = chatDao.sequence(); // 💡 DM 채팅 메시지용 시퀀스

	        ChatDto dmMessage = ChatDto.builder()
	                .chatNo(dmChatNo)
	                .chatRoomNo(dmRoomNo)
	                .chatType("DM")
	                .chatContent(chatContent)
	                .chatTime(new Timestamp(System.currentTimeMillis()))
	                .chatSender(memberNo)
	                .chatReceiver(leaderNo)
	                .chatRead(1L)
	                .build();

	        chatDao.insert(dmMessage);

	        MemberChatMessageVO dmVO = MemberChatMessageVO.builder()
	                .roomNo(dmRoomNo)
	                .senderNo(memberNo)
	                .receiverNo(leaderNo)
	                .senderNickname(memberName)
	                .content(dmMessage.getChatContent())
	                .type("DM")
	                .time(LocalDateTime.now())
	                .build();

	        messagingTemplate.convertAndSend("/private/member/chat/" + leaderNo, dmVO);
	    }
	}
}
