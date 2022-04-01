package com.kh.controller;

import java.util.ArrayList;

import com.kh.model.dao.MemberDao;
import com.kh.model.vo.Member;
import com.kh.view.MemberView;

/*
 *  * Controller : View 를 통해서 요청받은 기능을 처리하는 담당
 *                 해당 메소드로 전달된 데이터(매개변수)를 가공처리(VO 객체의 각 필드에 변수값들을 담겠다) 한후 
 *                 Dao 메소드를 호출할 때 전달 (매개변수로 보내겠다.) 
 *                 Dao 로부터 반환받은 결과 (리턴값)에 따라서 사용자가 보게 될 화면을 지정해주는 역할
 *                 (View 의 응답화면을 결정해주는 역할=> View 응답화면에 해당되는 메소드를 호출) 
 */
public class MemberController {

	//1.회원 추가 요청을 처리해주는 메소드 
	public void insertMember( //매개변수 순서 중요함!
			String userId, String userPwd, String userName,
			String gender, int age, String email, String phone,
			String address, String hobby) { 
		 
		//1) 전달된 데이터들을 Member 객체에 담기 (Member 객체 생성)
		//매개변수 생성자를 이용하거나, 기본생성자로 생성 후 setter 메소드 이용
		Member m = new Member(userId,userPwd,userName,gender,age,email,phone,address,hobby);
		
		//2) Dao의 메소드 호출(이때, 위에서 가공된 Member객체를 매개변수로 넘기겠다.)
		int result= new MemberDao().insertMember(m);
		
		//3) 성공 / 실패에 따라 응답화면을 지정 (View 의 메소드 호출)
		if(result>0) { //성공
			
			//회원 추가 성공 문구를 띄워주는 화면단으로 메소드 호출
			 new MemberView().displaySuccess("회원 추가 성공");			
		}
		else { //실패
			
			//실패 문구를 띄워주는 화면단으로 메소드 호출
			new MemberView().displayFail("회원 추가 실패");						
		}					
	}
	//2.회원 전체 조회 요청을 처리해주는 메소드
	public void selectAll() {
		
		//따로 넘겨받은 값이 없어서 VO 객체로 가공할 필요가 없음!
		
		//2) DAO 의 메소드 호출
		ArrayList<Member> list = new MemberDao().selectAll();		
		//3) 조회된 결과가 있는지 여부에 따라 응답 화면을 지정
		if(list.isEmpty()) { //텅 비어있는 리스트일 경우 => 조회결과가 없음			
	     new MemberView().displayNodata("전체 조회 결과가 없습니다.");
		 //System.out.println("전체 조회 결과가 없습니다.");			
		}
		else { //적어도 한 건이라도 조회가 되었을 경우=> 조회결과가 있음
			 
			new MemberView().displayList(list);
			/*
			for(int i=0; i<list.size();i++) {				
				System.out.println(list.get(i) );				
			}
			*/
		}				
	}
	//3.아이디로 검색 요청을 처리해주는 메소드
	public void selectByUserId(String userId) {		
	  // 1) 어차피 값이 하나이기 때문에 VO 객체로 가공은 패스
		
	  // 2) DAO 메소드를 호출 	
	   Member m = new MemberDao().selectByUserId(userId);
	   
	   //3) 조회된 결과가 있는지 없는지 판단 후 사용자가 보게 될 View 지정
	   // 조회결과가 없다면 m == null
	   if(m == null) { //조회 결과가 없는 경우		   
		   new MemberView().displayNodata(userId+"에 해당하는 검색 결과가 없습니다.");		   
	   }
	   else { //조회 결과가 있는 경우		   
		   new MemberView().displayOne(m);		   
	  }	
	}
	//4.이름 키워드로 검색 요청을 처리해주는 메소드 
	public void selectByUserName(String keyword) {
		//1) 어차피 값이 하나이기에 VO 객체 가공 패스
		//2) DAO 메소드 호출
        ArrayList<Member> list = new MemberDao().selectByUserName(keyword);
        //3) 조회된 결과가 있는지 여부에 따라 응답 화면 지정
        if(list.isEmpty()) {
        	new MemberView().displayNodata(keyword+"에 해당하는 검색 결과가 없습니다.");     	
        }else {
        	new MemberView().displayList(list);
        }
		
		
		
		
	}
	
	
	
	
}








