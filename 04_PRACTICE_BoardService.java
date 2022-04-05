package com.kh.model.service;

import java.sql.Connection;
import java.util.ArrayList;

import com.kh.common.JDBCTemplate;
import com.kh.model.dao.BoardDao;
import com.kh.model.vo.Board;

public class BoardService {

	//0. 게시글 전체 조회
	public ArrayList<Board> selectAll(){
		//0_1) Connection 객체 생성
		Connection conn = JDBCTemplate.getConnection();
		//0_2) DAO 메소드를 호출
		ArrayList<Board> list = new BoardDao().selectAll(conn);
		//0_3) Connection  객체 반납
		JDBCTemplate.close(conn);
		//0_4) 결과값을 Controller 로 리턴
		return list;
	}

	//1.게시글 추가요청
	public int insertBoard(Board b) {
	  //1_1) Connection 객체 생성
		Connection conn= JDBCTemplate.getConnection();
	  
		//1_2) DAO 메소드를 호출( Connection, Board전달값 둘 다 매개변수로 넘기기!)
		int result= new BoardDao().insertBoard(conn,b);
	  
		//1_3) 성공/ 실패에 따라 응답화면 지정 (view 메소드 호출)
		if(result >0) { //성공 ->COMMIT
		       JDBCTemplate.commit(conn);
		}else { //실패-> ROLLBACK
			   JDBCTemplate.rollback(conn);
		}
		//1_4)Connection 객체 반납
		JDBCTemplate.close(conn);
		//1_5)결과값을 Controller 로 리턴
		return result;
	}
	
	
	
}
