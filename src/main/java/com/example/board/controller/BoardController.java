package com.example.board.controller;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.board.entity.Board;
import com.example.board.service.BoardService;


@RestController
public class BoardController {
	
	@Autowired
	BoardService boardService;
	
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
				String path = "C:/Ebina/upload-file/";
				// 파일 있을 땐 파일명 지정, 없을땐 null
				filename = file.getOriginalFilename();
				File dFile = new File(path+filename);
				file.transferTo(dFile);
			}
											// id - AI : save 될 때 값을 가져옴 => @AllArgsConstructor
			boardService.writeBoard(new Board(null, writer, password, subject, content, filename));
			
			res = new ResponseEntity<String> ("게시글 저장 성공", HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();	//에러를 콘솔에 찍어주기 위함
			res = new ResponseEntity<String> ("게시글 저장 실패", HttpStatus.BAD_REQUEST);
		}
		return res;
	} 
	

}
