package com.kh.demo.restcontroller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.demo.dto.CrewDto;
import com.kh.demo.error.TargetNotFoundException;
import com.kh.demo.service.PayService;
import com.kh.demo.service.TokenService;
import com.kh.demo.vo.CrewPayRequestVO;
import com.kh.demo.vo.pay.PayApproveResponseVO;
import com.kh.demo.vo.pay.PayApproveVO;
import com.kh.demo.vo.pay.PayReadyResponseVO;
import com.kh.demo.vo.pay.PayReadyVO;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/pay")
public class PayRestController {
	@Autowired
	private PayService payService;
	@Autowired
	private TokenService tokenService;

	// Flash value를 저장하기 위한 저장소
	private Map<String, PayApproveVO> flashMap = Collections.synchronizedMap(new HashMap<>());// thread-safe

	// 현재 거래번호가 완료되면 돌아갈 페이지 주소를 저장
	private Map<String, String> returnUrlMap = Collections.synchronizedMap(new HashMap<>());// thread-safe

	// 모임 정보를 결제 성공 전까지 임시저장
	private Map<String, CrewDto> crewMap = Collections.synchronizedMap(new HashMap<>());// thread-safe

	// 결제준비 요청정보를 저장
	private Map<String, PayReadyVO> readyMap = Collections.synchronizedMap(new HashMap<>());// thread-safe

	@PostMapping("/ready")
	public PayReadyResponseVO ready(@RequestBody CrewPayRequestVO requestVO,
	                                 @RequestHeader("Authorization") String bearerToken,
	                                 @RequestHeader("Frontend-URL") String frontendUrl) throws URISyntaxException {

		log.debug("📦 Frontend-URL received = {}", frontendUrl);
		CrewDto crewDto = requestVO.getCrewDto();

	    PayReadyVO vo = new PayReadyVO();
	    vo.setPartnerOrderId(UUID.randomUUID().toString());

	    long memberNo = tokenService.parseBearerToken(bearerToken);
	    vo.setPartnerUserId(String.valueOf(memberNo));

	    if (crewDto.getCrewName() == null || crewDto.getCrewName().trim().isEmpty()) {
	        throw new IllegalArgumentException("itemName은 필수입니다");
	    }
	    vo.setItemName(crewDto.getCrewName());

	    vo.setTotalAmount(requestVO.getTotalAmount());

	    PayReadyResponseVO response = payService.ready(vo);

	    // 임시 저장
	    flashMap.put(vo.getPartnerOrderId(), PayApproveVO.builder()
	            .partnerOrderId(vo.getPartnerOrderId())
	            .partnerUserId(vo.getPartnerUserId())
	            .tid(response.getTid())
	            .build());
	    returnUrlMap.put(vo.getPartnerOrderId(), frontendUrl);
	    crewMap.put(vo.getPartnerOrderId(), crewDto);
	    readyMap.put(vo.getPartnerOrderId(), vo);

	    return response;
	}


	@GetMapping("/success/{partnerOrderId}")
	public void success(@PathVariable String partnerOrderId,
            @RequestParam("pg_token") String pgToken,
            HttpServletResponse response) throws URISyntaxException, IOException {

		// 1. 결제 승인정보 준비
		PayApproveVO vo = flashMap.remove(partnerOrderId);
		if(vo == null) throw new TargetNotFoundException("유효하지 않은 결제 정보");
		
		vo.setPgToken(pgToken);
		
		// 2. 결제 승인 요청 (카카오 API)
		PayApproveResponseVO approveResponse = payService.approve(vo);
		log.debug("approve = {}", approveResponse);
		
		// 3. 임시 저장된 정보 꺼냄
		PayReadyVO readyVO = readyMap.remove(partnerOrderId);
		CrewDto crewDto = crewMap.remove(partnerOrderId);
		
		// 4. DB 등록 (pay + pay_detail + crew)
		payService.insertDB(vo, readyVO, crewDto);
		
		// 5. 성공 페이지로 리다이렉트
		String returnUrl = returnUrlMap.remove(partnerOrderId);
		if (returnUrl == null || returnUrl.isBlank()) {
		    returnUrl = "http://localhost:5173";
		}
		response.sendRedirect("http://localhost:5173/crew/create-finish");
	}
//	
//	@GetMapping("/buy/cancel/{partnerOrderId}")
//	public void cancel(@PathVariable String partnerOrderId,
//								HttpServletResponse response) throws IOException {
//		flashMap.remove(partnerOrderId);
//		crewMap.remove(partnerOrderId);
//		readyMap.remove(partnerOrderId);
//		
//		String url = returnUrlMap.remove(partnerOrderId);
//		response.sendRedirect(url+"/cancel");
//	}
//	
//	@GetMapping("/buy/fail/{partnerOrderId}")
//	public void fail(@PathVariable String partnerOrderId,
//			HttpServletResponse response) throws IOException {
//		flashMap.remove(partnerOrderId);
//		crewMap.remove(partnerOrderId);
//		readyMap.remove(partnerOrderId);
//		
//		String url = returnUrlMap.remove(partnerOrderId);
//		response.sendRedirect(url+"/fail");
//	}
}
