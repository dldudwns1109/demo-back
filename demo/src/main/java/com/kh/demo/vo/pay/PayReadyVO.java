package com.kh.demo.vo.pay;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class PayReadyVO {
	private String partnerOrderId;
	private String partnerUserId;
	private String itemName;
	private long totalAmount;
}
