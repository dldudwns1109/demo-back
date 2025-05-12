package com.kh.demo.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.demo.dao.BoardDao;
import com.kh.demo.dto.BoardDto;
import com.kh.demo.vo.BoardVO;

@Service
public class BoardService {

	@Autowired
	private BoardDao boardDao;

	// 전체 게시글 조회 or 카테고리별 조회
	public List<BoardVO> getList(String category) {
		if (category == null || category.equals("전체")) {
			return boardDao.selectList();
		}
		return boardDao.selectListByCategory(category);
	}

	// crewNo + 카테고리로 조회 (카테고리 없으면 전체)
	public List<BoardVO> getListByCrewNo(Long crewNo, String category) {
	    if (category == null || category.equals("전체")) {
	        return boardDao.selectListByCrewNo(crewNo);
	    }
	    return boardDao.selectListByCrewNoAndCategory(crewNo, category);
	}

	// 상세조회
	public BoardDto get(Long boardNo) {
		return boardDao.selectOne(boardNo);
	}

	// 등록
	public void insert(BoardDto boardDto) {
		boardDao.insert(boardDto);
	}

	// 수정
	public boolean edit(BoardDto boardDto) {
		boardDto.setBoardWriteTime(Timestamp.from(Instant.now()));
		return boardDao.update(boardDto);
	}

	// 삭제
	public boolean delete(Long boardNo) {
		return boardDao.delete(boardNo);
	}
	
	 // crew_no가 NULL인 게시글 목록 (카테고리 필터링 포함)
    public List<BoardVO> getJoinBoardList(String category) {
        if (category == null || category.equals("전체")) {
            return boardDao.selectJoinBoardList();
        }
        return boardDao.selectJoinBoardListByCategory(category);
    }
    
}
