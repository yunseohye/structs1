package com.board;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import com.util.dao.CommonDAO;
import com.util.dao.CommonDAOImpl;

//DispatchAction - excute를 상속받을 필요 없이 메소드 단위로 움직인다.
public class BoardAction extends DispatchAction {

	public ActionForward created(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		return mapping.findForward("created");
	}

	public ActionForward created_ok(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		CommonDAO dao = CommonDAOImpl.getInstance();
	
		BoardForm f = (BoardForm)form;
		
		int maxNum = dao.getIntValue("bbs.maxNum");
	
		f.setNum(maxNum + 1);
		f.setIpAddr(request.getRemoteAddr());
		
		dao.insertData("bbs.insertData", f);
		
		//dao가 다 끝나고나면 null로 초기화를 시켜줘야함.
		dao = null;
		
		
		return mapping.findForward("created_ok");
	}
	
	public ActionForward list(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		return mapping.findForward("list");
	}
	
	
}
