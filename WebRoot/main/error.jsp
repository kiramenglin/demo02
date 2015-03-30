<%@ page contentType="text/html; charset=UTF-8" language="java"  errorPage="" %>
<%@page import="com.xmzy.framework.utils.*"%>

<%
	JspHelper helper = new JspHelper(request);
	String errMsg = helper.getErrMsg();
	String path = request.getContextPath();
	String logout = helper.getStringValue("MUST_LOGOUT");
	//马上退出
	String outImmediate = helper.getStringValue("OUT_IMMEDIATE");
	//立即退出
	if(logout!=null && "Y".equals(outImmediate)){
		String url = helper.getStringValue("LOGOUT_URL");
		url += "?service="+request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/"; 
		//request.getRequestDispatcher(url).(request, response);
		response.sendRedirect(url);
	}
	String ajaxAction = request.getParameter("AJAX_ACTION");
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>出错了</title>
<link href="/e9common/css/base.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="/e9common/jquery/js/jquery-1.7.2.min.js"></script>
<script type="text/javascript" src="/e9common/js/common.js"></script>
<style type="text/css">
<!--
.STYLE1 {
	border-top-width: 1px;
	border-right-width: 1px;
	border-bottom-width: 1px;
	border-left-width: 1px;
	border-bottom-style: solid;
	border-top-color: #666666;
	border-right-color: #666666;
	border-bottom-color: #666666;
	border-left-color: #666666;
	border-top-style: none;
	border-right-style: none;
	border-left-style: none;
	font-size: 25px;
	color: #003399;
	font-weight: bolder;
}
.ccy_top {
	border-top-width: 1px;
	border-right-width: 1px;
	border-bottom-width: 1px;
	border-left-width: 1px;
	border-bottom-style: none;
	border-top-color: #666666;
	border-right-color: #666666;
	border-bottom-color: #666666;
	border-left-color: #666666;
	border-top-style: none;
	border-right-style: none;
	border-left-style: none;
	font-size: 25px;
	color: #4465a2;
	font-weight: bolder;
}
-->
</style>
<script language="javascript" defer="true">
	
	var logoutUrl = "<%=com.e9rj.platform.common.BaseConstants.getGlobalValue(String.valueOf(com.e9rj.platform.common.Constants.PARA_SYS_CAS_URL),"")%>/logout";//CAS退出地址
	var appName = "<%=com.xmzy.framework.core.FrameworkConstant.getAppPath()%>";//应用部署名称
	var appprotocol = window.location.protocol;
	var apphostname = window.location.hostname;
	var appport = window.location.port; 
	var callbackurl='?service='+appprotocol+'//'+apphostname +':'+appport + '/'+appName;

	var url = logoutUrl+ encodeURI(callbackurl);
	//var url="HttpChannel?action=USER_LOGOUT";
	function relogout(){
		if(parent){
			if(parent.parent){
				parent.parent.location.href=url;
			}
			else{
				parent.location.href=url;
			}
		}else{
			location.href=url;
		}
	}
	//HideLoadingNotice();
</script>
<%if(logout!=null && !"Y".equals(outImmediate)){%>
<script language="javascript">
setTimeout("relogout()",5000);
var idx = 4;
setInterval(function(){
	if(idx>=0)$("#sec").html(idx--);
},1000);
</script>
<%}%>
</head>

<body bottommargin="0" leftmargin="0" rightmargin="0" topmargin="0">
<table width="760" border="0" cellpadding="0" cellspacing="0">
  <!--DWLayoutTable-->
  <tr>
    <td width="760" height="100" valign="top"><table width="100%" border="0" cellpadding="0" cellspacing="0">
      <!--DWLayoutTable-->
      <tr>
        <td width="100" rowspan="2" align="center" valign="middle"><img src="/e9common/images/file_important.jpg" width="80" height="80"></td>
        <td width="660" height="70" valign="bottom" class="STYLE1"><table height="80%" border="0">
          <tr>
            <td valign="bottom" class="ccy_top">因为程序或操作的原因，出错了！</td>
          </tr>
        </table></td>
      </tr>
      <tr>
        <td height="19" valign="top"><!--DWLayoutEmptyCell-->&nbsp;</td>
        </tr>
    </table></td>
  </tr>
  <tr>
    <td width="90%" height="120" align="right" valign="top" class="bd_wzms"><table width="87%" border="0">
      <tr>
        <td class="ccy_bt">
        错误模块：<%=helper.getObjValue("FlowAction")%><br>
        错误信息：<font color="red"><%=errMsg %></font><br><br>
        <% if(logout!=null&&ajaxAction==null){
        	out.println("<font color='red'>本系统将在&nbsp;<span id='sec' style='color:blue;'>5</span>&nbsp;秒钟后返回到登录页，请重新登录...</font>");
        }%>
        </td>
      </tr>
    </table></td>
  </tr>
  <tr>
    <td height="22" align="right" valign="top"><table width="86%" border="0">
      <tr>
        <td width="160" align="left" class="ccy_bt">您可以尝试以下操作：</td>
      </tr>
    </table></td>
  </tr>
  <tr>
    <td height="147" align="right" valign="top"><table width="86%" border="0">
      <tr>
        <td width="26"><img src="/e9common/images/bullet.jpg" width="15" height="15"></td>
        <td width="572" class="bd_wzms">请检查您的网络，确认已经连接</td>
        </tr>
      <tr>
        <td><img src="/e9common/images/bullet.jpg" width="15" height="15"></td>
        <td class="bd_dzznwz"><a href="#" onclick="history.back(-1);return(false);">点击返回上一页</a></td>
        </tr>
     <tr>
        <td><img src="/e9common/images/bullet.jpg" width="15" height="15"></td>
        <td class="bd_dzznwz"><a href="#" onclick="relogout();return(false);">从系统首页重新登录尝试</a></td>
        </tr>
     <tr>
        <td><img src="/e9common/images/bullet.jpg" width="15" height="15"></td>
        <td class="bd_dzznwz">如果长时间错误，请联系技术支持热线</td>
        </tr>        
    </table></td>
  </tr>
</table>


</body>
</html>