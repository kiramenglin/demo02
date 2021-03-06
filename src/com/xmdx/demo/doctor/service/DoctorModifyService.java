package com.xmdx.demo.doctor.service;

import java.io.File;
import java.net.URLEncoder;
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
@Service(name="doctor.modify")
public class DoctorModifyService extends BusinessServices {
	//功能号
	private static final String authFuncNo = "doctor.modify";
	//表名
	private static final String tableName = "DOCTOR";
	//主键名
	private static final String keyField = "DOCTOR_ID";
	//病患id
	private static String uid = null;
	//医生id
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
		
		StringBuilder sqlp = new StringBuilder("SELECT * FROM DOCTOR U ");
		if(StringUtils.isNotBlank(id)) {
			sqlp.append(" WHERE U.DOCTOR_ID LIKE '%").append(id).append("%' ");
		}
		DBDYPO[] pop =DBDYDao.selectBySQL(ac.getConnection(), sqlp.toString());
		ac.setObjValue("USER", pop[0]);
		
		checkAuth(ac, authFuncNo, RIGHT_ONE);
		
		ac.setStringValue("tabLogo", authFuncNo);
		ac.setStringValue(CONST_FORMNAME, "com/xmdx/demo/doctor/doctor_info_modify.html");		
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
		String title = ac.getHttpRequest().getParameter("TITLE");
		String hospital = ac.getHttpRequest().getParameter("HOSPITAL");
		String section = ac.getHttpRequest().getParameter("SUBSECTION");
		String major = ac.getHttpRequest().getParameter("MAJOR");
		String information = ac.getHttpRequest().getParameter("INFORMATION");
		String place = ac.getHttpRequest().getParameter("PLACE");
		
		DBDYPO user = new DBDYPO(tableName, keyField);
		
		user.set("DOCTOR_ID", id);
		DBDYDao.selectByID(ac.getConnection(), user);
			user.set("NAME", name);
			user.set("GENDER", gender);
			user.set("TITLE", title);
			user.set("HOSPITAL", hospital);
			user.set("SECTION", section);
			user.set("MAJOR", major);
			user.set("INFORMATION", information);
			user.set("PLACE", place);
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
		code = URLEncoder.encode(code, "UTF-8");
		// 处理头像

//			DBDYPO doctor = new DBDYPO("DOCTOR", "DOCTOR_ID");
//			doctor.set("DOCTOR_ID", id);
//			DBDYDao.selectByID(ac.getConnection(),doctor);
//			if(!StringIsNullOrEmpty(code))
//			{
//			
//				doctor.set("IMAGE", code);
//				result = DBDYDao.update(ac.getConnection(), doctor);
//				if(1==result)
//				{System.out.println("头像上传成功！");}
//				else
//				{System.out.println("头像上传失败！");}
//			    return CONST_RESULT_SUCCESS;
//			}
//			
			
		
		//ac.setObjValue("CODE", code);
		setMessage(ac, code);
		return CONST_RESULT_AJAX;
	}

}
