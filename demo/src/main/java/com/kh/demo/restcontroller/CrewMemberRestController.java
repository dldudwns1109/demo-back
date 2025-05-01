package com.kh.demo.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.demo.dao.CrewMemberDao;
import com.kh.demo.dao.MemberDao;
import com.kh.demo.dto.CrewMemberDto;
import com.kh.demo.error.TargetNotFoundException;
import com.kh.demo.service.TokenService;
import com.kh.demo.vo.CrewMemberVO;

@CrossOrigin
@RestController
@RequestMapping("/api/crew")
public class CrewMemberRestController {

	@Autowired
	private CrewMemberDao crewMemberDao;
	@Autowired
	private MemberDao memberDao;
	@Autowired
	private TokenService tokenService;
	
	//모임 가입 처리
	@PostMapping("/{crewNo}/join")
	public void join(@PathVariable Long crewNo,
					@RequestHeader("Authorizaion") String token) {
		long memberNo = tokenService.parse(token);
		
		CrewMemberDto crewMemberDto = CrewMemberDto.builder()
					.crewNo(crewNo)
					.memberNo(memberNo)
					.status("가입")
					.leader(false)
				.build();
		
		crewMemberDao.join(crewMemberDto);
	}
	
	//모임 탈퇴 처리
	@DeleteMapping("/{crewNo}/leave")
	public boolean leave(@PathVariable Long crewNo,
						@RequestHeader("Authorization") String token) {
		long memberNo = tokenService.parse(token);
		
		CrewMemberDto crewMemberDto = CrewMemberDto.builder()
					.crewNo(crewNo)
					.memberNo(memberNo)
				.build();
		
		return crewMemberDao.leave(crewMemberDto);
	}
	
	//모임장 여부 확인
	@GetMapping("/{crewNo}/leader")
	public boolean leader(@PathVariable Long crewNo,
						@RequestHeader("Authorization") String token) {
		long memberNo = tokenService.parse(token);
		
		CrewMemberDto crewMemberDto = CrewMemberDto.builder()
					.crewNo(crewNo)
					.memberNo(memberNo)
				.build();
		
		return crewMemberDao.isLeader(crewMemberDto);
	}
	
	//가입 여부 확인
	@GetMapping("/{crewNo}/member")
	public boolean member(@PathVariable Long crewNo,
						@RequestHeader("Authorization") String token) {
		long memberNo = tokenService.parse(token);
		
		CrewMemberDto crewMemberDto = CrewMemberDto.builder()
					.crewNo(crewNo)
					.memberNo(memberNo)
				.build();
		
		return crewMemberDao.isMember(crewMemberDto);
	}
	
	//모임 전체 회원 조회
	@GetMapping("/{crewNo}/members")
	public List<CrewMemberVO> selectListByCrew(@PathVariable Long crewNo) {
		return crewMemberDao.selectListByCrew(crewNo);
	}
	
	//모임장 회원 강퇴
	@DeleteMapping("/{crewNo}/kick/{memberNo}")
	public boolean kick(@PathVariable Long crewNo,
						@PathVariable Long memberNo,
						@RequestHeader("Authorization") String token) {
		long loginMemberNo = tokenService.parse(token);
		
		//모임장만 강퇴 가능
		CrewMemberDto checkDto = CrewMemberDto.builder()
					.crewNo(crewNo)
					.memberNo(loginMemberNo)
				.build();
		
		if(!crewMemberDao.isLeader(checkDto)) throw new TargetNotFoundException();
		
		CrewMemberDto kickDto = CrewMemberDto.builder()
					.crewNo(crewNo)
					.memberNo(memberNo)
				.build();
		
		return crewMemberDao.kick(kickDto);
	}
	
	
}












