package com.kh.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PayDetailDto {
	private long payDetailNo;//결제 상세 번호
	private long payDetailOrigin;//결제 번호
	private String payDetailName;//상품 이름
	private long payDetailPrice;//결제 가격
	private char payDetailStatus;//결제 상태
	
}
