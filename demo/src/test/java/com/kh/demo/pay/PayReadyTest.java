package com.kh.demo.pay;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import com.kh.demo.configuration.KakaoPayProperties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
public class PayReadyTest {
	@Autowired
	private KakaoPayProperties kakaoPayProperties;
	
	@Test
	public void test() throws URISyntaxException {
		String partnerOrderId = UUID.randomUUID().toString();
		String partnerUserId = "testuser1";
		String itemName = "축구하러가자 모임";
		int totalAmount = 20000;
		
		//Spring Boot에서 서버로 메세지를 보내는 방법
		//[1] RestTemplate을 이용하는 방법 (지금도 가능)
		//[2] WebClient를 이용하는 방법 (spring-webflux 필요)
		
		//(1) 전송 도구 생성
		RestTemplate restTemplate = new RestTemplate();
		
		//(2) 전송 주소 확인
		//POST /online/v1/payment/ready HTTP/1.1
		//Host: open-api.kakaopay.com
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
		body.put("partner_order_id", partnerOrderId);
		body.put("partner_user_id", partnerUserId);
		body.put("item_name", itemName);
		body.put("quantity", "1");
		body.put("total_amount", String.valueOf(totalAmount));
		body.put("tax_free_amount", "0");
		//카카오페이 개발자센터 플랫폼에 등록된 주소로 시작해야함
		body.put("approval_url", "http://localhost:8080/success");
		body.put("cancel_url", "http://localhost:8080/cancel");
		body.put("fail_url", "http://localhost:8080/fail");
		
		//(4+3)
		//HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);
		HttpEntity entity = new HttpEntity(body, headers);
		
		//2 + (4+3)
		//restTemplate.postForObject(주소객체, 헤더+바디, 결과물의 형태);
		Map response = restTemplate.postForObject(uri, entity, Map.class);
//		log.debug("response = {}", response);
		System.out.println("partnerOrderId="+partnerOrderId);
		System.out.println("partnerUserId="+partnerUserId);
		System.out.println("tid="+response.get("tid"));
		System.out.println("url="+response.get("next_redirect_pc_url"));
//		for(Object name : response.keySet()) {
//			Object value = response.get(name);
//			System.out.println(name+"="+value);
//		}
	}
}
