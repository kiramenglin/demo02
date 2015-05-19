package com.xmdx.demo.suggestion.service;

import java.io.File;
import java.net.URLEncoder;
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
@Service(name="doctor.suggestion")
/**
 * 
 * @author Jinghui Lu
 * 医生端建议服务
 *
 */
public class DoctorSuggestionService extends BusinessServices {
	/**
	 * 病患向医生咨询服务
	 * 
	 */
	//功能号
	private static final String authFuncNo = "doctor.suggestion";
	//表名
	private static final String tableName = "SUGGESTION";
	//主键名
	private static final String keyField = "SUGGESTION_ID";
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
		id = ac.getHttpRequest().getParameter("PATIENT_ID");
		System.out.println("patient id = "+id);
		
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
		ac.setStringValue(CONST_FORMNAME, "com/xmdx/demo/suggestion/suggestion_submit.html");		
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
		StringBuilder ssql = new StringBuilder("SELECT * FROM SUGGESTION U ");
		if(StringUtils.isNotBlank(id)) {
			ssql.append(" WHERE U.DOCTOR_ID LIKE '%").append(id).append("%' ORDER BY IS_REPLY DESC,TIME DESC ");
		}
		DBDYPO[] pop =DBDYDao.selectBySQL(ac.getConnection(), ssql.toString());
		if(pop.length==0)
		{
			ac.setStringValue("SIZE", String.valueOf(pop.length));
			ac.setStringValue(CONST_FORMNAME, "com/xmdx/demo/suggestion/doctor_suggestion_main.html");		
			return CONST_RESULT_SUCCESS;
		}
		
		else{
		int pageNumber = BaseConstants.getQueryPageNumber(ac);
		int pageSize = BaseConstants.getQueryPageSize(ac);
					
		JdbcPage page =  DBDYDao.select2JdbcPage(ac.getConnection(), ssql.toString(), pageNumber, 10);
		
		
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
			//po1.set("IMAGE", pop[i].get("IMAGE").toString());
			po1.set("DOCTOR_NAME", pop[i].get("DOCTOR_NAME").toString());
			po1.set("TIME", pop[i].get("TIME").toString());
			po1.set("TITLE", pop[i].get("TITLE").toString());
			po1.set("SUGGESTION_ID", pop[i].get("SUGGESTION_ID").toString());
			projects.add(po1);
		}
		
		ac.setObjValue("APP", projects);
		
		String jsonStr  = FastJsonUtil.jdbcPage2JsonString(page);
		JSONObject jsonObject = JSONObject.parseObject(jsonStr);
		ac.setObjValue("PAGE_BEAN", jsonObject);
		ac.setStringValue("SIZE", String.valueOf(pop.length));
		ac.setStringValue(CONST_FORMNAME, "com/xmdx/demo/suggestion/doctor_suggestion_main.html");
	
		return CONST_RESULT_SUCCESS;
		}
	}

	/**
	 * 
	 * @param ac
	 * @return
	 * @throws 医生查看建议详细信息
	 */
	public int read(ActionContext ac) throws Exception {
		System.out.println("enter read suggestion");
		int state = 1;
		int new_state = 0;
		String suggestionid = ac.getHttpRequest().getParameter("SUGGESTION_ID");
		System.out.println("suggestion id = "+suggestionid);
		StringBuilder sql = new StringBuilder("SELECT * FROM SUGGESTION U ");
		
		if(StringUtils.isNotBlank(suggestionid)) {
			sql.append(" WHERE U.SUGGESTION_ID LIKE '%").append(suggestionid).append("%' ");
		}
		
		StringBuilder ssql = new StringBuilder("SELECT * FROM SCOMMENT U ");
		
		if(StringUtils.isNotBlank(suggestionid)) {
			ssql.append(" WHERE U.SUGGESTION_ID LIKE '%").append(suggestionid).append("%' ORDER BY TIME ASC");
		}
		
		DBDYPO[] pocom = DBDYDao.selectBySQL(ac.getConnection(), ssql.toString());
		
		
		DBDYPO[] po =DBDYDao.selectBySQL(ac.getConnection(), sql.toString());

		DBDYPO pop = new DBDYPO(tableName,keyField);
		pop.set("SUGGESTION_ID", suggestionid);
		DBDYDao.selectByID(ac.getConnection(), pop);
		
		pop.set("IS_REPLY", new_state);
		DBDYDao.update(ac.getConnection(), pop);
		
		DBDYPO[] alist = DBDYDao.selectByID(ac.getConnection(), pop);
		
		pop.set("IS_NEW", new_state);
		DBDYDao.update(ac.getConnection(), pop);
		
		String doctor_id = alist[0].get("DOCTOR_ID").toString();
		String patient_id = alist[0].get("PATIENT_ID").toString();
		System.out.println("doctor_id="+doctor_id);
		System.out.println("patient_id="+patient_id);
		
		StringBuilder doctorsql = new StringBuilder("SELECT * FROM DOCTOR U ");
		if(StringUtils.isNotBlank(doctor_id)) {
			doctorsql.append(" WHERE U.DOCTOR_ID LIKE '%").append(doctor_id).append("%' ");
		}
		DBDYPO[] podoctor =DBDYDao.selectBySQL(ac.getConnection(), doctorsql.toString());
		ac.setObjValue("DOCTOR", podoctor[0]);
		
		StringBuilder patientsql = new StringBuilder("SELECT * FROM PATIENT U ");
		if(StringUtils.isNotBlank(patient_id)) {
			patientsql.append(" WHERE U.PATIENT_ID LIKE '%").append(patient_id).append("%' ");
		}
		DBDYPO[] popatient =DBDYDao.selectBySQL(ac.getConnection(), patientsql.toString());
		ac.setObjValue("PATIENT", popatient[0]);
		
		System.out.println("end1");
		
		ac.setObjValue("MESSAGE", po[0]);
		ac.setObjValue("COMMENT",pocom);
		ac.setStringValue(CONST_FORMNAME, "com/xmdx/demo/suggestion/doctor_suggestion_read.html");
		System.out.println("end2");
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
		
		StringBuilder ssql = new StringBuilder("SELECT * FROM PATIENT U ");
		if(StringUtils.isNotBlank(userName)) {
			ssql.append(" WHERE U.PATIENT_ID LIKE '%").append(id).append("%' ");
		}
		DBDYPO[] ppo =DBDYDao.selectBySQL(ac.getConnection(), ssql.toString());
		name = ppo[0].getString("NAME");
		System.out.println("patientname = "+name);
		int state = 0;
		int result = 0;
		DBDYPO[] po =DBDYDao.selectBySQL(DBConn.getConnection("SSOdbService"), sql.toString());
		String did =po[0].getString("PERSON_ID");
		String suggestion = ac.getHttpRequest().getParameter("MESSAGE");
		String title = ac.getHttpRequest().getParameter("TITLE");
		StringBuilder dsql = new StringBuilder("SELECT * FROM DOCTOR U ");
		if(StringUtils.isNotBlank(did)) {
			dsql.append(" WHERE U.DOCTOR_ID LIKE '%").append(did).append("%' ");
		}
		DBDYPO[] ppop =DBDYDao.selectBySQL(ac.getConnection(), dsql.toString());
		String doctorname = ppop[0].getString("NAME");
		int idlLen = 20;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		 Timestamp nowdate1 = new Timestamp(System.currentTimeMillis());
		 String datestr = sdf.format(nowdate1);
		String suggestionID = super.genIdString("", idlLen);
		
		System.out.println("content="+suggestion);
		System.out.println("doctor_id="+id);
		System.out.println("patient_id="+pid);
		System.out.println("doctor_name="+doctorname);
		System.out.println("patient_name="+name);
		System.out.println("time="+datestr);
		System.out.println("suggestion_id="+suggestionID);
		
		DBDYPO newMessage = new DBDYPO(tableName, keyField);
		newMessage.set(keyField, suggestionID);
		newMessage.set("CONTENT", suggestion);
		newMessage.set("DOCTOR_ID", did);
		newMessage.set("PATIENT_ID", id);
		newMessage.set("DOCTOR_NAME", doctorname);
		newMessage.set("PATIENT_NAME", name);
		newMessage.set("TIME", datestr);
		newMessage.set("IS_REPLY", state);
		newMessage.set("IS_NEW", state);
		newMessage.set("IS_READ", state);
		newMessage.set("IMAGE", code);
		newMessage.set("TITLE", title);
		
		result = DBDYDao.insert(ac.getConnection(),newMessage);
		
		if(0 == result) {
			
		    ac.getConnection().rollback();
		    setMessage(ac, "建议失败!");
			
		} 
		else{
			setMessage(ac, "建议成功!");
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
		String suggestionid = ac.getHttpRequest().getParameter("MESSAGE_ID");
		String doctorid = ac.getHttpRequest().getParameter("DOCTOR_ID");
		String patientid = ac.getHttpRequest().getParameter("PATIENT_ID");
		String doctorname = ac.getHttpRequest().getParameter("DOCTOR_NAME");
		String patientname = ac.getHttpRequest().getParameter("PATIENT_NAME");
		
		System.out.println("content="+content);
		System.out.println("doctorid="+doctorid);
		System.out.println("doctorname="+doctorname);
		System.out.println("patientid="+patientid);
		System.out.println("patientname="+patientname);
		System.out.println("suggestionid="+suggestionid);
	
		DBDYPO pop = new DBDYPO("SCOMMENT","SCOMMENT_ID");
		int idlLen = 20;
		int state = 0;
		int result = 0;
		int reply_state = 1;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		 Timestamp nowdate1 = new Timestamp(System.currentTimeMillis());
		 String datestr = sdf.format(nowdate1);
		String commentID = super.genIdString("", idlLen);
		pop.set("SCOMMENT_ID", commentID);
		pop.set("SUGGESTION_ID", suggestionid);
		pop.set("CONTENT", content);
		pop.set("TIME", datestr);
		pop.set("DOCTOR_ID", doctorid);
		pop.set("FROM_NAME", doctorname);
		pop.set("PATIENT_ID", patientid);
		pop.set("TO_NAME", patientname);
		pop.set("STATE", state);
		
		result = DBDYDao.insert(ac.getConnection(), pop);
		
		DBDYPO po = new DBDYPO(tableName,keyField);
		po.set("SUGGESTION_ID", suggestionid);
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
		code = URLEncoder.encode(code, "UTF-8");
		
		setMessage(ac, code);
		return CONST_RESULT_AJAX;
	}
	
	/**
	 * patient suggestion分页
	 * @param ac
	 * @return
	 * @throws Exception
	 */
	public int mysuggestion(ActionContext ac) throws Exception {
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
		StringBuilder ssql = new StringBuilder("SELECT * FROM SUGGESTION U ");
		if(StringUtils.isNotBlank(id)) {
			ssql.append(" WHERE U.DOCTOR_ID LIKE '%").append(id).append("%' ORDER BY IS_REPLY DESC,TIME DESC");
		}
		DBDYPO[] pop =DBDYDao.selectBySQL(ac.getConnection(), ssql.toString());
//		
//		ac.setObjValue("APP", pop);
//		ac.setStringValue(CONST_FORMNAME, "com/xmdx/demo/application/apply_main.html");		
//		return CONST_RESULT_SUCCESS;
		if(pop.length==0)
		{
			ac.setStringValue("SIZE", String.valueOf(pop.length));
			ac.setStringValue(CONST_FORMNAME, "com/xmdx/demo/suggestion/doctor_mysuggestion_main.html");		
			return CONST_RESULT_SUCCESS;
		}
		else{
		
		int pageNumber = BaseConstants.getQueryPageNumber(ac);
		int pageSize = BaseConstants.getQueryPageSize(ac);
					
		JdbcPage page =  DBDYDao.select2JdbcPage(ac.getConnection(), ssql.toString(), pageNumber, 10);
		
		
		List<DBDYPO> polist = page.getThisPageList();
		List<DBDYPO> projects = new ArrayList<DBDYPO>();
		String jsonStr  = FastJsonUtil.jdbcPage2JsonString(page);
		JSONObject jsonObject = JSONObject.parseObject(jsonStr);
		ac.setObjValue("PAGE_BEAN", jsonObject);
		String currentpage = jsonObject.getString("CurrentPage");
		int c = Integer.parseInt(currentpage);
		System.out.println("currentpage="+currentpage);
		int n = (c-1)*10;
		
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
			//po1.set("IMAGE", pop[n].get("IMAGE").toString());
			po1.set("DOCTOR_NAME", pop[n].get("DOCTOR_NAME").toString());
			po1.set("TIME", pop[n].get("TIME").toString());
			po1.set("TITLE", pop[n].get("TITLE").toString());
			po1.set("SUGGESTION_ID", pop[n].get("SUGGESTION_ID").toString());
			projects.add(po1);
		}
		
		ac.setObjValue("APP", projects);
		
		
		ac.setStringValue("SIZE", String.valueOf(pop.length));
		ac.setStringValue(CONST_FORMNAME, "com/xmdx/demo/suggestion/doctor_mysuggestion_main.html");
	
		return CONST_RESULT_SUCCESS;
		
		}
	}

}
