create table "user"
(
    id            uuid not null,
    nickname      text not null,
    email         text not null,
    salt          text not null,
    hash          text not null,
    display_name  text
);

alter table "user"
    add constraint user_pk primary key (id),
    add constraint user_nickname_unique unique (nickname);

create table dashboard
(
    id          uuid                     not null,
    owner_id    uuid                     not null,
    header      text                     not null,
    description text,
    visibility  text                     not null,
    created_at  timestamp with time zone not null,
    updated_at  timestamp with time zone
);

alter table dashboard
    add constraint dashboard_pk primary key (id),
    add constraint dashboard_owner_id_fk foreign key (owner_id) references "user"(id),
    add constraint dashboard_visibility_enumeration check (visibility = 'Public' or visibility = 'Private');

create table project
(
    id          uuid                     not null,
    owner_id    uuid                     not null,
    name        text                     not null,
    description text,
    created_at  timestamp with time zone not null,
    updated_at  timestamp with time zone
);

alter table project
    add constraint project_pk primary key (id),
    add constraint project_owner_id_fk foreign key (owner_id) references "user"(id) on
delete
cascade;

create table dashboard_entry
(
    dashboard_id uuid                     not null,
    project_id   uuid                     not null,
    created_at   timestamp with time zone not null
);

alter table dashboard_entry
    add constraint dashboard_entry_pk primary key (dashboard_id, project_id),
    add constraint dashboard_entry_dashboard_id_fk
        foreign key (dashboard_id) references dashboard(id) on delete cascade,
    add constraint dashboard_entry_project_id_fk
        foreign key (project_id) references project(id) on delete cascade;


create table task
(
    id         uuid                     not null,
    project_id uuid                     not null,
    name       text                     not null,
    unit       text,
    kind       text                     not null,
    reached    bigint                   not null,
    reachable  bigint                   not null,
    counting   bool                     not null,
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone
);

alter table task
    add constraint task_pk primary key (id, project_id),
    add constraint task_project_id
        foreign key (project_id) references project(id) on delete cascade,
    add constraint reached_non_negative check (reached >= 0),
    add constraint reachable_larger_than_reached check (reachable >= reached),
    add constraint reachable_positive check ( reachable > 0 ),
    add constraint kind_enumeration
        check (kind = 'Discrete' or kind = 'Percent' or kind = 'Fraction');

create table session
(
    id         uuid                     not null,
    user_id    uuid                     not null,
    created_at timestamp with time zone not null
);

alter table session
    add constraint session_pk primary key (id),
    add constraint session_user_id_fk
        foreign key (user_id) references "user"(id) on delete cascade;

create table login_throttle
(
    user_id         uuid      not null,
    failed_attempts int       not null,
    last_attempt_at timestamp not null
);

alter table login_throttle
    add constraint login_throttle_pk primary key (user_id),
    add constraint login_throttle_user_id_fk
        foreign key (user_id) references "user"(id) on delete cascade,
    add constraint login_throttle_failed_attempts_non_negative check (failed_attempts >= 0);