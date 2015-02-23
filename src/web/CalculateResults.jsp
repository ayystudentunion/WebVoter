<%@ include file="AdminHeader.jsp" %>
<%@ include file="Menu.jsp" %>
<%
	response.setContentType("text/html");
	try {
        	admin.calculateAll();
%>
<h1>Results calculated.</h1>
<%
	} catch(Exception e) {
%>
<h1>Error calculating the results. Probably no votes in the database.</h1>
<%
	}
%>

<%@ include file="admin_footer.html" %>
