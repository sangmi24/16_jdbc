package com.kh.model.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

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
	 * * JDBC 용 객체
	 * - Connection : DB의 연결정보를 담고 있는 객체
	 * - (Prepared)Statement : 해당 DB에 SQL문을 전달하고 실행한 후 결과를 받아내는 객체
	 * - ResultSet : 만일 내가 실행한 SQL 문이 SELECT 문일 경우 조회된 결과들이 담겨있는 객체 
	 *
	 * * Statement(부모) 와 PreparedStatement(자식)의 차이점
	 * - Statement 같은 경우  완성된 SQL문을 바로 실행하는 객체
	 *   (== SQL 문이 완전하게 완성된 형태로 셋팅되어있어야만 한다!!
	 *       애초에 사용자가 입력했던 값들이 다 채워진 채로 만들어져 있어야만 함)
	 *  > Connection 객체를 가지고 Statement 객체 생성 : createStatement() 메소드 이용
	 *     stmt = conn.createStatement();
	 *  > SQL 문 실행 시 : executeXXXX(쿼리문) 메소드 이용        
	 *     결과 = stmt.executeXXXXX(쿼리문);
	 *     ( == 쿼리문 실행하는 순간 완성된 쿼리문을 넘기겠다.)   
	 * 
	 * - PreparedStatement 같은 경우  SQL 문을 바로 실행하지 않고 잠시 보관을 해둘 수 있음 
	 *   (== "미완성된" SQL 문을 미리 보내놓고 잠시 보관해둘 수 있음!!
	 *        단, 사용자가 입력한 값들이 들어갈 수 있는 공간을 미리 확보 , ?(위치홀더) 로 구멍 뚫어놓기
	 *        해당 SQL 문을 실행하기 전에 완성형태로 만든 후 실행을 해야한다)
	 *  > Connection 객체를 가지고 PreparedStatement 객체 생성 : prepareStatement() 메소드 이용
	 *    pstmt = conn.prepareStatement(미완성된쿼리문);   
	 *    (== 객체를 생성하는 순간 미완성된 쿼리문을 먼저 넘겨두겠다.) 
	 *  > 현재 담긴 SQL 문이 미완성된  SQL문일 경우 : 빈 공간을 실제 값으로 채워주는 과정 
	 *     (완성된 쿼리문을 넘긴 경우에는 생략 가능)
	 *      pstmt.setString(1,"실제값");  / pstmt.setInt(2,실제값);                               
	 *  > excuteXXXX() 메소드를 이용해서 SQL문을 실행
	 *    결과 = pstmt.executeXXXX();
	 *    
	 * * JDBC 처리 순서
	 * 1) JDBC Driver 등록 : 해당 DBMS 가 제공하는 클래스 등록 (DriverManager)
	 * 2) Connection 객체 생성 : 접속하고자 하는 DB의 정보를 입력해서 DB에 접속하면서 생성
	 * 3_1) PreparedStatement 객체 생성 : Connection 객체를 이용해서 생성
	 *      ( 애초에 SQL 문을 담은 채로 생성)
	 * 3_2) 현재 미완성된 SQL문을 완성형태로 채우는 과정
	 *     => 미완성된 경우에만 해당 / 완성된 쿼리문을 3_1 단계에서 미리 보냈다면 이 단계는 생략
	 * 4) SQL문을 실행 : PreparedStatement 객체를 이용해서  (매개변수 없음!!) (오버라이딩)
	 *     > SELECT 문의 경우 - executeQuery() 메소드를 호출해서 실행
	 *     > INSERT, UPDATE, DELETE 문의 경우 - executeUpdate() 메소드를 호출해서 실행       
	 * 5) 결과 받기
	 *     > SELECT 문의 경우 - ResultSet 객체 (조회된 데이터들이 담겨있음)로 받기 => 6_1)로
	 *     > INSERT, UPDATE, DELETE 문의 경우 - int (처리된 행의 갯수)로 받기 => 6_2)로
	 * 6) 후처리
	 * 6_1) SELECT : ResultSet에 담겨있는 데이터들을 하나씩 뽑아서 VO 객체에 담기 (여러개 일 경우 ArrayList로 )
	 * 6_2) INSERT,UPDATE, DELETE : 트랜잭션 처리 ( 성공이면 COMMIT, 실패면 ROLLBACK)
	 * 7) 다 쓴 JDBC 용 자원들을 반납 (close) => 생성된 순서의 역순으로 
	 * 8) 결과를 반환 (Controller 한테)
	 *      > SELECT 문의 경우 -6_1) 에서 만들어진 VO 객체 또는 ArrayList 보내기
	 *      > INSERT, UPDATE,DELETE 문의 경우 - int (처리된 행의 갯수)    
	 *             
	 */
	
	//1.사용자가 회원 추가 요청 시 입력했던 값들을 가지고 INSERT 문을 실행하는 메소드
	public int insertMember(Member m) { //INSERT 문 => 처리된 행의 갯수 => 트랜잭션 처리
       
	   //0) JDBC 처리를 하기 전에 우선적으로 필요한 변수들 먼저 셋팅	=> 선언을 함
	   int result=0; //처리된 결과 (처리된 행의 갯수)를 담아줄 변수
	   Connection conn = null; //접속할 DB의 연결정보를 담는 변수
	   PreparedStatement pstmt = null ; // SQL문을 실행 후 결과를 받기 위한 변수 
	                                    // Statement 와 역할은 똑같지만 사용법은 다름!
	   
	   // 실행할 SQL 문 (미완성된 형태로 )=> 반드시 세미콜론을 떼고 넣어줄 것
	   // INSERT INTO MEMBER
	   // VALUES(SEQ_USERNO.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, ?, DEFAULT);
	   String sql= "INSERT INTO MEMBER " //한칸 띄어쓰기 주의!
	   		      + "VALUES(SEQ_USERNO.NEXTVAL, ?,?,?,?,?,?,?,?,?, DEFAULT)";	   	   
	   try {		   
			 // 1) JDBC Driver 등록 (DriverManager가 생김) --예외처리   
			Class.forName("oracle.jdbc.driver.OracleDriver");
			//ojdbc6.jar 가 누락되거나, 잘 추가되었지만 오타가 있을 경우
			//=> ClassNotFoundException 오류 발생
			
			// 2) Connection 객체 생성 (DB 와 연결->url, 계정명, 비밀번호) getConnection (약간 기울어진것=>static 메소드)
			conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","JDBC","JDBC");
			
            //3_1) PreparedStatement 객체 생성
			pstmt = conn.prepareStatement(sql);   //미리 sql을 넘기는 꼴
			//stmt =conn.createStatement(); 
			
			//3_2) 내가 담은 SQL문이 미완성된 상태라면  값을 채워넣기
			//      (pstmt.setXXX(위치홀더의순번, 매꿀값) 메소드 호출 ) 동적바인딩
			//       => pstmt.setString(위치홀더의순번, 매꿀값);  : '매꿀값'
			//       => pstmt.setInt(위치홀더의순번, 매꿀값); : 매꿀값
            pstmt.setString(1, m.getUserId()); //m으로 가공해서 받았기 때문에 
            pstmt.setString(2, m.getUserPwd());
            pstmt.setString(3, m.getUserName());
            pstmt.setString(4, m.getGender());
            pstmt.setInt(5, m.getAge());
            pstmt.setString(6, m.getEmail());
            pstmt.setString(7, m.getPhone());
            pstmt.setString(8, m.getAddress());
            pstmt.setString(9, m.getHobby()); 
            
            // PreparedStatement 의 최대 장점 : 쿼리 작성하기 간편해짐, 가독성 증가
            // PreparedStatement 의 최대 단점 : 구멍매꾸기가 귀찮아짐, 완성된 SQL문의 형태를 확인 불가 
            
			// 4,5) DB에 완성된 SQL문을 실행 후 결과를 받기 
            result = pstmt.executeUpdate();  //INSERT => int (처리된 행의 갯수)
			//result = stmt.executeUpdate(sql); 
			
			// 6_2) 트랜잭션 처리
			if(result > 0 ) { // 성공
				conn.commit(); //COMMIT 처리
			}
			else { //실패
				conn.rollback(); //ROLLBACK 처리
			}				
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {		
			try {
			//7) 다 쓴 JDBC 용 자원 반납 => 객체 생성의 역순으로  close =>try예외로 감싸기
			pstmt.close();
			conn.close();
		   }catch(SQLException e) {
		    e.printStackTrace();
		  }	   		
		}	
		//8) 결과 반환   
		return result; // 처리된 행의 갯수
	 }
	
	//2.사용자의 회원 전체 조회 요청시 SELECT 문을 실행할 메소드
	public ArrayList<Member> selectAll() { //SELECT=> ResultSet객체 (여러 행 조회)
		
	 // 0) 필요한 변수들 셋팅
	 //	조회된 결과를 뽑아서  담아줄 ArrayList 생성 (현재 텅 빈 리스트)
	 ArrayList<Member> list = new ArrayList<>(); //조회된 회원들이 담김
	 
	 Connection conn = null; //접속할 DB의 연결정보를 담는 변수
	 PreparedStatement pstmt = null; //SQL 문 실행 후 결과를 받기 위한 변수 
	 ResultSet rset = null; //SELECT 문이 실행된 조회결과값들이 처음에 실질적으로 담길 변수
	 
	 // SELECT 문의 경우는 굳이 PreparedStatement 를 사용하지 않아도 되지만
	 // PreparedStatement 사용 시 완성된 쿼리문을 사용할 수도 있기 때문에 연습삼아 해보자 
	 
	 // 실행할 SQL 문 (완성된 형태로 ,세미콜론 X)
	 // SELECT * FROM MEMBER
	 String sql = "SELECT * FROM MEMBER";	 
	 try {
		 //1) JDBC Driver 등록 (DriverManager)
		 Class.forName("oracle.jdbc.driver.OracleDriver");
		 
		 //2) Connection 객체 생성
		 conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","JDBC","JDBC");
	 	
		 //3_1) PreparedStatement 객체 생성  (SQL문 전달을 여기서 진행)
		 pstmt = conn.prepareStatement(sql);
		 //stmt= conn.createStatement();
		 
		 //3_2) 미완성된 SQL문 완성 단계
	     //=> 쿼리문이 완성된 형태로 넘어갔기 때문에 생략 가능
		 
		 //4,5) SQL문을 (SELECT)을 전달해서 실행 후 결과 받기
		 rset = pstmt.executeQuery();
		 //rset = stmt.executeQuery(spl);
		 
		 //6_1) 현재 조회 결과가 담긴 ResultSet 에서 한행씩 뽑아서 VO객체에 담기
		 while(rset.next()) { //더이상 뽑아낼 게 없을 때까지 반복을 돌리겠다.			 
			 // 뽑아낼 게 있다면
			 // 모든 컬럼에 대해서 값을 일일이 다 뽑아야 한다!!
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
	  }catch(ClassNotFoundException e) {
		  e.printStackTrace(); 
	  }catch(SQLException e) {
		  e.printStackTrace();
	  } finally {		  
		  try {
			  //7) 자원 반납 (생성된 순서의 역순)
			  rset.close();
			  pstmt.close();
			  conn.close();
		  } catch(SQLException e) {
			  e.printStackTrace();
	    }		
	  }
	 //8) 결과 반환
	  return list;
	 
	}
	
	//3.사용자의 아이디로 회원 검색 요청시 SELECT문을 실행할 메소드
	public Member selectByUserId(String userId) { //SElECT 문 => ResultSet 객체 (한 행 조회)
		
		// 0) 필요한 변수들 셋팅
		// 조회된 한 회원에 대한 정보를 담을 변수
		Member m = null;
		
		Connection conn = null; //접속할 DB의 연결정보를 담는 변수
		PreparedStatement pstmt = null; // SQL문 실행 후 결과를 받기 위한 변수
		ResultSet rset = null; // SELECT 문이 실행된 조회결과값들이 처음에 실질적으로 담길 변수
		
		// 실행할 SQL 문 ( 미완성된 형태, 세미콜론X )
		// SELECT * FROM MEMBER WHERE USERID= ? 
		String sql = "SELECT * FROM MEMBER WHERE USERID = ?";	//	
		try {
			// 1) JDBC Driver 등록 (DriverManager)
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			//2) Connection 객체 생성
			conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","JDBC","JDBC");
			
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
		} catch (ClassNotFoundException e) {			
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {						
			try {
				//7) 다 쓴 JDBC 용 객체 반납 ( 생성된 순서의 역순)
				rset.close();
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}						
		}
		//8)결과 반환
		return m; //조회된 한명의 회원의 정보
	}
	//4.사용자의 이름 키워드로 회원 검색 요청시 SELECT문을 실행할 메소드
	public ArrayList<Member> selectByUserName(String keyword) { //SELECT문=>ResultSet객체 
		
        //조회된 결과를 뽑아서  담아줄 ArrayList 생성 => 키워드로 검색하기때문에 여러 행이 나올수 있음
		ArrayList<Member> list = new ArrayList<>(); //조회된 회원들이 담김
		// 0) 필요한 변수들 셋팅
	    // 조회된 한 회원에 대한 정보를 담을 변수
		Connection conn = null; 
		PreparedStatement pstmt = null; 
		ResultSet rset = null; 
        
		//SELECT * FROM MEMBER WHERE USERNAME LIKE '%XXX%'
		//String sql = "SELECT * FROM MEMBER WHERE USERNAME LIKE '%?%' ";
		// 정상적으로 수행이 되지 않을 것임! 왜? 문자열의 경우는 매꿔질 때 홑따옴표가 자동으로 들어가기 때문에 '%'?'%'이 이 모양이 될 것임 (문법 오류)
				
		//방법1) 오라클의 연결연산자를 이용한 방법
		//String sql = "SELECT * FROM MEMBER WHERE USERNAME LIKE '%'|| ? || '%'  ";
		//정상적으로 수행이 될것임! 왜? 문자열의 경우는 매꿔질 때 홑따옴표가 자동으로 들어가기 때문에  '%'|| '?' || '%' 이 모양이 될 것임
		//방법2) 
		String sql = "SELECT * FROM MEMBER WHERE USERNAME LIKE ?";
		// 단, 구멍을 매꾸는 과정에서 양쪽에 %를 붙여서 매꿔줘야 함 

		try {
		//1) JDBC Driver 등록
		Class.forName("oracle.jdbc.driver.OracleDriver");
		//2) Connection 객체 생성
		conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","JDBC","JDBC");
		//3_1) Statement 객체 생성 (SQL문을 이 시점에서 넘길 예정)
		pstmt = conn.prepareStatement(sql);
		
		//3_2) 미완성된 SQL문을 완성시키기
		//String sql = "SELECT * FROM MEMBER WHERE USERNAME LIKE '%?%' ";
		//정상적으로 수행이 안되는 케이스
		//pstmt.setString(1, keyword); //'%'keyword'%'=>문법오류
				
		//방법1)오라클의 연결연산자를 이용한 방법  =>또한 concat방법도 가능하다. 
		//String sql = "SELECT * FROM MEMBER WHERE USERNAME LIKE '%'|| ? || '%'  "; 
		//정상적으로 수행이 되는 케이스
		//pstmt.setString(1, keyword); // '%'||'keyword'|| '%' =>잘 실행됨

		//방법2) 
        //String sql = "SELECT * FROM MEMBER WHERE USERNAME LIKE ?";
		pstmt.setString(1, "%"+keyword+"%");  //'%keyword%' =>잘 실행됨
		
		//4,5) SQL문(SELECT)을 전달해서 실행 후 결과를 받기
		rset = pstmt.executeQuery();
		//6_1) 현재 조회 결과가 담긴 ResultSet에서 한행씩 뽑아서 VO객체에 담기
		// 우선적으로 커서를 한칸 내린후 뽑을 값이 있나 검사=> rset.next();
		// 여러 행이 조회될 가능성이 높을 경우 반복적으로 검사가 진행되야 함
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
		}catch(ClassNotFoundException e){
			e.printStackTrace();
		} catch (SQLException e) {		
			e.printStackTrace();
		}finally {
			try {
				//7) jdbc 객체 반납 (역순!)
				rset.close();
				pstmt.close();
				conn.close();
			}catch(SQLException e) {
				e.printStackTrace();
			}
		}
		//8) 결과 반환
		 return list;
	}
    //5. 회원 변경 요청이 들어왔을 때 UPDATE 구문을 실행할 메소드
	public int updateMember(Member m) { //UPDATE 문=> int (처리된 행의 갯수) => 트랜잭션 처리
		
		// 0) 필요한 변수들 셋팅
		int result = 0; // 최종적으로 SQL 문을 실행할 결과를 담을 변수		
		Connection conn = null;
		PreparedStatement pstmt = null;		
		//실행할 SQL문 (미완성된 형태로, 세미콜론 X)
		String sql = "UPDATE MEMBER "
				      + "SET USERPWD = ? , EMAIL = ? , PHONE = ?, ADDRESS = ? "
				      + "WHERE USERID = ? ";		
		try {			
			//1) JDBC Driver 등록
			Class.forName("oracle.jdbc.driver.OracleDriver");
			//2) Connection 객체 생성
			conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "JDBC","JDBC");
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
			//6_2) 트랜잭션 처리
			if(result>0) {// result 값이 0보다 크다면 => 성공 (COMMIT)		   
				conn.commit();
			}
			else { // 실패 (ROLLBACK)
				conn.rollback();				
			}
		} catch (ClassNotFoundException e) {		
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			//7) 자원 반납 (생성된 순서의 역순!)
             try {
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		  //8) 결과 반환
		return result; // UPDATE 된 행의 갯수		
	}
	//6.회원  탈퇴 요청시 DELETE 구문을 실행할 메소드
	public int deleteMember(String userId) { //DELETE문 = > int ( 처리 된 행의 갯수)=> 트랜잭션 처리
		
		//0) JDBC 처리를 하기 전에 필요한 변수들 먼저 셋팅
		int result= 0; //결과를 담을 용도의 변수 
		Connection conn =null;
		PreparedStatement pstmt = null;
		
		//실행할 sql문 (미완성된 형태로, 세미콜론 x)
		//DELETE FROM MEMBE WHERE USERID = 'XXXX'
		String sql = "DELETE FROM MEMBER "
				   + "WHERE USERID = ?";		
		try {
			//1)JDBC Driver 등록
			Class.forName("oracle.jdbc.driver.OracleDriver");
			//2)Connection 객체 생성
			conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","JDBC","JDBC");
			//3) PreparedStatement 객체 생성
			pstmt = conn.prepareStatement(sql);
			//3_2) 미완성된 SQL문을 완성시키는 단계
			pstmt.setString(1, userId);
			
			//4,5) SQL문 전달해서 실행 후 결과 받기
			result=pstmt.executeUpdate();
			//6_2) 트랜잭션 처리
			if(result>0) {
				conn.commit();
			}else {
				conn.rollback();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				//7) 자원 반납 역순!
				pstmt.close();
				conn.close();
			}catch(SQLException e) {
				e.printStackTrace();
			}
		}
		//8) 결과 반환
		return result;
	}
	
	
	
	
}












