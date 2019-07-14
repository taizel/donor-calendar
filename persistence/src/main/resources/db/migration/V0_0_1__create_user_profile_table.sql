create table user_profile
(
  user_id                bigint  not null
    constraint user_profile_pk
    primary key,
  blood_type             varchar(3) not null,
  days_between_reminders integer,
  email                  varchar(255) not null
    constraint uk_user_profile
    unique,
  last_donation          date,
  name                   varchar(255) not null,
  next_reminder          date,
  user_status            integer not null
);
