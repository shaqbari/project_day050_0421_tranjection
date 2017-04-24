/*1. 정보를 한곳에 두기
 * -데이터베이스 계정정보를 중복해서 기재하지 않기 위함
 * (db연동을 하는 각각의 클래스에서)
 * 
 * 2. 인스턴스의 갯수를 한개만 두기!
 * -어플리케이션 가동중 생성되는 connection 객체를 하나로 통일하기 위함
 * */

package com.ss.tranjection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBManager { //여기서 class는 클래스 생성하는 예약어
	static private DBManager instance;
	private String driver="oracle.jdbc.driver.OracleDriver";
	private String url="jdbc:oracle:thin:@localhost:1521:XE";
	private String user="bread";
	private String password="bread";
	
	private Connection con;
	
	
	private DBManager(){
		/*1.드라이버 로드
		 *2. 접속
		 *3. 쿼리문 수행
		 *4. 반납, 해체*/
		try {
			Class.forName(driver); //Class class는 클래스에대한 정보를 가지고 있다. 어떤 메소드와 변수로 구성되어 있는지 알 수 있다.
			con=DriverManager.getConnection(url, user, password);
			if (con!=null) {
				System.out.println("접속성공");
			} else {
				System.out.println("접속실패");			
			}
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
	}

	static public DBManager getInstance() {
		if (instance==null) {
			instance=new DBManager();
		}		
		
		return instance;
	}

	public Connection getConnection() {
		return con;
	}

	public void disConnect(Connection con) {
		if (con!=null) {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}	
	
}
