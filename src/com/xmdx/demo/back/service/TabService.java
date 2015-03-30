package com.xmdx.demo.back.service;

import org.apache.commons.lang.StringUtils;

import com.e9rj.platform.common.services.BusinessServices;
import com.xmzy.frameext.business.service.annotate.Service;
import com.xmzy.framework.context.ActionContext;

@Service(name="back.tab")
public class TabService extends BusinessServices {
	
	//功能号
	private static final String AUTH_FUNC_NO = "back.tab";

	/**
	 * 打开tab_main主页面
	 */
	@Override
	public int init(ActionContext ac) throws Exception {
		checkAuth(ac, AUTH_FUNC_NO, RIGHT_ONE);
	
		ac.setStringValue(CONST_FORMNAME, "com/xmdx/demo/back/tabs_main.html");
		return CONST_RESULT_SUCCESS;
	}

	@Override
	public int query(ActionContext ac) throws Exception {
		
		
		return CONST_RESULT_AJAX;
	}
	
	@Override
	public int save(ActionContext ac) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int delete(ActionContext ac) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * 打开tab标签页
	 */
	@Override
	public int goTo(ActionContext ac) throws Exception {
		
		ac.setObjValue("tabLogo", AUTH_FUNC_NO);
		
		String tabid = ac.getHttpRequest().getParameter("TABID");
		if(StringUtils.isNotBlank(tabid)){
			ac.setStringValue("FORMNAME", "com/xmdx/demo/back/tabs_tab" + tabid + ".html");
		}
		
		return CONST_RESULT_SUCCESS;
	}

	@Override
	public int insertExportData(ActionContext ac) throws Exception {
		
		return CONST_RESULT_SUCCESS;
	}
	
}
