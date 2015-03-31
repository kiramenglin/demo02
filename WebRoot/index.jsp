<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
	request.getSession().invalidate();
	request.getRequestDispatcher("HttpChannel?action=IG_FRONT_ACTION").forward(request, response);
 %>
