package com.kh.demo.service;

import java.io.IOException;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.demo.dao.MeetingDao;
import com.kh.demo.dao.MeetingMemberDao;
import com.kh.demo.dao.MemberDao;
import com.kh.demo.dto.MeetingMemberDto;
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
	
	@Autowired
	private MeetingMemberDao meetingMemberDao;
	
	@Autowired
	private MeetingDao meetingDao;
	
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

	    System.out.println("=== 비밀번호 확인 로깅 ===");
	    System.out.println("전달된 memberNo : " + memberCheckVO.getMemberNo());
	    System.out.println("입력한 평문 비밀번호 : " + memberCheckVO.getMemberPw());
	    
	    if (findDto == null) {
	        System.out.println("해당 회원을 찾을 수 없습니다.");
	        return false;
	    }

	    System.out.println("DB 저장된 해시 비밀번호 : " + findDto.getMemberPw());

	    boolean match = passwordEncoder.matches(memberCheckVO.getMemberPw(), findDto.getMemberPw());
	    System.out.println("비교 결과 (match): " + match);

	    return match;
	}
	
	public MemberDto getMemberProfile(Long memberNo) {
		return memberDao.findMemberByNo(memberNo);
	}
	
	public boolean changePassword(Long memberNo, String newPw) {
	    MemberDto findDto = memberDao.findMemberByNo(memberNo);
	    if (findDto == null) return false;

	    String encodedPw = passwordEncoder.encode(newPw);
	    findDto.setMemberPw(encodedPw);

	    return memberDao.updatePasswordByNo(findDto); // 새로 만든 쿼리 사용
	}
	
	/**
     * crewNo 소속 정모 중에서 memberNo가 leader 였다면
     *  - 후임을 위임하거나
     *  - 혼자 남았으면 정모 자체를 삭제
     */
    @Transactional
    public void reassignOrDeleteMeetings(Long crewNo, Long memberNo) {
        List<Long> meetingNos = meetingMemberDao
            .findMeetingNoListByCrewNoAndMemberNo(crewNo, memberNo);

        for (Long meetingNo : meetingNos) {
            if (!meetingMemberDao.isLeader(meetingNo, memberNo)) continue;

            List<MeetingMemberDto> others = meetingMemberDao.findOthers(meetingNo, memberNo);
            if (others.isEmpty()) {
                meetingDao.delete(meetingNo);
            } else {
                long newLeader = others.get(0).getMemberNo();
                meetingMemberDao.updateLeaderStatus(meetingNo, newLeader);
                meetingDao.updateOwner(meetingNo, newLeader);
                meetingMemberDao.delete(meetingNo, memberNo);
            }
        }
    }
}