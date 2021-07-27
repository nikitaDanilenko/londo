alter table plain_task
    add constraint reachable_positive check (reachable is null or reachable > 0),
    drop constraint plain_task_weight_non_negative,
    add constraint plain_task_weight_positive check (weight > 0);

alter table project_reference_task
    drop constraint project_reference_task_weight_non_negative,
    add constraint project_reference_task_weight_positive check (weight > 0);

alter table dashboard_project_association
    drop constraint dashboard_project_association_weight_non_negative,
    add constraint dashboard_project_association_weight_positive check (weight > 0);