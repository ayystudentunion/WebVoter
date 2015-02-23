<%@ include file="AdminHeader.jsp" %>
<%@ include file="Menu.jsp" %>
<%
		admin.cancelVoter(personnumber);
%>
<b>Person <%=personnumber%> marked as not having voted.</b><br>
<%
	}
%>
                      <form action="GetVote.jsp" method="POST">
                        <input type="hidden" name="action" value="login" />
                        <table>
                          <tr><td>Username:</td><td><input type="text" name="uid" /></td></tr>
                          <tr><td>Password:</td><td><input type="password" name="passwd" /></td></tr>
                          <tr><td><input type="submit" value="Login" /></td>
                        </table>
                      </form>

<%@ include file="admin_footer.html" %>
