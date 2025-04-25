package com.kh.demo.configuration;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.security.Keys;
import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "custom.token")
public class TokenProperties {
	private String issuer;
	private String secretKey;
	private long accessLimit;
	private long refreshLimit;
	private long renewalLimit;
	
	public SecretKey getKey() {
		return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
	}
}
