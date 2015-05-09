package com.xmdx.demo.section.service;

import org.apache.commons.lang.StringUtils;

import com.e9rj.platform.common.CodeNameConvert;
import com.e9rj.platform.common.services.BusinessServices;
import com.xmzy.frameext.business.service.annotate.Service;
import com.xmzy.frameext.simpledb.DBDYDao;
import com.xmzy.frameext.simpledb.DBDYPO;
import com.xmzy.framework.context.ActionContext;
@Service(name="internal.nervous")
public class NervousService extends BusinessServices {
	//功能号
	private static final String authFuncNo = "internal.nervous";
	//表名
	private static final String tableName = "TB_USER";
	//主键名
	private static final String keyField = "U_ID";
	
	private static String text = null;
	@Override
	public int delete(ActionContext arg0) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int goTo(ActionContext ac) throws Exception {
		DBDYPO po = new DBDYPO(tableName, keyField, ac.getHttpRequest());
		String uid = ac.getHttpRequest().getParameter("DOCTOR_ID");
		if (StringUtils.isNotEmpty(uid)) {
			
			if("read".equalsIgnoreCase(ac.getStringValue(CONST_RESOURCEAUTH))) {
				//查看
				checkAuth(ac, authFuncNo, RIGHT_ONE);
			} else {
				// 修改
				checkAuth(ac, authFuncNo, RIGHT_FOUR);
			}
			
			DBDYPO[] pos = DBDYDao.selectByID(ac.getConnection(), po);
			
			if(pos.length == 0) {
				ac.setErrorContext("您所选择的医生已被删除！");
				return CONST_RESULT_ERROR;
			}
			DBDYPO old = pos[0];
			old.setCmd("U");
			ac.setObjValue("USER_BEAN", old);
		} else {
			// 新增
			checkAuth(ac, authFuncNo, RIGHT_TWO);
			po.setCmd("A");
			ac.setObjValue("USER_BEAN", po);
		}
		ac.setStringValue("FORMNAME", "com/xmdx/demo/doctor/doctor_edit.html");
		return CONST_RESULT_SUCCESS;
	}

	@Override
	public int init(ActionContext ac) throws Exception {
		text= new String(request.getParameter("TEXT").getBytes("ISO-8859-1"),"utf-8");
		System.out.println("text="+text);
		checkAuth(ac, authFuncNo, RIGHT_ONE);
		
		ac.setStringValue("tabLogo", authFuncNo);
		ac.setStringValue(CONST_FORMNAME, "com/xmdx/demo/section/internal_nervous.html");		
		return CONST_RESULT_SUCCESS;
	}

	@Override
	public int insertExportData(ActionContext arg0) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int query(ActionContext ac) throws Exception {
		// TODO Auto-generated method stub
		StringBuilder sql = new StringBuilder("SELECT * FROM DOCTOR U");
		if(StringUtils.isNotBlank(text)) {
			sql.append(" WHERE U.SECTION LIKE '%").append(text).append("%' ");
		}
		String userName = new String(request.getParameter("NAME").getBytes("ISO-8859-1"),"utf-8");
		
		if(StringUtils.isNotBlank(userName)) {
			sql.append("AND U.NAME LIKE '%").append(userName).append("%' ");
		}
		
		//设置排序条件，默认的按BIRTHDAY降序
		sql.append(super.order(ac, "U.BIRTHDAY", "DESC"));
		
		querySql = sql.toString();
		addNameCodeConvert("GENDER", CodeNameConvert.getCachedData("SYSCODE", "SEX"), REPLACE);
		return CONST_RESULT_AJAX;
	}

	@Override
	public int save(ActionContext arg0) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

}
