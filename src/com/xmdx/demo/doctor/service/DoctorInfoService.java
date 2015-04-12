package com.xmdx.demo.doctor.service;

import java.sql.Connection;

import org.apache.commons.lang.StringUtils;

import com.e9rj.platform.common.GenID;
import com.e9rj.platform.common.services.BusinessServices;
import com.e9rj.platform.util.SessionUtil;
import com.xmdx.demo.back.dao.ZkgkConstants;
import com.xmzy.frameext.business.service.annotate.Service;
import com.xmzy.frameext.simpledb.DBConn;
import com.xmzy.frameext.simpledb.DBDYDao;
import com.xmzy.frameext.simpledb.DBDYPO;
import com.xmzy.framework.context.ActionContext;
@Service(name="doctor.info")
public class DoctorInfoService extends BusinessServices {
	//功能号
	private static final String authFuncNo = "doctor.info";
	//表名
	private static final String tableName = "FRIEND_APPLY";
	//主键名
	private static final String keyField = "PERSON_ID";
	
	private static String uid = null;
	@Override
	public int delete(ActionContext arg0) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int goTo(ActionContext ac) throws Exception {
		System.out.println("enter goto");
		uid = ac.getHttpRequest().getParameter("DOCTOR_ID");
		System.out.println("doctor id = "+uid);
		// TODO Auto-generated method stub

		
		StringBuilder sqlp = new StringBuilder("SELECT * FROM DOCTOR U ");
		if(StringUtils.isNotBlank(uid)) {
			sqlp.append(" WHERE U.DOCTOR_ID LIKE '%").append(uid).append("%' ");
		}
		DBDYPO[] pop =DBDYDao.selectBySQL(ac.getConnection(), sqlp.toString());
		ac.setObjValue("USER", pop[0]);
		
		checkAuth(ac, authFuncNo, RIGHT_ONE);
		
		ac.setStringValue("tabLogo", authFuncNo);
		ac.setStringValue(CONST_FORMNAME, "com/xmdx/demo/doctor/doctor_info_connected.html");		
		return CONST_RESULT_SUCCESS;
	}

	@Override
	public int init(ActionContext ac) throws Exception {
		String userName=SessionUtil.getOpno(ac);
		System.out.println(userName+"这个是sessionid哦");
		StringBuilder sql = new StringBuilder("SELECT * FROM TS_OP U ");
		
		if(StringUtils.isNotBlank(userName)) {
			sql.append(" WHERE U.OPNO LIKE '%").append(userName).append("%' ");
		}
		DBDYPO[] po =DBDYDao.selectBySQL(DBConn.getConnection("SSOdbService"), sql.toString());
		String id =po[0].getString("PERSON_ID");
		
		StringBuilder sqlp = new StringBuilder("SELECT * FROM DOCTOR U ");
		if(StringUtils.isNotBlank(id)) {
			sqlp.append(" WHERE U.DOCTOR_ID LIKE '%").append(id).append("%' ");
		}
		DBDYPO[] pop =DBDYDao.selectBySQL(ac.getConnection(), sqlp.toString());
		ac.setObjValue("USER", pop[0]);
		
		checkAuth(ac, authFuncNo, RIGHT_ONE);
		
		ac.setStringValue("tabLogo", authFuncNo);
		ac.setStringValue(CONST_FORMNAME, "com/xmdx/demo/doctor/doctor_info.html");		
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
		// TODO Auto-generated method stub
		String userName=SessionUtil.getOpno(ac);
		StringBuilder sql = new StringBuilder("SELECT * FROM TS_OP U ");
		if(StringUtils.isNotBlank(userName)) {
			sql.append(" WHERE U.OPNO LIKE '%").append(userName).append("%' ");
		}
		DBDYPO[] po =DBDYDao.selectBySQL(DBConn.getConnection("SSOdbService"), sql.toString());
		String id =po[0].getString("PERSON_ID");
		int result = 0;
		String message = ac.getHttpRequest().getParameter("MESSAGE");
		System.out.println("patient_id ="+id);
		System.out.println("doctor_id ="+uid);
		System.out.println("message ="+message);
		
		java.sql.Timestamp createTime = new java.sql.Timestamp(System.currentTimeMillis());
		
		DBDYPO user = new DBDYPO(tableName, "DOCTOR_ID,PAITENT_ID");
		
		
	
			user.set("PATIENT_ID", id);
			user.set("DOCTOR_ID", uid);
			user.set("STATE", 0);
			user.set("MESSAGE", message);
			System.out.println("message="+message);
			user.set("CREATE_TIME", createTime);
			result = DBDYDao.insert(ac.getConnection(), user);
		
		if(0 == result) {
			
			setMessage(ac, "已向改医生提交过申请!");
		} else {
			
			setMessage(ac, "提交成功!");
		}
		
		return CONST_RESULT_AJAX;
		
		
	}

}
