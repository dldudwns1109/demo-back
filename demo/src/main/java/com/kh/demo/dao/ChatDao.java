package com.kh.demo.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.demo.dto.ChatDto;

@Repository
public class ChatDao {
	
	@Autowired
	private SqlSession sqlSession;
	
	public long roomSequence() {
		return sqlSession.selectOne("chat.roomSequence", Long.class);
	}
	
	public void insert(ChatDto chatDto) {
		sqlSession.insert("chat.insert", chatDto);
	}
	
	public List<Long> selectList(long memberNo) {
		return sqlSession.selectList("chat.findChatList", memberNo);
	}
	
	public List<ChatDto> selectChatList(long roomNo) {
		return sqlSession.selectList("chat.findChatRoom", roomNo);
	}
	
	public List<ChatDto> selectChatMessageList(long roomNo) {
		return sqlSession.selectList("chat.findChatMessageList", roomNo);
	}
	
	public List<Long> selectChatSender(long roomNo) {
		return sqlSession.selectList("chat.findChatSender", roomNo);
	}
	
	public long findRoomByCrewNo(long crewNo) {
	    return sqlSession.selectOne("chat.findRoomByCrewNo", crewNo);
	}
	
	public Long findDmRoom(long memberA, long memberB) {
	    Map<String, Object> params = new HashMap<>();
	    params.put("memberA", memberA);
	    params.put("memberB", memberB);
	    return sqlSession.selectOne("chat.findDmRoom", params);
	}
}
