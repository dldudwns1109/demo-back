package com.kh.demo.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.demo.dto.AttachmentDto;
import com.kh.demo.dto.MeetingDto;
import com.kh.demo.vo.MeetingVO;

@Repository
public class MeetingDao {

	@Autowired
	private SqlSession sqlSession;

	// 정모 추가
	public long sequence() {
		return sqlSession.selectOne("meeting.sequence");
	}

	public MeetingDto insert(MeetingDto meetingDto) {
		sqlSession.insert("meeting.insert", meetingDto);
		return meetingDto;
	}

	// 정모 수정
	public boolean update(MeetingDto meetingDto) {
		return sqlSession.update("meeting.update", meetingDto) > 0;
	}

	// 정모 삭제
	public boolean delete(Long meetingNo) {
		return sqlSession.delete("meeting.delete", meetingNo) > 0;
	}

	// 특정 정모 상세 조회
	public MeetingVO selectVO(Long meetingNo) {
		return sqlSession.selectOne("meeting.selectVO", meetingNo);
	}

	// 이미지 연결
	public void connect(MeetingDto meetingDto, AttachmentDto attachmentDto) {
		Map<String, Object> params = new HashMap<>();
		params.put("meetingNo", meetingDto.getMeetingNo());
		params.put("attachmentNo", attachmentDto.getAttachmentNo());
		sqlSession.insert("meeting.connect", params);
	}

	public void connect(Long meetingNo, Long attachmentNo) {
		Map<String, Object> params = new HashMap<>();
		params.put("meetingNo", meetingNo);
		params.put("attachmentNo", attachmentNo);
		sqlSession.insert("meeting.connect", params);
	}

	// 이미지 찾기
	public long findImage(long meetingNo) {
		return sqlSession.selectOne("meeting.findImage", meetingNo);
	}
	
	//정모 목록 조회 특정 crewNo
	public List<MeetingVO> selectListByCrew(long crewNo) {
	    return sqlSession.selectList("meeting.selectListByCrew", crewNo);
	}
	
	//정모 모임장 위임
	public boolean updateOwner(long meetingNo, long newOwnerNo) {
	    Map<String, Object> params = new HashMap<>();
	    params.put("meetingNo", meetingNo);
	    params.put("newOwnerNo", newOwnerNo);
	    return sqlSession.update("meeting.updateOwner", params) > 0;
	}

}
