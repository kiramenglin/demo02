package com.xmdx.demo.patient.service;

import java.io.File;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.e9rj.platform.common.GenID;
import com.e9rj.platform.common.StringUtil;
import com.e9rj.platform.common.services.BusinessServices;
import com.e9rj.platform.util.SessionUtil;
import com.xmdx.demo.back.dao.ZkgkConstants;
import com.xmzy.frameext.business.service.annotate.Service;
import com.xmzy.frameext.simpledb.DBConn;
import com.xmzy.frameext.simpledb.DBDYDao;
import com.xmzy.frameext.simpledb.DBDYPO;
import com.xmzy.frameext.simpledb.DBOperate;
import com.xmzy.framework.context.ActionContext;
@Service(name="patient.modify")
public class PatientModifyService extends BusinessServices {
	//功能号
	private static final String authFuncNo = "patient.modify";
	//表名
	private static final String tableName = "PATIENT";
	//主键名
	private static final String keyField = "PATIENT_ID";
	//医生id
	private static String uid = null;
	//病患id
	private static String id = null;
	//图片base64码
	private static String code = null;
	@Override
	public int delete(ActionContext arg0) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int goTo(ActionContext ac) throws Exception {
		return 0;
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
		id =po[0].getString("PERSON_ID");
		
		StringBuilder sqlp = new StringBuilder("SELECT * FROM PATIENT U ");
		if(StringUtils.isNotBlank(id)) {
			sqlp.append(" WHERE U.PATIENT_ID LIKE '%").append(id).append("%' ");
		}
		DBDYPO[] pop =DBDYDao.selectBySQL(ac.getConnection(), sqlp.toString());
		ac.setObjValue("USER", pop[0]);
		
		checkAuth(ac, authFuncNo, RIGHT_ONE);
		
		ac.setStringValue("tabLogo", authFuncNo);
		ac.setStringValue(CONST_FORMNAME, "com/xmdx/demo/patient/patient_info_modify.html");		
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
	/**好友apply的insert方法
	 * 
	 */
	@Override
	public int save(ActionContext ac) throws Exception {
		// TODO Auto-generated method stub
		
		int result = 0;
		int result1 = 0;
		String name = ac.getHttpRequest().getParameter("NAME");
		String gender = ac.getHttpRequest().getParameter("GENDER");
		String birthday = ac.getHttpRequest().getParameter("BIRTHDAY");
		String nation = ac.getHttpRequest().getParameter("NATION");
		String marriage = ac.getHttpRequest().getParameter("MARRIAGE");
		String workplace = ac.getHttpRequest().getParameter("WORKPLACE");
		String occupation = ac.getHttpRequest().getParameter("OCCUPATION");
		
		
		DBDYPO user = new DBDYPO(tableName, keyField);
		
		user.set("PATIENT_ID", id);
		DBDYDao.selectByID(ac.getConnection(), user);
			user.set("NAME", name);
			user.set("GENDER", gender);
			user.set("BIRTHDAY", birthday);
			user.set("NATION", nation);
			user.set("MARRIAGE", marriage);
			user.set("WORKPLACE", workplace);
			user.set("OCCUPATION", occupation);
			
			user.set("IMAGE",code);
		result = DBDYDao.update(ac.getConnection(), user);
		
		
		
		if(0 == result) {
			
			setMessage(ac, "修改失败!");
		} else {
			
			setMessage(ac, "修改成功!");
		}
		
		return CONST_RESULT_AJAX;
		
		
	}
	
	/**
	 * 将图片文件转化为Base64编码
	 * 
	 * @param ac
	 * @return
	 * @throws Exception
	 */
	public int convertToBase64(ActionContext ac) throws Exception {
		System.out.println("enter converttobase64");
		int result = 0;
		String filename = ac.getHttpRequest().getParameter("FILENAME");
		String imgFilePath = ac.getHttpSession().getServletContext().getRealPath("tmpfiles");
		imgFilePath = imgFilePath + File.separator + filename;
		code = StringUtil.convertToBase64(imgFilePath);
		
		ac.setObjValue("CODE", code);
		return CONST_RESULT_SUCCESS;
	}

}
