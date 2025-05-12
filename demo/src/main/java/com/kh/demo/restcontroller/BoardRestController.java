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

import com.kh.demo.dto.BoardDto;
import com.kh.demo.dto.MemberDto;
import com.kh.demo.error.TargetNotFoundException;
import com.kh.demo.service.BoardService;
import com.kh.demo.service.MemberService;
import com.kh.demo.vo.BoardVO;

@CrossOrigin
@RestController
@RequestMapping("/api/board")
public class BoardRestController {

	@Autowired
	private BoardService boardService;
	@Autowired
	private MemberService memberService;

	// 전체 또는 카테고리별 게시글 목록
	@GetMapping
	public List<BoardVO> getList(@RequestParam(required = false) String category) {
		return boardService.getList(category);
	}

	// 특정 게시글 상세 정보
	@GetMapping("/{boardNo}")
	public BoardDto detail(@PathVariable Long boardNo) {
		BoardDto boardDto = boardService.get(boardNo);
		if (boardDto == null)
			throw new TargetNotFoundException();
		return boardDto;
	}

	// 게시글 등록
	@PostMapping
	public void write(@RequestBody BoardDto boardDto) {
		if (boardDto.getBoardWriter() == 0)
			throw new TargetNotFoundException();
		boardService.insert(boardDto);
	}

	// 게시글 수정
	@PutMapping("/{boardNo}")
	public boolean edit(@PathVariable Long boardNo, @RequestBody BoardDto boardDto) {
		BoardDto target = boardService.get(boardNo);
		if (target == null)
			throw new TargetNotFoundException();
		boardDto.setBoardNo(boardNo);
		return boardService.edit(boardDto);
	}

	// 게시글 삭제
	@DeleteMapping("/{boardNo}")
	public boolean delete(@PathVariable Long boardNo) {
		BoardDto target = boardService.get(boardNo);
		if (target == null)
			throw new TargetNotFoundException();
		return boardService.delete(boardNo);
	}

	// 특정 crew의 게시글 목록 (카테고리 필터 포함)
	@GetMapping("/crew/{crewNo}")
	public List<BoardVO> getCrewBoardList(@PathVariable Long crewNo, @RequestParam(required = false) String category) {
		return boardService.getListByCrewNo(crewNo, category);
	}

	// crew_no가 NULL인 게시글 목록 (카테고리 필터링 포함)
	@GetMapping("/joinboard")
	public List<BoardVO> getJoinBoardList(@RequestParam(required = false) String category) {
		return boardService.getJoinBoardList(category);
	}

	// 회원 프로필 정보 조회
	@GetMapping("/profile/{memberNo}")
    public MemberDto getMemberProfile(@PathVariable Long memberNo) {
        MemberDto member = memberService.getMemberProfile(memberNo); // 여기에서 memberService로 접근해야 함
        if (member == null) {
            throw new TargetNotFoundException();
        }
        return member;
    }
	
	@GetMapping("/image/{attachmentNo}")
    public BoardVO getProfileImage(@PathVariable Long attachmentNo) {
        BoardVO vo = new BoardVO();
        vo.setBoardWriterProfileUrl(attachmentNo);

        // 이미지 경로 생성
        String profileUrl = attachmentNo != 0 ? "/uploads/" + attachmentNo + ".jpg" : "/images/default-profile.png";
        vo.setBoardWriterProfileUrl(attachmentNo); // attachment_no 그대로 전달

        return vo;
    }

}
