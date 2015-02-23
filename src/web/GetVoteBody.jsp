<%
	if( ! election.getIsOpen() ) {
%>
<%=language.getElectionClosed()%>
<%
		return;
	}
		
	uid = uid.toUpperCase();
    if( ! election.isProduction() || ! election.isFirstPassword() || LoginCheck.check( person, election.getAuthenticationURL(), uid, passwd) )
    {
    	if( election.isSTV() ) {
%>
<%@ include file="GetSTVVote.jsp" %>
<%
    	} else {
%>
  <form action="ConfirmVote.jsp" accept-charset="ISO-8859-1"
method="POST" />
  <%=language.getCandidateHeader()%>
<%
			Collection candidates = candidateHome.findByElection(electionS);
			Iterator i = candidates.iterator();
			while(i.hasNext()) {
				Candidate c = (Candidate) i.next();
				PartyValue pv = vote.findPartyByCandidate(c.getNumber());
				CoalitionValue cv = vote.findCoalitionByParty(pv.getName());
				String name = c.getName();
				String party = pv.getName();
				String coalition = cv == null ? "" : cv.getName();
				int number = c.getNumber();
%>
        <tr class="getvote">
          <td class="getvote"><%=number%></td>
          <td class="getvote"><%=name%></td>
          <td class="getvote"><%=party%></td>
          <td class="getvote"><%=coalition%></td>
        </tr>
<%
			}
%>
<%=language.getCandidateFooter()%>
<%
		}
    } else {
%>
<%=language.getInvalidLogin()%>
<%
	}
%>
