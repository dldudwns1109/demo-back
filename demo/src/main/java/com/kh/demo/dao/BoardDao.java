package com.kh.demo.dao;

import java.util.List;

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
	
}














