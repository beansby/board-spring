package com.example.board.service;


import java.io.File;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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
	public void writeBoard2(Board board, MultipartFile file) throws Exception {
		String filename = null;
		if (file != null && !file.isEmpty()) {
			String path = "C:/Ebina/upload-file/";
			filename = file.getOriginalFilename();
			File dFile = new File(path+filename);
			file.transferTo(dFile);
		}
		board.setFilename(filename);
		boardRepository.save(board);
	}
	
	// 상세페이지
	public Board detailBoard (Integer id) throws Exception {
		Optional<Board> oboard = boardRepository.findById(id);
		if(oboard.isPresent()) 
			return oboard.get();
		throw new Exception("글 번호 오류");
	}
	
	// 글 목록
	public List<Board> findList() throws Exception{
		// List를 가져올 때 최신글이 위로 오게 Sorting
		List<Board> boards = boardRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
		return boards;		
	}

}
