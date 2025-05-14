package com.kh.demo.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.demo.dto.BoardDto;
import com.kh.demo.vo.BoardVO;

@Repository
public class BoardDao {

	@Autowired
	private SqlSession sqlSession;

	// 게시글 목록 조회
	public List<BoardVO> selectList() {
		return sqlSession.selectList("board.selectList");
	}

	// 카테고리별 게시글 목록을 조회하는 메서드입니다
	public List<BoardVO> selectListByCategory(String boardCategory) {
		return sqlSession.selectList("board.selectListByCategory", boardCategory);
	}

	// 특정 게시글 조회
	public BoardDto selectOne(Long boardNo) {
		return sqlSession.selectOne("board.selectOne", boardNo);
	}

	// 게시글 등록
	public void insert(BoardDto boardDto) {
		sqlSession.insert("board.insert", boardDto);
	}

	// 게시글 수정
	public boolean update(BoardDto boardDto) {
		return sqlSession.update("board.update", boardDto) > 0;
	}

	// 게시글 삭제
	public boolean delete(Long boardNo) {
		return sqlSession.delete("board.delete", boardNo) > 0;
	}

	// 특정 크루 번호의 게시글 전체 조회
	public List<BoardVO> selectListByCrewNo(Long crewNo) {
		return sqlSession.selectList("board.selectListByCrewNo", crewNo);
	}

	// 특정 크루 번호 + 카테고리별 게시글 조회
	public List<BoardVO> selectListByCrewNoAndCategory(Long crewNo, String boardCategory) {
		Map<String, Object> param = new HashMap<>();
		param.put("crewNo", crewNo);
		param.put("category", boardCategory);
		return sqlSession.selectList("board.selectListByCrewNoAndCategory", param);
	}

	// crew_no가 NULL인 게시글 목록 조회
	public List<BoardVO> selectJoinBoardList() {
		return sqlSession.selectList("board.selectJoinBoardList");
	}

	// crew_no가 NULL인 게시글 목록 중 카테고리 필터링
	public List<BoardVO> selectJoinBoardListByCategory(String category) {
		Map<String, Object> param = new HashMap<>();
		param.put("category", category);
		return sqlSession.selectList("board.selectJoinBoardListByCategory", param);
	}

	// 특정 회원이 특정 모임에서 리더인지 여부 확인
	public boolean isLeader(Long memberNo, Long crewNo) {
		Map<String, Long> param = new HashMap<>();
		param.put("memberNo", memberNo);
		param.put("crewNo", crewNo);
		return sqlSession.selectOne("board.isLeader", param);
	}

	// 특정 회원의 모든 게시글 삭제
	public void deleteByWriter(Long memberNo) {
		sqlSession.delete("board.deleteByWriter", memberNo);
	}

	// 특정 모임에서 특정 회원이 작성한 게시글 삭제
	public void deleteByCrewAndWriter(Long crewNo, Long memberNo) {
		Map<String, Long> params = new HashMap<>();
		params.put("crewNo", crewNo);
		params.put("memberNo", memberNo);
		sqlSession.delete("board.deleteByCrewAndWriter", params);
	}

}
