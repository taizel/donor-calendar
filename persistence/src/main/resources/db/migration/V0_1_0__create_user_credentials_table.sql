create table user_credentials
(
    user_id  bigint       not null
        constraint user_credentials_pk
            primary key
        constraint user_id__fk
            references user_profile,
    password varchar(255) not null
);

