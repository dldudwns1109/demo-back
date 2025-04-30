package com.kh.demo.restcontroller;

import java.io.IOException;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.demo.dao.MemberDao;
import com.kh.demo.dao.TokenDao;
import com.kh.demo.dto.MemberDto;
import com.kh.demo.error.TargetNotFoundException;
import com.kh.demo.service.MemberService;
import com.kh.demo.service.TokenService;
import com.kh.demo.vo.MemberSigninRequestVO;
import com.kh.demo.vo.MemberSigninResponseVO;

import jakarta.mail.MessagingException;

@CrossOrigin
@RestController
@RequestMapping("/api/member")
public class MemberRestController {
	
	@Autowired
	private MemberService memberService;
	
	@Autowired
	private TokenService tokenService;
	
	@Autowired
	private MemberDao memberDao;
	
	@Autowired
	private TokenDao tokenDao;
	
	@PostMapping("/signup")
	public void signup(@RequestBody MemberDto memberDto) {
		memberService.signup(memberDto);
	}
	
	@PostMapping("/signin")
	public MemberSigninResponseVO signin(@RequestBody MemberSigninRequestVO requestVO) {
		ModelMapper mapper = new ModelMapper();
		MemberDto memberDto = mapper.map(requestVO, MemberDto.class);
		
		MemberDto findDto = memberService.signin(memberDto);
		if (findDto == null) throw new RuntimeException();
		
		return MemberSigninResponseVO.builder()
					.memberId(findDto.getMemberId())
					.accessToken(tokenService.generateAccessToken(findDto))
					.refreshToken(tokenService.generateRefreshToken(findDto))
				.build();
	}
	
	@PostMapping("/refresh")
	public MemberSigninResponseVO refresh(@RequestHeader("Authorization") String refreshToken) {
		long memberNo = tokenService.parseBearerToken(refreshToken);
		
		if (!tokenService.checkBearerToken(memberNo, refreshToken)) 
			throw new RuntimeException();
		
		return MemberSigninResponseVO.builder()
									.memberId(memberDao.findMemberByNo(memberNo).getMemberId())
									.accessToken(tokenService.generateAccessToken(memberNo))
									.refreshToken(tokenService.generateRefreshToken(memberNo))
									.build();
	}
	
	@PostMapping("/signout")
	 public void logout(@RequestHeader("Authorization") String accessToken) {
		long memberNo = tokenService.parse(accessToken);
	    tokenDao.clean(memberNo);
	}
	
	@GetMapping("/memberEmail/{memberEmail}")
	public String findId(@PathVariable String memberEmail) {
		return memberDao.findId(memberEmail);
	}
	
	@PatchMapping("/updatePw")
	public void findPw(@RequestBody String memberEmail) throws MessagingException, IOException {
		memberService.sendTempPassword(memberEmail);
	}
	
	@PatchMapping("/{memberId}")
	public void edit(@PathVariable String memberId, 
						@RequestBody MemberDto memberDto) {
		memberDto.setMemberId(memberId);
		
	    MemberDto findDto = memberDao.findMember(memberId);
	    if (findDto == null) throw new RuntimeException("대상 회원이 없습니다");

	    memberDto.setMemberNo(findDto.getMemberNo());

	    memberDao.update(memberDto);
	}
	
	@DeleteMapping("/{memberId}")
	public void deleteMember(@PathVariable String memberId) {
	    MemberDto findDto = memberDao.findMember(memberId);
	    if (findDto == null) throw new RuntimeException("회원이 존재하지 않습니다");

	    memberDao.deleteMember(findDto.getMemberNo());
	}
	
	@GetMapping("/mypage/{memberId}")
    public MemberDto MyPage(@PathVariable String memberId) {
        MemberDto memberDto = memberDao.findMember(memberId);
        if (memberDto == null) 
            throw new TargetNotFoundException();
        return memberDto;
    }
	
}
