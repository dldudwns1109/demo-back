package com.kh.demo.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.kh.demo.configuration.KakaoPayProperties;
import com.kh.demo.dao.CrewDao;
import com.kh.demo.dao.PayDao;
import com.kh.demo.dto.CrewDto;
import com.kh.demo.dto.PayDetailDto;
import com.kh.demo.dto.PayDto;
import com.kh.demo.vo.pay.PayApproveResponseVO;
import com.kh.demo.vo.pay.PayApproveVO;
import com.kh.demo.vo.pay.PayReadyResponseVO;
import com.kh.demo.vo.pay.PayReadyVO;

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
	
//	//결제DB에 등록
	public void insertDB(PayApproveVO approveVO, PayReadyVO readyVO, CrewDto crewDto) {
		// 1. 결제 대표정보 등록
		PayDto payDto = PayDto.builder()
				.payOwner(approveVO.getPartnerUserId()) // memberNo (String)
			    .payTid(approveVO.getTid())             // 거래번호
			    .payName(readyVO.getItemName())         // 상품명(모임 이름)
			    .payPrice(readyVO.getTotalAmount())     // 총 결제 금액
		    .build();
	
		long payNo = payDao.addPay(payDto); // 시퀀스 생성 + insert
	
		// 2. 결제 상세정보 등록 (단일 모임)
	    PayDetailDto payDetailDto = PayDetailDto.builder()
		        .payDetailOrigin(payNo)
		        .payDetailName(crewDto.getCrewName())
		        .payDetailPrice(readyVO.getTotalAmount())
		        .payDetailStatus('Y')
	        .build();
	
	    payDao.addPayDetail(payDetailDto); // 시퀀스 생성 + insert
	
	    // 3. 모임(Crew) 등록
	    crewDao.insert(crewDto); // 이 부분도 insert 성공되게만 만들면 완벽!
	}
}
