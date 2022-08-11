package com.utill;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

public class FileManager {
	
	//파일 다운로드 메소드
	/*파일 다운로드에 필요한 인수.
	  서버가 클라이언트에게 전달하는 것이기 때문에 Response가 필요함.
	  DB에 저장되어있는 save file, 
	  실제 저장된 장소인 original file을 구분하기 위한 name이 필요함
	  파일의 위치를 알아야 하기 때문에 path가 필요*/
	public static boolean doFileDownload(HttpServletResponse Response,
			String saveFileName, String originalFileName, String path) {
		
		try {
			
			String fullPath = path + File.separator + saveFileName;
			
			if(originalFileName==null || originalFileName.equals("")) {
				originalFileName = saveFileName;
			}

			//한글 파일이름 깨짐 방지 처리
			originalFileName =
					new String(originalFileName.getBytes("euc-kr"),"ISO-8859-1");
				
			File f = new File(fullPath);
			
			if(!f.exists()) {
				return false;
			}
			
			//파일 다운로드 -> 클라이언트에게 내보내는 것
			//octet-stream : 가운데 .이 찍혀있는 file라는 것을 표시
			Response.setContentType("application/octet-stream");
			
			//헤더와 바디 구분
			Response.setHeader("Content-disposition", 
					"attachment;fileName=" + originalFileName);
			
			//full file path를 읽어서 BufferedInputStreamdp 넣어놔라
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f));
			
			//내보낼 객체 생성
			OutputStream out = Response.getOutputStream();
			
			int data;
			
			byte[] bytes = new byte[4096];
			while((data=bis.read(bytes,0,4096))!= -1) {
				out.write(bytes,0,data);
			}
			
			out.flush();
			out.close();
			bis.close();
			
			
		} catch (Exception e) {
			System.out.println(e.toString());
			return false;
		}
		
		return true;
		
	}
	
	
	
	//파일 삭제 메소드
	/*
	 file의 위치와 지우고자하는 file의 이름을 알아야한다.
	 DB의 파일이 아닌 물리적인 파일을 삭제한다.
	 */
	public static void doFileDelete(String fileName,String path) {
		
		try {
			
			String filePath = path + File.separator + fileName;
			
			File f = new File(filePath);
			
			//물리적인 파일을 삭제하는 코딩
			if(f.exists()) {
				f.delete();
			}
			
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		
		
		
	}
	
	
}
