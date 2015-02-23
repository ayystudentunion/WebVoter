#!/bin/sh
psql webvoter <<EOF
insert into person (electionname,personnumber,lastname,firstname,hasvoted,votestyle) values ('test','12345A','Relander','Lauri Kristian',false,0);
insert into person (electionname,personnumber,lastname,firstname,hasvoted,votestyle) values ('test','54321','Kallio','Kyösti',false,0);
insert into person (electionname,personnumber,lastname,firstname,hasvoted,votestyle) values ('test','k98765','Mannerheim','Carl Gustaf Emil',false,0);
EOF