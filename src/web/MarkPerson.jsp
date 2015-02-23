<%@ include file="AdminHeader.jsp" %>
<%@ include file="Menu.jsp" %>
<%
    String personnumber = request.getParameter("number");
    Person p = personHome.findByPrimaryKey(new PersonPK(uid, personnumber));
    String name = p.getName();
    if( p.getVoteDate() != null ) {
%>
<h1>Person already voted!</h1>
<%
    } else {
        admin.markAsVoted(personnumber, PersonBean.VOTE_TRADITIONAL);
        System.out.println("Manual vote. Person: " + personnumber + " User: " + personUid);
%>
<h1>Voter <%=name%> marked as having voted.</h1>
<%
	}
%>
          <form action="ShowPeople.jsp" accept-charset="ISO-8859-1" method="POST" />
            Last name: <input name="name" />
            <input type="submit" value="Search" /><br />
          </form>
<%@ include file="admin_footer.html" %>
