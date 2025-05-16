package com.kh.demo.restcontroller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.demo.dao.CrewMemberDao;
import com.kh.demo.dto.CrewJoinRequestDto;
import com.kh.demo.dto.CrewMemberDto;
import com.kh.demo.error.TargetNotFoundException;
import com.kh.demo.service.BoardService;
import com.kh.demo.service.ChatService;
import com.kh.demo.service.ReplyService;
import com.kh.demo.service.TokenService;
import com.kh.demo.vo.CrewMemberVO;

@CrossOrigin
@RestController
@RequestMapping("/api/crewmember")
public class CrewMemberRestController {

	@Autowired
	private CrewMemberDao crewMemberDao;
	@Autowired
	private TokenService tokenService;
	@Autowired
	private BoardService boardService;
	@Autowired
	private ChatService chatService;
	@Autowired
	private ReplyService replyService;

	// 모임 가입 처리
//	@PostMapping("/{crewNo}/join")
//	public void join(@PathVariable Long crewNo,
//					@RequestHeader("Authorization") String token) {
//		System.out.println("Authorization Header: " + token);
//		
//		long memberNo = tokenService.parse(token.trim());
//		
//		long crewMemberNo = crewMemberDao.sequence(); // ★ 이거 꼭 필요함
//
//	    CrewMemberDto crewMemberDto = CrewMemberDto.builder()
//	            .crewMemberNo(crewMemberNo) // ★ 여기 추가!
//	            .crewNo(crewNo)
//	            .memberNo(memberNo)
//	            .leader("N")
//	            .joinDate(LocalDate.now().toString()) // joinDate도 직접 넣기로 했으므로
//	            .build();
//
//	    crewMemberDao.join(crewMemberDto);
//	}

//	@PostMapping("/{crewNo}/join")
//	public void join(@PathVariable Long crewNo, @RequestHeader("Authorization") String authorizationHeader) {
//
//		System.out.println("Authorization Header: " + authorizationHeader); // 로그 추가
//
//		long memberNo = tokenService.parseBearerToken(authorizationHeader);
//
//		long crewMemberNo = crewMemberDao.sequence();
//
//		CrewMemberDto crewMemberDto = CrewMemberDto.builder().crewMemberNo(crewMemberNo).crewNo(crewNo)
//				.memberNo(memberNo).leader("N").joinDate(LocalDate.now().toString()).build();
//
//		crewMemberDao.join(crewMemberDto);
//	}

	@Transactional
	@PostMapping("/{crewNo}/join")
	public void join(@PathVariable Long crewNo, @RequestHeader("Authorization") String authorizationHeader,
			@RequestBody CrewJoinRequestDto requestDto) {
		long memberNo = tokenService.parseBearerToken(authorizationHeader);
		String chatContent = requestDto.getChatContent();

		long crewMemberNo = crewMemberDao.sequence();
		CrewMemberDto crewMemberDto = CrewMemberDto.builder().crewMemberNo(crewMemberNo).crewNo(crewNo)
				.memberNo(memberNo).leader("N").joinDate(LocalDate.now().toString()).build();
		crewMemberDao.join(crewMemberDto);

		chatService.sendJoinSystemMessage(crewNo, memberNo); // 시스템 메세지
		chatService.sendJoinDmMessage(crewNo, memberNo, chatContent); // 가입인사 DM
	}

//	@DeleteMapping("/{crewNo}/leave")
//	public boolean leave(@PathVariable Long crewNo,
//	                     @RequestHeader("Authorization") String authorizationHeader) {
//	    long memberNo = tokenService.parseBearerToken(authorizationHeader);
//
//		CrewMemberDto crewMemberDto = CrewMemberDto.builder().crewNo(crewNo).memberNo(memberNo).build();
//
//	    boolean isLeft = crewMemberDao.leave(crewMemberDto);
//
//	    if (isLeft) {
//	        boardService.deleteByCrewAndWriter(crewNo, memberNo);
//
//	        chatService.sendLeaveSystemMessage(crewNo, memberNo);
//	    }
//	    return isLeft;
//	}
	@DeleteMapping("/{crewNo}/leave")
	public boolean leave(@PathVariable Long crewNo, @RequestHeader("Authorization") String authorizationHeader) {
		long memberNo = tokenService.parseBearerToken(authorizationHeader);

		CrewMemberDto memberInfo = CrewMemberDto.builder().crewNo(crewNo).memberNo(memberNo).build();

		boolean isLeft = crewMemberDao.leave(memberInfo);

		if (isLeft) {
			replyService.deleteRepliesByCrewAndWriter(crewNo, memberNo);
			boardService.deleteBoardsByCrewAndWriter(crewNo, memberNo);
			chatService.sendLeaveSystemMessage(crewNo, memberNo);
		}

		return isLeft;
	}

	// 모임장 여부 확인
	@GetMapping("/{crewNo}/leader")
	public boolean leader(@PathVariable Long crewNo,
			@RequestHeader(value = "Authorization", required = false) String token) {
		if (token == null || token.trim().isEmpty()) {
			throw new RuntimeException("Authorization token is missing");
		}

		try {
			long memberNo = tokenService.parseBearerToken(token);

			CrewMemberDto crewMemberDto = CrewMemberDto.builder().crewNo(crewNo).memberNo(memberNo).build();

			return crewMemberDao.isLeader(crewMemberDto);

		} catch (Exception e) {
			System.err.println("Error in leader() method: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	// 가입 여부 확인
	@GetMapping("/{crewNo}/member")
	public boolean member(@PathVariable Long crewNo, @RequestHeader("Authorization") String token) {
		long memberNo = tokenService.parseBearerToken(token);

		CrewMemberDto crewMemberDto = CrewMemberDto.builder().crewNo(crewNo).memberNo(memberNo).build();

		return crewMemberDao.isMember(crewMemberDto);
	}

	// 모임 전체 회원 조회
	@GetMapping("/{crewNo}/members")
	public List<CrewMemberVO> selectListByCrew(@PathVariable Long crewNo) {
		return crewMemberDao.selectListByCrew(crewNo);
	}

	// 모임장 회원 강퇴
//	@DeleteMapping("/{crewNo}/kick/{memberNo}")
//	public boolean kick(@PathVariable Long crewNo, @PathVariable Long memberNo,
//			@RequestHeader("Authorization") String authorizationHeader) {
//
//		long loginMemberNo = tokenService.parseBearerToken(authorizationHeader);
//
//		CrewMemberDto checkDto = CrewMemberDto.builder().crewNo(crewNo).memberNo(loginMemberNo).build();
//
//		if (!crewMemberDao.isLeader(checkDto)) {
//			return false;
//		}
//
//		CrewMemberDto kickDto = CrewMemberDto.builder().crewNo(crewNo).memberNo(memberNo).build();
//
//		return crewMemberDao.kick(kickDto);
//	}
	
	@DeleteMapping("/{crewNo}/kick/{memberNo}")
    public boolean kick(@PathVariable Long crewNo, @PathVariable Long memberNo,
                        @RequestHeader("Authorization") String authorizationHeader) {

        long loginMemberNo = tokenService.parseBearerToken(authorizationHeader);

        CrewMemberDto leaderCheck = CrewMemberDto.builder()
                .crewNo(crewNo)
                .memberNo(loginMemberNo)
                .build();

        if (!crewMemberDao.isLeader(leaderCheck)) {
            return false;
        }

        boolean isKicked = crewMemberDao.kick(CrewMemberDto.builder()
                .crewNo(crewNo)
                .memberNo(memberNo)
                .build());

        if (isKicked) {
            replyService.deleteRepliesByCrewAndWriter(crewNo, memberNo);
            boardService.deleteBoardsByCrewAndWriter(crewNo, memberNo);
        }

        return isKicked;
    }

}
