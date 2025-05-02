package com.kh.demo.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.demo.dto.AttachmentDto;
import com.kh.demo.dto.CrewDto;
import com.kh.demo.vo.CrewVO;

@Repository
public class CrewDao {

	@Autowired
	private SqlSession sqlSession;

	// 전체 모임 목록 조회
	public List<CrewVO> selectList() {
		return sqlSession.selectList("crew.selectList");
	}

	// 특정 모임 상세 조회
	public CrewDto selectOne(Long crewNo) {
		return sqlSession.selectOne("crew.selectOne", crewNo);
	}

	// 모임 등록
	public long sequence() {
	    return sqlSession.selectOne("crew.sequence");
	}
	public CrewDto insert(CrewDto crewDto) {
	    sqlSession.insert("crew.insert", crewDto);
	    return crewDto;
	}

	// 모임 수정
	public boolean update(CrewDto crewDto) {
		return sqlSession.update("crew.update", crewDto) > 0;
	}

	// 모임 삭제
	public boolean delete(Long crewNo) {
		return sqlSession.delete("crew.delete", crewNo) > 0;
	}
	
	// 이미지 연결
	public void connect(CrewDto crewDto, AttachmentDto attachmentDto) {
		Map<String, Object> params = new HashMap<>();
		params.put("crewNo", crewDto.getCrewNo());
		params.put("attachmentNo", attachmentDto.getAttachmentNo());
		sqlSession.insert("crew.connect", params);
	}
	
	public void connect(Long crewNo, Long attachmentNo) {
	    Map<String, Object> params = new HashMap<>();
	    params.put("crewNo", crewNo);
	    params.put("attachmentNo", attachmentNo);
	    sqlSession.insert("crew.connect", params);
	}
	
	// 이미지 찾기
	public int findImage(long crewNo) {
		return sqlSession.selectOne("crew.findImage", crewNo);
	}
}
