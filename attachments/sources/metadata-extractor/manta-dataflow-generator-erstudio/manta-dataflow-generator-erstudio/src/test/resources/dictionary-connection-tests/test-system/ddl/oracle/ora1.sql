create table t1 (
	a int,
	b int,
	c int
);

create table t2 (
	a int,
	b int,
	c int
);

create view v1 as
    select * from t1;
