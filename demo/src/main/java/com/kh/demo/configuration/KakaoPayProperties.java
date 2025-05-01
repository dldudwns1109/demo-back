package com.kh.demo.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data @Component @ConfigurationProperties(prefix = "custom.kakao")
public class KakaoPayProperties {
	private String secretKey;//custom.kakao.secret-key
	private String cid;//custom.kakao.cid
}
