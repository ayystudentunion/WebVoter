<div id="login" bgcolor="#FFFFFF">
<ul class="choices">
<%
	if( ! election.isProduction() && adminPerson == null ) {
%>
<li><a href="ShowCandidates.jsp">Show candidates</a></li>
<li><a href="ShowParties.jsp">Show parties</a></li>
<li><a href="ShowCoalitions.jsp">Show coalitions</a></li>
<%
	}
%>
<li><a href="ShowPeople.jsp">Show people</a></li>
<li><a href="ShowActivity.jsp">Show activity</a></li>
<%
	if( ! election.isProduction() && adminPerson == null ) {
%>
<li><a href="GetBulkPersons.jsp">Get Bulk Persons</a></li>
<li><a href="GetBulkVotes.jsp">Get Bulk Votes</a></li>
<%
    if( election.isSTV() ) {
%>
<li><a href="ShowSTVVotes.jsp">Show votes</a></li>
<li><a href="CalculateSTVResults.jsp">Calculate results</a></li>
<%
      if( ! election.isProduction() && adminPerson == null ) {
%>
<li><a href="DeleteAllSTVVotes.jsp">Delete all votes</a></li>
<%
      }
    } else {
%>
<li><a href="CalculateResults.jsp">Calculate results</a></li>
<%
    }
%>
<li><a href="ShowResults.jsp">Show results</a></li>
<li><a href="ShowTotals.jsp">Show summaries</a></li>
<%
	}
	if( adminPerson == null ) {
%>
<li><a href="ToggleActive.jsp">Toggle active status</a></li>
<li><a href="TogglePublic.jsp">Toggle public status</a></li>
<%  } %>
</ul>
<p>Voting is:</p>
<ul class="status">
<li><%= election.getIsOpen() ? "open" : "closed" %></li>
<li><%= election.getResultsPublic() ? "public" : "private" %></li>
<li><%= election.isProduction() ? "in production" : "in testing" %></li>
<%
		if( election.isSTV() )
		{
%>
<li>STV</li>
<%
		} else {
			if( election.isGovernment() )
			{
%>
<li>government</li>
<%
			} else {
%>
<li>proportional</li>
<%
			}
		}
%>
<li><%= election.isFirstPassword() ? "first password" : "no first password" %></li>
<li><%= election.isSecondPassword() ? "second password" : "no second password" %></li>
</ul>
</div>
