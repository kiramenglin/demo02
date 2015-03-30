package com.xmdx.demo.website.action;

import com.xmzy.framework.actions.ActionImp;
import com.xmzy.framework.context.ActionContext;

public class WebSiteMainAction extends ActionImp {

	@Override
	protected void doException(ActionContext ac, Exception e) {

	}

	@Override
	protected int performExecute(ActionContext ac) throws Exception {
		return 1;
	}

}
