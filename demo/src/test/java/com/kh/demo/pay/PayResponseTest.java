package com.kh.demo.pay;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

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
public class PayResponseTest {
	@Autowired
	private KakaoPayProperties kakaoPayProperties;
	
	@Test
	public void test() throws URISyntaxException {
		//(1) 전송 도구 생성
		RestTemplate restTemplate = new RestTemplate();
		
		//(2) 전송 주소 확인
		//POST /online/v1/payment/approve HTTP/1.1
		//Host: open-api.kakaopay.com
		URI uri = new URI("https://open-api.kakaopay.com/online/v1/payment/approve");
		
		//(3) 헤더 설정
		//Authorization: SECRET_KEY ${SECRET_KEY}
		//Content-Type: application/json
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "SECRET_KEY "+kakaoPayProperties.getSecretKey());
		headers.add("Content-Type", "application/json");
		
		//(4) 바디 설정
		Map<String, String> body = new HashMap<>();
		body.put("cid", kakaoPayProperties.getCid());
		body.put("tid", "T812ce09555b0f370439");
		body.put("partner_order_id", "08ef0b74-4a55-444d-be16-0c4c2b736463");
		body.put("partner_user_id", "testuser1");
		body.put("pg_token", "5458ba2ad7761bddc428");
		
		//(4+3)
		//HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);
		HttpEntity entity = new HttpEntity(body, headers);
		
		//2 + (4+3)
		//restTemplate.postForObject(주소객체, 헤더+바디, 결과물의 형태);
		Map response = restTemplate.postForObject(uri, entity, Map.class);
		//log.debug("response = {}", response);
		for(Object name : response.keySet()) {
			Object value = response.get(name);
			log.debug("{} = {}", name, value);
		}
	}
}