package com.kh.demo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeetingMemberVO {
    private Long memberNo;
    private String memberNickname;
    private Long attachmentNo;
    private String isLeader; // 모임장 여부
}

