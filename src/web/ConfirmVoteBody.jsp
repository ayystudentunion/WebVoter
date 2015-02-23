<%
    String voteVal = request.getParameter("vote");
%>

<%
	if( election.isSTV() ) {
%>
<%@ include file="ConfirmSTVVote.jsp" %>
<%	
	} else {
		Candidate candidate;
		try {
   			candidate = candidateHome.findByPrimaryKey(new CandidatePK(electionS, Integer.parseInt(voteVal)));
		} catch (Exception e) {
			e.printStackTrace();
%>
<%=language.getInvalidNumber()%>
<%@ include file="GetVoteBody.jsp" %>
<%
			return;
		}
		PartyLocal p = candidate.getParty();
   		String candName = candidate.getName();
	   	String candParty = p.getName();
	   	String confirmChoice = language.getConfirmChoice();
   		confirmChoice = confirmChoice.replaceAll( "NUMBER", voteVal );
	   	confirmChoice = confirmChoice.replaceAll( "NAME", candName );
   		confirmChoice = confirmChoice.replaceAll( "PARTY", candParty );
%>
<form action="EndVote.jsp" accept-charset="ISO-8859-1" method="POST">
<input type="hidden" name="vote" value="<%=voteVal%>" />
<%=confirmChoice%>
</form>
<%
	}
%>
