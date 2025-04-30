package com.kh.demo.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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
    public CrewVO selectOne(Long crewNo) {
        return sqlSession.selectOne("crew.selectOne", crewNo);
    }

    // 모임 등록
    public void insert(CrewDto crewDto) {
        sqlSession.insert("crew.insert", crewDto);
    }

    // 모임 수정
    public boolean update(CrewDto crewDto) {
        return sqlSession.update("crew.update", crewDto) > 0;
    }

    // 모임 삭제
    public boolean delete(Long crewNo) {
        return sqlSession.delete("crew.delete", crewNo) > 0;
    }
}
