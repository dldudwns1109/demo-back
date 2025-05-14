package com.kh.demo.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.demo.dto.MeetingMemberDto;
import com.kh.demo.vo.MeetingMemberVO;

@Repository
public class MeetingMemberDao {

	@Autowired
	private SqlSession sqlSession;

	// 시퀀스
	public long sequence() {
		return sqlSession.selectOne("meetingMember.sequence");
	}

	// 정모 참여
	public void insert(MeetingMemberDto meetingMemberDto) {
		long meetingMemberNo = sequence(); // 시퀀스 발급
		meetingMemberDto.setMeetingMemberNo(meetingMemberNo); // DTO에 설정
		sqlSession.insert("meetingMember.insert", meetingMemberDto); // 실제 INSERT
	}
	
	// 정모 나가기
	public boolean delete(long meetingNo, long memberNo) {
	    Map<String, Object> param = Map.of(
	        "meetingNo", meetingNo,
	        "memberNo", memberNo
	    );
	    return sqlSession.delete("meetingMember.delete", param) > 0;
	}

	// 정모 참여자 목록 조회 (모임장 포함)
	public List<MeetingMemberVO> selectListByMeetingNo(Long meetingNo) {
	    return sqlSession.selectList("meetingMember.selectListByMeetingNo", meetingNo); // ✅ ID 이름 수정
	}

	public boolean isJoined(long meetingNo, long memberNo) {
		int count = sqlSession.selectOne("meetingMember.isJoined",
				Map.of("meetingNo", meetingNo, "memberNo", memberNo));
		return count > 0;
	}
	
	//정모 모임장 위임
	public boolean updateLeaderStatus(long meetingNo, long newOwnerNo) {
	    Map<String, Object> params = new HashMap<>();
	    params.put("meetingNo", meetingNo);
	    params.put("newOwnerNo", newOwnerNo);
	    return sqlSession.update("meetingMember.updateLeaderStatus", params) > 0;
	}

}