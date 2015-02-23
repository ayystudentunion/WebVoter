<%@ include file="AdminHeader.jsp" %>
<%@ include file="Menu.jsp" %>

<%
    String lastName = request.getParameter("name");
	if(lastName == null) lastName = "";
	lastName = lastName + "%";
%>
          <form action="ShowPeople.jsp" accept-charset="ISO-8859-1" method="POST" />
            Start of last name: <input name="name" />
            <input type="submit" value="Find" /><br />
          </form>
<%
            if( ! lastName.equals( "%" ) )
            {
%>
    <table>
      <tr><th>ID</th><th>Name</th><th>Campus</th><th>Vote date</th><th>Style</th><th></th></tr>
<%
				Collection candidates = personHome.findByPartialMatch(lastName.toLowerCase(),uid);
				Iterator i = candidates.iterator();
				while(i.hasNext()) {
					Person c = (Person) i.next();
					String number = c.getAddress();
					String name = c.getName();
                                        String campus = c.getCity();
					String pass = c.getPassword();
					Date date = c.getVoteDate();
					String dateS = date == null ? "&nbsp;" : (new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(date);
					int style = c.getVoteStyle();
					String styleS = style == PersonBean.VOTE_WWW ? "WWW" : "Paper";
					if( date == null ) styleS = "&nbsp;";
%>
      <tr>
        <td style="border-bottom: 1px solid black"><%=number%></td>
        <td style="border-bottom: 1px solid black"><%=name%></td>
        <td style="border-bottom: 1px solid black"><%=campus%></td>
        <td style="border-bottom: 1px solid black"><%=dateS%></td>
        <td style="border-bottom: 1px solid black"><%=styleS%></td>
<%
					if( date == null ) {
%>
        <td style="border-bottom: 1px solid black">
          <a href="MarkPerson.jsp?number=<%=c.getPersonNumber()%>">Mark as voted</a>
        </td>
<%
					}
    			}
%>
    </table>
<%
            }
%>
<br />
<%@ include file="admin_footer.html" %>
