package com.kh.demo.dto;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class ReplyDto {
	private Long replyNo; //댓글 번호
	private Long replyWriter; //작성자 번호
	private Long replyOrigin; //게시글 번호 (댓글이 달린 글)
	private String replyContent; // 댓글 내용
	private Timestamp replyWtime; //작성 시간
	private Timestamp replyUtime; //수정 시간
}
