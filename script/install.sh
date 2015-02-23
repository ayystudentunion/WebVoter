#!/bin/sh

# The first parameter should be the WebVoter directory.
# The second parameter should be the JBoss deployment directory.

cp $1/postgres-ds.xml $1/webvoter.jar $1/webvoter.war $2/
cp $1/lib/postgresql-jdbc3.jar $1/lib/tkyutil.jar $2/../lib/

echo "Creating database."
echo "Pleaser remember to run createdb.sh after you've (re)started JBoss."

# Comment out the next two lines if the database is not local
createdb webvoter
createuser -A -D webvoter

