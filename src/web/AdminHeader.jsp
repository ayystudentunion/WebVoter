<%@ page import="net.killeri.webvote.*" %>
<%@ page import="java.util.*" %>

<%!
    public void jspInit()
    {
    	try {
    		javax.naming.InitialContext initialContext = new javax.naming.InitialContext();
			java.lang.Object objRef;
			objRef = initialContext.lookup("Admin");
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
			objRef = initialContext.lookup("ejb/webvote/STVVote");
			stvHome = (STVVoteHome) javax.rmi.PortableRemoteObject.narrow(objRef, STVVoteHome.class);

    	} catch (Exception e) {
    		e.printStackTrace();
    		throw new javax.ejb.EJBException(e);
    	}
    }
	ElectionHome electionHome;
	Election election;
	Person adminPerson;
	CandidateHome candidateHome;
	PartyHome partyHome;
	CoalitionHome coalitionHome;
	PersonHome personHome;
	STVVoteHome stvHome;
	AdminHome adminHome;
	Admin admin;

	String uid;
        String personUid;
	String passwd;
%>
<%
    uid    = request.getParameter("uid");
    personUid = request.getParameter("personuid");
    passwd = request.getParameter("passwd");
    if( uid == null || uid.length() == 0 ) {
    	uid = (String) session.getAttribute( "webvoteruid" );
    	personUid = (String) session.getAttribute( "webvoterpersonuid" );
    	passwd = (String) session.getAttribute( "webvoterpwd" );
    } else {
    	session.setAttribute( "webvoteruid", uid );
    	session.setAttribute( "webvoterpersonuid", personUid );
    	session.setAttribute( "webvoterpwd", passwd );
    }
    try {
    	election = electionHome.findByPrimaryKey(new ElectionPK(uid));
	    if( personUid != null && personUid.length > 0 ) adminPerson = personHome.findByPrimaryKey(new PersonPK(uid,personUid));
    } catch( Exception e ) {
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<HTML>
  <HEAD>
    <TITLE>WebVote Administration</TITLE>
  </HEAD>
  <BODY>
  <DIV ID="webvote">
    <h1>Unable to locate the election. Your session may have expired. Please try logging in again.</h1>
    <form action="ShowActivity.jsp" method="POST">
      <table>
        <tr><td>Election name (use test):</td><td><input type="text" name="uid" /></td></tr>
        <tr><td>User name (use test):</td><td><input type="text" name="personuid" /></td></tr>
        <tr><td>Password (use test):</td><td><input type="password" name="passwd" /></td></tr>
        <tr><td><input type="submit" value="Login" /></td>
      </table>
    </form>
  </DIV>
  </BODY>
</HTML>

<%
        return;
    }
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
	if( ( adminPerson == null && ( election.getPassword() == null || ! election.getPassword().equals(passwd) ) ) || ( adminPerson != null && ( adminPerson.getAdminPassword() == null || ! adminPerson.getAdminPassword().equals(passwd) ) ) )
    {
	    response.setContentType("text/html");
%>
<h1>Wrong username or password</h1>
<%
		return;
	}
	admin = adminHome.create();
	admin.init( uid );
%>

