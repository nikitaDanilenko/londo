create table "user"
(
    id            uuid not null,
    nickname      text not null,
    email         text not null,
    password_salt text not null,
    password_hash text not null,
    display_name  text
);

alter table "user"
    add constraint user_pk primary key (id),
    add constraint user_nickname_unique unique (nickname);

create table dashboard
(
    id               uuid not null,
    user_id          uuid not null,
    header           text not null,
    description      text,
    publicly_visible bool not null,
    created_at       date not null,
    updated_at       date
);

alter table dashboard
    add constraint dashboard_pk primary key (id),
    add constraint dashboard_user_id_fk foreign key (user_id) references "user"(id);

create table project
(
    id               uuid not null,
    owner_id         uuid not null,
    name             text not null,
    description      text,
    created_at       date not null,
    updated_at       date
);

alter table project
    add constraint project_pk primary key (id),
    add constraint project_owner_id_fk foreign key (owner_id) references "user"(id) on
delete
cascade;

create table dashboard_entry
(
    dashboard_id uuid not null,
    project_id   uuid not null
);

alter table dashboard_entry
    add constraint dashboard_entry_pk primary key (dashboard_id, project_id),
    add constraint dashboard_entry_dashboard_id_fk
        foreign key (dashboard_id) references dashboard(id) on delete cascade,
    add constraint dashboard_entry_project_id_fk
        foreign key (project_id) references project(id) on delete cascade;


create table plain_task
(
    id         uuid   not null,
    project_id uuid   not null,
    name       text   not null,
    unit       text,
    kind       text   not null,
    reached    bigint not null,
    reachable  bigint not null,
    counting   bool   not null
);

alter table plain_task
    add constraint plain_task_pk primary key (id, project_id),
    add constraint plain_task_project_id
        foreign key (project_id) references project(id) on delete cascade,
    add constraint reached_non_negative check (reached >= 0),
    add constraint reachable_larger_than_reached check (reachable >= reached),
    add constraint kind_enumeration
        check (kind = 'Discrete' or kind = 'Percentual' or kind = 'Fractional');

create table reference_task
(
    id                   uuid not null,
    project_id           uuid not null,
    project_reference_id uuid not null
);

alter table reference_task
    add constraint reference_task_pk primary key (id, project_id),
    add constraint reference_task_project_reference_id
        foreign key (project_id) references project(id) on delete cascade;

create table session
(
    id         uuid not null,
    user_id    uuid not null,
    created_at date not null
);

alter table session
    add constraint session_pk primary key (id),
    add constraint session_user_id_fk
        foreign key (user_id) references "user"(id) on delete cascade;

create table login_attempt
(
    user_id                                     uuid not null,
    failed_attempts_since_last_successful_login int  not null,
    last_successful_login                       timestamp
);

alter table login_attempt
    add constraint login_attempt_pk primary key (user_id),
    add constraint login_attempt_user_id_fk
        foreign key (user_id) references "user"(id) on delete cascade;