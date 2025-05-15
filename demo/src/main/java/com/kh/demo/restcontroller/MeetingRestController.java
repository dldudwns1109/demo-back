package com.kh.demo.restcontroller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kh.demo.dao.MeetingDao;
import com.kh.demo.dao.MeetingMemberDao;
import com.kh.demo.dto.AttachmentDto;
import com.kh.demo.dto.MeetingDto;
import com.kh.demo.dto.MeetingMemberDto;
import com.kh.demo.service.AttachmentService;
import com.kh.demo.service.TokenService;
import com.kh.demo.vo.MeetingVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/meeting")
public class MeetingRestController {
	@Autowired
	private MeetingDao meetingDao;
	@Autowired
	private AttachmentService attachmentService;
	@Autowired
	private TokenService tokenService;
	@Autowired
	private MeetingMemberDao meetingMemberDao;

	// 정모 추가
	@Transactional
	@PostMapping(value = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public MeetingDto insert(
	    @ModelAttribute MeetingDto meetingDto,
	    @RequestParam("attach") MultipartFile attach,
	    @RequestParam("crewNo") long crewNo,
	    @RequestHeader("Authorization") String bearerToken
	) throws IOException {
		log.debug("Received MeetingDto = {}", meetingDto);


	    long userNo = tokenService.parseBearerToken(bearerToken);

	    meetingDto.setMeetingCrewNo(crewNo);
	    meetingDto.setMeetingOwnerNo(userNo);

	    long meetingNo = meetingDao.sequence();
	    meetingDto.setMeetingNo(meetingNo);
	    meetingDao.insert(meetingDto);

	    if (!attach.isEmpty()) {
	        AttachmentDto attachmentDto = attachmentService.save(attach);
	        meetingDao.connect(meetingDto, attachmentDto);
	    }
	    
	    // ✅ 모임장으로 참여자 등록
	    log.debug("Creating meetingMemberDto with isLeader = Y");

	    MeetingMemberDto meetingMemberDto = MeetingMemberDto.builder()
	        .meetingNo(meetingNo)
	        .memberNo(userNo)
	        .isLeader("Y")
	        .meetingMemberNo(meetingMemberDao.sequence())
	        .build();
	    meetingMemberDao.insert(meetingMemberDto);

	    return meetingDto;
	    
	}

	// 이미지 반환
	@GetMapping("/image/{meetingNo}")
	public void showImage(@PathVariable long meetingNo, 
			HttpServletRequest request, 
			HttpServletResponse response) throws IOException {
		String contextPath = request.getContextPath();
		try {
			long attachmentNo = meetingDao.findImage(meetingNo);
			response.sendRedirect(contextPath + "/api/attachment/" + attachmentNo);
		} catch (Exception e) {
			response.sendRedirect("https://dummyimage.com/400x400/000/fff");
		}
	}

	// 정모 수정
	@Transactional
	@PutMapping("/{meetingNo}")
	public boolean update(
	    @PathVariable long meetingNo,
	    @ModelAttribute MeetingDto meetingDto,
	    @RequestParam(required = false) MultipartFile attach
	) throws IOException {
	    meetingDto.setMeetingNo(meetingNo);
	    boolean result = meetingDao.update(meetingDto);

	    if (attach != null && !attach.isEmpty()) {
	        AttachmentDto attachmentDto = attachmentService.save(attach);
	        meetingDao.delete(meetingNo); // 기존 연결 삭제 (meeting_image)
	        meetingDao.connect(meetingDto, attachmentDto); // 새로 연결
	    }

	    return result;
	}



	// 정모 삭제
	@DeleteMapping("/{meetingNo}")
	public boolean delete(@PathVariable Long meetingNo) {
		return meetingDao.delete(meetingNo);
	}

	// 정모 상세
	@GetMapping("/{meetingNo}")
	public MeetingVO detail(@PathVariable Long meetingNo) {
	    System.out.println("정모 번호: " + meetingNo);
	    return meetingDao.selectVO(meetingNo);
	}
	
	// 정모 목록 조회 (특정 crewNo 기준)
	@GetMapping("/list/{crewNo}")
	public List<MeetingVO> listByCrew(@PathVariable long crewNo) {
	    return meetingDao.selectListByCrew(crewNo);
	}
	
	//정모 모임장 위임
	@Transactional
	@PutMapping("/{meetingNo}/owner")
	public boolean updateOwner(
	    @PathVariable long meetingNo,
	    @RequestParam long newOwnerNo,
	    @RequestHeader("Authorization") String bearerToken
	) {
	    long requesterNo = tokenService.parseBearerToken(bearerToken);
	    MeetingVO meeting = meetingDao.selectVO(meetingNo);

	    // 요청자가 현재 모임장이 아니면 실패
	    if (meeting == null || !meeting.getMeetingOwnerNo().equals(requesterNo)) {
	        return false;
	    }

	    // 1. meeting 테이블에 owner 변경
	    boolean result = meetingDao.updateOwner(meetingNo, newOwnerNo);
	    if (!result) return false;

	    // 2. meeting_member 테이블에서 is_leader 상태 업데이트
	    meetingMemberDao.updateLeaderStatus(meetingNo, newOwnerNo); // DAO에 아래 메서드 필요

	    return true;
	}

	@GetMapping("/member/{memberNo}")
	public List<MeetingVO> listByMember(@PathVariable long memberNo) {
		return meetingDao.selectMeetingListByMember(memberNo);
	}
}
