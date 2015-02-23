<%@ include file="AdminHeader.jsp" %>
<%@ include file="Menu.jsp" %>
<%
    String votes = request.getParameter("votes");
   	admin.addBulkVotes(votes);
%>
<h1>Votes added!</h1>
<%@ include file="admin_footer.html" %>
