package com.kh.demo.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.demo.dto.ChatReadDto;

@Repository
public class ChatReadDao {

	@Autowired
	private SqlSession sqlSession;
	
	public void insert(ChatReadDto chatReadDto) {
		sqlSession.insert("chatread.insert", chatReadDto);
	}
	
	public boolean delete(ChatReadDto chatReadDto) {
		return sqlSession.delete("chatread.delete", chatReadDto) > 0;
	}
}
