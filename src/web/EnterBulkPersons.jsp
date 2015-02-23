<%@ include file="AdminHeader.jsp" %>
<%@ include file="Menu.jsp" %>
<%
    String persons = request.getParameter("persons");
   	admin.addBulkPersons(persons);
%>
<h1Voters added!</h1>
<%@ include file="admin_footer.html" %>
