package com.kh.demo.restcontroller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.demo.dao.ChatDao;
import com.kh.demo.dao.MemberDao;
import com.kh.demo.dto.ChatDto;
import com.kh.demo.dto.MemberDto;
import com.kh.demo.service.TokenService;
import com.kh.demo.vo.websocket.MemberChatRoomResponseVO;

@CrossOrigin
@RestController
@RequestMapping("/api/chat")
public class ChatRestController {
	
	@Autowired
	private ChatDao chatDao;
	
	@Autowired
	private MemberDao memberDao;
	
	@Autowired
	private TokenService tokenService;
	
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
			long targetNo = chatDto.getChatSender();		
			MemberDto memberDto = memberDao.findMemberByNo(targetNo);
			
			list.add(
				MemberChatRoomResponseVO.builder()
					.roomNo(roomNo)
					.accountNo(targetNo)
					.accountNickname(memberDto.getMemberNickname())
					.content(chatDto.getChatContent())
					.time(chatDto.getChatTime().toLocalDateTime())
					.chatRead(chatDto.getChatRead())
				.build()
			);
		}
		
		return list;
	}
	
	// 1:1 채팅방 조회
    @GetMapping("/dm/{targetNo}")
    public Map<String, Object> checkRoom(@PathVariable long targetNo,
                                         @RequestHeader("Authorization") String bearerToken) {
        long senderNo = tokenService.parseBearerToken(bearerToken);
        Long roomNo = chatDao.findDmRoom(senderNo, targetNo);

        Map<String, Object> map = new HashMap<>();
        map.put("roomNo", roomNo); // 없으면 null
        return map;
    }
    
    @PostMapping("/dm")
    public void createDmAndSendFirstMessage(@RequestBody Map<String, String> body,
                                            @RequestHeader("Authorization") String bearerToken) {
        long senderNo = tokenService.parseBearerToken(bearerToken);
        long receiverNo = Long.parseLong(body.get("targetNo"));
        String content = body.get("content");

        // DM 방 존재 여부 확인
        Long roomNo = chatDao.findDmRoom(senderNo, receiverNo);
        
        // 방이 없으면 메시지 insert와 동시에 생성
        if (roomNo == null) {
            roomNo = chatDao.roomSequence();
        }

        long chatNo = chatDao.sequence();
        ChatDto chatDto = ChatDto.builder()
                .chatNo(chatNo)
                .chatRoomNo(roomNo)
                .chatType("DM")
                .chatSender(senderNo)
                .chatReceiver(receiverNo)
                .chatContent(content)
                .chatTime(new Timestamp(System.currentTimeMillis()))
                .chatRead(1L)
                .build();

        chatDao.insert(chatDto);
    }

}
