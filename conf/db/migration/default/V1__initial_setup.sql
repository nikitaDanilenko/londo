create table "user" (
    id uuid not null,
    nickname text not null,
    email text not null,
    password_salt text not null,
    password_hash text not null
);

alter table "user"
    add constraint user_pk primary key (id),
    add constraint user_nickname_unique unique (nickname);

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
    add constraint dashboard_pk primary key (id, user_id),
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