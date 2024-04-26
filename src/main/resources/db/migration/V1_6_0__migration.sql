create table public.role
(
    id          serial       not null
        constraint role_pk
            primary key,
    name        varchar(100) not null
);

insert into public.role(name)
values ('ADMIN');

alter table public."user"
    add column role_id integer
        constraint user_role_id_fk
            references "role"
            on update set null on delete set null;
