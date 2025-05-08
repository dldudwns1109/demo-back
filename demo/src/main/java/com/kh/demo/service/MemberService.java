package com.kh.demo.service;

import java.io.IOException;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.kh.demo.dao.MemberDao;
import com.kh.demo.dto.MemberDto;
import com.kh.demo.dto.MemberLikeDto;
import com.kh.demo.util.RandomGenerator;
import com.kh.demo.vo.MemberCheckVO;
import com.kh.demo.vo.MemberVO;

import jakarta.mail.MessagingException;

@Service
public class MemberService {
	
	@Autowired
	private MemberDao memberDao;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private JavaMailSender sender;
	
	@Autowired
	private RandomGenerator randomGenerator;
	
	public long signup(MemberVO memberVO) {
		ModelMapper mapper = new ModelMapper();
		MemberDto memberDto = mapper.map(memberVO, MemberDto.class);
		memberDto.setMemberNo(memberDao.sequence());
		memberDto.setMemberPw(passwordEncoder.encode(memberDto.getMemberPw()));
		memberDao.insert(memberDto);
		for (String memberLike : memberVO.getMemberLike()) {
			memberDao.insertLike(
				MemberLikeDto.builder()
					.memberNo(memberDto.getMemberNo())
					.memberLike(memberLike)
				.build()
			);			
		}
		
		return memberDto.getMemberNo();
	}
	
	public MemberDto signin(MemberDto memberDto) {
		MemberDto findDto = memberDao.findMember(memberDto.getMemberId());
		if (findDto == null) return null;
		
		boolean isValid = passwordEncoder.matches(memberDto.getMemberPw(), findDto.getMemberPw());
		return isValid ? findDto : null;
	}
	
	public void sendTempPassword(String memberEmail) throws MessagingException, IOException {
		MemberDto findDto = memberDao.findMemberByEmail(memberEmail);
		if (findDto == null) throw new RuntimeException();
		
		SimpleMailMessage message = new SimpleMailMessage();
		
		message.setTo(memberEmail);
		message.setSubject("임시 비밀번호 발급 안내");
		
		String tempPassword = randomGenerator.randomNumber(6);
		message.setText("임시 비밀번호 [" + tempPassword + "]");
		sender.send(message);
		
		findDto.setMemberPw(passwordEncoder.encode(tempPassword));
		memberDao.updatePassword(findDto);
	}
	
	public boolean checkPassword(MemberCheckVO memberCheckVO) {
		MemberDto findDto = memberDao.findMemberByNo(memberCheckVO.getMemberNo());
		if (findDto == null) return false;
		
		return passwordEncoder.matches(memberCheckVO.getMemberPw(), findDto.getMemberPw());
	}
}
