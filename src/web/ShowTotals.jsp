<%@ page import="net.killeri.webvote.*" %>
<%@ page import="java.util.*" %>

<%!
    public void jspInit()
    {
    	try {
    		javax.naming.InitialContext initialContext = new javax.naming.InitialContext();
			java.lang.Object objRef = initialContext.lookup("Admin");
			adminHome = (AdminHome) javax.rmi.PortableRemoteObject.narrow(objRef, AdminHome.class);
			objRef = initialContext.lookup("ejb/webvote/Election");
			electionHome = (ElectionHome) javax.rmi.PortableRemoteObject.narrow(objRef, ElectionHome.class);
			objRef = initialContext.lookup("ejb/webvote/Candidate");
			candidateHome = (CandidateHome) javax.rmi.PortableRemoteObject.narrow(objRef, CandidateHome.class);
			objRef = initialContext.lookup("ejb/webvote/Party");
			partyHome = (PartyHome) javax.rmi.PortableRemoteObject.narrow(objRef, PartyHome.class);
			objRef = initialContext.lookup("ejb/webvote/Coalition");
			coalitionHome = (CoalitionHome) javax.rmi.PortableRemoteObject.narrow(objRef, CoalitionHome.class);
			objRef = initialContext.lookup("ejb/webvote/Person");
			personHome = (PersonHome) javax.rmi.PortableRemoteObject.narrow(objRef, PersonHome.class);

    	} catch (Exception e) {
    		e.printStackTrace();
    		throw new javax.ejb.EJBException(e);
    	}
    }
	ElectionHome electionHome;
	Election election;
	CandidateHome candidateHome;
	PartyHome partyHome;
	CoalitionHome coalitionHome;
	PersonHome personHome;
	AdminHome adminHome;
	Admin admin;
%>
<%
    String uid    = request.getParameter("uid");
    String passwd = request.getParameter("passwd");
    String csvS = request.getParameter("csv");
    boolean csv = ( csvS == null || csvS.length() == 0 ? false : true );
    if( uid == null || uid.length() == 0 ) {
    	uid = (String) session.getAttribute( "webvoteruid" );
    	passwd = (String) session.getAttribute( "webvoterpwd" );
    } else {
    	session.setAttribute( "webvoteruid", uid );
    	session.setAttribute( "webvoterpwd", passwd );
    }

	election = electionHome.findByPrimaryKey(new ElectionPK(uid));
	if( ! csv ) {
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<HTML>
  <HEAD>
    <TITLE>WebVote Administration - <%=uid%></TITLE>
    <link rel="stylesheet" href="webvoter.css" media="screen" />
  </HEAD>
  <BODY>
  <DIV ID="webvote">
<%
	}
	if( ! election.getResultsPublic() && ! election.getPassword().equals(passwd) )
    {
	    response.setContentType("text/html");
%>
<h1>Wrong username or password</h1>
<%
		return;
	}
	if( election.getPassword().equals(passwd) ) {
%>
<%@ include file="Menu.jsp" %>
<%
	}
	admin = adminHome.create();
	long allVotes = admin.getVotedCount();
   	Collection persons = personHome.findAllByElection(uid);
   	int voters = persons.size();
   	long totalVotes = 0;
   	Collection parties = partyHome.findByElection(uid);
   	Iterator i = parties.iterator();
   	while(i.hasNext())
   	{
   		totalVotes += ((Party)i.next()).getVotes();
   	}
   	float percentage = 100* ((float)allVotes) / voters;
   	float currentPercentage = 100* ((float)totalVotes) / allVotes;
%>
<p>    Electorate: <%=voters%><br/>
    Voters: <%=allVotes%><br/>
    Percentage: <%=percentage%></br>
    Current count percentage: <%=currentPercentage%></p>
    <table>
      <tr><th>Name</th><th>Votes</th><th>Places</th></tr>
<%
	Collection candidates = candidateHome.findByAllComparison(uid);
	Map partyVoteMap = new HashMap();
	Map coalitionVoteMap = new HashMap();
	int toElect = election.getToElect();
	int j = 0;
	i = candidates.iterator();
	while(i.hasNext()) {
		j++;
		Candidate c = (Candidate) i.next();
		String name = c.getName();
		if( c.getParty() != null ) {
		    Integer places = (Integer) partyVoteMap.get( c.getParty().getName() );
		    if( places == null ) places = new Integer(0);
		    places = new Integer( places.intValue() + 1 );
		    partyVoteMap.put( c.getParty().getName(), places );
		}
		if( c.getParty() != null && c.getParty().getCoalition() != null ) {
		    Integer places = (Integer) coalitionVoteMap.get( c.getParty().getCoalition().getName() );
		    if( places == null ) places = new Integer(0);
		    places = new Integer( places.intValue() + 1 );
		    coalitionVoteMap.put( c.getParty().getCoalition().getName(), places );
		}
		if( j == toElect ) break;
	}
	i = coalitionHome.findByElection(uid).iterator();
	while( i.hasNext() ) {
	    Coalition c = (Coalition) i.next();
	    Integer p = (Integer) coalitionVoteMap.get(c.getName());
	    int places = p == null ? 0 : p.intValue();
%>
<tr><td><%= c.getName() %></td><td><%= c.getVotes() %></td><td><%= places %></td></tr>
<%
	}
%>
</table>
<hr />
<table>
      <tr><th>Name</th><th>Votes</th><th>Places</th></tr>
<%
	i = partyHome.findByElection(uid).iterator();
	while( i.hasNext() ) {
	    Party pa = (Party) i.next();
	    Integer p = (Integer) partyVoteMap.get(pa.getName());
	    int places = p == null ? 0 : p.intValue();
%>
<tr><td><%= pa.getName() %></td><td><%= pa.getVotes() %></td><td><%= places %></td></tr>
<%
	}
%>
    </table>

<%@ include file="admin_footer.html" %>

