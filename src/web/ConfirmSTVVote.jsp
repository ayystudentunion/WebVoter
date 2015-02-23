<%
	HashMap votes = new HashMap();
	String voteString = request.getParameter("vote");
	
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

%>
  <form name="ballotform" action="EndVote.jsp" accept-charset="ISO-8859-1" method="POST"/>
  <input type="hidden" name="vote" value="<%=voteString%>" />
<%=language.getConfirmChoice()%>
<table>
<%

    StringTokenizer st = new StringTokenizer(voteString,"_");
    while(st.hasMoreTokens()) {
        String[] vote = st.nextToken().split( "-" );
        Object o = candidateMap.get( new Integer( vote[1] ) );
        if( o instanceof Candidate )
        {
            String name = ((Candidate) o).getName();
            int number = ((Candidate) o).getNumber();
            String rank = vote[0];
%>
<tr><td><%=number%> <%=name%></td><td><%=rank%></td></tr>
<%
        }
        else if( o != null )
        {
            String name = (String) o;
            String rank = vote[0];
%>
<tr><td><%=name%></td><td><%=rank%></td></tr>
<%
        }
        else
        {
            String rank = vote[0];
%>
<tr><td>All others</td><td><%=rank%></td></tr>
<%
        }
    }
%>
</table>
</form>
