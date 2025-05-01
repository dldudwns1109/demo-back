package com.kh.demo.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.demo.dto.PayDetailDto;
import com.kh.demo.dto.PayDto;
import com.kh.demo.vo.PayTotalVO;

@Repository
public class PayDao {
	
	@Autowired
	private SqlSession sqlSession;
	
	public long addPay(PayDto payDto) {
		long payNo = sqlSession.selectOne("pay.paySequence");
		payDto.setPayNo(payNo);
		sqlSession.insert("pay.addPay", payDto);
		return payNo;
	}
	public long addPayDetail(PayDetailDto payDetailDto) {
		long payDetailNo = sqlSession.selectOne("pay.payDetailSequence");
		payDetailDto.setPayDetailNo(payDetailNo);
		sqlSession.insert("pay.addPayDetail", payDetailDto);
		return payDetailNo;
	}
	
	public List<PayDto> listPay() {
		return sqlSession.selectList("pay.listPay");
	}
	public List<PayDetailDto> listPayDetail(long payDetailOrigin) {
		return sqlSession.selectList("pay.listPayDetail", payDetailOrigin);
	}
	
	public List<PayTotalVO> listTotalManual() {
		List<PayTotalVO> results = new ArrayList<>();
		
		List<PayDto> list = this.listPay();//[1]
		for(PayDto payDto : list) {//[2]
			List<PayDetailDto> details = this.listPayDetail(payDto.getPayNo());
			
			PayTotalVO vo = new PayTotalVO();//[3]
			vo.setPayDto(payDto);
			vo.setPayList(details);
			
			results.add(vo);
		}
		return results;
	}
	public List<PayTotalVO> listTotalAuto() {
		return sqlSession.selectList("pay.listPayAuto");
	}
	public List<PayTotalVO> listTotalAuto(String userId) {
		return sqlSession.selectList("pay.listPayAuto", userId);
	}
	
	public PayDto selectOne(long payNo) {
		return sqlSession.selectOne("pay.findPay", payNo);
	}
	
	public PayDetailDto selectDetailOne(long payDetailNo) {
		return sqlSession.selectOne("pay.findDetail", payDetailNo);
	}
}
