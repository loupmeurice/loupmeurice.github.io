-- *********************************************
-- * SQL SQLite generation                     
-- *--------------------------------------------
-- * DB-MAIN version: 11.0.1              
-- * Generator date: Dec  4 2018              
-- * Generation date: Tue May 19 15:46:30 2020 
-- * LUN file: C:\Users\lmeurice\eclipse-workspace-ee\App\src\main\resources\PROJECT.lun 
-- * Schema: SCHEMA/1-1 
-- ********************************************* 


-- Database Section
-- ________________ 


-- Tables Section
-- _____________ 

create table NewsletterTeam (
     team_id numeric(20) not null,
     constraint ID_NewsletterTeam primary key (team_id));

create table Newsletter (
     team_id numeric(20) not null,
     email varchar(200) not null,
     constraint ID_Newsletter primary key (team_id, email),
     foreign key (team_id) references NewsletterTeam);

create table Season (
     id integer primary key autoincrement,
     serialized varchar(10000) not null,
     lastUpdate varchar(50) not null);


-- Index Section
-- _____________ 

create unique index ID_NewsletterTeam
     on NewsletterTeam (team_id);

create unique index ID_Newsletter
     on Newsletter (team_id, email);

create index KEY_Newsletter
     on Newsletter (email);

create unique index ID_Season
     on Season (id);

