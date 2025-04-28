package com.kh.demo.member;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.kh.demo.dao.MemberDao;
import com.kh.demo.dao.TokenDao;
import com.kh.demo.dto.MemberDto;
import com.kh.demo.dto.TokenDto;
import com.kh.demo.service.MemberService;
import com.kh.demo.service.TokenService;

import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
public class MemberTest {

	@Autowired
	private MemberService memberService;

	@Autowired
	private MemberDao memberDao;

	@Autowired
	private TokenDao tokenDao;

	@Autowired
	private TokenService tokenService;

	@Test
	public void test() throws MessagingException, IOException {
		// 회원가입 테스트
//		memberService.signup(
//			MemberDto.builder()
//			.memberNo(memberDao.sequence())
//			.memberId("testuser11")
//			.memberPw("Testuser11!")
//			.memberNickname("홍길동11")ㄴ
//			.memberEmail("yno1109@naver.com")
//			.memberLocation("경기도 성남시")
//			.memberSchool("용인대학교")
//			.memberGender('M')
//			.memberBirth("2000-11-09")
//			.memberLike("스포츠")
//			.memberMbti("ISTJ")
//			.build()
//		);

		// 로그인 테스트
//		log.debug("member = {}", 
//				memberService.signin(
//					MemberDto.builder()
//						.memberId("testuser2")
//						.memberPw("fdskjbfdjksbjkfdsbkjdskjbk")
//					.build()
//				));

		// 아이디 찾기 테스트
//		log.debug("memberId = {}", memberDao.findId("demo@mail.com"));

		// 비밀번호 찾기 테스트
//		memberService.sendTempPassword("yno1109@naver.com");

		// 로그아웃 테스트
//		String memberId = "testuser1";
//
//	    // Bearer 없이 순수 accessToken만!
//	    String accessToken = tokenService.generateAccessToken(memberId);
//
//	    // 파싱
//	    String extractedMemberId = tokenService.parse(accessToken);
//
//	    // 로그아웃 처리
//	    tokenDao.clean(extractedMemberId);
//
//	    TokenDto tokenDto = TokenDto.builder()
//	        .tokenTarget(extractedMemberId)
//	        .build();
//
//	    TokenDto findToken = tokenDao.find(tokenDto);
//
//	    System.out.println("findToken = " + findToken);
	}
}
