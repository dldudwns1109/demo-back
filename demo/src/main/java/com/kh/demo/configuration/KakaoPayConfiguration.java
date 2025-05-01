package com.kh.demo.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

@Configuration
public class KakaoPayConfiguration {
	
	@Autowired
	private KakaoPayProperties kakaoPayProperties;

	@Bean
	public RestTemplate template() {
		RestTemplate template = new RestTemplate();
		//추가할 설정이 있다면 작성
		return template;
	}
	
	@Bean
	public HttpHeaders headers() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "SECRET_KEY "+kakaoPayProperties.getSecretKey());
		headers.add("Content-Type", "application/json");
		return headers;
	}
	
}
