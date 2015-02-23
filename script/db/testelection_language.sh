#!/bin/sh
psql webvoter <<EOF
DELETE FROM language WHERE election = 'test';
insert into language (election,languagecode,header,footer,candidateheader,candidatefooter,wrongfirstpassword,wrongsecondpassword,novoterfound,successfulvoting,failedvoting,invalidnumber,gotohomepage,invalidlogin,confirmchoice,electionclosed) values (
'test','fi',
'<html><link rel="stylesheet" href="styles/style.css" type="text/css"><title>Vaalit</title><body>',
'</body></html>',
'<p>Anna valitsemasi ehdokkaan ehdokasnumero: <input name="vote" type="text" /> <input name="submit" type="submit" value="Jatka" /></p><p><a href="https://VOTE.CHANGEDOMAIN.fi/">palaa etusivulle<a> - <a href="https://VOTE.CHANGEDOMAIN.fi/Shibboleth.sso/Logout?return=http%3A%2F%2Fwww.ayy.fi/">keskeytä äänestys</a></p><table><tr><th>Numero</th><th>Nimi</th><th>Liitto</th><th>Rengas</th></tr>',
'</table>',
'<p><b>Virheellinen salasana. Ole hyvä ja yritä uudelleen.</b></p>',
'<p><b>Virheellinen henkilökohtainen kertakäyttöoinen äänestystunnus. Ole hyvä ja yritä uudelleen.</b></p>',
'<p><b>Teitä ei ole rekisteröity äänestäjäksi.</b></p>',
'<p><b>Äänesi ehdokkaalle # on rekisteröity.</b></p>',
'<p><b>Äänestäminen epäonnistui. Olet jo käyttänyt äänesi.</b></p>',
'<p><b>Annoit virheellisen ehdokasnumeron. Ole hyvä ja yritä uudelleen.</b></p>',
'<p><a href="https://VOTE.CHANGEDOMAIN.fi/Shibboleth.sso/Logout?return=http%3A%2F%2Fwww.ayy.fi">Tietoturvasyistä, ole hyvä ja kirjaudu ulos tästä linkistä.</a></p>',
'<p><b>Virheellinen käyttäjätunnus tai salasana. Ole hyvä ja yritä uudelleen.</b></p>',
'<p><b>Olet valinnut ehdokkaan NUMBER, NAME, vaaliliitosta PARTY.</b> <input name="submit" type="submit" value="Vahvista äänesi." /></p><p><a href="https://VOTE.CHANGEDOMAIN.fi/">palaa etusivulle<a> - <a href="https://VOTE.CHANGEDOMAIN.fi/Shibboleth.sso/Logout?return=http%3A%2F%2Fwww.ayy.fi/vaalit">keskeytä äänestys</a></p>',
'<p><b>Äänestys on suljettu. Ole hyvä ja yritä myöhemmin uudelleen.</b></p>');
insert into language (election,languagecode,header,footer,candidateheader,candidatefooter,wrongfirstpassword,wrongsecondpassword,novoterfound,successfulvoting,failedvoting,invalidnumber,gotohomepage,invalidlogin,confirmchoice,electionclosed) values (
'test','en',
'<html><link rel="stylesheet" href="styles/style.css" type="text/css"><title>Elections</title><body>',
'</body></html>',
'<p>Enter your candidate''s candidate number: <input name="vote" type="text" /> <input name="submit" type="submit" value="continue" /></p><p><a href="https://VOTE.CHANGEDOMAIN.fi/">return to the frontpage<a> - <a href="https://VOTE.CHANGEDOMAIN.fi/Shibboleth.sso/Logout?return=http%3A%2F%2Fwww.ayy.fi/vaalit">interrupt voting</a></p><table><tr><th>Number</th><th>Name</th><th>Alliance</th><th>Coalition</th></tr>',
'</table>',
'<p><b>Invalid first password. Please try again.</b></p>',
'<p><b>Invalid personal identification code. Please try again.</b></p>',
'<p><b>You are not registered as a voter.</b></p>',
'<p><b>Your vote to the candidate # is registered.</b></p>',
'<p><b>Voting has failed. You have already used your vote.</b></p>',
'<p><b>You have entered an incorrect candidate number. Please try again.</b></p>',
'<p><a href="https://VOTE.CHANGEDOMAIN.fi/Shibboleth.sso/Logout?return=http%3A%2F%2Fwww.ayy.fi">For security reasons, please sign out from this link.</a></p>',
'<p><b>Invalid personal identification code. Please try again.</b></p>',
'<p><b>You have chosen the candidate NUMBER, NAME, from the election alliance PARTY.</b> <input name="submit" type="submit" value="Confirm your vote" /></p><p><a href="https://VOTE.CHANGEDOMAIN.fi/">return to the frontpage<a> - <a href="https://VOTE.CHANGEDOMAIN.fi/Shibboleth.sso/Logout?return=http%3A%2F%2Fwww.ayy.fi/vaalit">interrupt voting</a></p>',
'<p><b>Voting is closed. Please try again later.</b></p>');
insert into language (election,languagecode,header,footer,candidateheader,candidatefooter,wrongfirstpassword,wrongsecondpassword,novoterfound,successfulvoting,failedvoting,invalidnumber,gotohomepage,invalidlogin,confirmchoice,electionclosed) values (
'test','sv',
'<html><link rel="stylesheet" href="styles/style.css" type="text/css"><title>Val</title><body>',
'</body></html>',
'<p>Ange numret på kandidaten du valt: <input name="vote" type="text" /> <input name="submit" type="submit" value="Fortsätt" /></p><p><a href="https://VOTE.CHANGEDOMAIN.fi/">Tillbaka till framsidan<a> - <a href="https://VOTE.CHANGEDOMAIN.fi/Shibboleth.sso/Logout?return=http%3A%2F%2Fwww.ayy.fi/vaalit">Avbryt röstningen</a></p><table><tr><th>Nummer</th><th>Namn</th><th>Förbund</th><th>Ring</th></tr>',
'</table>',
'<p><b>Felaktigt lösenord. Försök på nytt.</b></p>',
'<p><b>Felaktig identifieringskod. Försök på nytt.</b></p>',
'<p><b>Du har inte registrerats som röstare.</b></p>',
'<p><b>Din röst för kandidaten # har registrerats.</b></p>',
'<p><b>Röstningen misslyckades. Du har redan använt din röst.</b></p>',
'<p><b>Du gav ett felaktigt kandidatnummer. Var vänlig och försök på nytt.</b></p>',
'<p><a href="https://VOTE.CHANGEDOMAIN.fi/Shibboleth.sso/Logout?return=http%3A%2F%2Fwww.ayy.fi">Av datasäkerhetsskäl, var vänlig och logga ut genom denna länk.</a></p>',
'<p><b>Felaktigt användarnamn eller lösenord. Försök på nytt.</b></p>',
'<p><b>Du har valt kandidat NUMBER, NAME, från valförbund PARTY.</b> <input name="submit" type="submit" value="Bekräftar din röst." /></p><p><a href="https://VOTE.CHANGEDOMAIN.fi/">Tillbaka till framsidan<a> - <a href="https://VOTE.CHANGEDOMAIN.fi/Shibboleth.sso/Logout?return=http%3A%2F%2Fwww.ayy.fi/vaalit">Avbryt röstningen</a></p>',
'<p><b>Röstningen är stängd. Var vänlig och försök på nytt senare.</b></p>');
EOF