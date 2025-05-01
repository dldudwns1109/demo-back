package com.kh.demo.dto;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ReplyListDto {
	private Long replyNo;
    private Long replyWriter;
    private Long replyOrigin;
    private String replyContent;
    private Timestamp replyWtime;
    private Timestamp replyUtime;

    // 추가 필드: 작성자 정보
    private String memberNickname;
    private String memberMbti;
    private String memberLocation;
    private String memberSchool;
    private String profileUrl;
}
