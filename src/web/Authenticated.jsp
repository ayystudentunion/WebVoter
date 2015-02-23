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

%>

<%
    String electionS = request.getParameter("vaali");
    String uidS = request.getParameter("opnro");
    String voteS = request.getParameter("ehdokas");

    try {
        election = electionHome.findByPrimaryKey(new ElectionPK(electionS));
        vote = voteHome.create();
        vote.init( electionS );
    } catch( Exception e ) {
%>
3
<%
    }
  
    if( ! election.getIsOpen() ) {
%>
5
<%
        return;
    }
	
    try {
        if( ! vote.authenticate(uidS.toUpperCase(), "") ) {
%>
4
<%
            return;
        }
    } catch( Exception e ) {
%>
4
<%
        return;
    }
    if( voteS == null || voteS.length() == 0 ) {
%>
0
<%
        return;
    }
    try {
        if( vote.vote(Integer.parseInt(voteS), PersonBean.VOTE_WWW) ) {
%>
0
<%
            } else {
%>
1
<%
        }
    } catch( Exception e ) {
%>
2
<%
    }
%>
