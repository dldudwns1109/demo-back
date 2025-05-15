package com.kh.demo.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.demo.dto.CrewMemberDto;
import com.kh.demo.vo.CrewMemberVO;

@Repository
public class CrewMemberDao {

	@Autowired
	private SqlSession sqlSession;
	
	// 모임 가입 처리
	public long sequence() {
	    return sqlSession.selectOne("crewmember.sequence");
	}
    public void join(CrewMemberDto crewMemberDto) {
        sqlSession.insert("crewmember.join", crewMemberDto);
    }
    
    public long selectMemberCnt(long crewNo) {
    	return sqlSession.selectOne("crewmember.selectMemberCnt", crewNo);
    }

    // 모임 탈퇴 처리
    public boolean leave(CrewMemberDto crewMemberDto) {
        return sqlSession.delete("crewmember.leave", crewMemberDto) > 0;
    }

    // 해당 사용자가 모임장인지 확인
//    public boolean isLeader(CrewMemberDto crewMemberDto) {
//        return sqlSession.selectOne("crewmember.isLeader", crewMemberDto);
//    }
    // 모임장 여부 확인
    public boolean isLeader(CrewMemberDto crewMemberDto) {
        Boolean result = sqlSession.selectOne("crewmember.isLeader", crewMemberDto);
        return result != null && result;
    }



    // 모임 가입 여부 확인
    public boolean isMember(CrewMemberDto crewMemberDto) {
        return sqlSession.selectOne("crewmember.isMember", crewMemberDto);
    }

    // 특정 모임의 전체 회원 목록
    public List<CrewMemberVO> selectListByCrew(Long crewNo) {
        return sqlSession.selectList("crewmember.selectListByCrew", crewNo);
    }

    // 모임장만 가능한 회원 강퇴
    public boolean kick(CrewMemberDto crewMemberDto) {
        return sqlSession.delete("crewmember.kick", crewMemberDto) > 0;
    }
    
    public List<Long> findCreated(long memberNo) {
    	return sqlSession.selectList("crewmember.findCreated", memberNo);
    }
    
    public List<Long> findJoined(long memberNo) {
    	return sqlSession.selectList("crewmember.findJoined", memberNo);
    }
    
    public List<Long> findLiked(long memberNo) {
    	return sqlSession.selectList("crewmember.findLiked", memberNo);
    }
    public long getMemberCount(long crewNo) {
        return sqlSession.selectOne("crewmember.getMemberCount", crewNo);
    }
    
    //모임장 조회
    public long findLeaderMemberNo(long crewNo) {
    	return sqlSession.selectOne("crewmember.findLeaderMemberNo", crewNo);
    }
    
}
