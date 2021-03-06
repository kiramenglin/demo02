package com.xmdx.demo.apply.service;


import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletResponse;





import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;


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

@Service(name="apply.info")
public class ApplyService extends BusinessServices {
	//功能号
		private static final String authFuncNo = "apply.info";
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
		
		System.out.println("patient_id="+pid);
		System.out.println("doctor_id="+did);
		
		DBDYPO pop = new DBDYPO(tableName,"DOCTOR_ID,PATIENT_ID");
		pop.set("DOCTOR_ID", did);
		pop.set("PATIENT_ID", pid);
	    DBDYDao.selectByID(ac.getConnection(), pop);

		result = DBDYDao.delete(ac.getConnection(), pop);
		json = new JSONObject();
		if(0 == result) {
			
			json.put("result","删除失败！");
		} else {
			 
			
			
			json.put("result","删除成功！");
		}
		ac.getHttpResponse().getWriter().write(json.toString());
		return CONST_RESULT_AJAX;
	}

	@Override
	public int goTo(ActionContext ac) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("enter goto");
		JSONObject json = null;
		int result = 0;
		String did = new String(request.getParameter("DOCTOR_ID").getBytes("ISO-8859-1"),"utf-8");
		String pid = new String(request.getParameter("PATIENT_ID").getBytes("ISO-8859-1"),"utf-8");
		
		System.out.println("patient_id="+pid);
		System.out.println("doctor_id="+did);
		String message = new String(request.getParameter("MESSAGE").getBytes("ISO-8859-1"),"utf-8");
		System.out.println("message="+message);
		DBDYPO pop = new DBDYPO(tableName,"DOCTOR_ID,PATIENT_ID");
		pop.set("DOCTOR_ID", did);
		pop.set("PATIENT_ID", pid);
		DBDYDao.selectByID(ac.getConnection(), pop);
		pop.set("STATE","2");
		pop.set("REJECT_MESSAGE", message);
		result = DBDYDao.update(ac.getConnection(), pop);
		System.out.println("resultsaoifjsaodjf="+result);
		json = new JSONObject();
		if(0 == result) {
			
			json.put("result","拒绝失败！");
		} else {
			 
			
			
			json.put("result","拒绝成功！");
		}
		ac.getHttpResponse().getWriter().write(json.toString());
		return CONST_RESULT_AJAX;
		
	}

	@Override
	public int init(ActionContext ac) throws Exception {
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
		StringBuilder ssql = new StringBuilder("SELECT * FROM FRIEND_APPLY U ");
		if(StringUtils.isNotBlank(id)) {
			ssql.append(" WHERE U.DOCTOR_ID LIKE '%").append(id).append("%' ");
		}
		DBDYPO[] pop =DBDYDao.selectBySQL(ac.getConnection(), ssql.toString());

		if(pop.length==0)
		{
			ac.setStringValue("SIZE", String.valueOf(pop.length));
			ac.setStringValue(CONST_FORMNAME, "com/xmdx/demo/application/apply_main.html");		
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
			po1.set("MESSAGE", pop[i].get("MESSAGE").toString());
			po1.set("CREATE_TIME", pop[i].get("CREATE_TIME").toString());
			po1.set("STATE", pop[i].get("STATE").toString());
			po1.set("PATIENT_ID", pop[i].get("PATIENT_ID").toString());
			po1.set("DOCTOR_ID", pop[i].get("DOCTOR_ID").toString());
			projects.add(po1);
		}
		
		ac.setObjValue("APP", projects);
		
		String jsonStr  = FastJsonUtil.jdbcPage2JsonString(page);
		JSONObject jsonObject = JSONObject.parseObject(jsonStr);
		ac.setObjValue("PAGE_BEAN", jsonObject);
		String currentpage = jsonObject.getString("CurrentPage");
		System.out.println("currentpage = "+currentpage);
		ac.setStringValue("SIZE", String.valueOf(pop.length));
		ac.setStringValue(CONST_FORMNAME, "com/xmdx/demo/application/apply_main.html");
	
		return CONST_RESULT_SUCCESS;
		
		}
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
		
		System.out.println("enter agree");
		JSONObject json = null;
		
		
		int result = 0;
		String did = ac.getHttpRequest().getParameter("DOCTOR_ID");
		String pid = ac.getHttpRequest().getParameter("PATIENT_ID");
		System.out.println("doctorid="+did);
		System.out.println("patient="+pid);
		DBDYPO pop = new DBDYPO(tableName,"DOCTOR_ID,PATIENT_ID");
		pop.set("DOCTOR_ID", did);
		pop.set("PATIENT_ID", pid);
		DBDYDao.selectByID(ac.getConnection(), pop);
		pop.set("STATE","1");
		result = DBDYDao.update(ac.getConnection(), pop);
		System.out.println("result2132133="+result);
		json = new JSONObject();
		if(0 == result) {
			
			json.put("result","通过失败！");
		} else {
			 
			
			
			json.put("result","通过成功！");
		}
		ac.getHttpResponse().getWriter().write(json.toString());
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
			ssql.append(" WHERE U.DOCTOR_ID LIKE '%").append(id).append("%' ");
		}
		DBDYPO[] pop =DBDYDao.selectBySQL(ac.getConnection(), ssql.toString());
//		
//		ac.setObjValue("APP", pop);
//		ac.setStringValue(CONST_FORMNAME, "com/xmdx/demo/application/apply_main.html");		
//		return CONST_RESULT_SUCCESS;
		if(pop.length==0)
		{
			ac.setStringValue("SIZE", String.valueOf(pop.length));
			ac.setStringValue(CONST_FORMNAME, "com/xmdx/demo/application/myapply_main.html");		
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
			po1.set("MESSAGE", pop[n].get("MESSAGE").toString());
			po1.set("CREATE_TIME", pop[n].get("CREATE_TIME").toString());
			po1.set("STATE", pop[n].get("STATE").toString());
			
			projects.add(po1);
		}
		
		ac.setObjValue("APP", projects);
		
		
		ac.setStringValue("SIZE", String.valueOf(pop.length));
		ac.setStringValue(CONST_FORMNAME, "com/xmdx/demo/application/myapply_main.html");
	
		return CONST_RESULT_SUCCESS;
		
		}
	}
}
