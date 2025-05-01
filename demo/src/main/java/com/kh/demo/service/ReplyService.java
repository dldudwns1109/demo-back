package com.kh.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.demo.dao.ReplyDao;
import com.kh.demo.dto.ReplyDto;

@Service
public class ReplyService {

	@Autowired
	private ReplyDao replyDao;
	
	 // 댓글 작성
    public void write(ReplyDto replyDto) {
        replyDao.insert(replyDto);
    }

    // 게시글에 달린 댓글 목록 조회
    public List<ReplyDto> list(Long replyOrigin) {
        return replyDao.selectListByOrigin(replyOrigin);
    }

    // 댓글 수정
    public boolean edit(ReplyDto replyDto) {
        return replyDao.update(replyDto);
    }

    // 댓글 삭제
    public boolean delete(Long replyNo, Long replyOrigin) {
        return replyDao.delete(replyNo, replyOrigin);
    }

    // 특정 게시글 댓글 수
    public int count(Long replyOrigin) {
        return replyDao.countByOrigin(replyOrigin);
    }
}
