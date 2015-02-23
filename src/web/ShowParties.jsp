<%@ include file="AdminHeader.jsp" %>
<%@ include file="Menu.jsp" %>
    <table>
      <tr><th>Name</th><th>Coalition</th><th>Votes</th></tr>
<%
			Collection candidates = partyHome.findByElection(uid);
			Iterator i = candidates.iterator();
			while(i.hasNext()) {
				Party c = (Party) i.next();
				String name = c.getName();
				String coalition = c.getCoalitionName();
				if( coalition == null ) coalition = "";
				long votes = c.getVotes();
				if(election.isOpen() && election.isProduction()) votes = 0;
%>
      <tr>
        <td><%=name%></td>
        <td><%=coalition%></td>
        <td><%=votes%></td>
      </tr>
<%
			}
%>
    </table>

<%@ include file="admin_footer.html" %>
