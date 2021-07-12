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

create table dashboard_read_access(
    dashboard_id uuid not null,
    is_allow_list bool not null
);

alter table dashboard_read_access
    add constraint dashboard_read_access_pk primary key (dashboard_id),
    add constraint dashboard_read_access_dashboard_id_fk foreign key (dashboard_id) references dashboard(id) on delete cascade;

create table dashboard_read_access_entry(
    dashboard_read_access_id uuid not null,
    user_id uuid not null
);

alter table dashboard_read_access_entry
    add constraint dashboard_read_access_entry_pk primary key (dashboard_read_access_id, user_id),
    add constraint dashboard_read_access_entry_dashboard_read_access_id_fk foreign key (dashboard_read_access_id) references dashboard_read_access(dashboard_id) on delete cascade,
    add constraint dashboard_read_access_entry_user_id_fk foreign key (user_id) references "user"(id) on delete cascade;

create table dashboard_write_access(
    dashboard_id uuid not null,
    is_allow_list bool not null
);

alter table dashboard_write_access
    add constraint dashboard_write_access_pk primary key (dashboard_id),
    add constraint dashboard_write_access_dashboard_id_fk foreign key (dashboard_id) references dashboard(id) on delete cascade;

create table dashboard_write_access_entry(
    dashboard_write_access_id uuid not null,
    user_id uuid not null
);

alter table dashboard_write_access_entry
    add constraint dashboard_write_access_entry_pk primary key (dashboard_write_access_id, user_id),
    add constraint dashboard_write_access_entry_dashboard_write_access_id_fk foreign key (dashboard_write_access_id) references dashboard_write_access(dashboard_id) on delete cascade,
    add constraint dashboard_write_access_entry_user_id_fk foreign key (user_id) references "user"(id) on delete cascade;

create table project(
    id uuid not null,
    owner_id uuid not null,
    name text not null,
    description text,
    parent_project_id uuid,
    flat_if_single_task boolean not null
);

alter table project
    add constraint project_pk primary key (id),
    add constraint project_parent_project_id_fk foreign key (parent_project_id) references project(id) on delete cascade,
    add constraint project_owner_id_fk foreign key (owner_id) references "user"(id) on delete cascade;

create table project_read_access(
    project_id uuid not null,
    is_allow_list bool not null
);

alter table project_read_access
    add constraint project_read_access_pk primary key (project_id),
    add constraint project_read_access_project_id_fk foreign key (project_id) references project(id) on delete cascade;

create table project_read_access_entry(
    project_read_access_id uuid not null,
    user_id uuid not null
);

alter table project_read_access_entry
    add constraint project_read_access_entry_pk primary key (project_read_access_id, user_id),
    add constraint project_read_access_entry_project_read_access_id_fk foreign key (project_read_access_id) references project_read_access(project_id) on delete cascade,
    add constraint project_read_access_entry_user_id_fk foreign key (user_id) references "user"(id) on delete cascade;

create table project_write_access(
    project_id uuid not null,
    is_allow_list bool not null
);

alter table project_write_access
    add constraint project_write_access_pk primary key (project_id),
    add constraint project_write_access_project_id_fk foreign key (project_id) references project(id) on delete cascade;

create table project_write_access_entry(
    project_write_access_id uuid not null,
    user_id uuid not null
);

alter table project_write_access_entry
    add constraint project_write_access_entry_pk primary key (project_write_access_id, user_id),
    add constraint project_write_access_entry_project_write_access_id_fk foreign key (project_write_access_id) references project_write_access(project_id) on delete cascade,
    add constraint project_write_access_entry_user_id_fk foreign key (user_id) references "user"(id) on delete cascade;

create table dashboard_project_association(
    dashboard_id uuid not null,
    project_id uuid not null,
    weight int not null
);

alter table dashboard_project_association
    add constraint dashboard_project_association_pk primary key (dashboard_id, project_id),
    add constraint dashboard_project_association_dashboard_id_fk foreign key (dashboard_id) references dashboard(id) on delete cascade,
    add constraint dashboard_project_association_project_id_fk foreign key (project_id) references project(id) on delete cascade,
    add constraint dashboard_project_association_weight_non_negative check (weight >= 0);


create table task_kind(
    id uuid not null,
    name text not null
);

alter table task_kind
    add constraint task_kind_pk primary key (id);

insert into task_kind values
    ('005c8772-56b6-4ebb-afa6-e27d1d987f86', 'Discrete'),
    ('7c1917d4-2743-4419-856e-c7a3b6ef540e', 'Percentual'),
    ('1cb2c09f-cdbd-4318-9dad-fcee1b16c0d4', 'Fractional');

create table plain_task(
    id uuid not null,
    project_id uuid not null,
    name text not null,
    unit text,
    kind_id uuid not null,
    reached numeric not null,
    reachable numeric not null,
    weight int not null
);

alter table plain_task
    add constraint plain_task_pk primary key (id, project_id),
    add constraint plain_task_project_id foreign key (project_id) references project(id) on delete cascade,
    add constraint plain_task_kind_id_fk foreign key (kind_id) references task_kind(id) on delete cascade,
    add constraint reached_non_negative check (reached is null or reached >= 0),
    add constraint reachable_larger_than_reached check (reachable is null or reachable >= reached),
    add constraint plain_task_weight_non_negative check (weight >= 0);

create table project_reference_task(
    id uuid not null,
    project_id uuid not null,
    project_reference_id uuid not null,
    weight int not null
);

alter table project_reference_task
    add constraint project_reference_task_pk primary key (id, project_id),
    add constraint project_reference_task_project_reference_id foreign key (project_id) references project(id) on delete cascade,
    add constraint project_reference_task_weight_non_negative check (weight >= 0);;

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