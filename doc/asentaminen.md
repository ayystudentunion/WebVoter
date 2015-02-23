# WebVoter - asentaminen

Sivulla on annettu lyhyet ohjeet WebVoter-järjestelmän asentamiseksi Shibboleth-kirjautumisella.
Ohjeissa oletetaan käytettävän Linux-käyttöjärjestelmää, 
JBoss-ohjelmistopalvelinta ja PostgreSQL-tietokantaa,
vaikka järjestelmää on mahdollista käyttää myös muilla alustoilla.
Apachen, JBossin ja Shibbolethin toimintaan ei tarkemmin perehdytä, vaan niiden käytön perusteet oletetaan.

## Vaaditut ohjelmistot

Asenna käyttökuntoon seuraavat ohjelmistot ja kirjastot.
Jäljempänä olevat versionumerot ovat ne,
joilla ohje on testattu.

*   Apache + mod_jk _versio 2.2.15_
*   Shibboleth _versio 2.4.3_
*   JBoss _versio 6.1.0_
*   PostgreSQL _versio 8.4.7_
*   Java SE_versio 1.6.0_
*   xdoclet-lib_versio 1.2.3_
*   Apache Ant käännöstyökalu

### Apache, mod_jk ja JBoss

Näiden asentamista ei käsitellä tarkemmin tässä ohjeessa,
mutta hyviä ohjeita löytää esim. [JBossin](http://community.jboss.org/wiki/UsingModjk12WithJBoss) sivuilta.

### Shibboleth

Muokkaa Shibboleth toimimaan Apachen kanssa ja suojaa sillä JBossin WebVoter-sovelluksen hakemisto.
Ohjeita löytää [Shibbolethin kotisivuilta](http://shibboleth.internet2.edu/) sekä [Googlella](http://www.google.com/search?q=shibboleth+apache).

Attribuutteina tulee hakea kielitieto sekä käyttäjät yksilöivä tunniste.
Tässä on käytetty funetEduPerson-skeeman mukaisia preferredLanguage ja schacPersonalUniqueCode (StudentID)-kenttiä,
jotka on nimetty vastaavasti A_LANGUAGE ja A_STUDENTID -attribuuteiksi.

## WebVoter

Pure [webvoter-ayy.zip](files/webvoter-ayy.zip) koodipaketti
ja kopioi xdoclet webvoterin alle xdoclet-1.2.3-hakemistoon
tai muokkaa build.xml-tiedostoon kirjaston oikea sijainti.

HUOM. Järjestelmän etusivu index.jsp ja vaalivun header VoteHeader.jsp sisältävät muutoksia alkuperäiseen WebVoteriin kirjautumisen kannalta ja ne odottavat 
Shibboleth-kohdassa mainittuja funetEduPerson-skeeman mukaisia kenttiä A_LANGUAGE ja A_STUDENTID -nimillä.
Kieliversioista käytetään "fi", "sv" ja "en", joista viimeistä käytetään kaikkien muiden kielten kohdalla.
Jos haluat muokata järjestelmän tunnistamia kieliä, tulee tämä muokata molempiin tiedostoihin.
Opiskelijanumero erotetaan A_STUDENTID-kentästä, poistamalla siitä koulutunnus ja jättämällä jäljelle vain opiskelijanumero,
joten käytettäessä muuta yksilöivää tunnistetta, on tämän käsittely muokattava VoteHeader.jsp-tiedostoon.

Käännä ohjelmisto komennolla "ant" ja luo war ja jar-paketit komennolla "ant jar"

Kopio tarvittavat paketit JBossiin script-hakemistosta löytyvällä install-scriptillä.
Scriptistä löytyy tarkemmat ohjeet tähän.
Käynnistä tämän jälkeen JBoss ja luo loput tietokantarakenteesta createdb-scriptillä.

### Käyttäminen

Äänestystietojen, ehdokkaiden yms. lisääminen onnistuu helpoiten Shell-scripteillä. scripts/db/ -hakemistossa on esimerkkejä,
joiden avulla voi lisätä tiedot tietokantaan.

Äänestyksen avaaminen ja sulkeminen, sekä tulosten ulosotto järjestelmästä hoituu helpoiten myös komentorivillä.
scripts/db/ -hakemistossa on esimerkkitiedostot äänestyksen aavamisesta, sulkemisesta ja niiden ajastamisesta cronilla.

## Lisätietoja

Ohjeet ovat varsin suppeat ja olettavat paljon osaamista ennestään.
Jos järjestelmän käyttö kiinnostaa ja tarvitset lisätietoja siitä, ota yhteyttä.

Jukka Karvonen, IT-asiantuntija, Aalto-yliopiston ylioppilaskunta.
Sähköposti: etunimi.sukunimi@ayy.fi | Puhelin: +358 50 520 9431.
