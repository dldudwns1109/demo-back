package com.kh.demo.chat;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.kh.demo.dao.ChatDao;
import com.kh.demo.dto.ChatDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
public class ChatTest {

	@Autowired
	private ChatDao chatDao;
	
	@Test
	public void test() {
//		chatDao.insert(
//			ChatDto.builder()
//				.chatCrewNo(null)
//				.chatRoomNo(2L)
//				.chatType("DM")
//				.chatContent("~님이 메세지를 보내셨습니다.")
//				.chatTime(Timestamp.valueOf(LocalDateTime.now()))
//				.chatSender(84L)
//				.chatReceiver(82L)
//			.build()
//		);
		
		chatDao.insert(
				ChatDto.builder()
					.chatNo(chatDao.sequence())
					.chatCrewNo(61L)
					.chatRoomNo(chatDao.roomSequence())
					.chatType("SYSTEM")
					.chatContent("모임 개설")
					.chatTime(Timestamp.valueOf(LocalDateTime.now()))
					.chatRead(0L)
					.chatSender(null)
					.chatReceiver(null)
				.build()
			);
	}
}
