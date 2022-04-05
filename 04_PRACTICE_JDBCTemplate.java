package com.kh.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCTemplate {

	//1. Connection객체 생성, 객체 반환해주는 메소드
	 public static Connection getConnection() {
		 Connection conn = null;			 
		  try {
			//1) JDBC Driver 등록
			Class.forName("oracle.jdbc.driver.OracleDriver");
			//2) Connection 객체 생성
			conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","JDBC","JDBC");
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		  return conn;	
	 }
	//2. 전달받은 jdbc용 객체를 지원반납 메소드
	 //2_1) Connection 객체를 매개변수로 전달받아서 반납시켜주는 메소드
	 public static void close(Connection conn) {
		 
		 try {
			if(conn!=null &&  !conn.isClosed()) { 
				 conn.close();
			 }
		} catch (SQLException e) {

			e.printStackTrace();
		}
	 }
	//2_2) (Prepared)Statement 객체를 매개변수로 전달받아서 반납시켜주는 메소드
	 public static void close(Statement stmt) {
		 try {
			if(stmt !=null && !stmt.isClosed()) {
				 stmt.close();
			 }
		} catch (SQLException e) {
			e.printStackTrace();
		}
	 }
	//2_3) ResultSet객체를 매개변수로 전달받아서 반납시켜주는 메소드
	 public static void close(ResultSet rset) {
		 try {
			if(rset != null && !rset.isClosed()) {
				 rset.close();
			 }
		} catch (SQLException e) {
			e.printStackTrace();
		}
	 }
	// 3. 전달받은 Connection 가지고 트랜잭션처리!
	 //3_1) COMMIT
	 public static void commit(Connection conn) {
		 try {
			if(conn != null && !conn.isClosed()) {
				 conn.commit();
			 }
		} catch (SQLException e) {

			e.printStackTrace();
		}
	 }
	 //3_2) ROLLBACK
	 public static void rollback(Connection conn) {
		 try {
			if(conn != null && !conn.isClosed()) {
				 conn.rollback();
			 }
		} catch (SQLException e) {
			e.printStackTrace();
		}
	 }
	
	
}







