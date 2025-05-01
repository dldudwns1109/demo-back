package com.kh.demo.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.demo.dao.CrewDao;
import com.kh.demo.dto.CrewDto;
import com.kh.demo.vo.CrewVO;

@CrossOrigin
@RestController
@RequestMapping("/api/crew")
public class CrewRestController {

	@Autowired
	private CrewDao crewDao;
	
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
	@GetMapping("/")
	public void insert(@RequestBody CrewDto crewDto) {
		crewDao.insert(crewDto);
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
}

















