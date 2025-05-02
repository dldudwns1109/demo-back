package com.kh.demo.dto;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PayDto {
	private long payNo;//결제번호
	private long payOwner;//결제자
	private String payTid;//거래번호
	private String payName;//상품이름
	private long payPrice;//결제 금액
	private Timestamp payTime;//결제 시각
}
