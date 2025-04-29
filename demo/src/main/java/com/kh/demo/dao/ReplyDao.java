package com.kh.demo.dao;

import java.util.List;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.kh.demo.dto.ReplyDto;

@Repository
public class ReplyDao {

    @Autowired
    private SqlSession sqlSession;

    public void insert(ReplyDto replyDto) {
        sqlSession.insert("reply.insert", replyDto);
        sqlSession.update("reply.updateBoardReplyUp", replyDto.getReplyOrigin()); // 댓글수 +1
    }

    public List<ReplyDto> selectListByOrigin(Long replyOrigin) {
        return sqlSession.selectList("reply.selectListByOrigin", replyOrigin);
    }

    public boolean update(ReplyDto replyDto) {
        return sqlSession.update("reply.update", replyDto) > 0;
    }

    public boolean delete(Long replyNo, Long replyOrigin) {
        int result = sqlSession.delete("reply.delete", replyNo);
        if (result > 0) {
            sqlSession.update("reply.updateBoardReplyDown", replyOrigin); // 댓글수 -1
        }
        return result > 0;
    }

    public int countByOrigin(Long replyOrigin) {
        return sqlSession.selectOne("reply.countByOrigin", replyOrigin);
    }
}
