package com.kh.demo.restcontroller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kh.demo.dao.MeetingDao;
import com.kh.demo.dto.AttachmentDto;
import com.kh.demo.dto.MeetingDto;
import com.kh.demo.service.AttachmentService;
import com.kh.demo.service.TokenService;

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

	// 정모 추가
	@PostMapping(value = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public MeetingDto insert(
	    @ModelAttribute MeetingDto meetingDto,
	    @RequestParam("attach") MultipartFile attach,
	    @RequestParam("crewNo") long crewNo,
	    @RequestHeader("Authorization") String bearerToken
	) throws IOException {

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

	    return meetingDto; // ✅ 여기가 핵심!
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
	@PutMapping("/")
	public boolean update(@RequestBody MeetingDto meetingDto) {
		return meetingDao.update(meetingDto);
	}

	// 정모 삭제
	@DeleteMapping("/{meetingNo}")
	public boolean delete(@PathVariable Long meetingNo) {
		return meetingDao.delete(meetingNo);
	}

	// 정모 상세
	@GetMapping("/{meetingNo}")
	public MeetingDto detail(@PathVariable Long meetingNo) {
		MeetingDto meetingData = meetingDao.selectVO(meetingNo);
		return meetingData;
	}
}
