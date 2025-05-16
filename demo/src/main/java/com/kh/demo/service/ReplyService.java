package com.kh.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.demo.dao.ReplyDao;
import com.kh.demo.dto.ReplyDto;
import com.kh.demo.dto.ReplyListDto;

@Service
public class ReplyService {

    @Autowired
    private ReplyDao replyDao;

    public ReplyListDto writeAndReturn(ReplyDto replyDto) {
        replyDao.insert(replyDto);
        return replyDao.selectLatestByWriter(replyDto.getReplyWriter(), replyDto.getReplyOrigin());
    }

    public List<ReplyListDto> list(Long replyOrigin) {
        return replyDao.selectListWithMemberInfo(replyOrigin);
    }

    public boolean edit(ReplyDto replyDto) {
        return replyDao.update(replyDto);
    }

    public boolean delete(Long replyNo, Long replyOrigin, Long userNo) {
        return replyDao.delete(replyNo, replyOrigin, userNo);
    }
    
    public void deleteRepliesByCrewAndWriter(Long crewNo, Long memberNo) {
        replyDao.deleteByCrewAndWriter(crewNo, memberNo);
    }
}

