drop table if exists public.signal_data;

drop table if exists public.signal;

drop table if exists public.user_token;

drop table if exists public.user_confirm;

drop table if exists public.module;

drop table if exists public.user;

create table public.user
(
    id              serial                              not null
        constraint user_pk
            primary key,
    first_name      varchar(100),
    last_name       varchar(100),
    patronymic      varchar(100),
    email           varchar(100)                        not null unique,
    password        varchar(100)                        not null,
    email_confirmed boolean                             not null,
    create_time     timestamp default CURRENT_TIMESTAMP not null
);

create table public.user_token
(
    user_id          int primary key,
    token            varchar(36)                         not null,
    last_action_time timestamp default CURRENT_TIMESTAMP not null,
    foreign key (user_id) references "user" on delete cascade
);

create table public.user_confirm
(
    user_id     int primary key,
    code        varchar(36)                         not null,
    create_time timestamp default CURRENT_TIMESTAMP not null,
    foreign key (user_id) references "user" on delete cascade
);

create table public.module
(
    id          serial  not null
        constraint module_pk
            primary key,
    user_id     integer null
        constraint module_user_id_fk
            references "user"
            on update cascade on delete cascade,
    module      varchar(100),
    name        varchar(100),
    container   varchar(10),
    for_menu    boolean not null,
    transformer boolean not null
);

create unique index module_unique_idx on public.module (lower(module.module));

create table public.signal
(
    id          serial                              not null
        constraint signal_pk
            primary key,
    user_id     integer                             not null
        constraint signal_user_id_fk
            references "user"
            on update cascade on delete cascade,
    name        varchar(100),
    description varchar,
    create_time timestamp default CURRENT_TIMESTAMP not null
);

create table public.signal_data
(
    id        serial  not null
        constraint signal_data_pk
            primary key,
    signal_id integer not null
        constraint signal_data_signal_id_fk
            references signal
            on update cascade on delete cascade,
    x         numeric,
    y         numeric
);

INSERT INTO public.module (module, name, container, for_menu, transformer)
VALUES ('DummyTransformer', 'Dummy transformer', null, false, true),
       ('LinearAmp', 'Linear amplifier', 'right', false, true),
       ('Integrator', 'Integrator', null, false, true),
       ('Differentiator', 'Differentiator', null, false, true),
       ('Inverter', 'Inverter', null, false, true),
       ('SpectrumAnalyser', 'Spectrum analyser', null, false, true),
       ('SelfCorrelator', 'Self correlator', null, false, true),
       ('LinearOscillator', 'Linear oscillator', 'right', false, true);
