<%@ include file="AdminHeader.jsp" %>
<%@ include file="Menu.jsp" %>
    <table>
      <tr><th>Name</th><th>Votes</th></tr>
<%
			Collection coalitions = admin.findCoalitionsByElection();
			Iterator i = coalitions.iterator();
			while(i.hasNext()) {
				String id = (String) i.next();
				Coalition c = coalitionHome.findByPrimaryKey(new CoalitionPK(uid,id));
				String name = c.getName();
				long votes = c.getVotes();
				if(election.isOpen() && election.isProduction()) votes = 0;
%>
      <tr>
        <td><%=name%></td>
        <td><%=votes%></td>
      </tr>
<%
			}
%>
    </table>

<%@ include file="admin_footer.html" %>
