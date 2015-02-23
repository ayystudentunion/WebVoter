# WebVoter - sähköinen äänestysjärjestelmä

WebVoter on Kalle Kivimaan kehittämä sähköinen äänestysjärjestelmä.
Järjestelmän alkuperäinen sivusto löytyy osoitteesta [http://kivimaa.fi/webvoter/](http://kivimaa.fi/webvoter/).
Järjestelmä on lisenssoitu GNU Lesser Public License version 2.1 lisenssillä.

Tämän sivuston tarkoituksena on lisätä järjestelmään tuki [Shibboleth](http://shibboleth.internet2.edu/)-kirjautumiselle
ja dokumentoida järjestelmän käyttö Aalto-yliopiston ylioppilaskunnan (AYY) edustajistovaaleissa 2011 ja 2013.

Järjestelmää on aikaisemmin käytetty menestykkäästi ainakin Teknillisen korkeakoulun ylioppilaskunnan,
Helsingin kauppakoreakoulun ylioppilaskunnan ja Hankenin ylioppilaskunnan vaaleissa vuosina 2004, 2005, 2006 ja 2008.

## Järjestelmäkuvaus

WebVoter on Java-sovellus, jota ajetaan JBoss-ohjelmistopalvelimella.
Sovellus jakautuu kahteen osaan,
eli käyttäjälle näkyvään www-liittymään sekä taustajärjestelmän,
joka hoitaa varsinaisen äänestysprosessin.

Käyttäjän suuntaan palvelinohjelmsitona toimii Apache,
joka hoitaa käyttäjän tunnistamisen Shibbolethin kanssa,
sekä välittää HTTP-pyynnöt ja kirjautumistiedot JBossille. 
Samoin Apachen avulla rajataan käyttäjän pääsy vain äänestyksessä tarvittaviin sivuihin JBossissa.

### Järjestelmän tietoturva

Vaalijärjestelmälle on toteutettu tietoturva-auditointi,
jossa tarkasteltiin AYY:n vaalijärjestyksessä sille asetettuja ehtoja.

*   vaalijärjestelmän tietoturvan taso on riittävä
*   äänestäjän henkilöllisyys varmistetaan uskottavasti ennen äänestämistä
*   äänestäjän henkilöllisyyttä ei pystytä jälkikäteen yhdistämään mihinkään tiettyyn annettuun ääneen
*   äänestäjä voi käyttää äänioikeuttaan ainoastaan kerran
*   vaalijärjestelmään tulee voida suorittaa annettujen äänten ja laskennan tarkistus kuitenkaan äänestyssalaisuutta vaarantamatta
*   vaalijärjestelmä perustuu avoimeen lähdekoodiin

Auditointiraportti on luettavissa [täältä](Auditointiraportti.pdf).

### Käytettävät ohjelmistot

AYY:n vaalijärjestelmä käyttää seuraavia ohjelmistoja.

*   RedHat Enterprise Linux _versio 6_
*   Apache + mod_jk _versio 2.2.15_
*   Shibboleth _versio 2.4.3_
*   JBoss _versio 6.1.0_
*   PostgreSQL _versio 8.4.7_
*   Java SE_versio 1.6.0_
*   WebVoter _tällä sivustolla oleva versio_

## Shibboleth-kirjautuminen

Shibboleth-kirjautuminen on toteutettu Apachen Shibboleth-moduulin avulla.
AYY:n järjestelmässä IdP:ltä haetaan sekä henkilökohainen tunniste,
että käyttöliittymän haluttu kieli.
Henkilökohtaisena tunnisteena käytetään opiskelijanumeroa.
Nämä parametrit välitetään mod_jk:n kautta JBossille.

Kirjautumista varten on tehty muutoksia vain alkuperäisen WebVoter-sovelluksen www-liittymän tiedostoihin.

*   Index-tiedosto on muutettu jsp-tiedostoksi, jotta sille voidaan hakea kielitieto ja näyttää sivu halutulla kielellä.
*   VoteHeader-tiedosto on muokattu käyttämään käyttäjän tunnisteena IdP:ltä haettua opiskelijanumeroa, sekä käyttäliittymän kielenä IdP:ltä haettua kielitietoa.

## Järjestelmän asentaminen

WebVoter-järjestelmän asentaminen toimintakuntoon on kuvattu [asentaminen.md](asentaminen.md).
