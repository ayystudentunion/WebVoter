<%@ include file="VoteHeader.jsp" %>

<%
    String votepw = request.getParameter("votepw");

	if( ! election.getIsOpen() ) {
%>
<%=language.getElectionClosed()%>
<%
		return;
	}
	
	if( election.isSTV() ) {
%>
<%@ include file="EndSTVVote.jsp" %>
<%	
	} else {
  		if( election.isProduction() && election.isFirstPassword() && ! LoginCheck.check( person, election.getAuthenticationURL(), uid, passwd) ) {
%>
<%=language.getWrongFirstPassword()%>
<%
        	return;
	  	}
  		try {
        	if( ! vote.authenticate(uid, votepw) ) {
%>
<%=language.getWrongSecondPassword()%>
<%@ include file="ConfirmVoteBody.jsp" %>
<%
        		return;
        	}
		} catch( Exception e ) {
%>
<%=language.getNoVoterFound()%>
<%
			return;
		}
				
 	   String voteVal = request.getParameter("vote");
		if( vote.vote(Integer.parseInt(voteVal), PersonBean.VOTE_WWW) ) {
			String successfulVoting = language.getSuccessfulVoting();
			successfulVoting = successfulVoting.replaceAll( "#", voteVal );
%>
<%=successfulVoting%>
<%=language.getGoToHomePage()%>
<%
   		} else {
%>
<%=language.getFailedVoting()%>
<%=language.getGoToHomePage()%>
<%
	   	}
	}
%>

<%@ include file="VoteFooter.jsp" %>
