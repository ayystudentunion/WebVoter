Installation Instructions
=========================

Software requirements:

PostgreSQL 8.0 or later
JBoss 4.0.0 or later

Localhost should be set to trust authentication in PostgreSQL
(in pg_hba.con).

Run the provided installation script (sh install.sh). Start JBoss and
wait until it has started. Run the database configuration script
(sh createdb.sh). Log in to the admin interface:

http://localhost:8080/webvoter/admin.html with election name "test" and
password "test".

Also, see the section JBOSS TROUBLESHOOTING at the bottom of this
file.

External Authentication
=======================

The code supports the following structure:

External authentication is available at an URL 
https://example.com/foo?username=bar&password=snafu
This URL needs to be configured in elections table, column authurl

If you are using HTTPS and the certificate on the authentication server
is not signed, create a keystore with the certificate:

keytool -import -file /path/to/yourcert.pem -keystore /etc/keystore.jks

Then start JBoss like this:

JAVA_OPTS="-Djavax.net.ssl.trustStore=/etc/keystore.jks" sh run.sh

Code Structure
==============

WebVoter consists of three distinct parts. The data is stored in
a PostgreSQL database under user webvoter. This data is accessed via
an EJB layer, which is contained in webvoter.jar. The presentation
layer is contained in webvoter.war and consists of static HTML pages
and dynamic JSP pages.


Database Structure
==================

The database contains the following tables:

ELECTION
--------
name (text) Primary key
password (text)
isopen (boolean) True if the election is open, false if closed.
                 This affects certain admin interface functionality
                 and of course prevents votes from added. This should
                 be used to temporarily close the election, for example
                 for the night.
production (boolean) True is the system is in production. This
                 affects certain admin interface functionality.
                 The vote calculation is available only if the election
                 is both closed and not in production.
firstpassword (boolean)
secondpassword (boolean) These control the usage of an external password
                 and a single-use password.
stv (boolean) Is the election STV or d'Hondt.
government (boolean) Controls d'Hondt result calculation. True means that
                 the candidates in a coalition are ordered by their
                 respective votes. False means that party comparisons
                 are used.
authurl (text) URL to use if firstpassword is true.

COALITION
---------
name (text) Primary key together with electionname.
electionname (text) Primary key together with name. References election table, column name.
votes (bigint) Vote count. Updated at result calculation stage.

PARTY
-----
name (text) Primary key together with electionname.
electionname (text) Primary key together with name. References election table, column name.
votes (bigint) Vote count. Updated at result calculation stage.
coalitionname (text) References coalition table, column name.

CANDIDATE
---------
electionname (text) Primary key together with number. References
                 election table, column name.
number (integer) Voting number. Primary key together with electionname.
lastname (text)
firstname (text)
random (double) Random number to use in case of ties.
votes (bigint) Vote count. Constantly updated.
partycomparison (real)
coalitioncomparison (real)
allcomparison (real)
partyname (text) References party table, column name.

PERSON
------
electionname (text) References election table, column name. Part of
                 the primary key.
personnumber (text) Part of the primary key.
lastname (text)
firstname (text)
emailaddress (text)
address (text)
city (text)
zipcode (text)
votedate (timestamp) When the person has voted.
votestyle (integer) 1 = paper, 2 = WWW.
password (text) Second password to be used to confirm the vote.
hasvoted (boolean) Has the person voted or not.

STVVOTE
-------
electionname (text) Primary key together with realid. References
                 election table, column name.
realid (text) Primary key.
id (text) Groups the votes.
candidate (integer) References candidate table, column number.
rank (integer)

LANGUAGE
--------
election (text) References election table, column name. Part of
                 the primary key.
languagecode (text) Part of the primary key.
header (text) Header for all user interface JSP pages.
footer (text) Footer for all user interface JSP pages.
candidateheader (text) 
candidatefooter (text) 
wrongfirstpassword (text) 
wrongsecondpassword (text) 
novoterfound (text) 
successfulvoting (text) 
failedvoting (text) 
invalidnumber (text) 
gotohomepage (text) 
invalidlogin (text) 
confirmchoice (text) 
electionclosed (text) 


JBOSS TROUBLESHOOTING
=====================

You may need to do the following change in JBoss
jboss/server/default/conf/standardjbosscmp-jdbc.xml:

In
      <type-mapping>
         <name>PostgreSQL</name>
         ...
      </type-mapping>

Change
         <mapping>
            <java-type>java.lang.Boolean</java-type>
            <jdbc-type>CHAR</jdbc-type>
            <sql-type>BOOLEAN</sql-type>
         </mapping>

To
         <mapping>
            <java-type>java.lang.Boolean</java-type>
            <jdbc-type>BOOLEAN</jdbc-type>
            <sql-type>BOOLEAN</sql-type>
         </mapping>

This change solves a problem where the server throws an exception when
the voter confirms the vote or an administrator tries to change the
election status via the admin interface. The reason for this is that
somehow my Eclipse mungles xDoclet generation so that the type-mapping
fails.
