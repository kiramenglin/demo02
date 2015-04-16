package com.xmdx.demo.section.service;

import org.apache.commons.lang.StringUtils;

import com.e9rj.platform.common.CodeNameConvert;
import com.e9rj.platform.common.services.BusinessServices;
import com.xmzy.frameext.business.service.annotate.Service;
import com.xmzy.framework.context.ActionContext;
@Service(name="internal.nervous")
public class NervousService extends BusinessServices {
	//功能号
	private static final String authFuncNo = "internal.nervous";
	//表名
	private static final String tableName = "TB_USER";
	//主键名
	private static final String keyField = "U_ID";
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
		StringBuilder sql = new StringBuilder("SELECT * FROM DOCTOR U  WHERE U.SECTION LIKE '%神经内科%'");
		String userName = request.getParameter("NAME");
		
		if(StringUtils.isNotBlank(userName)) {
			sql.append(" WHERE U.NAME LIKE '%").append(userName).append("%' ");
		}
		
		//设置排序条件，默认的按BIRTHDAY降序
		sql.append(super.order(ac, "U.BIRTHDAY", "DESC"));
		
		querySql = sql.toString();
		
		return CONST_RESULT_AJAX;
	}

	@Override
	public int save(ActionContext arg0) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

}
