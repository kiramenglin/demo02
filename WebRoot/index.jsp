<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
	request.getSession().invalidate();
	request.getRequestDispatcher("HttpChannel?action=PLATFORM_MAIN_ACTION").forward(request, response);
 %>
