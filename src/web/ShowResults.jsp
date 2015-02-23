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
	if( ! csv ) {
%>
<p>    Electorate: <%=voters%><br/>
    Voters: <%=allVotes%><br/>
    Percentage: <%=percentage%></br>
    Current count percentage: <%=currentPercentage%></p>
    <table>
      <tr><th>Position</th><th>Number</th><th>Name</th><th>Party</th><th>Coalition</th><th>Votes</th><th>Party comparison</th><th>Coalition comparison</th></tr>
<%
	} else {
	    response.setContentType("text/csv");
	}
	Collection candidates = candidateHome.findByAllComparison(uid);
	int toElect = election.getToElect();
	int j = 0;
	i = candidates.iterator();
	while(i.hasNext()) {
		j++;
		Candidate c = (Candidate) i.next();
		String name = c.getName();
		String party = "";
		if( c.getParty() != null ) party = c.getParty().getName();
		String coalition = "";
		if( c.getParty() != null && c.getParty().getCoalition() != null ) coalition = c.getParty().getCoalition().getName();
		int number = c.getNumber();
		long votes = c.getVotes();
		Float partyC = c.getPartyComparison();
		Float coalitionC = c.getCoalitionComparison();
		if( csv ) {
%>
<%=j%>,<%=number%>,"<%=name%>","<%=party%>","<%=coalition%>",<%=votes%>,<%=partyC%>,<%=coalitionC%>
<%
		} else {
%>
      <tr>
        <td><%=j%></td>
        <td><%=number%></td>
        <td><%=name%></td>
        <td><%=party%></td>
        <td><%=coalition%></td>
        <td><%=votes%></td>
        <td><%=partyC%></td>
        <td><%=coalitionC%></td>
      </tr>
<%
			if( j == toElect ) {
%>
	  <tr>
	    <td colspan="7">&nbsp;</td>
	  </tr>
<%
			}
		}
	}
	if( ! csv ) {
%>
    </table>

<%@ include file="admin_footer.html" %>
<%
	}
%>

