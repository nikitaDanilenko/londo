alter table task
    drop constraint task_pk,
    add constraint task_pk primary key (id);

create table simulation
(
    task_id          uuid                     not null,
    dashboard_id     uuid                     not null,
    reached_modifier bigint                   not null,
    created_at       timestamp with time zone not null,
    updated_at       timestamp with time zone
);

alter table simulation
    add constraint simulation_pk primary key (task_id, dashboard_id),
    add constraint simulation_task_id_fk foreign key (task_id) references task (id) on delete cascade,
    add constraint simulation_dashboard_id_fk foreign key (dashboard_id) references dashboard (id) on delete cascade;
