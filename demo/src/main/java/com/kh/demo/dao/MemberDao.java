package com.kh.demo.dao;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import com.kh.demo.dto.AttachmentDto;
import com.kh.demo.dto.MemberDto;
import com.kh.demo.dto.MemberLikeDto;
import com.kh.demo.service.AttachmentService;

@Repository
public class MemberDao {

	@Autowired
	private SqlSession sqlSession;
	@Autowired
	private AttachmentService attachmentService;
	
	public long sequence() {
		return sqlSession.selectOne("member.sequence");
	}
	
	public void insert(MemberDto memberDto) {
		sqlSession.insert("member.insert", memberDto);
	}
	
	public void insertLike(MemberLikeDto memberLikeDto) {
		sqlSession.insert("member.insertLike", memberLikeDto);
	}
	
	public void connect(MemberDto memberDto, AttachmentDto attachmentDto) {
		Map<String, Object> params = new HashMap<>();
		params.put("memberNo", memberDto.getMemberNo());
		params.put("attachmentNo", attachmentDto.getAttachmentNo());
		sqlSession.insert("member.connect", params);
	}
	
	public void connect(long memberNo, long attachmentNo) {
		Map<String, Object> params = new HashMap<>();
		params.put("memberNo", memberNo);
		params.put("attachmentNo", attachmentNo);
		sqlSession.insert("member.connect", params);
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
	
	public boolean updatePasswordByNo(MemberDto memberDto) {
		return sqlSession.update("member.updatePasswordByNo", memberDto) > 0;
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
	
	public long findImage(long memberNo) {
		return sqlSession.selectOne("member.findImage", memberNo);
	}
	
	public long findMemberNo(String memberNickname) {
		return sqlSession.selectOne("member.findMemberNo", memberNickname);
	}
	
	public void disconnectProfile(long memberNo) {
		sqlSession.delete("member.disconnectProfile", memberNo);
	}
	
	public void updateProfile(long memberNo, MultipartFile attach) throws IOException {
		Long oldAttachmentNo = findImage(memberNo);

		if (oldAttachmentNo != null) {
			disconnectProfile(memberNo);
			attachmentService.delete(oldAttachmentNo);
		}

		long newAttachmentNo = attachmentService.save(attach).getAttachmentNo();
		connect(memberNo, newAttachmentNo);
	}
	
	public void updateLikes(long memberNo, String likes) {
	    sqlSession.delete("member.deleteLike", memberNo);
	    if (likes != null && !likes.isEmpty()) {
	        for (String like : likes.split(",")) {
	            MemberLikeDto dto = new MemberLikeDto(memberNo, like);
	            sqlSession.insert("member.insertLike", dto);
	        }
	    }
	}
}
