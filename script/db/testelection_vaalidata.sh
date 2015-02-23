#!/bin/sh
psql webvoter <<EOF
insert into election (name,password,isopen,ispublic,production,firstpassword,secondpassword,stv,government,toelect)
values ('test','swefecR9xu',true,true,true,true,false,false,false,45);
insert into coalition (electionname,name,votes) values ('test','Elements',0);
insert into party (electionname,name,votes,coalitionname) values ('test','Alkali Metal',0,'Elements');
insert into party (electionname,name,votes,coalitionname) values ('test','Halogen',0,'Elements');
insert into party (electionname,name,votes,coalitionname) values ('test','Noble Gases',0,'Elements');
insert into party (electionname,name,votes) values ('test','The Olympians',0);
insert into candidate (electionname,number,lastname,firstname,votes,random,partyname) values ('test',2,'Hydrogen','Alpha',0,599618080630109,'Alkali Metal');
insert into candidate (electionname,number,lastname,firstname,votes,random,partyname) values ('test',3,'Lithium ','Beta',0,150572413377283,'Alkali Metal');
insert into candidate (electionname,number,lastname,firstname,votes,random,partyname) values ('test',4,'Sodium ','Gamma',0,0470386256976418,'Alkali Metal');
insert into candidate (electionname,number,lastname,firstname,votes,random,partyname) values ('test',5,'Fluorine','Delta',0,00385565730449777,'Halogen');
insert into candidate (electionname,number,lastname,firstname,votes,random,partyname) values ('test',6,'Chlorine','Epsilon',0,592334021301038,'Halogen');
insert into candidate (electionname,number,lastname,firstname,votes,random,partyname) values ('test',7,'Bromine','Zeta',0,349364135120216,'Halogen');
insert into candidate (electionname,number,lastname,firstname,votes,random,partyname) values ('test',8,'Iodine','Eta',0,848365075394182,'Halogen');
insert into candidate (electionname,number,lastname,firstname,votes,random,partyname) values ('test',9,'Helium','Theta',0,141315982826353,'Noble Gases');
insert into candidate (electionname,number,lastname,firstname,votes,random,partyname) values ('test',10,'Neon','Iota',0,0825515735338667,'Noble Gases');
insert into candidate (electionname,number,lastname,firstname,votes,random,partyname) values ('test',11,'Apollo','Kappa',0,474330542263151,'The Olympians');
insert into candidate (electionname,number,lastname,firstname,votes,random,partyname) values ('test',12,'Diana','Lambda',0,66107520379612,'The Olympians');
insert into candidate (electionname,number,lastname,firstname,votes,random,partyname) values ('test',13,'Juno','Mu',0,087433488059522,'The Olympians');
insert into candidate (electionname,number,lastname,firstname,votes,random,partyname) values ('test',14,'Jupiter','Nu',0,98653728875063,'The Olympians');
insert into candidate (electionname,number,lastname,firstname,votes,random,partyname) values ('test',15,'Mars','Xi',0,714390483087319,'The Olympians');
insert into candidate (electionname,number,lastname,firstname,votes,random,partyname) values ('test',16,'Saturn','Omicron',0,550673405156909,'The Olympians);
insert into candidate (electionname,number,lastname,firstname,votes,random,partyname) values ('test',17,'Venus','Pi',0,361364086179375,'The Olympians');
insert into candidate (electionname,number,lastname,firstname,votes,random,partyname) values ('test',18,'Vesta','Rho',0,48004933078866,'The Olympians');
EOF