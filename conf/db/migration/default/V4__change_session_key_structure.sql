alter table session_key
    drop constraint session_key_pk;

alter table session_key
    drop column public_key;

delete from session_key;

alter table session_key
    add column session_id uuid not null;

alter table session_key
    add constraint session_key_pk primary key (user_id, session_id);