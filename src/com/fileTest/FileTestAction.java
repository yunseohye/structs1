package com.fileTest;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.swing.event.ListSelectionEvent;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import com.util.dao.CommonDAO;
import com.util.dao.CommonDAOImpl;
import com.utill.FileManager;
import com.utill.MyPage;

public class FileTestAction extends DispatchAction{

	public ActionForward write(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		return mapping.findForward("write");
	}

	public ActionForward write_ok(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		CommonDAO dao = CommonDAOImpl.getInstance();

		HttpSession session = request.getSession();

		String root = session.getServletContext().getRealPath("/");

		String savePath = root + "pds" + File.separator + "saveFile";
		
		FileTestForm f = (FileTestForm)form;
		
		//파일 업로드
		String newFileName = FileManager.doFileUpload(f.getUpload(), savePath);
		
		//DB에 저장
		if(newFileName!=null) {
			
			int maxNum = dao.getIntValue("fileTest.maxNum");
			
			f.setNum(maxNum + 1); 
			f.setSaveFileName(newFileName);
			f.setOriginalFileName(f.getUpload().getFileName());
			
			dao.insertData("fileTest.insertData", f);
			
		}

		return mapping.findForward("write_ok");
	}

	public ActionForward list(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		CommonDAO dao = CommonDAOImpl.getInstance();
		String cp = request.getContextPath();
		
		MyPage myPage = new MyPage();
		
		int numPerPage = 5;
		int totalPage = 0;
		int totalDataCount = 0;
		
		String pageNum = request.getParameter("pageNum");
		
		int currentPage = 1;
		
		if(pageNum!=null && !pageNum.equals("")) {
			currentPage = Integer.parseInt(pageNum);
		}
		
		//데이터의 갯수
		totalDataCount = dao.getIntValue("fileTest.dataCount");
		
		//페이지수
		if(totalDataCount!=0) {
			totalPage = myPage.getPageCount(numPerPage, totalDataCount);
		}
		
		if(currentPage>totalPage) {
			currentPage = totalPage;
		}
		
		Map<String, Object> hMap = new HashMap<String, Object>();
		
		int start = (currentPage-1)*numPerPage+1;
		int end = currentPage*numPerPage;
		
		hMap.put("start", start);
		hMap.put("end", end);
		
		List<Object> lists = dao.getListData("fileTest.listData", hMap);
		
		//일렬번호 만들기
		Iterator<Object> it = lists.iterator();
		int listNum, n = 0;
		String str;
		while(it.hasNext()) {
			
			FileTestForm dto = (FileTestForm)it.next();
			
			listNum = totalDataCount - (start + n -1);
			
			dto.setListNum(listNum);
			
			n++;
			
			//파일다운경로
			str = cp + "/file.do?method=download&num=" + dto.getNum();
			dto.setUrlFile(str);
			
		}
		
		String urlList = cp + "/file.do?method=list";
		
		request.setAttribute("lists", lists);
		request.setAttribute("pageNum", currentPage);
		request.setAttribute("totalDataCount", totalDataCount);
		request.setAttribute("pageIndexList", 
				myPage.pageIndexList(currentPage, totalPage, urlList));

		return mapping.findForward("list");
	}

	public ActionForward delete_ok(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		CommonDAO dao = CommonDAOImpl.getInstance();

		HttpSession session = request.getSession();

		String root = session.getServletContext().getRealPath("/");

		String savePath = root + "pds" + File.separator + "saveFile";

		int num = Integer.parseInt(request.getParameter("num"));
		
		FileTestForm dto = 
				(FileTestForm)dao.getReadData("fileTest.readData",num);
		
		//파일삭제
		FileManager.doFileDelete(dto.getSaveFileName(), savePath);
		
		//DB삭제
		dao.deleteData("fileTest.deleteData", num);
		
		return mapping.findForward("delete_ok");
	}

	public ActionForward download(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		CommonDAO dao = CommonDAOImpl.getInstance();

		HttpSession session = request.getSession();

		String root = session.getServletContext().getRealPath("/");

		String savePath = root + "pds" + File.separator + "saveFile";

		int num = Integer.parseInt(request.getParameter("num"));
		
		FileTestForm dto = 
				(FileTestForm)dao.getReadData("fileTest.readData",num);
		
		boolean flag =
				FileManager.doFileDownload(response, dto.getSaveFileName(), 
						dto.getOriginalFileName(), savePath);
		
		if(!flag) {
			
			response.setContentType("text/html;charset=utf-8");
			PrintWriter out = response.getWriter();
			
			out.print("<script type='text/javascript'>");
			out.print("alert('다운로드 에러!!');");
			out.print("history.back()");
			out.print("</script>");
			
		}
		
		
		return mapping.findForward(null);
	}

}
