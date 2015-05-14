package com.xmdx.demo.patient.service;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.e9rj.platform.common.GenID;
import com.e9rj.platform.common.services.BusinessServices;
import com.e9rj.platform.util.SessionUtil;
import com.xmzy.frameext.business.service.annotate.Service;
import com.xmzy.frameext.simpledb.DBConn;
import com.xmzy.frameext.simpledb.DBDYDao;
import com.xmzy.frameext.simpledb.DBDYPO;
import com.xmzy.framework.context.ActionContext;
@Service(name="patient.question")
public class QuestionnaireService extends BusinessServices {
	//功能号
	private static final String authFuncNo = "patient.question";
	//表名
	private static final String tableName = "QUESTION";
	//主键名
	private static final String keyField = "RESULT_ID";
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
	public int save(ActionContext ac) throws Exception {
		String questionName=SessionUtil.getOpno(ac);
		
		StringBuilder sql = new StringBuilder("SELECT * FROM TS_OP U ");
		
		if(StringUtils.isNotBlank(questionName)) {
			sql.append(" WHERE U.OPNO LIKE '%").append(questionName).append("%' ");
		}
		DBDYPO[] po =DBDYDao.selectBySQL(DBConn.getConnection("SSOdbService"), sql.toString());
		String pid =po[0].getString("PERSON_ID");
		
		DBDYPO question = new DBDYPO(tableName, keyField, request);
		
		String uid = request.getParameter(keyField);
		
		String res = ac.getHttpRequest().getParameter("RES");
		
		System.out.println(res+" is res");
		checkAuth(ac, authFuncNo, RIGHT_TWO);
		int result = 0;
		int result1 = 0;
		boolean isAdd = false;
		
		uid = GenID.genIdString("Q", 21);
		question.set(keyField, uid);
		question.set("RESULT", res);
		question.set("DATE", new java.sql.Date(System.currentTimeMillis()));
		isAdd = true;
		
		
		DBDYPO con = new DBDYPO("PATIENT_QUESTION", "PATIENT_ID,RESULT_ID");
		con.set("RESULT_ID", uid);
		con.set("PATIENT_ID", pid);
		result = DBDYDao.insert(ac.getConnection(), question);
		result1 = DBDYDao.insert(ac.getConnection(), con);
		if(0 == result||(0==result1)) {
			log(ac, LOGLEVEL_W, "SYS01", question.getTableName(), uid, isAdd ? "insert" : "update", "保存失败!");
			setMessage(ac, "保存失败!");
		} else {
			log(ac, LOGLEVEL_I, "SYS01", question.getTableName(), uid, isAdd ? "insert" : "update", "保存成功!");
			setMessage(ac, "保存成功!");
		}
		
		return CONST_RESULT_AJAX;
	}

}
