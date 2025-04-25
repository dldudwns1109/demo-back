package com.kh.demo.service;

import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.demo.configuration.TokenProperties;
import com.kh.demo.dao.TokenDao;
import com.kh.demo.dto.MemberDto;
import com.kh.demo.dto.TokenDto;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Service
public class TokenService {
	
	@Autowired
	private TokenProperties tokenProperties;
	
	@Autowired
	private TokenDao tokenDao;
	
	public String generateAccessToken(MemberDto memberDto) {
		return generateToken((int) tokenProperties.getAccessLimit(), 
				memberDto);
	}
	
	public String generateRefreshToken(MemberDto memberDto) {
		String tokenValue = generateToken(
								(int) tokenProperties.getRefreshLimit(), 
								memberDto
							);
		tokenDao.insert(
			TokenDto.builder()
				.tokenNo(tokenDao.sequence())
				.tokenTarget(memberDto.getMemberId())
				.tokenValue(tokenValue)
			.build()
		);
		
		return tokenValue;
	}
	
	public String generateToken(int tokenLimit, MemberDto memberDto) {
		Calendar c = Calendar.getInstance();
		Date now = c.getTime();
		c.add(Calendar.MINUTE, tokenLimit);
		Date limit = c.getTime();
		
		return Jwts.builder()
					.signWith(tokenProperties.getKey())
					.expiration(limit)
					.issuer(tokenProperties.getIssuer())
					.issuedAt(now)
					.claim("memberId", memberDto.getMemberId())
				.compact();
	}
	
	public String parse(String token) {
		Claims claims = (Claims) Jwts.parser()
									.verifyWith(tokenProperties.getKey())
									.requireIssuer(tokenProperties.getIssuer())
									.build()
									.parse(token)
									.getPayload();
		
		return (String) claims.get("memberId");
	}
	
	public String parseBearerToken(String bearerToken) {
		if (bearerToken == null || !bearerToken.startsWith("Bearer ")) 
			throw new RuntimeException();
		
		return parse(bearerToken.substring(7));
	}
	
	public boolean checkBearerToken(String memberId, String bearerToken) {
		TokenDto tokenDto = tokenDao.find(
								TokenDto.builder()
									.tokenTarget(memberId)
									.tokenValue(bearerToken.substring(7))
								.build()
							);
		if (tokenDto != null) {
			tokenDao.delete(tokenDto);
			return true;
		}
		return false;
	}
	
	public String generateAccessToken(String memberId) {
		return generateAccessToken(
					MemberDto.builder()
						.memberId(memberId)
					.build()
				);
	}
	
	public String generateRefreshToken(String memberId) {
		return generateRefreshToken(
					MemberDto.builder()
						.memberId(memberId)
					.build()
				);
	}
}
