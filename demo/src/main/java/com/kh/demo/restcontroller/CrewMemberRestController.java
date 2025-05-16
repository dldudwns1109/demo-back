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
import com.kh.demo.dao.MeetingDao;
import com.kh.demo.dao.MeetingMemberDao;
import com.kh.demo.dto.CrewJoinRequestDto;
import com.kh.demo.dto.CrewMemberDto;
import com.kh.demo.dto.MeetingMemberDto;
import com.kh.demo.service.BoardService;
import com.kh.demo.service.ChatService;
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
	private MeetingMemberDao meetingMemberDao;
	@Autowired
	private MeetingDao meetingDao;

	// 모임 가입 처리
	@Transactional
	@PostMapping("/{crewNo}/join")
	public void join(@PathVariable Long crewNo, @RequestHeader("Authorization") String authorizationHeader,
			@RequestBody CrewJoinRequestDto requestDto) {
		long memberNo = tokenService.parseBearerToken(authorizationHeader);
		String chatContent = requestDto.getChatContent();
		
	    long crewMemberNo = crewMemberDao.sequence();
	    CrewMemberDto crewMemberDto = CrewMemberDto.builder()
	            .crewMemberNo(crewMemberNo)
	            .crewNo(crewNo)
	            .memberNo(memberNo)
	            .leader("N")
	            .joinDate(LocalDate.now().toString())
	            .build();
	    crewMemberDao.join(crewMemberDto);

	    chatService.sendJoinSystemMessage(crewNo, memberNo); // 시스템 메세지
	    chatService.sendJoinDmMessage(crewNo, memberNo, chatContent); // 가입인사 DM
	}
	//모임 탈퇴 처리
	@DeleteMapping("/{crewNo}/leave")
	public boolean leave(@PathVariable Long crewNo,
	                     @RequestHeader("Authorization") String authorizationHeader) {
	    long memberNo = tokenService.parseBearerToken(authorizationHeader);

		CrewMemberDto crewMemberDto = CrewMemberDto.builder().crewNo(crewNo).memberNo(memberNo).build();

	    boolean isLeft = crewMemberDao.leave(crewMemberDto);

	    if (isLeft) {
	        boardService.deleteByCrewAndWriter(crewNo, memberNo);

	        // 💬 탈퇴 메시지 전송
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

	//모임 전체 회원 조회
	@GetMapping("/{crewNo}/members")
	public List<CrewMemberVO> selectListByCrew(@PathVariable Long crewNo) {
		return crewMemberDao.selectListByCrew(crewNo);
	}

	// 모임장 회원 강퇴
	@DeleteMapping("/{crewNo}/kick/{memberNo}")
	public boolean kick(@PathVariable Long crewNo,
	                    @PathVariable Long memberNo,
	                    @RequestHeader("Authorization") String token) {

	    long loginMemberNo = tokenService.parseBearerToken(token);

	    // 모임장만 강퇴 가능
	    CrewMemberDto leaderCheck = CrewMemberDto.builder()
	            .crewNo(crewNo)
	            .memberNo(loginMemberNo)
	            .build();
	    if (!crewMemberDao.isLeader(leaderCheck)) return false;

	    // 강퇴 대상이 참여한 정모 목록 조회 (해당 모임 내)
	    List<Long> meetingNoList = meetingMemberDao.findMeetingNoListByCrewNoAndMemberNo(crewNo, memberNo);

	    for (Long meetingNo : meetingNoList) {
	        if (meetingMemberDao.isLeader(meetingNo, memberNo)) {
	        	// 정모장일 경우
	        	List<MeetingMemberDto> others = meetingMemberDao.findOthers(meetingNo, memberNo);

	        	if (others.isEmpty()) {
	        	    // 혼자였다면 정모 삭제
	        	    meetingDao.delete(meetingNo);
	        	} else {
	        	    // 위임 대상 선정
	        	    MeetingMemberDto nextLeader = others.get(0);

	        	    // ✅ 1. meeting_member 테이블에서 리더 변경
	        	    meetingMemberDao.updateLeaderStatus(meetingNo, nextLeader.getMemberNo());

	        	    // ✅ 2. meeting 테이블의 ownerNo도 같이 변경
	        	    meetingDao.updateOwner(meetingNo, nextLeader.getMemberNo());

	        	    // ✅ 3. 기존 리더 삭제
	        	    meetingMemberDao.delete(meetingNo, memberNo);
	        	}
	        }
	    }

	    // 모임에서 최종 강퇴
	    CrewMemberDto kickDto = CrewMemberDto.builder()
	            .crewNo(crewNo)
	            .memberNo(memberNo)
	            .build();

	    boolean isLeft = crewMemberDao.kick(kickDto);
	    
	    if (isLeft) {
	        //강퇴 메시지 전송
	        chatService.sendKickSystemMessage(crewNo, memberNo);
	    }
	    return isLeft;
	}


}
