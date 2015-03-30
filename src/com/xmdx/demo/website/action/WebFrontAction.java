package com.xmdx.demo.website.action;

import com.e9rj.platform.system.action.UserLoginUtil;
import com.xmzy.framework.actions.ActionImp;
import com.xmzy.framework.context.ActionContext;

public class WebFrontAction extends ActionImp {

	@Override
	protected void doException(ActionContext ac, Exception e) {
		// TODO Auto-generated method stub

	}

	@Override
	protected int performExecute(ActionContext ac) throws Exception {
		//登录URL索引赋值
		ac.setObjValue("LoginUrlIndex", UserLoginUtil.getLoginUrlIndex(ac) + "");
	
		//登录判断
		return new UserLoginUtil().checkUserLogin(ac);
	}

}
