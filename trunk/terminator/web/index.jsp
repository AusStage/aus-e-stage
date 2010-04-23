<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%
/*
 * This file is part of the AusStage Terminator Service
 *
 * The AusStage Terminator Service is free software: you can 
 * redistribute it and/or modify it under the terms of the GNU General 
 * Public License as published by the Free Software Foundation, either 
 * version 3 of the License, or (at your option) any later version.
 *
 * The AusStage Terminator Service is distributed in the hope 
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the 
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage Terminator Service.  
 * If not, see <http://www.gnu.org/licenses/>.
*/
%>
<%
	// invalidate any existing sessions
	if(session.isNew() == false) {
		session.invalidate();
	}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en">
<head>
	<title>AusStage Terminator</title>
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8"/>
	<meta name="viewport" content="initial-scale=1.0, user-scalable=no, width=device-width"/>
	<link rel="stylesheet" type="text/css" media="screen" href="assets/main.css"/>
	<script type="text/javascript" src="assets/javascript/libraries/jquery-1.4.2.min.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/jquery.validate.min.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/jquery.form.js"></script>
	<script type="text/javascript" src="assets/javascript/terminator.js"></script>
</head>
<body>
	<h1>AusStage Terminator Service</h1>
	<div id="auth_form">
		<p>
			Enter the authentication token into the form below and click the Authenticate button.
		</p>
		<form action="terminate/" method="post" id="authenticate" name="authenticate">
			<input type="hidden" name="action" id="action" value="auth"/>
			<table class="formTable">
				<tr>
					<th scope="row">
						<label for="auth_token">Authentication Token: </label>
					</th>
					<td>
						<input type="text" size="15" id="auth_token" name="auth_token"/>
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<input type="submit" name="submit" value="Authenticate"/>
					</td>
				</tr>
			</table>
		</form>
	</div>
	<div id="script_list" class="hidden">
	</div>
	<div id="status" class="hidden">
	</div>
</body>
</html>
