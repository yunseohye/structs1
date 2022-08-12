package com.board;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import com.util.dao.CommonDAO;
import com.util.dao.CommonDAOImpl;
import com.utill.MyPage;

//DispatchAction - excute를 상속받을 필요 없이 메소드 단위로 움직인다.
public class BoardAction extends DispatchAction {

	public ActionForward created(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response)
					throws Exception {
		
		String mode = request.getParameter("mode");
		
		if(mode==null) {
			request.setAttribute("mode", "insert");
		} else { //null이 아니다? update에 왔다.
			
			CommonDAO dao = CommonDAOImpl.getInstance();
			
			int num = Integer.parseInt(request.getParameter("num"));
			String pageNum = request.getParameter("pageNum");
			
			BoardForm dto = (BoardForm)dao.getReadData("bbs.readData",num);
			
			if(dto==null) {
				return mapping.findForward("list");
			}
			
			request.setAttribute("dto", dto);
			//update가 들어가있음, create일때는? insert
			request.setAttribute("mode", mode);
			request.setAttribute("pageNum", pageNum);
			
		}

		return mapping.findForward("created");
	}

	public ActionForward created_ok(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response)
					throws Exception {

		CommonDAO dao = CommonDAOImpl.getInstance();

		BoardForm f = (BoardForm)form;

		String mode = request.getParameter("mode");
		
		if(mode.equals("insert")) {
		
			int maxNum = dao.getIntValue("bbs.maxNum");
	
			f.setNum(maxNum + 1);
			f.setIpAddr(request.getRemoteAddr());
	
			dao.insertData("bbs.insertData", f);
			
		} else { //update - 수정
			
			String pageNum = request.getParameter("pageNum");
			
			dao.updateData("bbs.updateData", f);
			
			/*HttpSession session = request.getSession();
			session.setAttribute("pageNum", pageNum);*/
			
			ActionForward af = new ActionForward();
			af.setRedirect(true);
			af.setPath("/bbs.do?method=list&pageNum="+pageNum);
			
			return af;
			
		}
		//dao가 다 끝나고나면 null로 초기화를 시켜줘야함.
		dao = null;

		return mapping.findForward("created_ok");
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

		HttpSession session = request.getSession();
		
		if(pageNum==null) {
			pageNum = (String)session.getAttribute("pageNum");
		}
		session.removeAttribute("pageNum");
		
		if(pageNum!=null) {
			currentPage = Integer.parseInt(pageNum);
		}

		String searchKey = request.getParameter("searchKey");
		String searchValue = request.getParameter("searchValue");

		if(searchValue==null) {
			searchKey = "subject";
			searchValue = "";
		}

		if(request.getMethod().equalsIgnoreCase("GET")) {
			searchValue = URLDecoder.decode(searchValue,"UTF-8");
		}

		Map<String, Object> hMap = new HashMap<>();
		hMap.put("searchKey",searchKey);
		hMap.put("searchValue",searchValue);

		totalDataCount = dao.getIntValue("bbs.dataCount",hMap);

		if(totalDataCount!=0) {
			totalPage = myPage.getPageCount(numPerPage, totalDataCount);
		}

		if(currentPage>totalPage) {
			currentPage = totalPage;
		}

		int start = (currentPage-1)*numPerPage+1;
		int end = currentPage*numPerPage;

		hMap.put("start", start);
		hMap.put("end", end);

		List<Object> lists = dao.getListData("bbs.listData",hMap);

		String param = "";
		String urlArticle = "";
		String urlList = "";

		if(!searchValue.equals("")) {
			searchValue = URLEncoder.encode(searchValue,"UTF-8");
			param = "&searchKey=" + searchKey;
			param+= "&searchValue=" + searchValue; 
		}

		urlList = cp + "/bbs.do?method=list" + param;
		urlArticle = cp + "/bbs.do?method=article&pageNum=" + currentPage;
		urlArticle += param;

		request.setAttribute("lists", lists);
		request.setAttribute("urlArticle", urlArticle);
		request.setAttribute("pageNum", pageNum);
		request.setAttribute("pageIndexList", 
				myPage.pageIndexList(currentPage, totalPage, urlList));
		request.setAttribute("totalPage", totalPage);
		request.setAttribute("totalDataCount", totalDataCount);

		return mapping.findForward("list");
	}

	public ActionForward article(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response)
					throws Exception {

		CommonDAO dao = CommonDAOImpl.getInstance();
		String cp = request.getContextPath();

		int num = Integer.parseInt(request.getParameter("num"));
		String pageNum = request.getParameter("pageNum");

		String searchKey = request.getParameter("searchKey");
		String searchValue = request.getParameter("searchValue");

		if(searchValue==null) {
			searchKey="subject";
			searchValue= "";
		}

		if(request.getMethod().equalsIgnoreCase("GET")) {
			searchValue = URLDecoder.decode(searchValue,"UTF-8");
		}

		//조회수 증가
		dao.updateData("bbs.hitCountUpdate", num);

		//해당 데이터 가져오기
		BoardForm dto = (BoardForm)dao.getReadData("bbs.readData",num);

		if(dto==null) {
			return mapping.findForward("list");
		}

		int lineSu = dto.getContent().split("\n").length;

		dto.setContent(dto.getContent().replaceAll("\r\n", "<br/>"));

		//이전글 다음글
		String preUrl = "";
		String nextUrl = "";

		Map<String, Object> hMap = new HashMap<>();
		hMap.put("searchKey", searchKey);
		hMap.put("searchValue", searchValue);
		hMap.put("num", num);

		String preSubject = "";
		BoardForm preDTO = (BoardForm)dao.getReadData("bbs.preReadData", hMap);

		if(preDTO!=null) {

			preUrl = cp + "/bbs.do?method=article&pageNum=" + pageNum;
			preUrl+= "&num=" + preDTO.getNum();
			preSubject = preDTO.getSubject();
		}

		String nextSubject = "";
		BoardForm nextDTO = (BoardForm)dao.getReadData("bbs.nextReadData", hMap);

		if(nextDTO!=null) {

			nextUrl = cp + "/bbs.do?method=article&pageNum=" + pageNum;
			nextUrl+= "&num=" + nextDTO.getNum();
			nextSubject = nextDTO.getSubject();
		}

		String urlList = cp + "/bbs.do?method=list&pageNum=" + pageNum;

		if(!searchValue.equals("")) {

			searchValue = URLEncoder.encode(searchValue, "UTF-8");

			urlList += "&searchKey=" + searchKey
					+ "&searchValue=" + searchValue;

			if(!preUrl.equals("")) {

				preUrl += "&searchKey=" + searchKey
					   + "&searchValue=" + searchValue;

			}

			if(!nextUrl.equals("")) {

				preUrl += "&searchKey=" + searchKey
					   + "&searchValue=" + searchValue;

			}

		}

		//수정, 삭제에서 사용할 인수
		String paramArticle = "num=" + num + "&pageNum=" + pageNum;
		
		request.setAttribute("dto", dto);
		request.setAttribute("preSubject", preSubject);
		request.setAttribute("preUrl", preUrl);
		request.setAttribute("nextSubject", nextSubject);
		request.setAttribute("nextUrl", nextUrl);
		request.setAttribute("lineSu", lineSu);
		request.setAttribute("paramArticle", paramArticle);
		request.setAttribute("urlList", urlList);
		
		return mapping.findForward("article");
		
	}
	
	public ActionForward deleted(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response)
					throws Exception {
		
		CommonDAO dao = CommonDAOImpl.getInstance();
		
		int num = Integer.parseInt(request.getParameter("num"));
		String pageNum= request.getParameter("pageNum");
		
		dao.deleteData("bbs.deleteData", num);
		
		//HttpSession session = request.getSession();
		//session.setAttribute("pageNum", pageNum);
		
		ActionForward af = new ActionForward();
		af.setRedirect(true);
		af.setPath("/bbs.do?method=list&pageNum="+pageNum);
		
		return af;
		
	}

	
}
