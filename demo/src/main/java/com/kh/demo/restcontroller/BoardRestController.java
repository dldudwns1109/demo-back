package com.kh.demo.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.demo.dao.BoardDao;
import com.kh.demo.dto.BoardDto;
import com.kh.demo.error.TargetNotFoundException;
import com.kh.demo.service.BoardService;
import com.kh.demo.vo.BoardVO;

@CrossOrigin
@RestController
@RequestMapping("/api/board")
public class BoardRestController {

	@Autowired
	//필요시 사용
	private BoardDao boardDao;

	@Autowired
	private BoardService boardService;

	//전체 게시글 목록을 반환
	@GetMapping
	public List<BoardVO> getList(String category) {
	    if (category == null || category.equals("전체")) {
	        return boardDao.selectList();
	    }
	    return boardDao.selectListByCategory(category);
	}
	
	//특정 게시글 상세 정보를 반환
	@GetMapping("/{boardNo}")
	public BoardDto detail(@PathVariable Long boardNo) {
		BoardDto boardDto = boardService.get(boardNo);
		if (boardDto == null)
			throw new TargetNotFoundException();
		return boardDto;
	}

	//게시글 등록
	@PostMapping
	public void write(@RequestBody BoardDto boardDto) {
		if (boardDto.getBoardWriter() == 0)
			throw new TargetNotFoundException();
		boardService.insert(boardDto);
	}

	//게시글 수정
	//1.게시글 존재 여부 확인
	//2.작성자 본인인지 확인
	@PutMapping("/{boardNo}")
	public boolean edit(@PathVariable Long boardNo, @RequestBody BoardDto boardDto) {
		BoardDto target = boardService.get(boardNo);
		if (target == null)
			throw new TargetNotFoundException();
		//임시로 작성자 검증 생략 (로그인 기능 X)
//		if (target.getBoardWriter() != boardDto.getBoardWriter())
//			throw new TargetNotFoundException();
		boardDto.setBoardNo(boardNo);
		return boardService.edit(boardDto);
	}

	// 게시글 삭제
	//1.게시글 존재 여부 확인
	//2.작성자 본인인지 확인 (작성자만 삭제 가능)
	@DeleteMapping("/{boardNo}")
	public boolean delete(@PathVariable Long boardNo) {
		BoardDto target = boardService.get(boardNo);
		if (target == null)
			throw new TargetNotFoundException();
		//임시로 작성자 검증 생략 (로그인 기능 X)
//		if (target.getBoardWriter() != boardDto.getBoardWriter())
//			throw new TargetNotFoundException();
		return boardService.delete(boardNo);
	}

}









