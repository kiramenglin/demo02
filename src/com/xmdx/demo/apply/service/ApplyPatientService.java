package com.xmdx.demo.apply.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.e9rj.platform.common.BaseConstants;
import com.e9rj.platform.common.services.BusinessServices;
import com.e9rj.platform.util.SessionUtil;
import com.xmzy.frameext.business.service.annotate.Service;
import com.xmzy.frameext.json.FastJsonUtil;
import com.xmzy.frameext.simpledb.DBConn;
import com.xmzy.frameext.simpledb.DBDYDao;
import com.xmzy.frameext.simpledb.DBDYPO;
import com.xmzy.frameext.simpledb.page.JdbcPage;
import com.xmzy.framework.context.ActionContext;

@Service(name="apply.patient")
public class ApplyPatientService extends BusinessServices {
	//功能号
		private static final String authFuncNo = "apply.patient";
		//表名
		private static final String tableName = "FRIEND_APPLY";
		

	@Override
	public int delete(ActionContext ac) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("enter delete");
		JSONObject json = null;
		int result = 0;
		String did = new String(request.getParameter("DOCTOR_ID").getBytes("ISO-8859-1"),"utf-8");
		String pid = new String(request.getParameter("PATIENT_ID").getBytes("ISO-8859-1"),"utf-8");
		
		DBDYPO pop = new DBDYPO(tableName,"DOCTOR_ID,PATIENT_ID");
		pop.set("DOCTOR_ID", did);
		pop.set("PATIENT_ID", pid);
		DBDYDao.selectByID(ac.getConnection(), pop);
		
		result = DBDYDao.delete(ac.getConnection(), pop);
		json = new JSONObject();
		if(0 == result)
		{
			json.put("result","删除申请失败！");
		}
		else
		{
			json.put("result","删除申请成功！");
		}					
			
		ac.getHttpResponse().getWriter().write(json.toString());
		return CONST_RESULT_AJAX;
	}
	
	

	@Override
	public int goTo(ActionContext ac) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("enter edit");
		JSONObject json = null;
		int result = 0;
		String did = new String(request.getParameter("DOCTOR_ID").getBytes("ISO-8859-1"),"utf-8");
		String pid = new String(request.getParameter("PATIENT_ID").getBytes("ISO-8859-1"),"utf-8");
		String message = new String(request.getParameter("MESSAGE").getBytes("ISO-8859-1"),"utf-8");
		System.out.println("message="+message);
		DBDYPO pop = new DBDYPO(tableName,"DOCTOR_ID,PATIENT_ID");
		pop.set("DOCTOR_ID", did);
		pop.set("PATIENT_ID", pid);
		DBDYDao.selectByID(ac.getConnection(), pop);
		
		pop.set("MESSAGE", message);
		result = DBDYDao.update(ac.getConnection(), pop);
		json = new JSONObject();
		if(0 == result)
		{
			json.put("result","修改申请失败！");
		}
		else
		{
			json.put("result","修改申请成功！");
		}					
			
		ac.getHttpResponse().getWriter().write(json.toString());
		return CONST_RESULT_AJAX;
	}

	@Override
	public int init(ActionContext ac) throws Exception {
		// TODO Auto-generated method stub
		String userName=SessionUtil.getOpno(ac);
		
		StringBuilder sql = new StringBuilder("SELECT * FROM TS_OP U ");
		
		if(StringUtils.isNotBlank(userName)) {
			sql.append(" WHERE U.OPNO LIKE '%").append(userName).append("%' ");
		}
		DBDYPO[] po =DBDYDao.selectBySQL(DBConn.getConnection("SSOdbService"), sql.toString());
		String id =po[0].getString("PERSON_ID");
		System.out.println("id="+id);
		StringBuilder ssql = new StringBuilder("SELECT * FROM FRIEND_APPLY U ");
		if(StringUtils.isNotBlank(id)) {
			ssql.append(" WHERE U.PATIENT_ID LIKE '%").append(id).append("%' ");
		}
		DBDYPO[] pop =DBDYDao.selectBySQL(ac.getConnection(), ssql.toString());
		
		if(pop.length==0)
		{
			ac.setStringValue("SIZE", String.valueOf(pop.length));
			ac.setStringValue(CONST_FORMNAME, "com/xmdx/demo/application/applypatient_main.html");		
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
			po1.set("MESSAGE", pop[i].get("MESSAGE").toString());
			po1.set("CREATE_TIME", pop[i].get("CREATE_TIME").toString());
			po1.set("STATE", pop[i].get("STATE").toString());
			po1.set("PATIENT_ID", pop[i].get("PATIENT_ID").toString());
			po1.set("DOCTOR_ID", pop[i].get("DOCTOR_ID").toString());
			po1.set("DOCTOR_NAME", pop[i].get("DOCTOR_NAME").toString());
			projects.add(po1);
		}
		
		ac.setObjValue("APP", projects);
		
		String jsonStr  = FastJsonUtil.jdbcPage2JsonString(page);
		JSONObject jsonObject = JSONObject.parseObject(jsonStr);
		ac.setObjValue("PAGE_BEAN", jsonObject);
		String currentpage = jsonObject.getString("CurrentPage");
		System.out.println("currentpage = "+currentpage);
		ac.setStringValue("SIZE", String.valueOf(pop.length));
		ac.setStringValue(CONST_FORMNAME, "com/xmdx/demo/application/applypatient_main.html");
	
		return CONST_RESULT_SUCCESS;
		
		}
//		ac.setObjValue("APP", pop);
//		ac.setStringValue(CONST_FORMNAME, "com/xmdx/demo/application/applypatient_main.html");		
//		return CONST_RESULT_SUCCESS;
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
		System.out.println("enter agree");
		int result = 0;
		String did = ac.getHttpRequest().getParameter("DOCTOR_ID");
		String pid = ac.getHttpRequest().getParameter("PATIENT_ID");
		DBDYPO pop = new DBDYPO(tableName,"DOCTOR_ID,PATIENT_ID");
		pop.set("DOCTOR_ID", did);
		pop.set("PATIENT_ID", pid);
		DBDYDao.selectByID(ac.getConnection(), pop);
		pop.set("STATE","1");
		result = DBDYDao.update(ac.getConnection(), pop);
		if(0 == result) {
			
			setMessage(ac, "通过失败!");
		} else {
			
			setMessage(ac, "通过成功!");
		}
		return CONST_RESULT_AJAX;
	}
	
	public int myapply(ActionContext ac) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("enter my apply" +
				"");
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
		StringBuilder ssql = new StringBuilder("SELECT * FROM FRIEND_APPLY U ");
		if(StringUtils.isNotBlank(id)) {
			ssql.append(" WHERE U.PATIENT_ID LIKE '%").append(id).append("%' ");
		}
		DBDYPO[] pop =DBDYDao.selectBySQL(ac.getConnection(), ssql.toString());
//		
//		ac.setObjValue("APP", pop);
//		ac.setStringValue(CONST_FORMNAME, "com/xmdx/demo/application/apply_main.html");		
//		return CONST_RESULT_SUCCESS;
		if(pop.length==0)
		{
			ac.setStringValue("SIZE", String.valueOf(pop.length));
			ac.setStringValue(CONST_FORMNAME, "com/xmdx/demo/application/applypatient_main.html");		
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
			po1.set("MESSAGE", pop[n].get("MESSAGE").toString());
			po1.set("CREATE_TIME", pop[n].get("CREATE_TIME").toString());
			po1.set("STATE", pop[n].get("STATE").toString());
			po1.set("PATIENT_ID", pop[n].get("PATIENT_ID").toString());
			po1.set("DOCTOR_ID", pop[n].get("DOCTOR_ID").toString());
			po1.set("DOCTOR_NAME", pop[n].get("DOCTOR_NAME").toString());
			projects.add(po1);
		}
		
		ac.setObjValue("APP", projects);
		
		
		ac.setStringValue("SIZE", String.valueOf(pop.length));
		ac.setStringValue(CONST_FORMNAME, "com/xmdx/demo/application/myapplypatient_main.html");
	
		return CONST_RESULT_SUCCESS;
		
		}
	}

}
