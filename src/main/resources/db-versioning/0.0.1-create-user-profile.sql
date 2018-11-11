create table user_profile
(
  user_id                bigint  not null
    constraint user_profile_pkey
    primary key,
  blood_type             integer not null,
  days_between_reminders integer not null,
  email                  varchar(255)
    constraint uk_user_profile
    unique,
  last_donation          date,
  name                   varchar(255),
  next_reminder          date,
  user_status            integer not null
);

alter table user_profile
  owner to donor;