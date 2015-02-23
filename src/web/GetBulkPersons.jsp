<%@ include file="AdminHeader.jsp" %>
<%@ include file="Menu.jsp" %>
  <form action="EnterBulkPersons.jsp" accept-charset="ISO-8859-1" method="POST" />
    <input type="hidden" name="uid" value="<%=uid%>" />
    <input type="hidden" name="passwd" value="<%=passwd%>" />
	<textarea name="persons"></textarea><br>
    <input type="submit" name="action" value="Persons" />
  </form>
  <p>Please enter the persons in this format: NUMBER FIRSTNAME LASTNAME.
  Names may not contain spaces.</p>
<%@ include file="admin_footer.html" %>
