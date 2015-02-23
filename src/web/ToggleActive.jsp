<%@ include file="AdminHeader.jsp" %>
<%@ include file="Menu.jsp" %>

<h1>Election is now
<%
	if( ! election.getIsOpen() )
	{
		election.setIsOpen(true);
%>
open.
<%
	} else {
		election.setIsOpen(false);
%>
closed.
<%
	}
%>
</h1>

<%@ include file="admin_footer.html" %>
