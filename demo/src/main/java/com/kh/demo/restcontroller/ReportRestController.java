package com.kh.demo.restcontroller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.demo.dao.CrewDao;
import com.kh.demo.dao.MemberDao;
import com.kh.demo.dto.CrewDto;
import com.kh.demo.dto.MemberDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/report")
public class ReportRestController {

	@Autowired
	private JavaMailSender mailSender;
	@Autowired
	private CrewDao crewDao;
	@Autowired
	private MemberDao memberDao;

//	@PostMapping("")
//	public Map<String, Object> reportCrew(@RequestBody Map<String, Object> reportData) {
//		Map<String, Object> response = new HashMap<>();
//
//		try {
//			Long crewNo = Long.parseLong(String.valueOf(reportData.get("crewNo")));
//			Long reporterNo = Long.parseLong(String.valueOf(reportData.get("reporterNo")));
//			String reportContent = (String) reportData.get("reportContent");
//
//			// crewName 및 reporterName 조회
//			String crewName = crewDao.findCrewNameByNo(crewNo);
//			String reporterName = memberDao.findNicknameById(reporterNo);
//
//			if (crewName == null) {
//				throw new IllegalArgumentException("해당 모임이 존재하지 않습니다.");
//			}
//			if (reporterName == null) {
//				throw new IllegalArgumentException("해당 회원이 존재하지 않습니다.");
//			}
//
//			// 메일 전송
//			SimpleMailMessage message = new SimpleMailMessage();
//			message.setTo("kwkgsjd0410@gmail.com");
//			message.setSubject("[신고 접수] 모임명: " + crewName + " (모임 번호: " + crewNo + ")");
//			message.setText(
//					"신고자: " + reporterName + " (ID: " + reporterNo + ")\n" + 
//					"모임명: " + crewName + "\n" + 
//					"모임 번호: " + crewNo + "\n\n" + 
//					"내용:\n" + reportContent);
//
//			mailSender.send(message);
//
//			response.put("status", "success");
//			response.put("message", "신고가 접수되었습니다.");
//		} catch (Exception e) {
//			response.put("status", "fail");
//			response.put("message", "신고 접수에 실패했습니다.");
//			response.put("error", e.getMessage());
//			e.printStackTrace();
//		}
//
//		return response;
//	}
	
//	@PostMapping("")
//    public Map<String, Object> reportCrew(@RequestBody Map<String, Object> reportData) {
//        Map<String, Object> response = new HashMap<>();
//
//        try {
//            Long crewNo = Long.parseLong(String.valueOf(reportData.get("crewNo")));
//            Long reporterNo = Long.parseLong(String.valueOf(reportData.get("reporterNo")));
//            String reportContent = (String) reportData.get("reportContent");
//
//            // crewName과 reporterName 조회
//            CrewDto crew = crewDao.selectOne(crewNo);
//            MemberDto reporter = memberDao.findMemberByNo(reporterNo);
//
//            if (crew == null || reporter == null) {
//                log.warn("Crew or Reporter not found. crewNo: {}, reporterNo: {}", crewNo, reporterNo);
//                response.put("status", "fail");
//                response.put("message", "해당 모임 또는 회원이 존재하지 않습니다.");
//                return response;
//            }
//
//            String crewName = crew.getCrewName();
//            String reporterName = reporter.getMemberNickname();
//
//            log.info("신고 접수 - 모임명: {}, 신고자: {}, 내용: {}", crewName, reporterName, reportContent);
//
//            // 이메일 전송 로직
//            SimpleMailMessage message = new SimpleMailMessage();
//            message.setTo("kwkgsjd0410@gmail.com");
//            message.setSubject("[신고 접수] 모임명: " + crewName + " (모임 번호: " + crewNo + ")");
//            message.setText("신고자: " + reporterName + "\n내용: " + reportContent);
//
//            mailSender.send(message);
//            log.info("이메일 전송 성공 - 신고자: {}, 모임명: {}", reporterName, crewName);
//
//            response.put("status", "success");
//            response.put("message", "신고가 접수되었습니다.");
//        } catch (Exception e) {
//            log.error("신고 접수 중 오류 발생: ", e);
//            response.put("status", "fail");
//            response.put("message", "신고 접수에 실패했습니다.");
//            response.put("error", e.getMessage());
//        }
//
//        return response;
//    }
	@PostMapping("")
    public Map<String, Object> reportCrew(@RequestBody Map<String, Object> reportData) {
        Map<String, Object> response = new HashMap<>();

        try {
            Long crewNo = Long.parseLong(String.valueOf(reportData.get("crewNo")));
            Long reporterNo = Long.parseLong(String.valueOf(reportData.get("reporterNo")));
            String reportContent = (String) reportData.get("reportContent");

            // Crew Name과 Reporter Name을 조회
            String crewName = crewDao.findCrewName(crewNo);
            String reporterName = memberDao.findNicknameById(reporterNo);

            log.info("신고 접수 - 모임명: {}, 신고자: {}, 내용: {}", crewName, reporterName, reportContent);

            // 메일 전송
            SimpleMailMessage message = new SimpleMailMessage();
//            message.setTo("kwkgsjd0410@gmail.com");
            message.setTo("20zune00@gmail.com");
            message.setSubject("[신고 접수] 모임명: " + crewName + " (모임 번호: " + crewNo + ")");
            message.setText("신고자: " + reporterName + "\n내용: " + reportContent);

            mailSender.send(message);

            log.info("이메일 전송 성공 - 신고자: {}, 모임명: {}", reporterName, crewName);

            response.put("status", "success");
            response.put("message", "신고가 접수되었습니다.");

        } catch (Exception e) {
            log.error("신고 처리 중 오류 발생: ", e);
            response.put("status", "fail");
            response.put("message", "신고 접수에 실패했습니다.");
            response.put("error", e.getMessage());
        }

        return response;
    }
	
}
