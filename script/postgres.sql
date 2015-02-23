create table election (name varchar(100) primary key, isopen boolean);
create table coalition (name varchar(100) primary key, votes integer);
create table party (name varchar(100) primary key, votes integer, 
                    electionname varchar(100) references election(name),
                    coalitionname varchar(100) references coalition(name));
create table candidate (number integer primary key, lastname varchar(100), 
                        firstname varchar(100), 
                        partyname varchar(100) references party(name), 
                        random float, votes integer, partycomparison float, 
                        coalitioncomparison float, allcomparison float);
create table person (personnumber varchar(50) primary key,
                     lastname varchar(100), firstname varchar(100),
                     address varchar(100), city varchar(50), zipcode varchar(20),
                     votedate date, hasvoted boolean, votestyle integer,
                     password varchar(50));
create table stvvote (id integer, vote varchar(100) references election(name),
                      candidate integer references candidate(number),
                      rank integer);
