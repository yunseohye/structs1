package com.utill;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Calendar;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts.upload.FormFile;

public class FileManager {
	
	//파입 업로드 메소드
	public static String doFileUpload(FormFile upload, String path) throws Exception {
		
		String newFileName;
		
		if(upload==null) {
			return null;
		}
		
		//클라이언트가 업로드한 파일 이름 (originalFileName)
		String originalFileName = upload.getFileName();
		
		if(originalFileName.equals("")) {
			return null;
		}
		
		//확장자
		//originalFileName이 abc.txt라면 .에서부터 끝까지(확장자)를 찾아냄
		String fileExt = 
				originalFileName.substring(originalFileName.lastIndexOf("."));
		
		if(fileExt==null || fileExt.equals("")) {
			return null;
		}
		
		//서버에 저장할 새로운 파일명 생성
		//format안 대 소문자 구별 잘해주기
		newFileName = String.format("%1$tY%1$tm%1$td%1$tH%1$tM%1$tS", 
				Calendar.getInstance());
		
		//nanotime까지 입력해주면 절대 시간 계삭이 중복될 수 없다.
		newFileName += System.nanoTime(); //10의 -9승
		
		newFileName += fileExt;
		
		//업로드할 경로 생성
		//사용자가 넘겨주는 파일 업로드 path를 넘겨줄 것임
		
		File f = new File(path);
		if(!f.exists()) {
			f.mkdirs();
		}
		
		//사용자가 보내주는 패스는 폴더까지만 있기 때문에 자세한 파일 경로까지 만들어줘야함
		String fullFilePath = path + File.separator + newFileName;
		
		//struts1의 파일 업로드
		//윗부분의 작업은 struts1이 파일을 업로드 하도록 준비해주는 작업
		//이 부분이 struts1에서는 실제로 파일을 업로드 해주는 코딩이다.
		byte[] fileData = upload.getFileData();
		FileOutputStream fos = new FileOutputStream(fullFilePath);
		fos.write(fileData);
		fos.close();
		
		return newFileName;
		
	}
	
	
	
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
