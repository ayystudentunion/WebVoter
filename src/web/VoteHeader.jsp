<%@ page import="net.killeri.webvote.*" %>
<%@ page import="java.util.*" %>

<%!
    public void jspInit()
    {
    	try {
    		javax.naming.InitialContext initialContext = new javax.naming.InitialContext();
			java.lang.Object objRef = initialContext.lookup("ejb/webvote/Vote");
			voteHome = (VoteHome) javax.rmi.PortableRemoteObject.narrow(objRef, VoteHome.class);
			objRef = initialContext.lookup("ejb/webvote/Election");
			electionHome = (ElectionHome) javax.rmi.PortableRemoteObject.narrow(objRef, ElectionHome.class);
			objRef = initialContext.lookup("ejb/webvote/Language");
			languageHome = (LanguageHome) javax.rmi.PortableRemoteObject.narrow(objRef, LanguageHome.class);
			objRef = initialContext.lookup("ejb/webvote/Candidate");
			candidateHome = (CandidateHome) javax.rmi.PortableRemoteObject.narrow(objRef, CandidateHome.class);
			objRef = initialContext.lookup("ejb/webvote/Party");
			partyHome = (PartyHome) javax.rmi.PortableRemoteObject.narrow(objRef, PartyHome.class);
			objRef = initialContext.lookup("ejb/webvote/Coalition");
			coalitionHome = (CoalitionHome) javax.rmi.PortableRemoteObject.narrow(objRef, CoalitionHome.class);
			objRef = initialContext.lookup("ejb/webvote/Person");
			personHome = (PersonHome) javax.rmi.PortableRemoteObject.narrow(objRef, PersonHome.class);

       		electionHome = ElectionUtil.getHome();
			candidateHome = CandidateUtil.getHome();
			partyHome = PartyUtil.getHome();
			coalitionHome = CoalitionUtil.getHome();
			personHome = PersonUtil.getHome();

    	} catch (Exception e) {
    		e.printStackTrace();
    		throw new javax.ejb.EJBException(e);
    	}
    }
	ElectionHome electionHome;
	Election election;
	LanguageHome languageHome;
	Language language;
	CandidateHome candidateHome;
	PartyHome partyHome;
	CoalitionHome coalitionHome;
	PersonHome personHome;
	Person person;
	VoteHome voteHome;
	Vote vote;

	String languageS;
	String electionS;
	String uid;
	String passwd;
	
%>

<%
    uid    = request.getParameter("uid");
    passwd = request.getParameter("passwd");
    electionS = request.getParameter("election");
    languageS = request.getParameter("language");
    if( uid == null || uid.length() == 0 ) {
    	uid = (String) session.getAttribute( "webvoteruid" );
    	passwd = (String) session.getAttribute( "webvoterpwd" );
    	electionS = (String) session.getAttribute( "webvoterele" );
    	languageS = (String) session.getAttribute( "webvoterlang" );
    } else {
	    if( languageS == null || languageS.length() == 0 ) languageS = "en";
    	session.setAttribute( "webvoteruid", uid );
    	session.setAttribute( "webvoterpwd", passwd );
    	session.setAttribute( "webvoterele", electionS );
    	session.setAttribute( "webvoterlang", languageS );
    }
    try {
        person = personHome.findByPrimaryKey(new PersonPK(electionS, uid));
    } catch( Exception e ) {
%>
<html><title>WebVoter Problem</title><body><h1>Missing user <%= uid %> from the database!</h1></body></html>
<%
    }
	election = electionHome.findByPrimaryKey(new ElectionPK(electionS));
	try {
		language = languageHome.findByPrimaryKey(new LanguagePK(electionS,languageS));
	} catch (Exception e) {
		//No language in the database, let's try to recover
%>
<html><title>WebVoter Problem</title><body><h1>Language code <%= languageS %> is missing from the database!</h1></body></html>
<%
		return;
 }
	vote = voteHome.create();
	vote.init( electionS );
%>
<%=language.getHeader()%>
