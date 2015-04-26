package com.xmdx.demo.message.service;

import java.io.File;
import java.sql.Connection;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.e9rj.platform.common.BaseConstants;
import com.e9rj.platform.common.GenID;
import com.e9rj.platform.common.StringUtil;
import com.e9rj.platform.common.services.BusinessServices;
import com.e9rj.platform.util.SessionUtil;
import com.xmdx.demo.back.dao.ZkgkConstants;
import com.xmzy.frameext.business.service.annotate.Service;
import com.xmzy.frameext.json.FastJsonUtil;
import com.xmzy.frameext.simpledb.DBConn;
import com.xmzy.frameext.simpledb.DBDYDao;
import com.xmzy.frameext.simpledb.DBDYPO;
import com.xmzy.frameext.simpledb.page.JdbcPage;
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
		String userName=SessionUtil.getOpno(ac);
//		
		StringBuilder sql = new StringBuilder("SELECT * FROM TS_OP U ");
//		
		if(StringUtils.isNotBlank(userName)) {
			sql.append(" WHERE U.OPNO LIKE '%").append(userName).append("%' ");
		}
		DBDYPO[] po =DBDYDao.selectBySQL(DBConn.getConnection("SSOdbService"), sql.toString());
		String id =po[0].getString("PERSON_ID");
		System.out.println("id="+id);
		StringBuilder ssql = new StringBuilder("SELECT * FROM MESSAGE U ");
		if(StringUtils.isNotBlank(id)) {
			ssql.append(" WHERE U.PATIENT_ID LIKE '%").append(id).append("%' ORDER BY IS_REPLY DESC,TIME DESC ");
		}
		DBDYPO[] pop =DBDYDao.selectBySQL(ac.getConnection(), ssql.toString());
		if(pop.length==0)
		{
			ac.setStringValue("SIZE", String.valueOf(pop.length));
			ac.setStringValue(CONST_FORMNAME, "com/xmdx/demo/message/patient_message_main.html");		
			return CONST_RESULT_SUCCESS;
		}
		
		else{
		int pageNumber = BaseConstants.getQueryPageNumber(ac);
		int pageSize = BaseConstants.getQueryPageSize(ac);
					
		JdbcPage page =  DBDYDao.select2JdbcPage(ac.getConnection(), ssql.toString(), pageNumber, 2);
		
		
		List<DBDYPO> polist = page.getThisPageList();
		List<DBDYPO> projects = new ArrayList<DBDYPO>();
		for(int i = 0; i< polist.size();i++){
			DBDYPO po1 = polist.get(i);
			po1.set("PATIENT_NAME", pop[i].get("PATIENT_NAME").toString());
			po1.set("CONTENT", pop[i].get("CONTENT").toString());
			po1.set("IS_REPLY", pop[i].get("IS_REPLY").toString());
			po1.set("IS_READ", pop[i].get("IS_READ").toString());
			po1.set("IS_NEW", pop[i].get("IS_NEW").toString());
			po1.set("DOCTOR_ID", pop[i].get("DOCTOR_ID").toString());
			po1.set("PATIENT_ID", pop[i].get("PATIENT_ID").toString());
			po1.set("IMAGE", pop[i].get("IMAGE").toString());
			po1.set("DOCTOR_NAME", pop[i].get("DOCTOR_NAME").toString());
			po1.set("TIME", pop[i].get("TIME").toString());
			projects.add(po1);
		}
		
		ac.setObjValue("APP", projects);
		
		String jsonStr  = FastJsonUtil.jdbcPage2JsonString(page);
		JSONObject jsonObject = JSONObject.parseObject(jsonStr);
		ac.setObjValue("PAGE_BEAN", jsonObject);
		ac.setStringValue("SIZE", String.valueOf(pop.length));
		ac.setStringValue(CONST_FORMNAME, "com/xmdx/demo/message/patient_message_main.html");
	
		return CONST_RESULT_SUCCESS;
		}
	}

	/**
	 * 
	 * @param ac
	 * @return
	 * @throws 病患查看消息详细信息
	 */
	public int read(ActionContext ac) throws Exception {
		System.out.println("enter read message");
		int state = 1;
		int new_state = 0;
		String messageid = ac.getHttpRequest().getParameter("MESSAGE_ID");
		System.out.println("message id = "+messageid);
		StringBuilder sql = new StringBuilder("SELECT * FROM MESSAGE U ");
		
		if(StringUtils.isNotBlank(messageid)) {
			sql.append(" WHERE U.MESSAGE_ID LIKE '%").append(messageid).append("%' ");
		}
		
		StringBuilder ssql = new StringBuilder("SELECT * FROM COMMENT U ");
		
		if(StringUtils.isNotBlank(messageid)) {
			ssql.append(" WHERE U.MESSAGE_ID LIKE '%").append(messageid).append("%' ORDER BY TIME ASC");
		}
		
		DBDYPO[] pocom = DBDYDao.selectBySQL(ac.getConnection(), ssql.toString());
		
		
		DBDYPO[] po =DBDYDao.selectBySQL(ac.getConnection(), sql.toString());

		DBDYPO pop = new DBDYPO(tableName,keyField);
		pop.set("MESSAGE_ID", messageid);
		DBDYDao.selectByID(ac.getConnection(), pop);
		pop.set("IS_READ", state);
		pop.set("IS_REPLY", new_state);
		DBDYDao.update(ac.getConnection(), pop);
		
		
		
		ac.setObjValue("MESSAGE", po[0]);
		ac.setObjValue("COMMENT",pocom);
		ac.setStringValue(CONST_FORMNAME, "com/xmdx/demo/message/patient_message_read.html");
		
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

	/**
	 * 病患提咨询
	 * 
	 */
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
	 * 患者评论
	 * 
	 */
	
	public int new_save(ActionContext ac) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("enter save");
		
		String content = ac.getHttpRequest().getParameter("CONTENT");
		String messageid = ac.getHttpRequest().getParameter("MESSAGE_ID");
		String doctorid = ac.getHttpRequest().getParameter("DOCTOR_ID");
		String patientid = ac.getHttpRequest().getParameter("PATIENT_ID");
		String doctorname = ac.getHttpRequest().getParameter("DOCTOR_NAME");
		String patientname = ac.getHttpRequest().getParameter("PATIENT_NAME");
		DBDYPO pop = new DBDYPO("COMMENT","COMMENT_ID");
		int idlLen = 20;
		int state = 0;
		int result = 0;
		int reply_state = 1;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		 Timestamp nowdate1 = new Timestamp(System.currentTimeMillis());
		 String datestr = sdf.format(nowdate1);
		String commentID = super.genIdString("", idlLen);
		pop.set("COMMENT_ID", commentID);
		pop.set("MESSAGE_ID", messageid);
		pop.set("CONTENT", content);
		pop.set("TIME", datestr);
		pop.set("DOCTOR_ID", doctorid);
		pop.set("FROM_NAME", patientname);
		pop.set("PATIENT_ID", patientid);
		pop.set("TO_NAME", doctorname);
		pop.set("STATE", state);
		
		result = DBDYDao.insert(ac.getConnection(), pop);
		
		DBDYPO po = new DBDYPO(tableName,keyField);
		po.set("MESSAGE_ID", messageid);
		DBDYDao.selectByID(ac.getConnection(), po);
		//给病患指示器，0时表示医生无新回复，1时表示医生有新回复
		po.set("IS_NEW", reply_state);
		int result1 = DBDYDao.update(ac.getConnection(), po);
		if((0 == result)||(0 == result1)) {
			
			setMessage(ac, "回复失败!");
		} else {
			
			setMessage(ac, "回复成功!");
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
		return CONST_RESULT_AJAX;
	}
	
	/**
	 * patient message分页
	 * @param ac
	 * @return
	 * @throws Exception
	 */
	public int mymessage(ActionContext ac) throws Exception {
		// TODO Auto-generated method stub
		
		String userName=SessionUtil.getOpno(ac);
//		
		StringBuilder sql = new StringBuilder("SELECT * FROM TS_OP U ");
//		
		if(StringUtils.isNotBlank(userName)) {
			sql.append(" WHERE U.OPNO LIKE '%").append(userName).append("%' ");
		}
		DBDYPO[] po =DBDYDao.selectBySQL(DBConn.getConnection("SSOdbService"), sql.toString());
		String id =po[0].getString("PERSON_ID");
		System.out.println("id="+id);
		StringBuilder ssql = new StringBuilder("SELECT * FROM MESSAGE U ");
		if(StringUtils.isNotBlank(id)) {
			ssql.append(" WHERE U.PATIENT_ID LIKE '%").append(id).append("%' ORDER BY IS_REPLY DESC,TIME DESC");
		}
		DBDYPO[] pop =DBDYDao.selectBySQL(ac.getConnection(), ssql.toString());
//		
//		ac.setObjValue("APP", pop);
//		ac.setStringValue(CONST_FORMNAME, "com/xmdx/demo/application/apply_main.html");		
//		return CONST_RESULT_SUCCESS;
		if(pop.length==0)
		{
			ac.setStringValue("SIZE", String.valueOf(pop.length));
			ac.setStringValue(CONST_FORMNAME, "com/xmdx/demo/message/patient_mymessage_main.html");		
			return CONST_RESULT_SUCCESS;
		}
		else{
		
		int pageNumber = BaseConstants.getQueryPageNumber(ac);
		int pageSize = BaseConstants.getQueryPageSize(ac);
					
		JdbcPage page =  DBDYDao.select2JdbcPage(ac.getConnection(), ssql.toString(), pageNumber, 2);
		
		
		List<DBDYPO> polist = page.getThisPageList();
		List<DBDYPO> projects = new ArrayList<DBDYPO>();
		String jsonStr  = FastJsonUtil.jdbcPage2JsonString(page);
		JSONObject jsonObject = JSONObject.parseObject(jsonStr);
		ac.setObjValue("PAGE_BEAN", jsonObject);
		String currentpage = jsonObject.getString("CurrentPage");
		int c = Integer.parseInt(currentpage);
		System.out.println("currentpage="+currentpage);
		int n = (c-1)*2;
		
		for(int i = 0; i< polist.size();i++,n++){
			System.out.println("enter if");
			DBDYPO po1 = polist.get(i);
			po1.set("PATIENT_NAME", pop[n].get("PATIENT_NAME").toString());
			po1.set("CONTENT", pop[n].get("CONTENT").toString());
			po1.set("IS_REPLY", pop[n].get("IS_REPLY").toString());
			po1.set("IS_READ", pop[n].get("IS_READ").toString());
			po1.set("IS_NEW", pop[n].get("IS_NEW").toString());
			po1.set("DOCTOR_ID", pop[n].get("DOCTOR_ID").toString());
			po1.set("PATIENT_ID", pop[n].get("PATIENT_ID").toString());
			po1.set("IMAGE", pop[n].get("IMAGE").toString());
			po1.set("DOCTOR_NAME", pop[n].get("DOCTOR_NAME").toString());
			po1.set("TIME", pop[n].get("TIME").toString());
			projects.add(po1);
		}
		
		ac.setObjValue("APP", projects);
		
		
		ac.setStringValue("SIZE", String.valueOf(pop.length));
		ac.setStringValue(CONST_FORMNAME, "com/xmdx/demo/message/patient_mymessage_main.html");
	
		return CONST_RESULT_SUCCESS;
		
		}
	}

}
