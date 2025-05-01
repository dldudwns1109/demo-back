package com.kh.demo.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.demo.dto.AttachmentDto;

@Repository
public class AttachmentDao {
	@Autowired
	private SqlSession sqlSession;
	
	public AttachmentDto insert(AttachmentDto attachmentDto) {
		long attachmentNo = sqlSession.selectOne("attachment.sequence");
		attachmentDto.setAttachmentNo(attachmentNo);
		sqlSession.insert("attachment.insert", attachmentDto);
		return attachmentDto;
	}
	
	public AttachmentDto selectOne(long attachmentNo) {
		return sqlSession.selectOne("attachment.findImg", attachmentNo);
	}

	public void delete(long attachmentNo) {
		sqlSession.delete("attachment.delete", attachmentNo);
	}
}
