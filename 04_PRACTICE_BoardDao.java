package com.kh.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.kh.common.JDBCTemplate;
import com.kh.model.vo.Board;

public class BoardDao {

	//0.게시글 전체 조회
	public ArrayList<Board> selectAll(Connection conn){
		//0_1) 필요한 변수들 셋팅
		ArrayList<Board> list = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		//0_2)실행할 sql문 (완성된 형태)
		String sql = "SELECT * FROM BOARD";
		
		try {
			//0_3) preparedStatement 객체 생성
			pstmt = conn.prepareStatement(sql);
			//0_4) sql문을 전달해서 실행후 결과 받기
			rset = pstmt.executeQuery();
			//0_5) 현재 조회 결과가 담긴 ResultSet에서 한행씩 뽑아서 vo객체에 담기
			while(rset.next()) {
				//Board 테이블에는 6개의 컬럼이 있음
				Board b = new Board();
				//뽑자마자 setter 로 필드에 값을 담아주기
				b.setBno(rset.getInt("BNO"));
				b.setTitle(rset.getString("TITLE"));
				b.setContent(rset.getString("CONTENT"));
				b.setCreateDate(rset.getDate("CREATE_DATE"));
				b.setWriter(rset.getString("WRITER"));
				b.setDeleteYN(rset.getString("DELETE_YN"));
				
				list.add(b); //리스트에 담기
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			//0_6) 자원 반납(역순)
			JDBCTemplate.close(rset);
			JDBCTemplate.close(pstmt);
		} //0_7)
		  return list;
	}
	
	//1. 게시글 추가요청
	public int insertBoard(Connection conn, Board b) {
		//0_1) 필요한 변수들 셋팅
		int result=0;
		PreparedStatement pstmt = null;
		
		//0_2)실행할 sql문 (미완성)
		/*INSERT INTO BOARD
		VALUES(SEQ_BOARD.NEXTVAL, '~~~', '~~',DEFAULT,'~~',DEFAULT);*/
		String sql = "INSERT INTO BOARD "
				+ "VALUES(SEQ_BOARD.NEXTVAL,?,?,DEFAULT,?,DEFAULT)";
		
		try {
			//1_1) preparedStatement 객체 생성
			pstmt = conn.prepareStatement(sql);
			//1_2) 값 채워넣기!
			pstmt.setString(1, b.getTitle());
			pstmt.setString(2, b.getContent());
			pstmt.setString(3, b.getWriter());
			//1_3,4) DB에 완성된 SQL문을 실행후 결과 받기
			result = pstmt.executeUpdate();	
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			//1_5) pstmt 객체 반납
			JDBCTemplate.close(pstmt);
		} //결과 반환
		 return result; 
		
	}
	
	
	
}
