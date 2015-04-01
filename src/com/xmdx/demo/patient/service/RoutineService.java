package com.xmdx.demo.patient.service;

import com.e9rj.platform.common.services.BusinessServices;
import com.xmzy.frameext.business.service.annotate.Service;
import com.xmzy.framework.context.ActionContext;
@Service(name="patient.routine")
public class RoutineService extends BusinessServices {
	//功能号
	private static final String authFuncNo = "patient.routine";
	//表名
	private static final String tableName = "TB_USER";
	//主键名
	private static final String keyField = "U_ID";
	@Override
	public int delete(ActionContext arg0) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int goTo(ActionContext arg0) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int init(ActionContext ac) throws Exception {
		
		checkAuth(ac, authFuncNo, RIGHT_ONE);
		
		ac.setStringValue("tabLogo", authFuncNo);
		ac.setStringValue(CONST_FORMNAME, "com/xmdx/demo/doctor/doctor_blog.html");		
		return CONST_RESULT_SUCCESS;
	}

	@Override
	public int insertExportData(ActionContext arg0) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int query(ActionContext arg0) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int save(ActionContext arg0) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

}
