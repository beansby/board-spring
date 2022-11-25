package com.example.board.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.board.entity.Board;
import com.example.board.service.BoardService;


@RestController
public class BoardController {
	
	@Autowired
	BoardService boardService;
	
	// 작성 : 첫번째 방법
	@PostMapping("/writeboard")
	public ResponseEntity<String> writeboard(@RequestParam("writer") String writer,
			@RequestParam("password") String password,
			@RequestParam("subject") String subject,
			@RequestParam("content") String content,
			@RequestParam(name="file", required=false) MultipartFile file) {
		ResponseEntity<String> res = null;
		// 파일이 있으면
		try {
			String filename = null; 
			if (file != null && !file.isEmpty()) {
				// 특정 서버에 저장 : 업로드 폴더 지정 => 파일 업로드 -> 서비스로 옮겨주고 싶음..?
				String path = "C:/Ebina/react-js/board-app/upload-file/";
				// 파일 있을 땐 파일명 지정, 없을땐 null
				filename = file.getOriginalFilename();
				File dFile = new File(path+filename);
				file.transferTo(dFile);
			}
											// id - AI : save 될 때 값을 가져옴 => @AllArgsConstructor
			boardService.writeBoard(new Board (null, writer, password, subject, content, filename));
			
			res = new ResponseEntity<String> ("게시글 저장 성공", HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();	//에러를 콘솔에 찍어주기 위함
			res = new ResponseEntity<String> ("게시글 저장 실패", HttpStatus.BAD_REQUEST);
		}
		return res;
	} 
	
	// 작성 : 두번째 방법, 테이블간 연동되지 않게 하기 위해 Embedded 
	@PostMapping("/writeboard2")
	public ResponseEntity<String> writeboard2 (@ModelAttribute Board board, @RequestParam(name="file", required =false) MultipartFile file){
		ResponseEntity<String> res = null;
		try {
			boardService.writeBoard2(board, file);
			res = new ResponseEntity<String> ("게시글 저장 성공", HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();	
			res = new ResponseEntity<String> ("게시글 저장 실패", HttpStatus.BAD_REQUEST);
		}
		return res;
	}
	
	// 상세페이지 조회
	@GetMapping("/boarddetail/{id}")
	public ResponseEntity<Board> detailboard (@PathVariable Integer id){
		ResponseEntity<Board> res = null;
		try {
			Board board = boardService.detailBoard(id);
			res = new ResponseEntity<Board> (board, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();	
			res = new ResponseEntity<Board> (HttpStatus.BAD_REQUEST);
		}
		return res;
	}
	
	// 상세페이지 : 이미지 가져오기
	@GetMapping("/img/{filename}")
	public void imageView(@PathVariable String filename, HttpServletResponse response) {
		try {
			String path = "C:/Ebina/react-js/board-app/upload-file/";
			FileInputStream fis = new FileInputStream(path+filename);
			OutputStream out = response.getOutputStream();
			FileCopyUtils.copy(fis, out);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();	
		}
	}
	
	// 글 목록 
	@GetMapping("/boardlist")
	public ResponseEntity<List<Board>> findList() {
		ResponseEntity<List<Board>> res = null;
		List<Board> boards = null;
		try {
			boards = boardService.findList();
			res = new ResponseEntity<List<Board>> (boards, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();	
			res = new ResponseEntity<List<Board>> (boards, HttpStatus.BAD_REQUEST);
		}
		return res;
	}

	// 글 수정
	@PutMapping("/updateboard/{id}")
	public ResponseEntity<String> updateBoard(@PathVariable Integer id, @RequestParam("subject") String subject, @RequestParam("content") String content){
		ResponseEntity<String> res = null;
		try {
			Board board = boardService.detailBoard(id);
			boardService.updateBoard(id, subject, content);
			res = new ResponseEntity<String>("게시글 수정 성공", HttpStatus.OK);
		} catch (Exception e){
			e.printStackTrace();
			res = new ResponseEntity<String>("게시글 수정 실패", HttpStatus.BAD_REQUEST);
		}
		return res;
	}
	

}
