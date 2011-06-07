<%@page language="java" contentType="text/html"%>
<%
/*
 * This file is part of the AusStage Data Exchange Service
 *
 * AusStage Data Exchange Service is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * AusStage Data Exchange Service is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with AusStage Data Exchange Service.  
 * If not, see <http://www.gnu.org/licenses/>.
 */
%>
<div class="footer b-186">
	<%
		ServletContext context = getServletContext();
		String systemVersion   = (String)context.getInitParameter("systemVersion");
	%>
	<p><a href="http://beta.ausstage.edu.au/" title="Aus-e-Stage homepage">Aus-e-Stage</a> | <a href="http://beta.ausstage.edu.au/exchange2/" title="Data Exchange homepage">Data Exchange</a> Version: <%=systemVersion%> | <a href="http://beta.ausstage.edu.au/?tab=contacts" title="Contact information">Contact Us</a></p>
</div>
