package com.kh.demo.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.kh.demo.configuration.KakaoPayProperties;
import com.kh.demo.dao.AttachmentDao;
import com.kh.demo.dao.ChatDao;
import com.kh.demo.dao.CrewDao;
import com.kh.demo.dao.CrewMemberDao;
import com.kh.demo.dao.PayDao;
import com.kh.demo.dto.AttachmentDto;
import com.kh.demo.dto.ChatDto;
import com.kh.demo.dto.CrewDto;
import com.kh.demo.dto.CrewMemberDto;
import com.kh.demo.dto.PayDetailDto;
import com.kh.demo.dto.PayDto;
import com.kh.demo.vo.pay.PayApproveResponseVO;
import com.kh.demo.vo.pay.PayApproveVO;
import com.kh.demo.vo.pay.PayReadyResponseVO;
import com.kh.demo.vo.pay.PayReadyVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PayService {
	@Autowired
	private KakaoPayProperties kakaoPayProperties;
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private HttpHeaders headers;
	@Autowired
	private PayDao payDao;
	@Autowired
	private CrewDao crewDao;
	@Autowired
	private AttachmentDao attachmentDao;
	@Autowired
	private CrewMemberDao crewMemberDao;
	@Autowired
	private ChatDao chatDao;
	
	//결제 준비(ready)
	public PayReadyResponseVO ready(PayReadyVO vo) throws URISyntaxException {
		//(1) 전송 도구 생성
		RestTemplate restTemplate = new RestTemplate();
		
		//(2) 전송 주소 확인
		URI uri = new URI("https://open-api.kakaopay.com/online/v1/payment/ready");
		
		//(3) 헤더 설정
		//Authorization: SECRET_KEY ${SECRET_KEY}
		//Content-Type: application/json
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "SECRET_KEY "+kakaoPayProperties.getSecretKey());
		headers.add("Content-Type", "application/json");
		
		//(4) 바디 설정
		Map<String, String> body = new HashMap<>();
		body.put("cid", kakaoPayProperties.getCid());
		body.put("partner_order_id", vo.getPartnerOrderId());
		body.put("partner_user_id", vo.getPartnerUserId());
		body.put("item_name", vo.getItemName());
		body.put("quantity", "1");
		body.put("total_amount", String.valueOf(vo.getTotalAmount()));
		body.put("tax_free_amount", "0");
		//카카오페이 개발자센터 플랫폼에 등록된 주소로 시작해야함
		body.put("approval_url", "http://localhost:8080/api/pay/success/" + vo.getPartnerOrderId());
		body.put("cancel_url", "http://localhost:8080/api/pay/cancel/" + vo.getPartnerOrderId());
		body.put("fail_url", "http://localhost:8080/api/pay/fail/" + vo.getPartnerOrderId());

		
		//(4+3)
		//HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);
		HttpEntity entity = new HttpEntity(body, headers);
		
		//2 + (4+3)
		//restTemplate.postForObject(주소객체, 헤더+바디, 결과물의 형태);
		PayReadyResponseVO response = restTemplate.postForObject(
								uri, entity, PayReadyResponseVO.class);
		return response;
	
	}
	//결제 승인(approve)
	public PayApproveResponseVO approve(PayApproveVO vo) throws URISyntaxException{
		URI uri = new URI("https://open-api.kakaopay.com/online/v1/payment/approve");
		
		Map<String, String> body = new HashMap<>();
		body.put("cid", kakaoPayProperties.getCid());
		body.put("tid", vo.getTid());
		body.put("partner_order_id", vo.getPartnerOrderId());
		body.put("partner_user_id", vo.getPartnerUserId());
		body.put("pg_token", vo.getPgToken());
		
		HttpEntity entity = new HttpEntity(body, headers);
		PayApproveResponseVO response = 
				restTemplate.postForObject(uri, entity, PayApproveResponseVO.class);
		
		return response;
	}
//	//결제 조회(order)
//	public PayOrderResponseVO order(PayOrderVO vo) throws URISyntaxException {
//		URI uri = new URI("https://open-api.kakaopay.com/online/v1/payment/order");
//		
//		Map<String, String> body = new HashMap<>();
//		body.put("cid", kakaoPayProperties.getCid());
//		body.put("tid", vo.getTid());
//		
//		HttpEntity entity = new HttpEntity(body, headers);
//		
//		return restTemplate.postForObject(
//							uri, entity, PayOrderResponseVO.class);
//	}
//	//결제 취소(cancel)
//	public PayCancelResponseVO cancel(PayCancelVO vo) throws URISyntaxException {
//		URI uri = new URI("https://open-api.kakaopay.com/online/v1/payment/cancel");
//		
//		Map<String, String> body = new HashMap<>();
//		body.put("cid", kakaoPayProperties.getCid());
//		body.put("tid", vo.getTid());
//		body.put("cancel_amount", String.valueOf(vo.getCancelAmount()));
//		body.put("cancel_tax_free_amount", "0");
//		
//		HttpEntity entity = new HttpEntity(body, headers);
//		
//		return restTemplate.postForObject(
//							uri, entity, PayCancelResponseVO.class);
//	}
	
	@Transactional
    public Long insertDB(PayApproveVO approveVO, PayReadyVO readyVO, CrewDto crewDto, long attachmentNo) {
        log.debug("📌 [insertDB] 결제 DB 등록 시작");

     // 1. 결제 대표 정보 등록
        PayDto payDto = PayDto.builder()
            .payOwner(Long.parseLong(approveVO.getPartnerUserId()))
            .payTid(approveVO.getTid())
            .payName(readyVO.getItemName())
            .payPrice(readyVO.getTotalAmount())
            .build();
        log.debug("✅ [1] 결제 정보 준비 완료 = {}", payDto);

        long payNo = payDao.addPay(payDto);
        log.debug("✅ [1] 결제 정보 저장 완료 payNo = {}", payNo);

        // 2. 결제 상세 정보 등록
        PayDetailDto payDetailDto = PayDetailDto.builder()
            .payDetailOrigin(payNo)
            .payDetailName(crewDto.getCrewName())
            .payDetailPrice(readyVO.getTotalAmount())
            .payDetailStatus('Y')
            .build();
        log.debug("✅ [2] 결제 상세 정보 준비 완료 = {}", payDetailDto);

        payDao.addPayDetail(payDetailDto);
        log.debug("✅ [2] 결제 상세 정보 저장 완료");

        // 3. crew_no 시퀀스 수동 조회
        long crewNo = crewDao.sequence();
        crewDto.setCrewNo(crewNo);
        log.debug("🔁 [3] crew 시퀀스 수동 조회 및 설정 crewNo = {}", crewNo);

        // 4. 모임 등록
        log.debug("🔁 [4] 모임 등록 시작 crewDto = {}", crewDto);
        crewDao.insert(crewDto);
        log.debug("✅ [4] 모임 등록 완료");

        // 5. 이미지 연결
        log.debug("🔁 [5] 이미지 연결 시작 attachmentNo = {}", attachmentNo);
        AttachmentDto saved = attachmentDao.selectOne(attachmentNo);
        log.debug("✅ [5] attachment 조회 완료 = {}", saved);

        crewDao.connect(crewNo, saved.getAttachmentNo());
        log.debug("✅ [5] crew_image 연결 완료");
        
        // 6. 모임장 등록
        long crewMemberNo = crewMemberDao.sequence();
        CrewMemberDto leaderDto = CrewMemberDto.builder()
        	.crewMemberNo(crewMemberNo)
            .crewNo(crewNo)
            .memberNo(Long.parseLong(approveVO.getPartnerUserId()))
            .joinDate(LocalDate.now().toString())
            .leader("Y")
            .build();
        log.debug("🔁 [6] 모임장 등록 시작 leaderDto = {}", leaderDto);

        crewMemberDao.join(leaderDto);
        log.debug("✅ [6] 모임장 등록 완료");
        
        // 7. 채팅방 생성
        long chatRoomNo = chatDao.roomSequence();
        chatDao.insert(ChatDto.builder()
        	.chatRoomNo(chatRoomNo)
        	.chatCrewNo(crewNo)
        	.chatType("CREW") // ← 필수 설정
        	.chatContent("채팅방이 생성되었습니다.")
        	.chatTime(new Timestamp(System.currentTimeMillis()))
        	.chatSender(Long.parseLong(approveVO.getPartnerUserId())) // 생성자
        	.build()
        );
        log.debug("✅ [7] 채팅방 생성 메시지 등록 완료");
        
        // 8. 환영 메시지 삽입
        chatDao.insert(ChatDto.builder()
            .chatRoomNo(chatRoomNo)
            .chatCrewNo(crewNo)
            .chatType("SYSTEM")
            .chatContent("🎉 새로운 모임이 개설되었습니다. 인사해 보세요!")
            .chatTime(new Timestamp(System.currentTimeMillis()))
            .chatSender(Long.parseLong(approveVO.getPartnerUserId()))
            .build()
        );
        log.debug("✅ [8] 환영 메시지 등록 완료");

        log.debug("🎉 [insertDB] 전체 트랜잭션 성공 완료");
        
        return crewDto.getCrewNo();
    }
}