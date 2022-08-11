package com.utill;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConn {
	
	private static Connection conn = null;
	
	public static Connection getConnection() {
		
		String url = "jdbc:oracle:thin:@localhost:1521:xe";
		String user = "suzi";
		String pwd = "a123";
		
		if(conn==null) {
			
			//DB연결은 무조건 try catch문 사용
			try {
				
				Class.forName("oracle.jdbc.driver.OracleDriver");
				conn = DriverManager.getConnection(url, user, pwd);
				
			} catch (Exception e) {
				System.out.println(e.toString());
			}
			
		}
		
		return conn;
		
	}
	
	//DB close
	public static void close() {
		
		if(conn==null) {
			return;
			
		}
		
		try {
			if(!conn.isClosed()) {
				conn.close();
			}
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		//연결이 끝나고 나면 반드시 null로 초기화
		//이 작업을 안해주면 adapter연결 오류가 뜨면서 연결이 되지 않는다.
		conn = null;
		
	}

}
