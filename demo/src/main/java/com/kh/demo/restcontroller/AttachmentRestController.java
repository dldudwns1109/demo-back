package com.kh.demo.restcontroller;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.demo.dao.AttachmentDao;
import com.kh.demo.dto.AttachmentDto;
import com.kh.demo.service.AttachmentService;

import io.jsonwebtoken.io.IOException;

//@CrossOrigin//<img src=""> 로 부르기 때문
@RestController
@RequestMapping("/api/attachment")
public class AttachmentRestController {
	@Autowired
	private AttachmentDao attachmentDao;
	
	@Autowired
	private AttachmentService attachmentService;

	@GetMapping("/{attachmenNo}")
	public ResponseEntity<ByteArrayResource> download(
							@PathVariable long attachmentNo) throws IOException, java.io.IOException {
		byte[] data = attachmentService.load(attachmentNo);
		AttachmentDto attachmentDto = attachmentDao.selectOne(attachmentNo);
		
		//포장(wrap)
		ByteArrayResource resource = new ByteArrayResource(data);
		
		//반환
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_ENCODING, "UTF-8")
				.header(HttpHeaders.CONTENT_TYPE, attachmentDto.getAttachmentType())//알 때
				//.contentType(MediaType.APPLICATION_OCTET_STREAM)//모를 때
				.contentLength(attachmentDto.getAttachmentSize())
				.header(HttpHeaders.CONTENT_DISPOSITION, 
					ContentDisposition.attachment()
						.filename(attachmentDto.getAttachmentName(), 
										StandardCharsets.UTF_8)
					.build().toString()
				)
			.body(resource);
	}
}
