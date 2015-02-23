#!/bin/sh
psql webvoter <<EOF
DELETE FROM party WHERE electionname = 'test';
DELETE FROM coalition WHERE electionname = 'test';
DELETE FROM language WHERE election = 'test';
DELETE FROM election WHERE name = 'test';
DELETE FROM person WHERE electionname = 'test';
DELETE FROM candidate WHERE electionname = 'test';
EOF
