package com.kh.demo.vo;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import lombok.Data;

@Data
public class BoardVO {
	private Long boardNo; // 게시글 번호
	private String boardTitle; // 게시글 제목
	private String boardCategory; // 게시글 카테고리명
	private String boardWriterNickname; // 작성자의 닉네임
	private Long boardWriterProfileUrl; //회원 프로필
	private Timestamp boardWriteTime; // 사용자에게 보여주기 위한 작성 시간 (예: "2025.04.24 14:23")
	private long boardReply; // 댓글 수
	private String boardContent; // 글 내용
	
    private String boardWriterGender;
    private String boardWriterBirth;
    private String boardWriterMbti;
    private String boardWriterLocation;
    private String boardWriterSchool;

	// 작성 시간 포맷팅된 문자열을 반환하는 메서드
	public String getFormattedWriteTime() {
		if (boardWriteTime == null) return "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분");
		return sdf.format(boardWriteTime);
	}
	
	public String getFormattedWriterBirth() {
	    if (boardWriterBirth == null) return "";
	    return boardWriterBirth.replace("-", "년 ").replaceFirst("-", "월 ") + "일";
	}


}
