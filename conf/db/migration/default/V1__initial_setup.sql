create table "user" (
    id uuid not null,
    nickname text not null,
    email text not null,
    password_salt text not null,
    password_hash text not null,
    iterations int not null
);

alter table "user"
    add constraint user_pk primary key (id),
    add constraint user_nickname_unique unique (nickname),
    add constraint iterations_positive check (iterations > 0);

create table user_details (
    user_id uuid not null,
    first_name text,
    last_name text,
    description text
);

alter table user_details
    add constraint user_details_user foreign key (user_id) references "user"(id) on delete cascade,
    add constraint user_details_pk primary key (user_id);

create table user_settings (
    user_id uuid not null,
    dark_mode bool not null
);

alter table user_settings
    add constraint user_settings_user foreign key (user_id) references "user"(id) on delete cascade,
    add constraint user_settings_pk primary key (user_id);

create table dashboard(
    id uuid not null,
    user_id uuid not null,
    header text not null,
    description text
);

alter table dashboard
    add constraint dashboard_pk primary key (id),
    add constraint dashboard_user_id_fk foreign key (user_id) references "user"(id);

create table dashboard_restriction(
    dashboard_id uuid
);

alter table dashboard_restriction
    add constraint dashboard_restriction_pk primary key (dashboard_id),
    add constraint dashboard_restriction_dashboard_id_fk foreign key (dashboard_id) references dashboard(id) on delete cascade;

create table dashboard_restriction_access(
    dashboard_restriction_id uuid not null,
    user_id uuid not null
);

alter table dashboard_restriction_access
    add constraint dashboard_restriction_access_pk primary key (dashboard_restriction_id, user_id),
    add constraint dashboard_restriction_acess_dashboard_restriction_id_fk foreign key (dashboard_restriction_id) references dashboard_restriction(dashboard_id) on delete cascade,
    add constraint dashboard_restriction_access_user_id_fk foreign key (user_id) references "user"(id) on delete cascade;

create table project(
    id uuid not null,
    owner_id uuid not null,
    name text not null,
    description text,
    parent_project_id uuid
);

alter table project
    add constraint project_pk primary key (id),
    add constraint project_parent_project_id_fk foreign key (parent_project_id) references project(id) on delete cascade,
    add constraint project_owner_id_fk foreign key (owner_id) references "user"(id) on delete cascade;

create table project_access(
    project_id uuid not null,
    user_id uuid not null,
    write_allowed boolean not null
);

alter table project_access
    add constraint project_access_pk primary key (project_id, user_id),
    add constraint project_access_project_id foreign key (project_id) references project(id) on delete cascade,
    add constraint project_access_user_id foreign key (user_id) references "user"(id) on delete cascade;

create table task_kind(
    id uuid not null,
    name text not null
);

alter table task_kind
    add constraint task_kind_pk primary key (id);

create table task(
    id uuid not null,
    project_id uuid not null,
    name text not null,
    unit text,
    kind_id uuid not null,
    reached numeric not null,
    reachable numeric not null,
    weight int not null
);

alter table task
    add constraint task_pk primary key (id, project_id),
    add constraint task_project_id foreign key (project_id) references project(id) on delete cascade,
    add constraint task_kind_id_fk foreign key (kind_id) references task_kind(id) on delete cascade,
    add constraint reached_non_negative check (reached >= 0),
    add constraint reachable_larger_than_reached check (reachable >= reached),
    add constraint weight_non_negative check (weight >= 0);

create table session_key(
    user_id uuid not null,
    public_key text not null
);

alter table session_key
    add constraint session_key_pk primary key (user_id),
    add constraint session_key_user_id_fk foreign key (user_id) references "user"(id) on delete cascade;

create table registration_token(
    email text not null,
    token text not null
);

alter table registration_token
    add constraint registration_token_ok primary key (email);

create table login_attempt(
    user_id uuid not null,
    failed_attempts_since_last_successful_login int not null,
    last_successful_login timestamp
);

alter table login_attempt
    add constraint login_attempt_pk primary key (user_id),
    add constraint login_attempt_user_id_fk foreign key (user_id) references "user"(id) on delete cascade;