package com.kh.view;

import java.util.ArrayList;
import java.util.Scanner;

import com.kh.controller.BoardController;
import com.kh.model.vo.Board;

public class BoardView {

	//전역으로 다 쓸 수 있게끔 Scanner
	private Scanner sc = new Scanner(System.in);
	//BoardController 의 메소드로 요청할 수 있게끔 객체 생성 
	private BoardController bc = new BoardController();
	
	//메인 화면을 담당하는 메소드
	public void mainMenu() {
		
		while(true) {
			System.out.println("*** 게시판 프로그램  ***");
			selectAll(); //처음에 전체조회가 가능하게 만들기 
            System.out.println("1. 게시글 추가 ");
            System.out.println("2. 작성자로 검색");
            System.out.println("3. 작성자 키워드 검색");
            System.out.println("4. 게시글 수정");
            System.out.println("5. 게시글 삭제");
            System.out.println("0. 프로그램 종료");
            System.out.println("------------------------");
            System.out.print("이용할 메뉴 선택: ");
            int menu = sc.nextInt();
            sc.nextLine();
            
            switch(menu) {
             case 1: insertBoard(); break;
             case 2: selectByWriter(); break;
             case 3: selectByWriterName(); break;
             case 4: updateBoard(); break;
             case 5: deleteBoard(); break;
             case 0: System.out.println("프로그램을 종료합니다."); return;
             default : System.out.println("번호를 잘못 입력했습니다. 다시 입력해주새요.");
            }			
		}
	}
	   //0. 전체 조회 화면
	   public void selectAll() {
		   System.out.println("--- 게시물 전체 조회 ---");
		    bc.selectAll();
	   }	
		//1. 게시글 추가용 화면
		public void insertBoard() {
			System.out.println("---  게시글 추가  ---");
			
			//게시글 추가 시 입력받아야 하는 데이터
			//게시글제목,게시글내용,작성자 
			System.out.print("게시글제목: ");
			String title= sc.nextLine();
			
			System.out.print("게시글내용: ");
			String content = sc.nextLine();
			
			System.out.print("작성자:");
			String writer = sc.nextLine();
			
			bc.insertBoard(title,content,writer);		
		}
		//2.작성자로 검색
		public void selectByWriter() {
			System.out.println("--- 게시물 작성자로 검색 ---");
			
		}
		//3.작성자키워드로 검색
		public void selectByWriterName() {
			System.out.println("--- 게시물 작성자 키워드로 검색 ---");
		}
		//4.게시글 수정
		public void updateBoard() {
			System.out.println("--- 게시물 수정 ---");
		}
		//5.게시글 삭제
		public void deleteBoard() {
			System.out.println("-- 게시글 삭제 ---");
		}
		
		
		
		// 성공 메세지 출력용 메소드
		public void displaySuccess(String message) {
			System.out.println("서비스 요청 성공: "+ message);
		}
		// 실패 메세지 출력용 메소드
		public void displayFail (String message) {
			System.out.println("서비스 요청 실패: "+ message);
		}
		// 조회 서비스 요청시 조회결과가 없을때
		public void displayNodata(String message) {
			System.out.println(message);
		}
		//조회 서비스 요청시 여러행이 조회된 결과를 받아서 보게 될 화면을 만드는 메소드
		public void displayList(ArrayList<Board> list) {
			System.out.println("조회된 데이터는 총" + list.size()+"건 입니다.");
			for(int i=0; i<list.size();i++) {
				System.out.println(list.get(i));
			}
		}
		//조회 서비스 요청 시 한개 행이 조회된 결과를 받아서 보게될 화면 메소드..
		public void displayOne(Board b) {
			System.out.println("조회된 데이터는 다음과 같습니다.");
			System.out.println(b);
		}
		
		
		
	}
	
	
	

