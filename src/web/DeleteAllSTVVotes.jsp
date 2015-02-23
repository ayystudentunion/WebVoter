<%@ include page="AdminHeader.jsp" %>
<%@ include file="Menu.jsp" %>

<%
            Collection votes = stvHome.findAllByRank();
            Iterator i = votes.iterator();
            while( i.hasNext() )
			{
			    STVVote vote = (STVVote) i.next();
			    vote.remove();
			}			
			
%>
<h1>Votes removed!</h1>
<%@ include file="admin_footer.html" %>
