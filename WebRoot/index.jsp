<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
	request.getSession().invalidate();
	request.getRequestDispatcher("HttpChannel?action=WebFrontAction").forward(request, response);
 %>
