<%
String tempLanguage;
String language;

tempLanguage = (String)request.getAttribute("A_LANGUAGE");
if (tempLanguage == null ) {
	language = "en";
} else if( tempLanguage.equals("fi")) {
	language = "fi";
} else if( tempLanguage.equals("sv")) {
	language = "sv";
} else {
	language = "en";
}
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "HTML">
<html>

<%
if (language.equals("fi")) {
%>
	<head>
		<link rel="stylesheet" href="styles/style.css" type="text/css">
		<title>Vaalit</title>
	</head>
<body>

<H1>TERVETULOA ÄÄNESTYSJÄRJESTELMÄÄN</H1>

<p>Äänestämisen vaiheet ovat seuraavat:</p>
<ol>
<li>Valitse ehdokasnumero.</li>
<li>Vahvista annettu ääni.</li>
<li>Kirjaudu ulos vaalijärjestelmästä.</li>
</ol>
</p>
		<form action="GetVote.jsp" method="POST">
			<input type="hidden" name="election" value="test" />
			<input type="hidden" name="uid" value="temp" />
			<tr><td><input type="submit" value="Siirry äänestämään" /></td>
		</table>
		</form>
	</body>
<%
} else if (language.equals("sv")) {
%>
	<head>
		<link rel="stylesheet" href="styles/style.css" type="text/css">
		<title>Val</title>
	</head>
<body>
		

<H1>VVÄLKOMMEN TILL FULLTNINGSSYSTEM</H1>

<p>De olika skedena i röstningen är följande:</p>
<ol>
<li>Välj kandidatnummer.</li>
<li>Bekräfta den givna rösten.</li>
<li>Logga ut från valsystemet.</li>
</ol>
</p>
		<form action="GetVote.jsp" method="POST">
			<input type="hidden" name="election" value="test" />
			<input type="hidden" name="uid" value="temp" />
			<tr><td><input type="submit" value="Fortsätt" /></td>
		</table>
		</form>
	</body>

<%
} else {
%>
	<head>
		<link rel="stylesheet" href="styles/style.css" type="text/css">
		<title>Elections</title>
	</head>
<body>
		
<H1>WELCOME TO THE VOTING SYSTEM</H1>

<p>Voting stages are as follows:</p>
<ol>
<li>Choose a candidate number.</li>
<li>Confirm your vote.</li>
<li>Log out of the election system.</li>
</ol>
</p>
		<form action="GetVote.jsp" method="POST">
			<input type="hidden" name="election" value="test" />
			<input type="hidden" name="uid" value="temp" />
			<tr><td><input type="submit" value="Continue" /></td>
		</table>
		</form>
	</body>

<%
}
%>

</html>
