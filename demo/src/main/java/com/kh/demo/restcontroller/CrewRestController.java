package com.kh.demo.restcontroller;

import java.io.IOException;
import java.util.List;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kh.demo.dao.CrewDao;
import com.kh.demo.dto.AttachmentDto;
import com.kh.demo.dto.CrewDto;
import com.kh.demo.service.AttachmentService;
import com.kh.demo.vo.CrewVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@CrossOrigin
@RestController
@RequestMapping("/api/crew")
public class CrewRestController {

	@Autowired
	private CrewDao crewDao;
	@Autowired
	private AttachmentService attachmentService;
	
	//전체 모임 목록 조회
	@GetMapping("/list")
	public List<CrewVO> list() {
		return crewDao.selectList();
	}
	
	//모임 상세 조회
	@GetMapping("/{crewNo}")
    public CrewVO detail(@PathVariable Long crewNo) {
        return crewDao.selectOne(crewNo);
    }
	
	//모임 등록
	@PostMapping(value = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public void insert(@ModelAttribute CrewDto crewDto,
	    @RequestParam MultipartFile attach) throws IOException {
		
	    CrewDto resultDto = crewDao.insert(crewDto);

	    // 파일이 있다면 저장 및 연결
	    if (attach != null && !attach.isEmpty()) {
	        AttachmentDto attachmentDto = attachmentService.save(attach);
	        crewDao.connect(resultDto, attachmentDto);
	    }
	}
	
	//모임 수정
	@PutMapping("/")
	public boolean update(@RequestBody CrewDto crewDto) {
		return crewDao.update(crewDto);
	}
	
	//모임 삭제
	@DeleteMapping("/{crewNo}")
	public boolean delete(@PathVariable Long crewNo) {
		return crewDao.delete(crewNo);
	}
	
	//모임 이미지 업로드
	@PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void uploadCrewImage(@RequestParam long crewNo,
                                @RequestParam MultipartFile attach) throws IllegalStateException, IOException {

        if (!attach.isEmpty()) {
            AttachmentDto attachmentDto = attachmentService.save(attach);

            // crewDto는 crewNo만 필요함
            CrewDto crewDto = new CrewDto();
            crewDto.setCrewNo(crewNo);

            crewDao.connect(crewDto, attachmentDto);
        }
    }
	
	//이미지 반환
    @GetMapping("/image/{crewNo}")
    public void showImage(@PathVariable long crewNo,
                          HttpServletRequest request,
                          HttpServletResponse response) throws IOException {
        try {
            int attachmentNo = crewDao.findImage(crewNo);
            String contextPath = request.getContextPath();
            response.sendRedirect(contextPath + "/api/attachment/" + attachmentNo);
        } catch (Exception e) {
            // 기본 이미지 경로
            response.sendRedirect("https://dummyimage.com/400x400/000/fff");
        }
    }
}

















