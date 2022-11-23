package com.example.board.service;


import java.io.File;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.board.entity.Board;
import com.example.board.repository.BoardRepository;

@Service
public class BoardService {
	
	@Autowired
	BoardRepository boardRepository;
	
	// 작성 : 첫번째 방법 
	public void writeBoard (Board board) throws Exception {
		boardRepository.save(board);
	}
	
	// 작성 : 두번째 방법
	public void writeBoard2(Board board) throws Exception {
		MultipartFile file = board.getFile();
		if (file != null && !file.isEmpty()) {
			String path = "C:/Ebina/upload-file/";
			String filename = file.getOriginalFilename();
			File dFile = new File(path+filename);
			file.transferTo(dFile);
			board.setFilename(filename);
		}
		boardRepository.save(board);
	}
	
	public Board detailBoard (Integer id) throws Exception {
		Optional<Board> oboard = boardRepository.findById(id);
		if(oboard.isPresent()) 
			return oboard.get();
		throw new Exception("글 번호 오류");
	}

}
