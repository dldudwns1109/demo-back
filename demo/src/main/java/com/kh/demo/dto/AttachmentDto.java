package com.kh.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AttachmentDto {
	private long attachmentNo;//시퀀스번호
	private String attachmentName;//업로드한 파일명
	private String attachmentType;//업로드한 파일유형
	private long attachmentSize;//업로드한 파일크기
}
