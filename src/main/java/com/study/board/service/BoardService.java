package com.study.board.service;

import com.study.board.entity.Board;
import com.study.board.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.UUID;

@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;

    public void write(Board board, MultipartFile file) throws Exception{

        String projectPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\files";

        UUID uuid = UUID.randomUUID(); // 랜덤으로 식별자 만들기

        String fileName = uuid + "_" + file.getOriginalFilename();

        //  File saveFile = new File(이 경로에 넣어줄거, 이 이름으로);
        File saveFile = new File(projectPath, fileName);

        file.transferTo(saveFile);

        board.setFilename(fileName);
        board.setFilepath("/files/" + fileName);

        boardRepository.save(board);
    }

    //public List<Board> boardList(Pageable pageable) {
    // 기존에 findAll() 에 매개변수가 없는 경우에는 List<>로 받지만
    // 이경우에는 pageable을 매개변수로 받기 떄문에 Page<> 로 받는다
    public Page<Board> boardList(Pageable pageable) {

        return boardRepository.findAll(pageable);
       // return boardRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    public Page<Board> boardSearchList(String searchKeyword, Pageable pageable){
        return boardRepository.findByTitleContaining(searchKeyword, pageable);
    }

    public Board boardView(Integer id) {

        // findById는 optional 값으로 받아오기 때문에 get()을 붙여준다.
        return boardRepository.findById(id).get();
    }

    public void boardDelete(Integer id) {

        boardRepository.deleteById(id);
    }


}
