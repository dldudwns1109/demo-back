package com.kh.demo.vo;

import com.kh.demo.dto.CrewDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CrewPayRequestVO {
    private CrewDto crewDto;        // 모임 정보
    private int totalAmount;     // 결제 금액
    private Long attachmentNo;
}
