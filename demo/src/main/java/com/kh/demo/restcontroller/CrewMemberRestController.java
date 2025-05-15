package com.kh.demo.restcontroller;

import java.sql.Timestamp;
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

import com.kh.demo.dao.ChatDao;
import com.kh.demo.dao.CrewMemberDao;
import com.kh.demo.dao.MemberDao;
import com.kh.demo.dto.ChatDto;
import com.kh.demo.dto.CrewJoinRequestDto;
import com.kh.demo.dto.CrewMemberDto;
import com.kh.demo.error.TargetNotFoundException;
import com.kh.demo.service.BoardService;
import com.kh.demo.service.TokenService;
import com.kh.demo.vo.CrewMemberVO;

@CrossOrigin
@RestController
@RequestMapping("/api/crewmember")
public class CrewMemberRestController {

	@Autowired
	private CrewMemberDao crewMemberDao;
	@Autowired
	private MemberDao memberDao;
	@Autowired
	private TokenService tokenService;
	@Autowired
	private BoardService boardService;
	@Autowired
	private ChatDao chatDao;

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
	public void join(
	    @PathVariable Long crewNo,
	    @RequestHeader("Authorization") String authorizationHeader,
	    @RequestBody CrewJoinRequestDto requestDto
	) {
	    // 1. 사용자 정보 추출
	    long memberNo = tokenService.parseBearerToken(authorizationHeader);
	    String chatContent = requestDto.getChatContent();

	    // 2. crew_member 테이블에 가입 등록
	    long crewMemberNo = crewMemberDao.sequence();
	    CrewMemberDto crewMemberDto = CrewMemberDto.builder()
	        .crewMemberNo(crewMemberNo)
	        .crewNo(crewNo)
	        .memberNo(memberNo)
	        .leader("N")
	        .joinDate(LocalDate.now().toString())
	        .build();
	    crewMemberDao.join(crewMemberDto);

	    // 3. 사용자 닉네임 조회
	    String memberName = memberDao.findNicknameById(memberNo); // ← DAO에 추가 필요

	    // 4. 모임 채팅방 번호 조회
	    Long crewChatRoomNo = chatDao.findRoomByCrewNo(crewNo);

	    // 5. 모임 채팅방에 시스템 메시지 전송
	    if (crewChatRoomNo != null) {
	    	chatDao.insert(ChatDto.builder()
		    	.chatRoomNo(crewChatRoomNo)
		    	.chatCrewNo(crewNo)
		    	.chatType("SYSTEM")
		    	.chatContent(memberName + "님이 들어오셨습니다!")
		    	.chatTime(new Timestamp(System.currentTimeMillis()))
		    	.chatSender(memberNo)
		    	.build());
	    }

	    // 6. 모임장 번호 조회
	    Long leaderNo = crewMemberDao.findLeaderMemberNo(crewNo);

	    if (leaderNo != null && leaderNo != memberNo) {
	        // 7. 1:1 채팅방 존재 여부 확인
	        Long dmRoomNo = chatDao.findDmRoom(memberNo, leaderNo);

	        // 8. 없으면 채팅방 새로 생성
	        if (dmRoomNo == null) {
	            dmRoomNo = chatDao.roomSequence();
	        }

	        // 9. 모임장에게 DM 전송
	        chatDao.insert(ChatDto.builder()
	            .chatRoomNo(dmRoomNo)
	            .chatType("DM")
	            .chatContent(memberName + "님이 모임에 가입했습니다!\n가입인사: " + chatContent)
	            .chatTime(new Timestamp(System.currentTimeMillis()))
	            .chatSender(memberNo)
	            .chatReceiver(leaderNo)
	            .build()
	        );
	    }
	}

	// 모임 탈퇴 처리
//	@DeleteMapping("/{crewNo}/leave")
//	public boolean leave(@PathVariable Long crewNo,
//	                     @RequestHeader("Authorization") String authorizationHeader) {
//
//	    long memberNo = tokenService.parseBearerToken(authorizationHeader);
//
//	    CrewMemberDto crewMemberDto = CrewMemberDto.builder()
//	            .crewNo(crewNo)
//	            .memberNo(memberNo)
//	            .build();
//
//	    return crewMemberDao.leave(crewMemberDto);
//	}
	
	// 모임 탈퇴 처리 + 해당 모임에서 작성한 게시글 삭제
	@DeleteMapping("/{crewNo}/leave")
	public boolean leave(@PathVariable Long crewNo,
	                     @RequestHeader("Authorization") String authorizationHeader) {

	    long memberNo = tokenService.parseBearerToken(authorizationHeader);

	    CrewMemberDto crewMemberDto = CrewMemberDto.builder()
	            .crewNo(crewNo)
	            .memberNo(memberNo)
	            .build();

	    // 모임 탈퇴 처리
	    boolean isLeft = crewMemberDao.leave(crewMemberDto);

	    // 해당 모임에서 작성한 게시글 삭제
	    if (isLeft) {
	        boardService.deleteByCrewAndWriter(crewNo, memberNo);
	    }

	    return isLeft; 
	}



	// 모임장 여부 확인
//	@GetMapping("/{crewNo}/leader")
//	public boolean leader(@PathVariable Long crewNo,
//						@RequestHeader("Authorization") String token) {
//		long memberNo = tokenService.parse(token);
//		
//		CrewMemberDto crewMemberDto = CrewMemberDto.builder()
//					.crewNo(crewNo)
//					.memberNo(memberNo)
//				.build();
//		
//		return crewMemberDao.isLeader(crewMemberDto);
//	}
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

//	모임 전체 회원 조회
	@GetMapping("/{crewNo}/members")
	public List<CrewMemberVO> selectListByCrew(@PathVariable Long crewNo) {
		return crewMemberDao.selectListByCrew(crewNo);
	}
//	@GetMapping("/{crewNo}/members")
//	public List<CrewMemberVO> selectListByCrew(@PathVariable Long crewNo) {
//	    List<CrewMemberVO> members = crewMemberDao.selectListByCrew(crewNo);
//	    System.out.println("Fetched Members Data: " + members);
//	    return members;
//	}

	// 모임장 회원 강퇴
	@DeleteMapping("/{crewNo}/kick/{memberNo}")
	public boolean kick(@PathVariable Long crewNo, @PathVariable Long memberNo,
			@RequestHeader("Authorization") String token) {
		long loginMemberNo = tokenService.parse(token);

		// 모임장만 강퇴 가능
		CrewMemberDto checkDto = CrewMemberDto.builder().crewNo(crewNo).memberNo(loginMemberNo).build();

		if (!crewMemberDao.isLeader(checkDto))
			throw new TargetNotFoundException();

		CrewMemberDto kickDto = CrewMemberDto.builder().crewNo(crewNo).memberNo(memberNo).build();

		return crewMemberDao.kick(kickDto);
	}

}
