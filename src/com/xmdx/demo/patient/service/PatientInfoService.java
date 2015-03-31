package com.xmdx.demo.patient.service;

import org.apache.commons.lang.StringUtils;

import com.e9rj.platform.common.services.BusinessServices;
import com.xmzy.frameext.business.service.annotate.Service;
import com.xmzy.frameext.simpledb.DBDYDao;
import com.xmzy.frameext.simpledb.DBDYPO;
import com.xmzy.framework.context.ActionContext;
@Service(name="patient.info")
public class PatientInfoService extends BusinessServices {
	//功能号
	private static final String authFuncNo = "back.patient";
	//表名
	private static final String tableName = "PATIENT";
	//主键名
	private static final String keyField = "PATIENT_ID";
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
		
		StringBuilder sql = new StringBuilder("SELECT * FROM PATIENT U ");
		String userName = "Kira";
		
		if(StringUtils.isNotBlank(userName)) {
			sql.append(" WHERE U.NAME LIKE '%").append(userName).append("%' ");
		}
		DBDYPO[] po =DBDYDao.selectBySQL(ac.getConnection(), sql.toString());
		
		ac.setObjValue("USER", po[0]);
		
		checkAuth(ac, authFuncNo, RIGHT_ONE);
		
		ac.setStringValue("tabLogo", authFuncNo);
		ac.setStringValue(CONST_FORMNAME, "com/xmdx/demo/back/patient_info.html");		
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
	public int save(ActionContext arg0) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

}
