#!/bin/sh

# Please run this only after JBoss has successfully deployed the
# webvoter.jar file (and thus created the database tables).

# This file fixes some tables for which xDoclet creates wrong column
# types and adds database constraints.

psql webvoter <<EOF
alter table party add foreign key (electionname) references election(name);
alter table party add foreign key (coalitionname) references coalition(name);
alter table person add foreign key (election) references election(name);
alter table candidate add foreign key (partyname) references party(name);
alter table language add foreign key (election) references election(name);
insert into election (name,password,isopen,ispublic,production,firstpassword,
secondpassword,stv,government,toelect) values ('test','test',false,false,false,true,
false,false,false,10);
insert into language (election,languagecode,header,footer,
candidateheader,candidatefooter,wrongfirstpassword,wrongsecondpassword,
novoterfound,successfulvoting,failedvoting,invalidnumber,gotohomepage,
invalidlogin,confirmchoice,electionclosed) values ('test','en',
'<html><title>WebVoter</title><body>',
'</body></html>',
'<p>Candidate: <input name="vote" type="text" /> <input name="submit" type="submit" value="Vote" /></p><table><tr><th>Number</th><th>Name</th><th>Party</th><th>Coalition</th></tr>',
'</table>',
'<p><b>Invalid first password. Please try again.</b></p>',
'<p><b>Invalid personal identification code. Please try again.</b></p>',
'<p><b>You are not registered as a voter.</b></p>',
'<p><b>Your vote for candidate # was registered.</b></p>',
'<p><b>Voting failed.</b></p>',
'<p><b>You entered an invalid number. Please try again.</b></p>',
'<p><a href="http://killeri.net/webvoter">Back to WebVoter home page.</a></p>',
'<p><b>Invalid user name or password. Please try again.</b></p>',
'<p><b>Confirm the vote for NUMBER, NAME, PARTY.</b> <input name="submit" type="submit" value="Confirm" /></p>',
'<p><b>Election is closed. Please try again later.</b></p>');
insert into language (election,languagecode,header,footer,
candidateheader,candidatefooter,wrongfirstpassword,wrongsecondpassword,
novoterfound,successfulvoting,failedvoting,invalidnumber,gotohomepage,
invalidlogin,confirmchoice,electionclosed) values ('test','fi',
'<html><title>WebVoter</title><body>',
'</body></html>',
'<p>Ehdokas: <input name="vote" type="text" /> <input name="submit" type="submit" value="&Auml;&auml;nest&auml;" /></p><table><tr><th>Numero</th><th>Nimi</th><th>Liitto</th><th>Rengas</th></tr>',
'</table>',
'<p><b>Virheellinen salasana. Ole hyv&auml; ja yrit&auml; uudelleen.</b></p>',
'<p><b>Virheellinen henkil&ouml;kohtainen kertak&auml;ytt&ouml;inen &auml;&auml;nestystunnus. Ole hyv&auml; ja yrit&auml; uudelleen.</b></p>',
'<p><b>Sinua ei ole rekister&ouml;ity &auml;&auml;nest&auml;j&auml;ksi.</b></p>',
'<p><b>&Auml;&auml;nesi ehdokkaalle # on rekister&ouml;ity.</b></p>',
'<p><b>&Auml;&auml;nestys ep&auml;onnistui.</b></p>',
'<p><b>Annoit virheellisen numeron. Ole hyv&auml; ja yrit&auml; uudelleen.</b></p>',
'<p><a href="http://killeri.net/webvoter">Takaisin WebVoterin kotisivulle.</a></p>',
'<p><b>Virheellinen k&auml;ytt&auml;j&auml;tunnus tai salasana. Ole hyv&auml; ja yrit&auml; uudelleen.</b></p>',
'<p><b>Vahvista &auml;&auml;nesi ehdokkaalle NUMBER, NAME, PARTY.</b> <input name="submit" type="submit" value="Vahvista" /></p>',
'<p><b>&Auml;&auml;nestys on suljettu. Ole hyv&auml; ja yrit&auml; my&ouml;hemmin uudelleen.</b></p>');
insert into language (election,languagecode,header,footer,
candidateheader,candidatefooter,wrongfirstpassword,wrongsecondpassword,
novoterfound,successfulvoting,failedvoting,invalidnumber,gotohomepage,
invalidlogin,confirmchoice,electionclosed) values ('test','sv',
'<html><title>WebVoter</title><body>',
'</body></html>',
'<p>Kandidat: <input name="vote" type="text" /> <input name="submit" type="submit" value="R&ouml;sta" /></p><table><tr><th>Nummer</th><th>Namn</th><th>Valf&ouml;rbund</th><th>Valring</th></tr>',
'</table>',
'<p><b>Felaktigt l&ouml;senord. F&ouml;rs&ouml;k p&aring; nytt.</b></p>',
'<p><b>Felaktig identifieringskod. F&ouml;rs&ouml;k p&aring; nytt.</b></p>',
'<p><b>You are not registered as a voter.</b></p>',
'<p><b>Din r&ouml;st f&ouml;r kandidat # har registrerats.</b></p>',
'<p><b>R&ouml;stning misslyckades.</b></p>',
'<p><b>Felaktigt kandidatnummer. F&ouml;rs&ouml;k p&aring; nytt.</b></p>',
'<p><a href="http://killeri.net/webvoter">Tillbaka till Webvoter sida.</a></p>',
'<p><b>Felaktigt anv&auml;ndarnamn eller l&ouml;senord. F&ouml;rs&ouml;k p&aring; nytt.</b></p>',
'<p><b>Godk&auml;nn din r&ouml;st f&ouml;r NUMBER, NAME, PARTY.</b> <input name="submit" type="submit" value="Godk&auml;nn" /></p>',
'<p><b>R&ouml;stning &auml;r st&auml;ngd. F&ouml;rs&ouml;k p&aring; nytt.</b></p>');
EOF
