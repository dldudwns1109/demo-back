package com.kh.demo.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.demo.dto.TokenDto;

@Repository
public class TokenDao {
	
	@Autowired
	private SqlSession sqlSession;
	
	public long sequence() {
		return sqlSession.selectOne("token.sequence");
	}
	
	public void insert(TokenDto tokenDto) {
		sqlSession.insert("token.insert", tokenDto);
	}
	
	public TokenDto find(TokenDto tokenDto) {
		return sqlSession.selectOne("token.find", tokenDto);
	}
	
	public boolean delete(TokenDto tokenDto) {
		return sqlSession.delete("token.delete", tokenDto) > 0;
	}
	
	public boolean clean(String tokenTarget) {
		return sqlSession.delete("token.deleteByTokenTarget", tokenTarget) > 0;
	}
	
	public boolean clean(TokenDto tokenDto) {
		return sqlSession.delete("token.deleteByTokenTarget", tokenDto) > 0;
	}
}
