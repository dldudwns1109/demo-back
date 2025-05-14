package com.kh.demo.dto;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeetingMemberDto {
    private Long meetingMemberNo;
    private Long meetingNo;
    private Long memberNo;
    private Timestamp meetingMemberJoinDate;
    private String isLeader;
}
