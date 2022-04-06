package com.kh.model.dao;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

import com.kh.common.JDBCTemplate;
import com.kh.model.vo.Member;

/*
 *  * DAO (Data Access Object)
 *  Controller 로부터 전달받은 요청 기능을 수행하기 위해서 
 *  DB에 직접적으로 접근 한 후 해당 SQL 을 실행하고 결과를 받아내는 역할
 *  => 실질적인 JDBC 코드 작성
 *  추가적으로 결과값을 가공하거나 , 성공 실패 여부에 따라서 트랜잭션 처리
 *  결과를 Controller 로 리턴해줌 
 */
public class MemberDao {

	/*
	 * * 기존의 방식 : DAO 클래스에 사용자가 요청할 때 마다 실행해야 하는 SQL문을
	 *              자바 소스코드 내에 직접 명시적으로 작성함 => 정적 코딩
	 *  - 문제점 : SQL 구문을 수정해야 할 경우 자바 소스코드를 수정하는 셈
	 *           즉, 수정된 구문을 반영시키고자 할 경우에는 프로그램을 재구동 해야 함!
	 *  - 해결방식 : SQL 구문들을 별도로 관리하는 외부 파일(.xml)로 만들어서
	 *            실시간으로 이 파일에 기록된 SQL 문들을 동적으로 읽어들여서 실행되게끔=> 동적 코딩 방식                     
	 */
	
	
	private Properties prop = new Properties();
	// new MemberDao().xxx(); 할때 마다 MemberDao 객체가 생성되면서 기본생성자가 호출됨
	public MemberDao() {
		
		try {
		prop.loadFromXML(new FileInputStream("resources/query.xml"));
		
		 }catch(IOException e) {
			 e.printStackTrace();
		 }		
	}
	
	
	
	//1.사용자가 회원 추가 요청 시 입력했던 값들을 가지고 INSERT 문을 실행하는 메소드
	public int insertMember(Connection conn,Member m) { //INSERT 문 => 처리된 행의 갯수 => 트랜잭션 처리      
	   //0) JDBC 처리를 하기 전에 우선적으로 필요한 변수들 먼저 셋팅	=> 선언을 함
	   int result=0; //처리된 결과 (처리된 행의 갯수)를 담아줄 변수

	   PreparedStatement pstmt = null ; // SQL문을 실행 후 결과를 받기 위한 변수 
	   
	   // 실행할 SQL 문 (미완성된 형태로 )=> 반드시 세미콜론을 떼고 넣어줄 것
	   String sql = prop.getProperty("insertMember");
	   //String sql= "INSERT INTO MEMBER " //한칸 띄어쓰기 주의!
	   //		      + "VALUES(SEQ_USERNO.NEXTVAL, ?,?,?,?,?,?,?,?,?, DEFAULT)";	   	   
	   try {		   
            //3_1) PreparedStatement 객체 생성
			pstmt = conn.prepareStatement(sql);   //미리 sql을 넘기는 꼴
			
			//3_2) 내가 담은 SQL문이 미완성된 상태라면  값을 채워넣기
            pstmt.setString(1, m.getUserId()); //m으로 가공해서 받았기 때문에 
            pstmt.setString(2, m.getUserPwd());
            pstmt.setString(3, m.getUserName());
            pstmt.setString(4, m.getGender());
            pstmt.setInt(5, m.getAge());
            pstmt.setString(6, m.getEmail());
            pstmt.setString(7, m.getPhone());
            pstmt.setString(8, m.getAddress());
            pstmt.setString(9, m.getHobby());           
			// 4,5) DB에 완성된 SQL문을 실행 후 결과를 받기 
            result = pstmt.executeUpdate();  //INSERT => int (처리된 행의 갯수)
				
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {					
			//7) pstmt 객체 반납
			JDBCTemplate.close(pstmt);		   		
		}	
		//8) 결과 반환 => service로   
		return result; // 처리된 행의 갯수
	 }
	
	//2.사용자의 회원 전체 조회 요청시 SELECT 문을 실행할 메소드
	public ArrayList<Member> selectAll(Connection conn) { //SELECT=> ResultSet객체 (여러 행 조회)
		
	 // 0) 필요한 변수들 셋팅
	 //	조회된 결과를 뽑아서  담아줄 ArrayList 생성 (현재 텅 빈 리스트)
	 ArrayList<Member> list = new ArrayList<>(); //조회된 회원들이 담김
	 
	 PreparedStatement pstmt = null; //SQL 문 실행 후 결과를 받기 위한 변수 
	 ResultSet rset = null; //SELECT 문이 실행된 조회결과값들이 처음에 실질적으로 담길 변수
	  
	 // 실행할 SQL 문 (완성된 형태로 ,세미콜론 X)
	 //String sql = "SELECT * FROM MEMBER";	 
	 
	 String sql = prop.getProperty("selectAll");
	 
	 try {
		 
		 //3_1) PreparedStatement 객체 생성  (SQL문 전달을 여기서 진행)
		 pstmt = conn.prepareStatement(sql);
		 
		 //3_2) 미완성된 SQL문 완성 단계
	     //=> 쿼리문이 완성된 형태로 넘어갔기 때문에 생략 가능
		 
		 //4,5) SQL문을 (SELECT)을 전달해서 실행 후 결과 받기
		 rset = pstmt.executeQuery();
		 
		 //6_1) 현재 조회 결과가 담긴 ResultSet 에서 한행씩 뽑아서 VO객체에 담기
		 while(rset.next()) { //더이상 뽑아낼 게 없을 때까지 반복을 돌리겠다.			 
			 // Member 테이블에는 11개의 컬럼이 있음
			 Member m = new Member();
			 
			 //뽑자마자  setter로 필드에 값을 담아주기
			 m.setUserNo(rset.getInt("USERNO")); //rset.getInt(1)
			 m.setUserId(rset.getString("USERID")); //rset.getString(2)
			 m.setUserPwd(rset.getString("USERPWD")); 
			 m.setUserName(rset.getString("USERNAME"));
			 m.setGender(rset.getString("GENDER"));
			 m.setAge(rset.getInt("AGE"));
			 m.setEmail(rset.getString("EMAIL"));
			 m.setPhone(rset.getString("PHONE"));
			 m.setAddress(rset.getString("ADDRESS"));
			 m.setHobby(rset.getString("HOBBY"));
			 m.setEnrollDate(rset.getDate("ENROLLDATE"));
			 //한 행에 대한 모든 데이터값들을  하나의 Member 객체에 옮겨담기 끝!			 
			 // 리스트에 해당 Member 객체를 add
			 list.add(m);
		 }	 
	  }catch(SQLException e) {
		  e.printStackTrace();
	  } finally {		  	  
		  //7) 자원 반납 (역순)
		  JDBCTemplate.close(rset);
		  JDBCTemplate.close(pstmt);		  
	  }
	 //8) 결과 반환
	  return list;
	 
	}
	
	//3.사용자의 아이디로 회원 검색 요청시 SELECT문을 실행할 메소드
	public Member selectByUserId(Connection conn,String userId) { //SElECT 문 => ResultSet 객체 (한 행 조회)
		
		// 0) 필요한 변수들 셋팅
		// 조회된 한 회원에 대한 정보를 담을 변수
		Member m = null;
		PreparedStatement pstmt = null; // SQL문 실행 후 결과를 받기 위한 변수
		ResultSet rset = null; // SELECT 문이 실행된 조회결과값들이 처음에 실질적으로 담길 변수
		
		// 실행할 SQL 문 ( 미완성된 형태, 세미콜론X ) 
		//String sql = "SELECT * FROM MEMBER WHERE USERID = ?";
		String sql = prop.getProperty("selectByUserId");
		
		try {
		    //3_1) PreparedStatement 객체 생성 (SQL문을 이 시점에서 미리 보내두기)
			pstmt = conn.prepareStatement(sql);
			
			//3_2) 미완성된 SQL문을 매꾸는 작업
			pstmt.setString(1, userId);  //들어가는 값은 매개변수로 매꾸면 됨 
			
			//4,5) SQL 문(SELECT)을 실행 후 결과를 받기
             rset = pstmt.executeQuery();	
             
             //6_1) 현재 조회 결과가 담긴 ResultSet에서 한행씩 뽑아서 VO 객체에 담기
             // 만약 next() 메소드 실행 후 뽑아낼게 있다면 true 반환
             if( rset.next()) { //다 true 
            	 m = new Member(rset.getInt("USERNO"),
            			        rset.getString("USERID"),
            			        rset.getString("USERPWD"),
            			        rset.getString("USERNAME"),
            			        rset.getString("GENDER"),
            			        rset.getInt("AGE"),
            			        rset.getString("EMAIL"),
            			        rset.getString("PHONE"),
            			        rset.getString("ADDRESS"),
            			        rset.getString("HOBBY"),
            			        rset.getDate("ENROLLDATE"));               	
             }
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {		
			//7) 자원 반납 (역순)
			JDBCTemplate.close(rset);
			JDBCTemplate.close(pstmt);		
		}
		//8)결과 반환
		return m; //조회된 한명의 회원의 정보
	}
	//4.사용자의 이름 키워드로 회원 검색 요청시 SELECT문을 실행할 메소드
	public ArrayList<Member> selectByUserName(Connection conn,String keyword) { //SELECT문=>ResultSet객체 
		
        //조회된 결과를 뽑아서  담아줄 ArrayList 생성 => 키워드로 검색하기때문에 여러 행이 나올수 있음
		ArrayList<Member> list = new ArrayList<>(); //조회된 회원들이 담김
		// 0) 필요한 변수들 셋팅
	    // 조회된 한 회원에 대한 정보를 담을 변수
		PreparedStatement pstmt = null; 
		ResultSet rset = null; 
        
		//String sql = "SELECT * FROM MEMBER WHERE USERNAME LIKE ?";
		// 단, 구멍을 매꾸는 과정에서 양쪽에 %를 붙여서 매꿔줘야 함 
		String sql = prop.getProperty("selectByUserName");
		
		try {
		//3_1) Statement 객체 생성 (SQL문을 이 시점에서 넘길 예정)
		pstmt = conn.prepareStatement(sql);
		
		pstmt.setString(1, "%"+keyword+"%");  //'%keyword%' =>잘 실행됨
		
		//4,5) SQL문(SELECT)을 전달해서 실행 후 결과를 받기
		rset = pstmt.executeQuery();
		//6_1) 현재 조회 결과가 담긴 ResultSet에서 한행씩 뽑아서 VO객체에 담기
		// 우선적으로 커서를 한칸 내린후 뽑을 값이 있나 검사=> rset.next();
		while(rset.next()) { 
		         list.add((new Member(rset.getInt("USERNO"),
						     rset.getString("USERID"),
						     rset.getString("USERPWD"),
			   			     rset.getString("USERNAME"),
			   			     rset.getString("GENDER"),
			   			     rset.getInt("AGE"),
			   			     rset.getString("EMAIL"),
			   			     rset.getString("PHONE"),
			   			     rset.getString("ADDRESS"),
			   			     rset.getString("HOBBY"),
			   			     rset.getDate("ENROLLDATE"))));		         
	        }
		} catch (SQLException e) {		
			e.printStackTrace();
		}finally {
          //7) 자원 반납
			JDBCTemplate.close(rset);
			JDBCTemplate.close(pstmt);	
		}
		//8) 결과 반환
		 return list;
	}
    //5. 회원 변경 요청이 들어왔을 때 UPDATE 구문을 실행할 메소드
	public int updateMember(Connection conn,Member m) { //UPDATE 문=> int (처리된 행의 갯수) => 트랜잭션 처리		
		// 0) 필요한 변수들 셋팅
		int result = 0; // 최종적으로 SQL 문을 실행할 결과를 담을 변수		
		PreparedStatement pstmt = null;		
		//실행할 SQL문 (미완성된 형태로, 세미콜론 X)
		//String sql = "UPDATE MEMBER "
		//		      + "SET USERPWD = ? , EMAIL = ? , PHONE = ?, ADDRESS = ? "
		//		      + "WHERE USERID = ? ";	
		String sql = prop.getProperty("updateMember");
			
		try {			
			//3_1) prepareStatement 객체 생성
			pstmt = conn.prepareStatement(sql);
			//3_2) 값 채워넣기
			pstmt.setString(1, m.getUserPwd());
			pstmt.setString(2, m.getEmail()); 
			pstmt.setString(3, m.getPhone());
			pstmt.setString(4, m.getAddress());
			pstmt.setString(5, m.getUserId());
			//4,5) SQL 문을 실행 후 결과 받기
			result = pstmt.executeUpdate();			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			//7) 자원 반납 (생성된 순서의 역순!)
			JDBCTemplate.close(pstmt);
		}//8) 결과 반환		  
		return result; // UPDATE 된 행의 갯수		
	}
	//6.회원  탈퇴 요청시 DELETE 구문을 실행할 메소드
	public int deleteMember(Connection conn,String userId) { //DELETE문 = > int ( 처리 된 행의 갯수)=> 트랜잭션 처리
		
		//0) JDBC 처리를 하기 전에 필요한 변수들 먼저 셋팅
		int result= 0; //결과를 담을 용도의 변수 
		PreparedStatement pstmt = null;
		
		//실행할 sql문 (미완성된 형태로, 세미콜론 x)
		//String sql = "DELETE FROM MEMBER "
		//		   + "WHERE USERID = ?";
		String sql = prop.getProperty("deleteMember");
		
		try {
			//3) PreparedStatement 객체 생성
			pstmt = conn.prepareStatement(sql);
			//3_2) 미완성된 SQL문을 완성시키는 단계
			pstmt.setString(1, userId);			
			//4,5) SQL문 전달해서 실행 후 결과 받기
			result=pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
            //7) 자원 반납
			JDBCTemplate.close(pstmt);
		}
		//8) 결과 반환
		return result; //삭제된 행의 갯수
	}
	
	
	
	
}












