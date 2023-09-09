create table public.folder
(
    id          serial       not null
        constraint folder_pk
            primary key,
    user_id     integer      not null
        constraint folder_user_id_fk
            references "user"
            on update cascade on delete cascade,
    name        varchar(100) not null,
    description varchar
);

create unique index folder_unique_idx on public.folder (user_id, name);

create table public.signal_in_folder
(
    folder_id integer not null
        constraint signal_in_folder_folder_id_fk
            references "folder"
            on update cascade on delete cascade,
    signal_id integer not null
        constraint signal_in_folder_signal_id_fk
            references "signal"
            on update cascade on delete cascade
);

create unique index signal_in_folder_unique_idx on public.signal_in_folder (folder_id, signal_id);
