package com.xmdx.demo.patient.service;

import org.apache.commons.lang.StringUtils;
import com.xmzy.framework.utils.StringUtil;
import com.e9rj.platform.common.services.BusinessServices;
import com.e9rj.platform.util.SessionUtil;
import com.xmzy.frameext.business.service.annotate.Service;
import com.xmzy.frameext.simpledb.DBConn;
import com.xmzy.frameext.simpledb.DBDYDao;
import com.xmzy.frameext.simpledb.DBDYPO;
import com.xmzy.framework.context.ActionContext;
import com.e9rj.platform.common.Encrypt;
@Service(name="patient.password")
public class PatientPasswordService extends BusinessServices {
	private static final String authFuncNo = "patient.password";
	
	private static final String tableName = "TS_OP";
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
		// TODO Auto-generated method stub
		checkAuth(ac, authFuncNo, RIGHT_ONE);
		
		ac.setStringValue("tabLogo", authFuncNo);
		ac.setStringValue(CONST_FORMNAME, "com/xmdx/demo/patient/patient_password.html");		
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
	/**患者修改密码
	 * 
	 */
	public int save(ActionContext ac) throws Exception {
		// TODO Auto-generated method stub
		int result = 0;
		System.out.println("enter save");
		String oldpwd = ac.getHttpRequest().getParameter("OLD_PASSWORD");
		String newpwd = ac.getHttpRequest().getParameter("NEW_PASSWORD");
		System.out.println("oldpwd="+oldpwd);
		System.out.println("newpwd"+newpwd);
		
		String userName=SessionUtil.getOpno(ac);
		
		StringBuilder sql = new StringBuilder("SELECT * FROM TS_OP U ");
		
		if(StringUtils.isNotBlank(userName)) {
			sql.append(" WHERE U.OPNO LIKE '%").append(userName).append("%' ");
		}
		DBDYPO[] po =DBDYDao.selectBySQL(DBConn.getConnection("SSOdbService"), sql.toString());
		String pwd =po[0].getString("PWD");
		String id =po[0].getString("PERSON_ID");
		
		String validpwd = Encrypt.getMixMD5(userName, "" + oldpwd);
		System.out.println("pwd = "+pwd);
		System.out.println("validpwd = "+validpwd);
		
		if(pwd.equals(validpwd))
		{
			
			DBDYPO pop = new DBDYPO(tableName,"PERSON_ID");
			pop.set("PERSON_ID", id);
			DBDYDao.selectByID(DBConn.getConnection("SSOdbService"), pop);
			pop.set("PWD", Encrypt.getMixMD5(userName, "" + newpwd));
			result = DBDYDao.update(DBConn.getConnection("SSOdbService"), pop);
			if(0 == result) {
				
				setMessage(ac, "修改失败!");
			} else {
				
				setMessage(ac, "修改成功!");
			}
			return CONST_RESULT_AJAX;
			
			
		}
		else{
			setMessage(ac, "原密码验证错误!");
			return CONST_RESULT_AJAX;
		}
		
		
		
		
		
	}

	

}
