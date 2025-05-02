package com.kh.demo.service;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.kh.demo.configuration.FileuploadProperties;
import com.kh.demo.dao.AttachmentDao;
import com.kh.demo.dto.AttachmentDto;
import com.kh.demo.error.TargetNotFoundException;

import io.jsonwebtoken.io.IOException;

@Service
public class AttachmentService {

	@Autowired
	private AttachmentDao attachmentDao;
	
	@Autowired
	private FileuploadProperties fileuploadProperties;

	//파일 저장
	public AttachmentDto save(MultipartFile attach) throws IllegalStateException, IOException, java.io.IOException {
		if(attach.isEmpty()) return null;
		//저장위치 생성
		//File dir = new File(fileuploadProperties.getRoot());
		File dir = fileuploadProperties.getRootDir();
		dir.mkdirs();
		//파일 정보 저장
		AttachmentDto attachmentDto = new AttachmentDto();
		attachmentDto.setAttachmentName(attach.getOriginalFilename());
		attachmentDto.setAttachmentType(attach.getContentType());
		attachmentDto.setAttachmentSize(attach.getSize());
		AttachmentDto resultDto = attachmentDao.insert(attachmentDto);
		//물리 파일 저장
		File target = new File(dir, String.valueOf(resultDto.getAttachmentName()));//파일명으로 설정
		attach.transferTo(target);//저장
		//파일 번호 반환
		return resultDto;
	}
	
	//파일 삭제
	public void delete(long attachmentNo) {
		//[1] 실제 파일을 지우고
		File dir = fileuploadProperties.getRootDir();
		File target = new File(dir, String.valueOf(attachmentNo));
		if(target.isFile() == false) return;
		
		target.delete();//파일 삭제
		
		//[2] DB 정보를 삭제
		attachmentDao.delete(attachmentNo);
	}

	//파일 불러오기 (+유효성 검사)
	public byte[] load(long attachmentNo) throws IOException, java.io.IOException {
		//[1] 유효한 파일 번호인지 확인
		AttachmentDto attachmentDto = attachmentDao.selectOne(attachmentNo);
		if(attachmentDto == null) {
			throw new TargetNotFoundException("존재하지 않는 파일번호");
		}
		
		//[2] 실제 파일이 존재하는지 확인
		File dir = fileuploadProperties.getRootDir();
		File target = new File(dir, String.valueOf(attachmentNo));
		if(target.isFile() == false) {
			throw new TargetNotFoundException("파일이 존재하지 않습니다");
		}
		
		//[3] 실제 파일을 불러온다
		byte[] data = FileUtils.readFileToByteArray(target);
		
		return data;
	}
}
