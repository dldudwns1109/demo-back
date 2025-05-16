package com.kh.demo.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.demo.dao.ReplyDao;
import com.kh.demo.dto.ReplyDto;
import com.kh.demo.dto.ReplyListDto;
import com.kh.demo.service.ReplyService;

@CrossOrigin
@RestController
@RequestMapping("/api/reply")
public class ReplyRestController {

    @Autowired
    private ReplyService replyService;
    @Autowired
    private ReplyDao replyDao;

    // 댓글 작성 + 작성한 댓글 반환
    @PostMapping
    public ReplyListDto write(@RequestBody ReplyDto replyDto) {
        return replyService.writeAndReturn(replyDto);
    }

    // 댓글 목록 조회
    @GetMapping("/{replyOrigin}")
    public List<ReplyListDto> list(@PathVariable Long replyOrigin) {
        return replyService.list(replyOrigin);
    }

    // 댓글 수정
    @PutMapping("/{replyNo}")
    public boolean edit(@PathVariable Long replyNo, @RequestBody ReplyDto replyDto) {
        replyDto.setReplyNo(replyNo);
        return replyService.edit(replyDto);
    }

    // 댓글 삭제
//    @DeleteMapping("/{replyNo}")
//    public boolean remove(@PathVariable Long replyNo, @RequestParam Long replyOrigin) {
//        return replyService.delete(replyNo, replyOrigin);
//    }
    @DeleteMapping("/{replyNo}")
    public boolean remove(
            @PathVariable Long replyNo, 
            @RequestParam Long replyOrigin, 
            @RequestParam Long userNo) {

        return replyService.delete(replyNo, replyOrigin, userNo);
    }
    @GetMapping("/count/{boardNo}")
    public int getReplyCount(@PathVariable("boardNo") long boardNo) {
        return replyDao.getReplyCount(boardNo);
    }
    

}

