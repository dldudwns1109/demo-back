package com.kh.demo.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.demo.dao.MeetingDao;
import com.kh.demo.dao.MeetingMemberDao;
import com.kh.demo.dto.MeetingMemberDto;
import com.kh.demo.service.TokenService;
import com.kh.demo.vo.MeetingMemberVO;
import com.kh.demo.vo.MeetingVO;

@CrossOrigin
@RestController
@RequestMapping("/api/meetingMember")
public class MeetingMemberRestController {
	@Autowired
	private MeetingMemberDao meetingMemberDao;
	@Autowired
	private MeetingDao meetingDao;
	@Autowired
	private TokenService tokenService;
	
	//정모 참여자 등록 (시퀀스 포함)
    @PostMapping("/")
    public ResponseEntity<?> insert(@RequestBody MeetingMemberDto meetingMemberdto, @RequestHeader("Authorization") String bearertoken) {
        long memberNo = tokenService.parseBearerToken(bearertoken); 
        meetingMemberdto.setMemberNo(memberNo);
        meetingMemberDao.insert(meetingMemberdto);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/{meetingNo}")
    public boolean delete(
        @PathVariable long meetingNo,
        @RequestHeader("Authorization") String bearerToken
    ) {
        long userNo = tokenService.parseBearerToken(bearerToken);

        MeetingVO meeting = meetingDao.selectVO(meetingNo);
        if (meeting == null) return false;

        if (meeting.getMeetingOwnerNo().equals(userNo)) {
            // 모임장이면 false로 처리
            return false;
        }

        // 일반 유저면 삭제 처리
        return meetingMemberDao.delete(meetingNo, userNo);
    }

    
    @GetMapping("/{meetingNo}")
    public List<MeetingMemberVO> getMeetingMemberList(@PathVariable Long meetingNo) {
        return meetingMemberDao.selectListByMeetingNo(meetingNo);
    }
    
    //정모 참여자 중 로그인 한 유저가 있는지
    @GetMapping("/{meetingNo}/check")
    public boolean isJoined(
    		@PathVariable long meetingNo,
    		@RequestHeader("Authorization") String bearerToken) {
    	long userNo = tokenService.parseBearerToken(bearerToken);
    	return meetingMemberDao.isJoined(meetingNo, userNo);
    }
}
