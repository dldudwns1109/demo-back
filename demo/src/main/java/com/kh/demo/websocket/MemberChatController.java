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
import com.kh.demo.vo.websocket.ChatReadDeleteVO;
import com.kh.demo.vo.websocket.ChatUserVO;
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
			ChatReadDeleteVO.builder()
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
		if (message.getPayload().getCrewNo() == null) {
			Set<ChatUserVO> set = new HashSet<>(chatDao.selectChatTarget(message.getPayload().getTarget()));
			ChatUserVO chatUser = set.iterator().next();
			
			if (chatUser.getChatSender() != memberNo) 
				targetNo = chatUser.getChatSender();
			else targetNo = chatUser.getChatReceiver();
		}
		
		List<MemberChatResponseVO> chatList = new ArrayList<>();
		for (ChatDto chat : list) {
			MemberDto memberDto = null;
			if (chat.getChatSender() != null)
				memberDto = memberDao.findMemberByNo(chat.getChatSender());
			chatList.add(
				MemberChatResponseVO.builder()
					.messageNo(chat.getChatNo())
					.targetNo(chat.getChatReceiver())
					.accountNo(chat.getChatSender())
					.accountNickname(memberDto == null ? null : memberDto.getMemberNickname())
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
				.chatRead(vo.getCrewNo() == null ? 1L : crewMemberDao.selectMemberCnt(vo.getCrewNo()) - 1L)
				.build();
		
		messagingTemplate.convertAndSend("/private/member/chat/" 
				+ String.valueOf(vo.getTarget()), response);
		
		long targetNo = -1;
		if (vo.getCrewNo() == null) {
			Set<ChatUserVO> set = new HashSet<>(chatDao.selectChatTarget(vo.getTarget()));
			ChatUserVO chatUser = set.iterator().next();
			
			if (chatUser.getChatSender() != memberNo) 
				targetNo = chatUser.getChatSender();
			else targetNo = chatUser.getChatReceiver();
		}
		
		// 그룹 모임 시 조건처리하여 crewNo 넣어야함
		chatDao.insert(
			ChatDto.builder()
				.chatNo(chatNo)
				.chatCrewNo(vo.getCrewNo() == null ? null : vo.getCrewNo())
				.chatRoomNo(vo.getTarget())
				.chatType(vo.getCrewNo() == null ? "DM" : "CREW")
				.chatContent(vo.getContent())
				.chatTime(Timestamp.valueOf(response.getTime()))
				.chatRead(vo.getCrewNo() == null ? 1L : crewMemberDao.selectMemberCnt(vo.getCrewNo()) - 1L)
				.chatSender(memberNo)
				.chatReceiver(vo.getCrewNo() == null ? targetNo : null)
			.build()
		);
		
		if (vo.getCrewNo() == null) {
			chatReadDao.insert(
				ChatReadDto.builder()
					.chatNo(chatNo)
					.chatRoomNo(vo.getTarget())
					.unreadMemberNo(targetNo)
				.build()
			);			
		} else {
			for (long crewMemberNo: crewMemberDao.findCrewMemberNo(vo.getCrewNo())) {
				if (crewMemberNo == memberNo) continue;
				chatReadDao.insert(
					ChatReadDto.builder()
						.chatNo(chatNo)
						.chatRoomNo(vo.getTarget())
						.unreadMemberNo(crewMemberNo)
					.build()
				);
			}
		}
		
	}
}
