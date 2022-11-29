package com.example.board.service;


import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.example.board.vo.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
			String path = "C:/Ebina/react-js/board-app/upload-file/";
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

	// 글 수정
	public void updateBoard (Integer id, String subject, String content) throws Exception {
		Optional<Board> oboard = boardRepository.findById(id);
		if(oboard.isEmpty()){
			throw new Exception("글 조회 오류");
		}
		Board board = oboard.get();
		board.setSubject(subject);
		board.setContent(content);
		boardRepository.save(board);
	}

	// 글 삭제
	public Integer deleteBoard(Integer id, String password) throws Exception {
		Optional<Board> oboard = boardRepository.findById(id);
		// id가 없으면 -1
		if(oboard.isEmpty()){
			return -1;
		}
		// 비밀번호가 일치하지 않으면 -2
		Board board = oboard.get();
		if (!Objects.equals(board.getPassword(), password)) {
			return -2;
		}
		// 삭제 완료됐으면 0
		boardRepository.delete(board);
		return 0;
	}

	// 페이징 처리
	public List<Board> pageBoard(PageInfo pageInfo) throws Exception {
		// page는 컨트롤러에서 넘겨줌 : 0부터 시작하기 때문에 -1
		PageRequest pageRequest = PageRequest.of(pageInfo.getCurPage() -1, 5, Sort.by(Sort.Direction.DESC, "id"));
		Page<Board> pages = boardRepository.findAll(pageRequest);

		int maxPage = pages.getTotalPages();
		// 정수의 값을 가져오기 위해 10으로 나눈 후 다시 10을 곱해줌 (시작페이지는 11, 21, 31... 이기 때문에 마지막에 +1을 해줌)
		// 페이지처리가 5개씩 현재페이지는 13일때 startPage = 11, endPage = 15가 되어야 함 => 13/5*5+1 = 11
		int startPage = pageInfo.getCurPage() / 10 * 10 + 1;
		// 11+5-1 = 15
		int endPage = startPage + 10 - 1;

		if (endPage > maxPage) {
			endPage = maxPage;
		}
		pageInfo.setAllPage(maxPage);
		pageInfo.setStartPage(startPage);
		pageInfo.setEndPage(endPage);

		// Page 타입을 List 형태로 변환해서 반환하기 위해서
		return pages.getContent();
	}
}
