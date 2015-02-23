<%@ include file="AdminHeader.jsp" %>
<%@ include file="Menu.jsp" %>
<%
			java.text.NumberFormat format = java.text.NumberFormat.getInstance();
			format.setMaximumFractionDigits(1);
			format.setMinimumFractionDigits(1);

			long voters = admin.getPersonCountByElection();
        	long totalVotes = admin.getVotedCount();
        	float percentage = 100* ((float)totalVotes) / voters;
            int toElect = election.getToElect();
        	List result = admin.calculateResult( toElect );
        	List cleanResult = new ArrayList();
        	for( int i = 0; i < result.size(); i++ ) {
        		try {
        			Integer candidate = (Integer) result.get(i);
					Candidate cand = candidateHome.findByPrimaryKey(new CandidatePK(election.getName(), candidate.intValue()));
					cleanResult.add( cand );
				} catch( Exception e ) {
					// Ignored on purpose
				}
			}
		    response.setContentType("text/html");
%>
Electorate: <%=voters%><br/>
Voters: <%=totalVotes%><br/>
Percentage: <%=format.format(percentage)%><br/>
<table>
<tr><th>Order</th><th>Number</th><th>Name</th><th>Party</th></tr>
<%
			for( int i = 1; i<=cleanResult.size(); i++ )
			{
                if( i == toElect + 1 )
                {
%>
<tr><td colspan="4"><hr></td></tr>
<%
                }
				Candidate cand = (Candidate) cleanResult.get(i-1);
				int candidate = cand.getNumber();
				String name = cand.getName();
				String party = cand.getParty().getName();
				if(party == null) party="";
%>
<tr><td><%=i%></td><td><%=candidate%></td><td><%=name%></td><td><%=party%></td></tr>
<%
			}			
%>
</table>
<%@ include file="admin_footer.html" %>
