use uam;
create table department(
	deptId bigint(20) not null,	
	enterpriseid bigint(20) not null,
	parentid bigint(20) not null ,
	domain varchar(255) not null,
	name varchar(255),
	state tinyint(1) default '0' not null,
	PRIMARY KEY (id)

)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

alter table enterprise add isdepartment tinyint(1) default '0' not null;