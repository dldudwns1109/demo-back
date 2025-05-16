package com.kh.demo.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.kh.demo.dto.ReplyDto;
import com.kh.demo.dto.ReplyListDto;

@Repository
public class ReplyDao {

    @Autowired
    private SqlSession sqlSession;

    public void insert(ReplyDto replyDto) {
        sqlSession.insert("reply.insert", replyDto);
        sqlSession.update("reply.updateBoardReplyUp", replyDto.getReplyOrigin());
    }

    public List<ReplyListDto> selectListWithMemberInfo(Long replyOrigin) {
        return sqlSession.selectList("reply.selectListWithMemberInfo", replyOrigin);
    }

    public ReplyListDto selectLatestByWriter(Long replyWriter, Long replyOrigin) {
        Map<String, Object> param = new HashMap<>();
        param.put("replyWriter", replyWriter);
        param.put("replyOrigin", replyOrigin);
        return sqlSession.selectOne("reply.selectLatestByWriter", param);
    }

    public boolean update(ReplyDto replyDto) {
        return sqlSession.update("reply.update", replyDto) > 0;
    }

//    public boolean delete(Long replyNo, Long replyOrigin) {
//        int result = sqlSession.delete("reply.delete", replyNo);
//        if (result > 0) {
//            sqlSession.update("reply.updateBoardReplyDown", replyOrigin);
//        }
//        return result > 0;
//    }
    
    public boolean delete(Long replyNo, Long replyOrigin, Long userNo) {
        Map<String, Object> param = new HashMap<>();
        param.put("replyNo", replyNo);
        param.put("replyOrigin", replyOrigin);
        param.put("userNo", userNo);

        int result = sqlSession.delete("reply.delete", param);

        if (result > 0) {
            sqlSession.update("reply.updateBoardReplyDown", replyOrigin);
        }

        return result > 0;
    }



    
}

