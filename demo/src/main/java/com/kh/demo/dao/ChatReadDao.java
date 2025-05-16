package com.kh.demo.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.demo.dto.ChatReadDto;
import com.kh.demo.vo.websocket.ChatReadDeleteVO;

@Repository
public class ChatReadDao {

	@Autowired
	private SqlSession sqlSession;
	
	public void insert(ChatReadDto chatReadDto) {
		sqlSession.insert("chatread.insert", chatReadDto);
	}
	
	public boolean delete(ChatReadDeleteVO chatReadDeleteVO) {
		return sqlSession.delete("chatread.delete", chatReadDeleteVO) > 0;
	}
	
	public long countChatUnread(long chatNo) {
		return sqlSession.selectOne("chatread.countChatUnread", chatNo);
	}
	
	public long countChatRoomUnread(ChatReadDto chatReadDto) {
		return sqlSession.selectOne("chatread.countChatRoomUnread", chatReadDto);
	}
}
