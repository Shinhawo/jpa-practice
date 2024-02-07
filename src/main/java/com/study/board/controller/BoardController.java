package com.study.board.controller;

import com.study.board.entity.Board;
import com.study.board.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class BoardController {

    @Autowired
    private BoardService boardService;

    @GetMapping("/board/write") // localhost:8080/board/write
    public String boardWriteForm(){

        return "boardwrite";
    }

    @PostMapping("/board/writepro")
    public String boardWritePro(Board board, Model model, MultipartFile file) throws Exception {
        System.out.println("제목 : " + board.getTitle());
        System.out.println("내용 : " + board.getContent());

        if (board.getTitle().equals("") || board.getContent().equals("")) {
            model.addAttribute("message", "제목 또는 내용을 작성해 주십시오.");
            model.addAttribute("searchUrl", "/board/write");
            return "message";
        } else {
            boardService.write(board, file);
            model.addAttribute("message", "글 작성이 완료되었습니다.");
            model.addAttribute("searchUrl", "/board/list");
            return "message";
        }
    }

    @GetMapping("/board/list")           // sort는 service에서도 설정 가능
    public String boardList(Model model,
                            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                            String searchKeyword) {

        // boardSerice에서 반환된 데이터(boardService.boardList())를 list라는 이름으로 받아서
        // boardList에 넘기겠다.
        // model.addAttribute("list", boardService.boardList(pageable));

        Page<Board> list = null;

        if(searchKeyword == null) {
            list = boardService.boardList(pageable);
        } else {
            list = boardService.boardSearchList(searchKeyword,pageable);
        }

        int nowPage = list.getPageable().getPageNumber() + 1; // 현재 페이지 pageable.getPageNumber() 도 됨
        int startPage = Math.max(nowPage - 4, 1);
        int endPage = Math.min(nowPage + 5, list.getTotalPages());

        model.addAttribute("list", list);
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "boardList";
    }

    @GetMapping("/board/view")
    public String boardView(Model model, Integer id) {

        model.addAttribute("board", boardService.boardView(id));
        return "boardDetail";
    }

    @GetMapping("/board/delete")
    public String boardDelete (Integer id, Model model) {
        boardService.boardDelete(id);

        model.addAttribute("message", "글 삭제가 완료되었습니다.");
        model.addAttribute("searchUrl", "/board/list");

        return "message";
    }

    @GetMapping("/board/modify/{id}") // 삭제 방법으로도, 이 방법으로도 가능
    public String boardModify(@PathVariable("id") Integer id, Model model){
        // PathVaiable : url이 들어왔을 때  {} 부분이 인식이 되서 Integer 형태의 id로 들어온다

        model.addAttribute("board", boardService.boardView(id));

        return "boardModify";
    }

    @PostMapping("/board/update/{id}")
    public String boardUpdate(@PathVariable("id") Integer id, Board board, Model model, MultipartFile file) throws Exception{

        Board boardTemp = boardService.boardView(id);
        boardTemp.setTitle(board.getTitle());
        boardTemp.setContent(board.getContent());

        if (board.getTitle().equals("") || board.getContent().equals("")) {
            model.addAttribute("message", "제목 또는 내용을 작성해 주십시오.");
            model.addAttribute("searchUrl", "/board/modify/"+board.getId());
            return "message";
        } else {
            boardService.write(boardTemp, file);
            model.addAttribute("message", "수정이 완료되었습니다.");
            model.addAttribute("searchUrl", "/board/view?id="+board.getId());
            return "message";
        }
        
    }
}
