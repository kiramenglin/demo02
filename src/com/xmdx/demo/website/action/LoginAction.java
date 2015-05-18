package com.xmdx.demo.website.action;

import org.apache.commons.lang.StringUtils;

import com.e9rj.platform.common.bean.UserBean;
import com.e9rj.platform.system.action.UserLoginUtil;
import com.e9rj.platform.util.SessionUtil;
import com.xmzy.frameext.simpledb.DBConn;
import com.xmzy.frameext.simpledb.DBDYDao;
import com.xmzy.frameext.simpledb.DBDYPO;
import com.xmzy.framework.actions.ActionImp;
import com.xmzy.framework.context.ActionContext;

public class LoginAction extends ActionImp{

	@Override
	protected void doException(ActionContext ac, Exception e) {

	}

	@Override
	protected int performExecute(ActionContext ac) throws Exception {
		ac.setObjValue("LoginUrlIndex", (new StringBuilder(String.valueOf(UserLoginUtil.getLoginUrlIndex(ac)))).toString());
		int result = (new UserLoginUtil()).checkUserLogin(ac);
		if(0==result){
			return 0;
		}
		else {
			UserBean userpo = (UserBean)ac.getObjValue("SESSION_USER_BEAN");
			String opNo = userpo.getSysUserPO().getOpNo();
			System.out.println("username="+opNo);
			StringBuilder sql = new StringBuilder("SELECT * FROM TS_OPROLE U");
		
			if(StringUtils.isNotBlank(opNo)) {
				sql.append(" WHERE U.OPNO LIKE '%").append(opNo).append("%' ");
			 }
			DBDYPO[] po =DBDYDao.selectBySQL(DBConn.getConnection("SSOdbService"), sql.toString());
			String roleid =po[0].get("ROLEID").toString();
				if(roleid.equals("RL150411151257140001"))
				{
					StringBuilder ssql = new StringBuilder("SELECT * FROM TS_OP U ");
					
					if(StringUtils.isNotBlank(opNo)) {
						ssql.append(" WHERE U.OPNO LIKE '%").append(opNo).append("%' ");
					}
					DBDYPO[] pop =DBDYDao.selectBySQL(DBConn.getConnection("SSOdbService"), ssql.toString());
					String id =pop[0].getString("PERSON_ID");
					
					StringBuilder sqlp = new StringBuilder("SELECT * FROM PATIENT U ");
					if(StringUtils.isNotBlank(id)) {
						sqlp.append(" WHERE U.PATIENT_ID LIKE '%").append(id).append("%' ");
					}
					DBDYPO[] popp =DBDYDao.selectBySQL(ac.getConnection(), sqlp.toString());
					ac.setObjValue("USER", popp[0]);
					return 3;
				}
				else if(roleid.equals("RL150411151250652001")){
					
					StringBuilder ssql = new StringBuilder("SELECT * FROM TS_OP U ");
					
					if(StringUtils.isNotBlank(opNo)) {
						ssql.append(" WHERE U.OPNO LIKE '%").append(opNo).append("%' ");
					}
					DBDYPO[] pop =DBDYDao.selectBySQL(DBConn.getConnection("SSOdbService"), ssql.toString());
					String id =pop[0].getString("PERSON_ID");
					
					StringBuilder sqlp = new StringBuilder("SELECT * FROM DOCTOR U ");
					if(StringUtils.isNotBlank(id)) {
						sqlp.append(" WHERE U.DOCTOR_ID LIKE '%").append(id).append("%' ");
					}
					DBDYPO[] popp =DBDYDao.selectBySQL(ac.getConnection(), sqlp.toString());
					ac.setObjValue("USER", popp[0]);
					return 2;
				}
				else{
					return 1;
				}
		}
		
	}

}
