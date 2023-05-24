alter table dashboard
    drop constraint dashboard_owner_id_fk,
    add constraint dashboard_owner_id_fk foreign key (owner_id) references "user"(id) on delete cascade;