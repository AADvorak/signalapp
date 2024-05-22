insert into public.role(name)
values ('EXTENDED_STORAGE');

create table public.user_in_role
(
    role_id integer not null
        constraint user_in_role_role_id_fk
            references "role"
            on update cascade on delete cascade,
    user_id integer not null
        constraint user_in_role_user_id_fk
            references "user"
            on update cascade on delete cascade
);

create unique index user_in_role_unique_idx on public.user_in_role (role_id, user_id);
