<%@ include file="AdminHeader.jsp" %>
<%@ include file="Menu.jsp" %>

<h1>Election is now
<%
	if(election.getResultsPublic() )
	{
		election.setResultsPublic(false);
%>
private.
<%
	} else {
		election.setResultsPublic(true);
%>
public.
<%
	}
%>
</h1>

<%@ include file="admin_footer.html" %>
