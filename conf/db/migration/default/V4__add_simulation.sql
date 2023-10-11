create table simulation
(
    task_id          uuid not null,
    project_id       uuid not null,
    dashboard_id     uuid not null,
    reached_modifier int  not null
);

alter table simulation
    add constraint simulation_pk primary key (task_id, dashboard_id),
    add constraint simulation_project_id_fk foreign key (project_id) references project(id) on delete cascade,
    add constraint simulation_task_id_fk foreign key (task_id, project_id) references task (id, project_id) on delete cascade,
    add constraint simulation_dashboard_id_fk foreign key (dashboard_id) references dashboard (id) on delete cascade;
