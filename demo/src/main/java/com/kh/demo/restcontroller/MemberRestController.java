package com.kh.demo.restcontroller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kh.demo.dao.MemberDao;
import com.kh.demo.dao.TokenDao;
import com.kh.demo.dto.MemberDto;
import com.kh.demo.dto.MemberLikeDto;
import com.kh.demo.service.AttachmentService;
import com.kh.demo.service.MemberService;
import com.kh.demo.service.TokenService;
import com.kh.demo.vo.MemberCheckVO;
import com.kh.demo.vo.MemberSigninRequestVO;
import com.kh.demo.vo.MemberSigninResponseVO;
import com.kh.demo.vo.MemberVO;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
	
	@Autowired
	private AttachmentService attachmentService;
	
	@PostMapping("/signup")
	public void signup(@ModelAttribute MemberVO memberVO, 
			@RequestParam("memberImg") MultipartFile memberImg) throws IOException, IllegalStateException, IOException {
		long memberNo = memberService.signup(memberVO);
		ModelMapper mapper = new ModelMapper();
		MemberDto memberDto = mapper.map(memberVO, MemberDto.class);
		memberDto.setMemberNo(memberNo);
		memberDao.connect(memberDto, attachmentService.save(memberImg));
	}
	
	@PostMapping("/signin")
	public MemberSigninResponseVO signin(@RequestBody MemberSigninRequestVO requestVO) {
		ModelMapper mapper = new ModelMapper();
		MemberDto memberDto = mapper.map(requestVO, MemberDto.class);
		
		MemberDto findDto = memberService.signin(memberDto);
		if (findDto == null) throw new RuntimeException();
		
		return MemberSigninResponseVO.builder()
					.memberNo(findDto.getMemberNo())
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
									.memberNo(memberNo)
									.accessToken(tokenService.generateAccessToken(memberNo))
									.refreshToken(tokenService.generateRefreshToken(memberNo))
									.build();
	}
	
	@PostMapping("/signout")
	 public void logout(@RequestHeader("Authorization") String accessToken) {
		long memberNo = tokenService.parseBearerToken(accessToken);
	    tokenDao.clean(memberNo);
	}
	
	@GetMapping("/memberEmail/{memberEmail}")
	public String findId(@PathVariable String memberEmail) {
		return memberDao.findId(memberEmail);
	}
	
	@PatchMapping("/updatePw")
	public void findPw(@RequestBody Map<String, String> memberEmail) throws MessagingException, IOException {
		memberService.sendTempPassword(memberEmail.get("memberEmail"));
	}
	
	@GetMapping("/checkDuplicatedId/{memberId}")
	public boolean checkDuplicatedId(@PathVariable String memberId) {
		if (memberDao.findMember(memberId) == null) return false;
		else return true;
	}
	
	@GetMapping("/checkDuplicatedEmail/{memberEmail}")
	public boolean checkDuplicatedEmail(@PathVariable String memberEmail) {
		if (memberDao.findMemberByEmail(memberEmail) == null) return false;
		else return true;
	}
	
	@GetMapping("/checkDuplicatedNickname/{memberNickname}")
	public boolean checkDuplicatedNickname(@PathVariable String memberNickname) {
		if (memberDao.findMemberByNickname(memberNickname) == null) return false;
		else return true;
	}
	
	@PatchMapping(value = "/{memberNo}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public void edit(@PathVariable long memberNo,
	                 @ModelAttribute MemberDto memberDto,
	                 @ModelAttribute MemberLikeDto memberLikeDto,
	                 @RequestParam(required = false) MultipartFile attach) throws IOException {
	    
	    memberDto.setMemberNo(memberNo);

	    // 1. 기존 정보 확인
	    MemberDto origin = memberDao.findMemberByNo(memberNo);
	    if (origin == null) throw new RuntimeException("회원 없음");

	    // 2. 이미지 변경
	    if (attach != null && !attach.isEmpty()) {
	        Long oldAttachmentNo = memberDao.findImage(memberNo);
	        if (oldAttachmentNo != null) {
	            memberDao.disconnectProfile(memberNo);
	            attachmentService.delete(oldAttachmentNo);
	        }
	        Long newAttachmentNo = attachmentService.save(attach).getAttachmentNo();
	        memberDao.connect(memberNo, newAttachmentNo);
	    }

	    // 3. 회원 정보 수정
	    memberDao.update(memberDto);
	    
	    // 4. 관심사 수정
	    memberDao.updateLikes(memberNo, memberLikeDto.getMemberLike());
	}
	
	@GetMapping("/{memberNo}")
	public MemberVO getMemberInfo(@PathVariable long memberNo) {
	    MemberDto memberDto = memberDao.findMemberByNo(memberNo);
	    if (memberDto == null) throw new RuntimeException("회원 없음");

	    memberDto.setMemberPw(null); // 비밀번호 제거

	    ModelMapper mapper = new ModelMapper();
	    MemberVO memberVO = mapper.map(memberDto, MemberVO.class);
	    memberVO.setMemberLike(memberDao.findMemberLike(memberNo));

	    return memberVO;
	}
	
	@DeleteMapping("/{memberNo}")
	public void deleteMember(@RequestHeader("Authorization") String accessToken, 
			@PathVariable long memberNo) {
	    MemberDto findDto = memberDao.findMemberByNo(memberNo);
	    if (findDto == null) throw new RuntimeException("회원이 존재하지 않습니다");
	    memberDao.deleteMember(findDto.getMemberNo());
	    
	    long findNo = tokenService.parseBearerToken(accessToken);
	    tokenDao.clean(findNo);
	}
	
	@GetMapping("/mypage/{memberNo}")
    public MemberVO MyPage(@PathVariable long memberNo) {
        MemberDto memberDto = memberDao.findMemberByNo(memberNo);
        memberDto.setMemberPw(null);
        
        ModelMapper mapper = new ModelMapper();
        MemberVO memberVO = mapper.map(memberDto, MemberVO.class);
        memberVO.setMemberLike(memberDao.findMemberLike(memberNo));
        
        return memberVO;
    }
		
	
	@GetMapping("/image/{memberNo}")
	public void showImage(@PathVariable long memberNo,
	                      HttpServletRequest request,
	                      HttpServletResponse response) throws IOException {
	    try {
	        System.out.println("Requested Member No: " + memberNo);
	        long attachmentNo = memberDao.findImage(memberNo);
	        System.out.println("Attachment No Found: " + attachmentNo);
	        String contextPath = request.getContextPath();
	        response.sendRedirect(contextPath + "/api/attachment/" + attachmentNo);
	    } catch (Exception e) {
	        e.printStackTrace();
	        response.sendRedirect("https://dummyimage.com/400x400/000/fff");
	    }
	}
//	@GetMapping("/image/{attachmentNo}")
//	public void showImage(@PathVariable long memberNo,
//	                      HttpServletRequest request,
//	                      HttpServletResponse response) throws IOException {
//	    try {
//	        System.out.println("Requested Member No: " + memberNo);
//	        long attachmentNo = memberDao.findImage(memberNo);
//	        System.out.println("Attachment No Found: " + attachmentNo);
//	        String contextPath = request.getContextPath();
//	        response.sendRedirect(contextPath + "/api/attachment/" + attachmentNo);
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	        response.sendRedirect("https://dummyimage.com/400x400/000/fff");
//	    }
//	}

	
	
	@GetMapping("/findMemberNo/{memberNickname}")
	public long findMemberNo(@PathVariable String memberNickname) {
		return memberDao.findMemberNo(memberNickname);
	}
	
	@PostMapping("/checkPassword")
	public boolean checkPassword(@RequestBody MemberCheckVO memberCheckVO) {
		return memberService.checkPassword(memberCheckVO);
	}
}
