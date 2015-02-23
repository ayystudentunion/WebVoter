<%@ include file="AdminHeader.jsp" %>
<%@ include file="Menu.jsp" %>

<%@ page import="java.text.*" %>
<%
	try {
			int persons = personHome.findAllByElection(uid).size();
%>
	<p>Voters in the database: <%=persons%></p>
    <table>
      <tr><th>Date</th><th>WWW</th><th>Paper</th><th>Total</th><th>Percentage</th></tr>
<%
			int grandTotal = 0;
			int webTotal = 0;
			int paperTotal = 0;
			Map activity = admin.showActivityByDate();
			Collection keys = activity.keySet();
			NumberFormat format = NumberFormat.getInstance();
			format.setMaximumFractionDigits(1);
			format.setMinimumFractionDigits(1);
			Iterator i = keys.iterator();
			while(i.hasNext()) {
				String dateString = (String) i.next();
				List list = (List) activity.get(dateString);
				if(list == null || list.size() == 0) {
%>
<b>Empty list for day <%=dateString%> - this really should not happen!</b>
<%
					return;
				}
				Integer paper = (Integer) list.get(PersonBean.VOTE_TRADITIONAL);
				Integer web = new Integer(0);
				if(list.size() > PersonBean.VOTE_WWW) web = (Integer) list.get(PersonBean.VOTE_WWW);
				int total = web.intValue() + paper.intValue();
				webTotal += web.intValue();
				paperTotal += paper.intValue();
				grandTotal += total;
				double percentage = ((float) total * 100) / persons;
				String perc = format.format(percentage);
%>
      <tr>
        <td><%=dateString%></td>
        <td><%=web%></td>
        <td><%=paper%></td>
        <td><%=total%></td>
        <td><%=perc%></td>
      </tr>
<%
			}
			double percentageTotal = ((float) grandTotal * 100) / persons;
			String percTotal = format.format(percentageTotal);
%>
      <tr>
        <td>Grand total</td>
        <td><%=webTotal%></td>
        <td><%=paperTotal%></td>
        <td><%=grandTotal%></td>
        <td><%=percTotal%></td>
      </tr>
    </table>
<%
	} catch(Exception e) {
%>
<h1>No votes in the database!</h1>
<%
		e.printStackTrace();
	}
%>
<%@ include file="admin_footer.html" %>
