#!/bin/sh

# The first parameter should be the location of the install zip file.
# The second parameter should be the destination directory for
# the WebVoter files.
# The third parameter should be the JBoss deployment directory.

mkdir $2
unzip $1 -d $2
cp $1/postgres-ds.xml $1/webvoter.jar $1/webvoter.war $3/
cp $1/postgresql-jdbc3.jar $1/tkyutil.jar $3/../lib/

echo Creating database.
echo Pleaser remember to run createdb.sh after you've (re)started JBoss.

# Comment out the next two lines if the database is not local
createdb webvoter
createuser -A -D webvoter

