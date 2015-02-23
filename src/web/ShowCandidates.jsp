<%@ include file="AdminHeader.jsp" %>
<%@ include file="Menu.jsp" %>
    <table>
      <tr><th>Number</th><th>Name</th><th>Party</th><th>Votes</th><th>Party comparison</th><th>Coalition comparison</th></tr>
<%
			Collection candidates = candidateHome.findByElection(uid);
			Iterator i = candidates.iterator();
			while(i.hasNext()) {
				Candidate c = (Candidate) i.next();
				PartyLocal p = c.getParty();
				String name = c.getName();
				String party = "";
				if( p != null ) party = p.getName();
				int number = c.getNumber();
				long votes = c.getVotes();
				Float partyC = c.getPartyComparison();
				Float coalitionC = c.getCoalitionComparison();
				if(election.isOpen() && election.isProduction() ) {
					votes = 0;
					partyC = new Float(0f);
					coalitionC = new Float(0f);
				}
%>
      <tr>
        <td><%=number%></td>
        <td><%=name%></td>
        <td><%=party%></td>
        <td><%=votes%></td>
        <td><%=partyC%></td>
        <td><%=coalitionC%></td>
      </tr>
<%
			}
%>
    </table>

<%@ include file="admin_footer.html" %>
