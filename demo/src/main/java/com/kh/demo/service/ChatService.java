package com.kh.demo.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.kh.demo.dao.ChatDao;
import com.kh.demo.dao.CrewMemberDao;
import com.kh.demo.dao.MemberDao;
import com.kh.demo.dto.ChatDto;
import com.kh.demo.dto.MemberDto;
import com.kh.demo.vo.websocket.MemberChatMessageVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ChatService {

    @Autowired
    private ChatDao chatDao;

    @Autowired
    private MemberDao memberDao;

    @Autowired
    private CrewMemberDao crewMemberDao;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * 모임 가입 시 시스템 메시지 전송
     */
    public void sendJoinSystemMessage(long crewNo, long memberNo) {
        MemberDto memberDto = memberDao.findMemberByNo(memberNo);
        String memberName = memberDto.getMemberNickname();

        Long chatRoomNo = chatDao.findRoomByCrewNo(crewNo);
        log.debug("chatRoomNo = {}", chatRoomNo);

        if (chatRoomNo != null) {
            long chatNo = chatDao.sequence();

            ChatDto welcomeMessage = ChatDto.builder()
                    .chatNo(chatNo)
                    .chatRoomNo(chatRoomNo)
                    .chatCrewNo(crewNo)
                    .chatType("SYSTEM")
                    .chatContent(memberName + "님이 들어오셨습니다!\n")
                    .chatTime(new Timestamp(System.currentTimeMillis()))
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
    }

    /**
     * 모임 탈퇴 시 시스템 메시지 전송
     */
    public void sendLeaveSystemMessage(long crewNo, long memberNo) {
        MemberDto memberDto = memberDao.findMemberByNo(memberNo);
        String memberName = memberDto.getMemberNickname();

        Long chatRoomNo = chatDao.findRoomByCrewNo(crewNo);
        log.debug("chatRoomNo = {}", chatRoomNo);

        if (chatRoomNo != null) {
            long chatNo = chatDao.sequence();

            ChatDto leaveMessage = ChatDto.builder()
                    .chatNo(chatNo)
                    .chatRoomNo(chatRoomNo)
                    .chatCrewNo(crewNo)
                    .chatType("SYSTEM")
                    .chatContent(memberName + "님이 모임을 떠났습니다.\n")
                    .chatTime(new Timestamp(System.currentTimeMillis()))
                    .chatRead(0L)
                    .build();

            chatDao.insert(leaveMessage);

            MemberChatMessageVO vo = MemberChatMessageVO.builder()
                    .roomNo(chatRoomNo)
                    .senderNo(memberNo)
                    .receiverNo(null)
                    .senderNickname(memberName)
                    .content(leaveMessage.getChatContent())
                    .type("SYSTEM")
                    .time(LocalDateTime.now())
                    .build();

            messagingTemplate.convertAndSend("/private/member/chat/" + chatRoomNo, vo);
        }
    }
    
    /**
     * 모임 강퇴 시 시스템 메시지 전송
     */
    public void sendKickSystemMessage(long crewNo, long memberNo) {
        MemberDto memberDto = memberDao.findMemberByNo(memberNo);
        String memberName = memberDto.getMemberNickname();

        Long chatRoomNo = chatDao.findRoomByCrewNo(crewNo);
        log.debug("chatRoomNo = {}", chatRoomNo);

        if (chatRoomNo != null) {
            long chatNo = chatDao.sequence();

            ChatDto leaveMessage = ChatDto.builder()
                    .chatNo(chatNo)
                    .chatRoomNo(chatRoomNo)
                    .chatCrewNo(crewNo)
                    .chatType("SYSTEM")
                    .chatContent(memberName + "님이 강퇴 되었습니다!")
                    .chatTime(new Timestamp(System.currentTimeMillis()))
                    .chatRead(0L)
                    .build();

            chatDao.insert(leaveMessage);

            MemberChatMessageVO vo = MemberChatMessageVO.builder()
                    .roomNo(chatRoomNo)
                    .senderNo(memberNo)
                    .receiverNo(null)
                    .senderNickname(memberName)
                    .content(leaveMessage.getChatContent())
                    .type("SYSTEM")
                    .time(LocalDateTime.now())
                    .build();

            messagingTemplate.convertAndSend("/private/member/chat/" + chatRoomNo, vo);
        }
    }
    
    /**
     * 모임 가입 시 모임 회장에게 DM 메시지 전송
     */
    public void sendJoinDmMessage(long crewNo, long senderNo, String content) {
        MemberDto senderDto = memberDao.findMemberByNo(senderNo);
        String senderName = senderDto.getMemberNickname();

        // 1. 모임 회장 번호 조회
        long leaderNo = crewMemberDao.findLeaderMemberNo(crewNo);

        // 2. 자기 자신에게 보낼 필요는 없음
        if (leaderNo == senderNo) return;

        // 3. DM 채팅방 조회 or 생성
        Long roomNo = chatDao.findDmRoom(senderNo, leaderNo);
        if (roomNo == null) {
            roomNo = chatDao.roomSequence();
        }

        long chatNo = chatDao.sequence();
        LocalDateTime now = LocalDateTime.now();

        // 4. DB 저장
        chatDao.insert(ChatDto.builder()
                .chatNo(chatNo)
                .chatRoomNo(roomNo)
                .chatType("DM")
                .chatContent(content)
                .chatTime(Timestamp.valueOf(now))
                .chatSender(senderNo)
                .chatReceiver(leaderNo)
                .chatRead(1L) // 보내는 사람은 읽음 처리
                .build());

        // 5. 수신자에게 실시간 전송
        MemberChatMessageVO dmVO = MemberChatMessageVO.builder()
                .roomNo(roomNo)
                .senderNo(senderNo)
                .receiverNo(leaderNo)
                .senderNickname(senderName)
                .content(content)
                .type("DM")
                .time(now)
                .build();

        messagingTemplate.convertAndSend("/private/member/chat/" + leaderNo, dmVO);
    }

}        
