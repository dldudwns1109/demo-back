package com.kh.demo.restcontroller;

import java.io.IOException;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.demo.dao.MemberDao;
import com.kh.demo.dto.MemberDto;
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
	public MemberSigninResponseVO refresh(@RequestBody String refreshToken) {
		String memberId = tokenService.parseBearerToken(refreshToken);
		
		if (!tokenService.checkBearerToken(memberId, refreshToken)) 
			throw new RuntimeException();
		
		return MemberSigninResponseVO.builder()
									.memberId(memberId)
									.accessToken(tokenService.generateAccessToken(memberId))
									.refreshToken(tokenService.generateRefreshToken(memberId))
									.build();
	}
	
	@GetMapping("/memberEmail/{memberEmail}")
	public String findId(@PathVariable String memberEmail) {
		return memberDao.findId(memberEmail);
	}
	
	@PatchMapping("/updatePw")
	public void findPw(@PathVariable String memberEmail) throws MessagingException, IOException {
		memberService.sendTempPassword(memberEmail);
	}
}
