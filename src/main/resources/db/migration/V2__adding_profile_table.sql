create table profiles
(
    id              bigint primary key,
    bio             TEXT          ,
    phone_number    varchar(255)  ,
    date_of_birth   DATE          ,
    loyality_points int default 0 ,
    constraint profiles_users_id_fk
        foreign key (id) references users (id)
);