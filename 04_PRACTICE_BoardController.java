package com.kh.controller;

import java.util.ArrayList;

import com.kh.model.service.BoardService;
import com.kh.model.vo.Board;
import com.kh.view.BoardView;

public class BoardController {

	//0.게시글 전체 조회
	public void selectAll() {
		//0_1) Service의 메소드 호출
		ArrayList<Board> list = new BoardService().selectAll();
		//0_2) 조회된 결과가 있는지 여부에 따라 응답 화면 지정
		if(list.isEmpty()) { //텅비면=> 조회결과가 없음
			new BoardView().displayNodata("전체 조회 결과가 없습니다.");
		}else { //하나라도 있으면 ok
			new BoardView().displayList(list);
		}
		
	}

	//1. 게시글 추가요청을 처리해주는 메소드
	public void insertBoard(String title,String content
			                ,String writer ) {
	 //1_1) 전달된 데이터를 Board객체에 담기 
		Board b= new Board(title,content,writer);
	//1_2) service 의 메소드 호출
	    int result = new BoardService().insertBoard(b);
	//1_3) 성공/ 실패에 따라 응답화면을 지정 	
		if(result>0) {
			new BoardView().displaySuccess("화면 추가 성공");
		}else {
			new BoardView().displayFail("회원 추가 실패");
		}		
	}
	
	
	
}
