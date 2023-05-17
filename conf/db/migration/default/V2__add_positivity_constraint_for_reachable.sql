alter table task
    add constraint reachable_positive check ( reachable > 0 );