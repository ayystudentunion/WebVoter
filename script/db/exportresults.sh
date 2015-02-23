#!/bin/sh
psql webvoter <<EOF
\copy person to /path/to/results/person.csv
\copy candidate to /path/to/results/candidate.csv
EOF
