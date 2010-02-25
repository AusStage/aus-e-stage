<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@page import="au.edu.ausstage.mapping.DataManager"%>
<%@page import="au.edu.ausstage.mapping.BrowseDataBuilder"%>
<%
/*
 * This file is part of the AusStage Mapping Service
 *
 * The AusStage Mapping Service is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * The AusStage Mapping Service is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage Mapping Service.  
 * If not, see <http://www.gnu.org/licenses/>.
*/
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en">
<head>
	<title>AusStage Mapping Service (Beta)</title>
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8"/> 
</head>
<body>
<%
	String venueID = request.getParameter("id");
	//out.write("<h2>" + venueID + "</h2>");
	//ServletConfig config = getServletConfig();
	DataManager dataManager = new DataManager(config);
	BrowseDataBuilder dataBuilder = new BrowseDataBuilder(dataManager);
	out.write(dataBuilder.doSearch(venueID));
%>
</body>
</html>
