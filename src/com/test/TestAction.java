 package com.test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class TestAction extends Action{

	@Override
	public ActionForward execute(ActionMapping mapping, 
		   ActionForm form, HttpServletRequest request,
		   HttpServletResponse response) throws Exception {
		
		String uri = request.getRequestURI();
		
		//주소가 test_ok.do라면 ActionForm form에서 데이터를 넘겨준다.
		if(uri.indexOf("/write_ok.do")!=-1) {
			
			//자바가 넘겨주는 것은 무조건 object이기 때문에 다운캐스트 해야함
			TestForm f = (TestForm)form;
			
			//write_ok.jsp에 vo의 데이터를 받아 f에 넘겨줄 것임
			request.setAttribute("vo", f);
			
			//struts-config_test.xm로 넘어간다.
			return mapping.findForward("ok");
			
		}
		
		return mapping.findForward("error");
	}

	
	
}
