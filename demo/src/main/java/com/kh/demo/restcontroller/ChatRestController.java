package com.kh.demo.restcontroller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.demo.dao.ChatDao;
import com.kh.demo.dao.MemberDao;
import com.kh.demo.dto.ChatDto;
import com.kh.demo.dto.MemberDto;
import com.kh.demo.vo.websocket.MemberChatRoomResponseVO;

@CrossOrigin
@RestController
@RequestMapping("/api/chat")
public class ChatRestController {
	
	@Autowired
	private ChatDao chatDao;
	
	@Autowired
	private MemberDao memberDao;
	
	@GetMapping("/list/{memberNo}")
	public List<MemberChatRoomResponseVO> list(@PathVariable long memberNo) {
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
		
		return list;
	}
	
	@GetMapping("/messages/{roomNo}")
	public List<MemberChatRoomResponseVO> messageList(@PathVariable long roomNo) {
		List<MemberChatRoomResponseVO> list = new ArrayList<>();
		for (ChatDto chatDto: chatDao.selectChatMessageList(roomNo)) {
			Long targetNo = chatDto.getChatSender();
			
			MemberDto memberDto = null;
			if (targetNo != null)
				memberDto = memberDao.findMemberByNo(targetNo);
			
			list.add(
				MemberChatRoomResponseVO.builder()
					.roomNo(roomNo)
					.accountNo(targetNo)
					.accountNickname(memberDto == null ? null : memberDto.getMemberNickname())
					.content(chatDto.getChatContent())
					.time(chatDto.getChatTime().toLocalDateTime())
					.chatRead(chatDto.getChatRead())
				.build()
			);
		}
		
		return list;
	}
	
	@GetMapping("/crew/{crewNo}")
	public Long findCrewNo(@PathVariable long crewNo) {
		return chatDao.findRoomNoByCrewNo(crewNo);
	}
}
