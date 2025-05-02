package com.kh.demo.restcontroller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kh.demo.dto.CrewDto;
import com.kh.demo.error.TargetNotFoundException;
import com.kh.demo.service.AttachmentService;
import com.kh.demo.service.PayService;
import com.kh.demo.service.TokenService;
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
	@Autowired
	private AttachmentService attachmentService;

	// Flash value를 저장하기 위한 저장소
	private Map<String, PayApproveVO> flashMap = Collections.synchronizedMap(new HashMap<>());// thread-safe

	// 현재 거래번호가 완료되면 돌아갈 페이지 주소를 저장
	private Map<String, String> returnUrlMap = Collections.synchronizedMap(new HashMap<>());// thread-safe

	// 모임 정보를 결제 성공 전까지 임시저장
	private Map<String, CrewDto> crewMap = Collections.synchronizedMap(new HashMap<>());// thread-safe

	// 결제준비 요청정보를 저장
	private Map<String, PayReadyVO> readyMap = Collections.synchronizedMap(new HashMap<>());// thread-safe
	
	private Map<String, Long> attachmentNoMap = Collections.synchronizedMap(new HashMap<>());
	
	@PostMapping(value = "/ready", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public PayReadyResponseVO ready(
	    @ModelAttribute CrewDto crewDto,
	    @RequestParam("totalAmount") int totalAmount,
	    @RequestParam("attach") MultipartFile attach,
	    @RequestHeader(value = "Authorization", required = false) String bearerToken,
	    @RequestHeader("Frontend-URL") String frontendUrl
	) throws URISyntaxException, IOException {

	    // 1. 주문 정보 준비
	    String partnerOrderId = UUID.randomUUID().toString();
	    long memberNo = tokenService.parseBearerToken(bearerToken);

	    PayReadyVO vo = PayReadyVO.builder()
	        .partnerOrderId(partnerOrderId)
	        .partnerUserId(String.valueOf(memberNo))
	        .itemName(crewDto.getCrewName())
	        .totalAmount(totalAmount)
	        .build();

	    // 2. 카카오페이 결제 준비
	    PayReadyResponseVO response = payService.ready(vo);

	    // 3. 이미지 먼저 저장 (attachmentNo 확보)
	    long attachmentNo = attachmentService.save(attach).getAttachmentNo();

	    // 4. 결제 정보 및 모임 정보 임시 저장 (Flash Map)
	    flashMap.put(partnerOrderId, PayApproveVO.builder()
	        .partnerOrderId(partnerOrderId)
	        .partnerUserId(vo.getPartnerUserId())
	        .tid(response.getTid())
	        .build());

	    crewMap.put(partnerOrderId, crewDto);
	    readyMap.put(partnerOrderId, vo);
	    returnUrlMap.put(partnerOrderId, frontendUrl);
	    attachmentNoMap.put(partnerOrderId, attachmentNo);

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
		
		// 3. 임시 저장된 정보 가져오기
	    PayReadyVO readyVO = readyMap.remove(partnerOrderId);
	    CrewDto crewDto = crewMap.remove(partnerOrderId);
	    Long attachmentNo = attachmentNoMap.remove(partnerOrderId);
	    String returnUrl = returnUrlMap.remove(partnerOrderId);

	    // 4. 결제 및 모임 + 모임장 DB 등록
	    payService.insertDB(vo, readyVO, crewDto, attachmentNo);

	    // 5. 리다이렉트
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
