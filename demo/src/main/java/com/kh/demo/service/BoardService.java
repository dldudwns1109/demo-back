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

	public List<BoardVO> getList(String category) {
		if (category == null || category.equals("전체")) {
			return boardDao.selectList();
		}
		return boardDao.selectListByCategory(category);
	}
	
	public BoardDto get(Long boardNo) {
		return boardDao.selectOne(boardNo);
	}


	public void insert(BoardDto boardDto) {
		boardDao.insert(boardDto);
	}

	//수정 메소드 (작성시간 현재시간으로 반영)
	public boolean edit(BoardDto boardDto) {
		//수정시 작성시간을 현재시간으로 업데이트
		boardDto.setBoardWriteTime(Timestamp.from(Instant.now()));
        return boardDao.update(boardDto);
	}

	public boolean delete(Long boardNo) {
		return boardDao.delete(boardNo);
	}
}











