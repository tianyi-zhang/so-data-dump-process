create database deeplearning;
use deeplearning;
create table questions(Id INT NOT NULL PRIMARY KEY, AcceptedAnswerId INT, Tags VARCHAR(255), ViewCount INT, Title VARCHAR(255), CreationDate datetime, LastEditDate datetime, Body text);
create table answers(Id INT NOT NULL PRIMARY KEY, ParentId INT, Body text, Score INT, IsAccepted BOOLEAN, Tags VARCHAR(256), ViewCount INT, CreationDate DATETIME, LastEditDate DATETIME);
 
/* show table schema*/
describe questions;
describe answers;