package com.kh.demo.member;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.kh.demo.dao.MemberDao;
import com.kh.demo.dto.MemberDto;
import com.kh.demo.service.MemberService;

import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
public class MemberTest {
	
	@Autowired
	private MemberService memberService;
	
	@Autowired
	private MemberDao memberDao;
	
	@Test
	public void test() throws MessagingException, IOException {
		// 회원가입 테스트
//		memberService.signup(
//			MemberDto.builder()
//			.memberNo(memberDao.sequence())
//			.memberId("testuser1")
//			.memberPw("fdskjbfdjksbjkfdsbkjdskjbk")
//			.memberNickname("테스트유저1")
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
	}
}
