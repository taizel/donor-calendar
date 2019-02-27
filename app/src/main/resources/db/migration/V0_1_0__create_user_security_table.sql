create table user_security
(
  user_id  bigint       not null
    constraint user_security_pkey
    primary key
    constraint user_id__fk
    references user_profile,
  password varchar(255) not null
);

alter table user_security
  owner to donor;