package com.xmdx.demo.patient.service;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.e9rj.platform.common.services.BusinessServices;
import com.xmzy.frameext.business.service.annotate.Service;
import com.xmzy.frameext.simpledb.DBDYDao;
import com.xmzy.frameext.simpledb.DBDYPO;
import com.xmzy.framework.context.ActionContext;
@Service(name="patient.question")
public class QuestionnaireService extends BusinessServices {
	//功能号
	private static final String authFuncNo = "patient.list";
	//表名
	private static final String tableName = "PATIENT";
	//主键名
	private static final String keyField = "PATIENT_ID";
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
		StringBuilder sql = new StringBuilder("SELECT * FROM QUESTIONNAIRE Q ");
		
		List<DBDYPO> pos = DBDYDao.selectBySQL2List(ac.getConnection(), sql.toString());
		ac.setObjValue("Q", pos);
		checkAuth(ac, authFuncNo, RIGHT_ONE);
		
		ac.setStringValue("tabLogo", authFuncNo);
		ac.setStringValue(CONST_FORMNAME, "com/xmdx/demo/patient/questionnaire.html");		
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
