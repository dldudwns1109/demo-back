package com.kh.demo.restcontroller;

import java.io.IOException;
import java.util.ArrayList;
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
import com.kh.demo.dao.CrewMemberDao;
import com.kh.demo.dao.MemberDao;
import com.kh.demo.dto.AttachmentDto;
import com.kh.demo.dto.CrewDto;
import com.kh.demo.dto.CrewLikeDto;
import com.kh.demo.service.AttachmentService;
import com.kh.demo.vo.CrewDetailVO;
import com.kh.demo.vo.CrewVO;
import com.kh.demo.vo.SearchVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@CrossOrigin
@RestController
@RequestMapping("/api/crew")
public class CrewRestController {

	@Autowired
	private CrewDao crewDao;
	
	@Autowired
	private CrewMemberDao crewMemberDao;
	
	@Autowired
	private MemberDao memberDao;
	
	//전체 모임 목록 조회
	@GetMapping("/list")
	public List<CrewVO> list() {
		return crewDao.selectList();
	}
	
	@PostMapping("/updateLike")
	public void updateLike(@RequestBody CrewLikeDto crewLikeDto) {
		crewDao.updateLike(crewLikeDto);
	}
	
	@DeleteMapping("/deleteLike")
	public void deleteLike(@RequestBody CrewLikeDto crewLikeDto) {
		crewDao.deleteLike(crewLikeDto);
	}
	
	@GetMapping("/findCreatedGroup/{memberNo}")
	public List<CrewDetailVO> findCreatedGroup(@PathVariable Long memberNo) {
		List<CrewDetailVO> createdList = new ArrayList<>();
		
		for (long crewNo : crewMemberDao.findCreated(memberNo)) {
			for (CrewDto crew : crewDao.selectGroupByNo(crewNo)) {
				createdList.add(
					CrewDetailVO.builder()
						.crewNo(crew.getCrewNo())
						.crewName(crew.getCrewName())
						.crewCategory(crew.getCrewCategory())
						.crewLocation(crew.getCrewLocation())
						.crewLimit(crew.getCrewLimit())
						.crewIntro(crew.getCrewIntro())
						.crewIsLiked(memberNo == null 
							? false 
							: crewDao.selectLike(
								CrewLikeDto.builder()
									.crewNo(crew.getCrewNo())
									.memberNo(memberNo)
								.build()
						))
						.crewMemberCnt(crewMemberDao.selectMemberCnt(crew.getCrewNo()))
						.crewAttachmentNo(crewDao.findImage(crew.getCrewNo()))
					.build()
				);
			}
		}
		
		return createdList;
	}
	
	@GetMapping("/findJoinedGroup/{memberNo}")
	public List<CrewDetailVO> findJoinedGroup(@PathVariable Long memberNo) {
		List<CrewDetailVO> joinedList = new ArrayList<>();
		
		for (long crewNo : crewMemberDao.findJoined(memberNo)) {
			for (CrewDto crew : crewDao.selectGroupByNo(crewNo)) {
				joinedList.add(
					CrewDetailVO.builder()
						.crewNo(crew.getCrewNo())
						.crewName(crew.getCrewName())
						.crewCategory(crew.getCrewCategory())
						.crewLocation(crew.getCrewLocation())
						.crewLimit(crew.getCrewLimit())
						.crewIntro(crew.getCrewIntro())
						.crewIsLiked(memberNo == null 
							? false 
							: crewDao.selectLike(
								CrewLikeDto.builder()
									.crewNo(crew.getCrewNo())
									.memberNo(memberNo)
								.build()
						))
						.crewMemberCnt(crewMemberDao.selectMemberCnt(crew.getCrewNo()))
						.crewAttachmentNo(crewDao.findImage(crew.getCrewNo()))
					.build()
				);
			}
		}
		
		return joinedList;
	}
	
	@GetMapping("/findLikeGroup/{memberNo}")
	public List<CrewDetailVO> findLikeGroup(@PathVariable Long memberNo) {
		List<CrewDetailVO> likedList = new ArrayList<>();
		
		for (long crewNo : crewMemberDao.findLiked(memberNo)) {
			for (CrewDto crew : crewDao.selectGroupByNo(crewNo)) {
				likedList.add(
					CrewDetailVO.builder()
						.crewNo(crew.getCrewNo())
						.crewName(crew.getCrewName())
						.crewCategory(crew.getCrewCategory())
						.crewLocation(crew.getCrewLocation())
						.crewLimit(crew.getCrewLimit())
						.crewIntro(crew.getCrewIntro())
						.crewIsLiked(memberNo == null 
							? false 
							: crewDao.selectLike(
								CrewLikeDto.builder()
									.crewNo(crew.getCrewNo())
									.memberNo(memberNo)
								.build()
						))
						.crewMemberCnt(crewMemberDao.selectMemberCnt(crew.getCrewNo()))
						.crewAttachmentNo(crewDao.findImage(crew.getCrewNo())
						)
					.build()
				);
			}
		}
		
		return likedList;
	}
	
	@PostMapping("/search")
	public List<CrewDetailVO> search(@RequestBody SearchVO searchVO) {
		List<CrewDetailVO> searchList = new ArrayList<>();
		
		for (CrewDto crew : crewDao.selectSearch(searchVO)) {
			searchList.add(
				CrewDetailVO.builder()
					.crewNo(crew.getCrewNo())
					.crewName(crew.getCrewName())
					.crewCategory(crew.getCrewCategory())
					.crewLocation(crew.getCrewLocation())
					.crewLimit(crew.getCrewLimit())
					.crewIntro(crew.getCrewIntro())
					.crewIsLiked(searchVO.getMemberNo() == null 
						? false 
						: crewDao.selectLike(
							CrewLikeDto.builder()
								.crewNo(crew.getCrewNo())
								.memberNo(searchVO.getMemberNo())
							.build()
					))
					.crewMemberCnt(crewMemberDao.selectMemberCnt(crew.getCrewNo()))
//					.crewAttachmentNo(crewDao.findImage(crew.getCrewNo()))
				.build()
			);
		}
		
		return searchList;
	}
	
	@GetMapping("/findLikedGroup/{userNo}")
	public List<CrewDetailVO> selectLikedGroup(@PathVariable Long userNo) {
		List<CrewDetailVO> searchList = new ArrayList<>();
		
		for (CrewDto crew : crewDao.selectLikedGroup(memberDao.findMemberLike(userNo))) {
			searchList.add(
				CrewDetailVO.builder()
					.crewNo(crew.getCrewNo())
					.crewName(crew.getCrewName())
					.crewCategory(crew.getCrewCategory())
					.crewLocation(crew.getCrewLocation())
					.crewLimit(crew.getCrewLimit())
					.crewIntro(crew.getCrewIntro())
					.crewIsLiked(userNo == null 
						? false 
						: crewDao.selectLike(
							CrewLikeDto.builder()
								.crewNo(crew.getCrewNo())
								.memberNo(userNo)
							.build()
					))
					.crewMemberCnt(crewMemberDao.selectMemberCnt(crew.getCrewNo()))
//					.crewAttachmentNo(crewDao.findImage(crew.getCrewNo()))
				.build()
			);
		}
		return searchList;
	}
	
	//모임 상세 조회
	@GetMapping("/{crewNo}")
    public CrewDto detail(@PathVariable Long crewNo) {
        return crewDao.selectOne(crewNo);
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
	
	//이미지 반환
    @GetMapping("/image/{crewNo}")
    public void showImage(@PathVariable long crewNo,
                          HttpServletRequest request,
                          HttpServletResponse response) throws IOException {
        try {
            long attachmentNo = crewDao.findImage(crewNo);
            String contextPath = request.getContextPath();
            response.sendRedirect(contextPath + "/api/attachment/" + attachmentNo);
        } catch (Exception e) {
            // 기본 이미지 경로
            response.sendRedirect("https://dummyimage.com/400x400/000/fff");
        }
    }
}

















