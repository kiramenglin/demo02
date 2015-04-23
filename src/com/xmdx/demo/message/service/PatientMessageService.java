package com.xmdx.demo.message.service;

import java.io.File;
import java.sql.Connection;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

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
import com.xmzy.framework.context.ActionContext;
@Service(name="patient.message")
/**
 * 
 * @author Jinghui Lu
 * 病患端消息服务
 *
 */
public class PatientMessageService extends BusinessServices {
	/**
	 * 病患向医生咨询服务
	 * 
	 */
	//功能号
	private static final String authFuncNo = "patient.message";
	//表名
	private static final String tableName = "MESSAGE";
	//主键名
	private static final String keyField = "MESSAGE_ID";
	//医生id
	private static String id = null;
	//诊疗咨询图片
	private static String code = null;
	//医生名字
	private static String name = null;
	@Override
	public int delete(ActionContext arg0) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int goTo(ActionContext ac) throws Exception {
		System.out.println("enter goto");
		id = ac.getHttpRequest().getParameter("DOCTOR_ID");
		System.out.println("doctor id = "+id);
		
		// TODO Auto-generated method stub

		
//		StringBuilder sqlp = new StringBuilder("SELECT * FROM DOCTOR U ");
//		if(StringUtils.isNotBlank(uid)) {
//			sqlp.append(" WHERE U.DOCTOR_ID LIKE '%").append(uid).append("%' ");
//		}
//		DBDYPO[] pop =DBDYDao.selectBySQL(ac.getConnection(), sqlp.toString());
//		ac.setObjValue("USER", pop[0]);
//		
//		checkAuth(ac, authFuncNo, RIGHT_ONE);
//		
//		ac.setStringValue("tabLogo", authFuncNo);
		ac.setStringValue(CONST_FORMNAME, "com/xmdx/demo/message/message_submit.html");		
		return CONST_RESULT_SUCCESS;

	}

	@Override
	public int init(ActionContext ac) throws Exception {
		return 0;
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
		
		StringBuilder ssql = new StringBuilder("SELECT * FROM DOCTOR U ");
		if(StringUtils.isNotBlank(userName)) {
			ssql.append(" WHERE U.DOCTOR_ID LIKE '%").append(id).append("%' ");
		}
		DBDYPO[] ppo =DBDYDao.selectBySQL(ac.getConnection(), ssql.toString());
		name = ppo[0].getString("NAME");
		System.out.println("doctorname = "+name);
		int state = 0;
		int result = 0;
		DBDYPO[] po =DBDYDao.selectBySQL(DBConn.getConnection("SSOdbService"), sql.toString());
		String pid =po[0].getString("PERSON_ID");
		String message = ac.getHttpRequest().getParameter("MESSAGE");
		String title = ac.getHttpRequest().getParameter("TITLE");
		StringBuilder psql = new StringBuilder("SELECT * FROM PATIENT U ");
		if(StringUtils.isNotBlank(pid)) {
			psql.append(" WHERE U.PATIENT_ID LIKE '%").append(pid).append("%' ");
		}
		DBDYPO[] ppop =DBDYDao.selectBySQL(ac.getConnection(), psql.toString());
		String patientname = ppop[0].getString("NAME");
		int idlLen = 20;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		 Timestamp nowdate1 = new Timestamp(System.currentTimeMillis());
		 String datestr = sdf.format(nowdate1);
		String messageID = super.genIdString("", idlLen);
		
		System.out.println("content="+message);
		System.out.println("doctor_id="+id);
		System.out.println("patient_id="+pid);
		System.out.println("doctor_name="+name);
		System.out.println("patient_name="+patientname);
		System.out.println("time="+datestr);
		System.out.println("message_id="+messageID);
		
		DBDYPO newMessage = new DBDYPO(tableName, keyField);
		newMessage.set(keyField, messageID);
		newMessage.set("CONTENT", message);
		newMessage.set("DOCTOR_ID", id);
		newMessage.set("PATIENT_ID", pid);
		newMessage.set("DOCTOR_NAME", name);
		newMessage.set("PATIENT_NAME", patientname);
		newMessage.set("TIME", datestr);
		newMessage.set("IS_REPLY", state);
		newMessage.set("IS_NEW", state);
		newMessage.set("IS_READ", state);
		newMessage.set("IMAGE", code);
		newMessage.set("TITLE", title);
		
		result = DBDYDao.insert(ac.getConnection(),newMessage);
		
		if(0 == result) {
			
		    ac.getConnection().rollback();
		    setMessage(ac, "询问失败!");
			
		} 
		else{
			setMessage(ac, "询问成功!");
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
