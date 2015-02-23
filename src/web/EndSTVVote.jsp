<%
	HashMap votes = new HashMap();
    String passwd2 = request.getParameter("passwd2");

	Collection candidates = candidateHome.findByElection(electionS);
   	int candidateCount = candidates.size();
   	Map candidateMap = new HashMap();
    String previousParty = "";
    Iterator i = candidates.iterator();
    int j = 1;
    while(i.hasNext()) {
	    Candidate c = (Candidate) i.next();
	    String name = c.getName();
		String party = c.getParty().getName();
	    int number = c.getNumber();
        if( party != null && ! previousParty.equals( party ) )
        {  
            previousParty = party;
            candidateMap.put( new Integer(j), party );
            j++;
        }
        candidateMap.put( new Integer(j), c );
        j++;
    }

    StringTokenizer st = new StringTokenizer(request.getParameter("vote"),"_");
    while(st.hasMoreTokens()) {
        String[] vote = st.nextToken().split( "-" );
        Object o = candidateMap.get( new Integer( vote[1] ) );
        if( o instanceof Candidate )
        {
            votes.put( new Integer( ((Candidate) o).getNumber() ), new Integer(Integer.parseInt(vote[0])) );
        }
        else if( o != null )
        {
            Collection cands = candidateHome.findByVotes( (String) o );
            i = cands.iterator();
            while( i.hasNext() )
            {
                Candidate c = (Candidate) i.next();
                Integer number = new Integer( c.getNumber() );
                if( ! votes.containsKey( number ) )
                {
                    votes.put( number, new Integer(Integer.parseInt(vote[0])) );
                    //System.out.println( "Added vote " + number + ", " + vote[0] );
                }
            }
        }
        else
        {
            Collection cands = candidateHome.findAll();
            i = cands.iterator();
            while( i.hasNext() )
            {
                Candidate c = (Candidate) i.next();
                Integer number = new Integer( c.getNumber() );
                if( ! votes.containsKey( number ) )
                {
                    votes.put( number, new Integer(Integer.parseInt(vote[0])) );
                }
            }
        }
    }
	if( vote == null )
	{
    	vote = voteHome.create();
    	vote.init( electionS );
	}
    if( passwd2 == null || passwd2.length() == 0 )
    {
%>
<%=language.getWrongSecondPassword()%>
<%
        return;
    }
    if( election.isProduction() && election.isFirstPassword() && ! LoginCheck.check(person, null, null, null) )
    {
%>
<%=language.getWrongFirstPassword()%>
<%
        return;
    }
    try 
    {
   	    if( ! vote.authenticate(uid, passwd2) )
   	    {
%>
<%=language.getNoVoterFound()%>
<%
            return;
   	    }
	} catch( Exception e ) {
%>
<%=language.getNoVoterFound()%>
<%
	    return;
	}
	String voteId = null;
	if(vote != null) voteId = vote.vote(votes, PersonBean.VOTE_WWW);
	if( voteId != null && ! voteId.equals( "Not open" ) && ! voteId.equals( "Not authenticated" ) && ! voteId.equals( "Already voted" ) ) {
	    response.setContentType("text/html");
%>
<%=language.getSuccessfulVoting()%>
<%
   	}
   	else
   	{
%>
<%=language.getFailedVoting()%>
<%
   	}
%>
<%=language.getGoToHomePage()%>
