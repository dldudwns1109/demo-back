package com.kh.demo.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.demo.dto.MemberDto;
import com.kh.demo.dto.MemberLikeDto;

@Repository
public class MemberDao {

	@Autowired
	private SqlSession sqlSession;
	
	public long sequence() {
		return sqlSession.selectOne("member.sequence");
	}
	
	public void insert(MemberDto memberDto) {
		sqlSession.insert("member.insert", memberDto);
	}
	
	public void insertLike(MemberLikeDto memberLikeDto) {
		sqlSession.insert("member.insertLike", memberLikeDto);
	}
	
	public MemberDto findMember(String memberId) {
		return sqlSession.selectOne("member.findMember", memberId);
	}
	
	public MemberDto findMemberByNo(long memberNo) {
		return sqlSession.selectOne("member.findMemberByNo", memberNo);
	}
	
	public MemberDto findMemberByEmail(String memberEmail) {
		return sqlSession.selectOne("member.findMemberByEmail", memberEmail);
	}
	
	public MemberDto findMemberByNickname(String memberNickname) {
		return sqlSession.selectOne("member.findMemberByNickname", memberNickname);
	}
	
	public String findId(String memberEmail) {
		return sqlSession.selectOne("member.findId", memberEmail);
	}
	
	public boolean updatePassword(MemberDto memberDto) {
		return sqlSession.update("member.updatePassword", memberDto) > 0;
	}
	
	public boolean update(MemberDto memberDto) {
		return sqlSession.update("member.editUnit", memberDto) > 0;
	}

	public void deleteMember(long memberNo) {
	    sqlSession.delete("member.deleteMember", memberNo);
	}
	
	public List<String> findMemberLike(long memberNo) {
		return sqlSession.selectList("member.findMemberLike", memberNo);
	}
}
