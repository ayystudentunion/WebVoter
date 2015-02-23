#!/bin/sh
psql webvoter <<EOF
\copy person to /path/to/results/person_start.csv
\copy candidate to /path/to/results/candidate_start.csv
UPDATE election SET isopen = false WHERE name = 'test';
EOF
