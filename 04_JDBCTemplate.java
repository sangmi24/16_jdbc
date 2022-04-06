package com.kh.common;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class JDBCTemplate {

   // JDBC 과정 중 반복적으로 쓰이는 구문들을 각각의 메소드로 정의해둘 곳
   // "재사용할 목적" 으로 공통 템플릿 작업 진행
	
   // 이 클래스에서의 모든 메소드들은 전부 다 static 메소드로 만들 것 (재사용의 개념)
   // 싱글톤 패턴 : 메모리 영역에 단 한번만 올라간 것을 재사용하는 개념 (== 매번 객체를 생성할 필요가 없음)
	
   // 1. DB 와 접속된 Connection 객체를  생성해서 그 Connection 객체를 반환해주는 메소드
	public static Connection getConnection() {		
	/*
	 * * 기존의 방식 : JDBC Driver 구문, 접속할 DB의 연결정보(url, 계정명, 비밀번호) 들을
	 *              자바 소스코드 내에 직접 명시적으로 작성 => 정적 코딩 방식 	
	 *  - 문제점 : DBMS 종류가 변경되었을 경우, 접속할 DB의 url 또는 계정명 또는 비밀번호가 변경되었을 경우
	 *            => 자바 코드를 수정해야 함
	 *            => 자바 코드가 수정된 상태에서 변경된 설정사항들이 적용될려면 프로그램을 재구동 해야함
	 *               (프로그램 사용중 비상적으로 종료되었다가 다시 구동될 수 있는 여지가 발생함)
	 *            => 유지보수 시 불편하다. 
	 *  - 해결방식 : DB와 관련된 정보들을 별도로 관리하는 외부 파일 (.properties)로 만들어서 관리
	 *             외부 파일로 만들어서 읽어들여서 반영시킬 것 => 동적 코딩 방식                       
	 */
	
		Connection conn = null;
		
		Properties prop = new Properties();
		
		try {
			prop.load(new FileInputStream("resources/driver.properties")); //input은 입력
			
			// 1) JDBC Driver 등록
			Class.forName(prop.getProperty("driver"));
			//2) Connection 객체 생성
			conn = DriverManager.getConnection(prop.getProperty("url")
					                          ,prop.getProperty("username")
					                          ,prop.getProperty("password"));
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {			
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		 return conn;		
	   	    
	}
	 //2. 전달받은 JDBC 용 객체를 자원반납시켜주는 메소드
     //2_1) Connection 객체를 매개변수로 전달받아서 반납시켜주는 메소드
	 public static void close(Connection conn) {
		 	 
		 try {
		   // 애초에 NullpointerException 방지하기 위함
			if(conn != null && !conn.isClosed()) { //null도 아니고 닫히지 않았다면 닫아주겠다. 
				 conn.close();	
			}				 
		} catch (SQLException e) {
			e.printStackTrace();
		}		 
	 }
	//2_2) (Prepared)Statement 객체를 매개변수로 전달받아서 반납시켜주는 메소드
	 //=> 다형성으로 인해 PreparedStatement (자식) 객체 또한 매개변수로 전달 가능하다. 
	 public static void close(Statement stmt) { // 오버로딩 (메소드 이름은 같은데 매개변수만 다름 )
		 
		 try {
			if(stmt != null && !stmt.isClosed()) {
				 stmt.close();
			 }
		} catch (SQLException e) {
			e.printStackTrace();
		}
	 }
	//2_3) ResultSet 객체를 매개변수로 전달받아서 반납시켜주는 메소드
	 public static void close(ResultSet rset) {
		 
		 try {
			if(rset != null && !rset.isClosed()) { //null값이 아니고 반납이 안될경우
				 rset.close();
			 }
		} catch (SQLException e) { 
			e.printStackTrace();
		}
	 }
	 //3. 전달받은 Connection 객체를 가지고 트랜잭션 처리를 해주는 메소드
	 //3_1) 매개변수로 전달받은 Connection 객체를 가지고 COMMIT 시켜주는 메소드
	 public static void commit(Connection conn) {
		 
		 try {			 
			if(conn != null && !conn.isClosed() ) { //null값이 아니고 반납이 안될경우
				 conn.commit();
			 }
		} catch (SQLException e) {
			e.printStackTrace();
		}		 
	 }
	 //3_2) 매개변수로 전달받은 Connection 객체를 가지고 ROOLBACK 시켜주는 메소드
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





















