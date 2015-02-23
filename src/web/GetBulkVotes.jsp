<%@ include file="AdminHeader.jsp" %>
<%@ include file="Menu.jsp" %>
<%
	if( ! election.getIsOpen() )
    {
%>
  <p>Enter paper votes in this format, one per line: NUMBER VOTES.</p>
  <form action="EnterBulkVotes.jsp" accept-charset="ISO-8859-1" method="POST" />
    <input type="hidden" name="uid" value="<%=uid%>" />
    <input type="hidden" name="passwd" value="<%=passwd%>" />
	<textarea rows="30" cols="40" name="votes"></textarea><br>
    <input type="submit" name="action" value="Votes" />
  </form>
<%
    } else {
%>
<h1>Voting is open. Manual votes may not be added!</h1>
<%
	}
%>
<%@ include file="admin_footer.html" %>
