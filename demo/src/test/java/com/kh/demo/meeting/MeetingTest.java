package com.kh.demo.meeting;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.kh.demo.dao.MeetingDao;
import com.kh.demo.dto.MeetingDto;

@SpringBootTest
public class MeetingTest {
	@Autowired
	private MeetingDao meetingDao;
	
	@Test
    public void MeetingInsertTest() throws ParseException {
        long meetingNo = meetingDao.sequence();

        MeetingDto dto = new MeetingDto();
        dto.setMeetingNo(meetingNo);
        dto.setMeetingCrewNo(1L); // 존재하는 crew_no
        dto.setMeetingOwnerNo(33L); //crew에 속하면서 정모를 만든 member_no
        dto.setMeetingName("테스트 정모");

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date parsedDate = format.parse("2025-05-20 19:30:00");
        dto.setMeetingDate(new Timestamp(parsedDate.getTime()));

        dto.setMeetingLocation("서울시 강남구");
        dto.setMeetingPrice(15000L);
        dto.setMeetingLimit(10L);

        meetingDao.insert(dto);

        System.out.println("등록된 정모: " + dto);
    }
}
