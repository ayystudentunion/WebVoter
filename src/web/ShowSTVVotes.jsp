<%@ include file="AdminHeader.jsp" %>
<%@ include file="Menu.jsp" %>
<%
            Collection votes = stvHome.findAllByRank();
		    response.setContentType("text/html");
%>
<h1>Votes cast</h1>
<table border="1">
<tr>
<td>
<%
            String currentId = null;
            int currentPlace = 1;
            Iterator i = votes.iterator();
            while( i.hasNext() )
			{
			    STVVote vote = (STVVote) i.next();
			    if( currentId != null && currentId != vote.getId() )
			    {
%>
</tr>
<tr>
<td>
<%
                    currentPlace = 1;
                }
			    if( currentPlace != vote.getRank() )
			    {
%>
</td>
<td>
<%
                }
%>
<%=vote.getCandidate()%>
<%
                currentId = vote.getId();
                currentPlace = vote.getRank();
			}			
%>
</td>
</tr>
</table>
<%@ include file="admin_footer.html" %>
