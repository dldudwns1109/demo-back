package com.kh.demo.vo.pay;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class PayApproveVO {
	private String tid;//거래번호
	private String partnerOrderId;//주문번호
	private String partnerUserId;//구매자 ID
	private String pgToken;//유효성 검증용 토큰
}
