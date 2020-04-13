use scrumtool;
insert into User (password, mail, username, firstname, lastname, isAdmin, numProjects)
	values ('$2a$10$2VqiIm8sO1CvA65JXO9O2OWyXtwJK1Cbzj/CfiE6VmOyor.AKWcAu', 'orestis@gmail.com', 'orestisgarmpis', 'Orestis', 'Garmpis', False, 2);
insert into User (password, mail, username, firstname, lastname, isAdmin, numProjects)
	values ('$2a$10$xgAzw4vx19hQxuw7jjFyku/GCT8izH27zKIdjnVZJhZg4lCh2EH1m', 'kostas@gmail.com', 'kostaskotronis', 'Kostas', 'Kotronis', False, 4);
insert into User (password, mail, username, firstname, lastname, isAdmin, numProjects)
	values ('$2a$10$FFZIXZWNB5chE3QbidHfXe/W1apJon4lMLSp.zh1YEIg5e6mpBfmC', 'markos@gmail.com', 'markosvarv', 'Markos', 'Varvagiannis', False, 3);
update User set username = 'dimitramav' where firstname = 'Dimitra';
select * from User;
insert into Project (title, isDone, deadlineDate) values ('Twitter', FALSE, '2019-11-17');
insert into Project (title, isDone, deadlineDate) values ('Scrum', FALSE, '2018-07-15');
insert into Project (title, isDone, deadlineDate) values ('Facebook', FALSE, '2018-09-16');
insert into Project (title, isDone, deadlineDate) values ('Trivaggos', TRUE, '2017-09-30');
update Project set title = 'ScrumTool', deadlineDate = '2018-08-07' where idProject = 2;
select * from Project;
insert into Project_has_User (Project_id, User_id, role) values (4, 1, 'Product Owner');
insert into Project_has_User (Project_id, User_id, role) values (2, 1, 'Developer');
insert into Project_has_User (Project_id, User_id, role) values (1, 2, 'Developer');
insert into Project_has_User (Project_id, User_id, role) values (2, 2, 'Developer');
insert into Project_has_User (Project_id, User_id, role) values (3, 2, 'Developer');
insert into Project_has_User (Project_id, User_id, role) values (4, 2, 'Scrum Master');
insert into Project_has_User (Project_id, User_id, role) values (1, 3, 'Developer');
insert into Project_has_User (Project_id, User_id, role) values (2, 3, 'Developer');
insert into Project_has_User (Project_id, User_id, role) values (3, 3, 'Developer');
select * from Project_has_User;
select * from Project where idProject in (select Project_id from Project_has_User where User_id = 1);
insert into Sprint (deadlineDate, goal, plan, isCurrent, numSprint, Project_id)
	values ('2018-08-08', 'Recreate Messenger', 'Copy awesome Telegram', FALSE, 1, 3);
insert into Sprint (deadlineDate, goal, plan, isCurrent, numSprint, Project_id)
	values ('2018-02-10', 'Help Trump become president', 'Steal Data From Users', TRUE, 2, 3);
insert into Sprint (deadlineDate, goal, plan, isCurrent, numSprint, Project_id)
	values ('2018-11-22', 'Make it more popular', 'Hack Facebook', TRUE, 1, 1);
select * from Sprint;
insert into PBI (title, description, priority, isEpic, Project_id, Sprint_id)
	values ('Communicate with Donald', 'Constantly inform him about statistics from stolen data', 2, TRUE, 3, 1);
insert into PBI (title, description, priority, isEpic, Project_id)
	values ('Stop Telegram Uprise', 'Make Telegram programmers life difficult to work', 1, TRUE, 3);
insert into PBI (title, description, priority, isEpic, Project_id, Epic_id)
	values ('Telegram & ISIS', 'Blame Telegram for permitting Isis soldiers to use their app', 3, FALSE, 3, 2);
insert into PBI (title, description, priority, isEpic, Project_id, Epic_id)
	values ('Bad Telegram Reputation', 'Pay websites to write negative reviews for our enemy', 2, FALSE, 3, 2);
update PBI set description = 'Return token', Epic_id = 5 where idPBI = 5;
select * from PBI;
select * from Sprint where Project_id = 3 and isCurrent = TRUE;
insert into Task (description, state, PBI_id) values ('Hire Cambridge Analytica', 3, 1);
insert into Task (description, state, PBI_id) values ('Do not let the press know about anything', 2, 1);
insert into Task (description, state, PBI_id) values ('Find the users with open profiles first', 2, 1);
insert into Task (description, state, PBI_id) values ('Show the results to the Republican Party', 1, 1);
select * from Task;
select * from Task where PBI_id in (select idPBI from PBI where Sprint_id = 1);
insert into Issue (description, state, Task_id) values ('Rumors in England', 2, 2);
insert into Issue (description, state, Task_id) values ('They ask for more money', 3, 1);
select * from Issue;
insert into Notification (Project_id, projectTitle, role, FromUsername, ToUserEmail, type)
	values (7, 'Kostas Project', 'Scrum Master', 'kostaskotronis', 'orestis@gmail.com', 'Accept/Decline');
insert into Notification (ToUserEmail, type, text) values ('kostas@gmail.com', 'Text', 'Good morning sir!');
select * from Notification;
