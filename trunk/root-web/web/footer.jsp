<%@page language="java" contentType="text/html"%>
<%
/*
 * This file is part of the Aus-e-Stage Beta Root Website
 *
 * The Aus-e-Stage Beta Root Website is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * The Aus-e-Stage Beta Root Website is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Aus-e-Stage Beta Root Website.  
 * If not, see <http://www.gnu.org/licenses/>.
*/
%>
<div id="footer">
	<%
		ServletContext context = getServletContext();
		String systemName      = (String)context.getInitParameter("systemName");
		String systemVersion   = (String)context.getInitParameter("systemVersion");
		String buildVersion    = (String)context.getInitParameter("buildVersion");
		String moreInfo		   = (String)context.getInitParameter("moreInfo");
	%>
	<p><%=systemName%> Version: <%=systemVersion%> Build: <%=buildVersion%> | <a href="<%=moreInfo%>" title="More Information About the System">More Info</a> | <a href="http://beta.ausstage.edu.au/" title="More info about the projet">Contact Us</a></p>
</div>