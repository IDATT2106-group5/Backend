create table item
(
    id             int auto_increment
        primary key,
    name           varchar(255)                                           not null,
    item_type      enum ('LIQUIDS', 'FOOD', 'FIRST AID', 'TOOL', 'OTHER') not null,
    caloric_amount int                                                    null,
    constraint name
        unique (name)
);

create table map_icon
(
    id            int auto_increment
        primary key,
    type          enum ('INCIDENT', 'MEETINGPLACE', 'HOSPITAL', 'HEARTSTARTER', 'SHELTER', 'FOODSTATION') null,
    address       varchar(255)                                                                            null,
    latitude      double                                                                                  not null,
    longitude     double                                                                                  not null,
    description   varchar(1000)                                                                           null,
    opening_hours varchar(255)                                                                            null,
    contact_info  varchar(255)                                                                            null
);

create table news
(
    id         int auto_increment
        primary key,
    title      varchar(255)             not null,
    url        varchar(255)             null,
    content    text                     null,
    source     varchar(255)             not null,
    created_at datetime default (now()) null
);

create table scenario
(
    id           int auto_increment
        primary key,
    name         varchar(255) not null,
    description  text         null,
    to_do        text         null,
    packing_list text         null,
    icon_name    varchar(255) null
);

create table incident
(
    id            int auto_increment
        primary key,
    name          varchar(255)                    not null,
    description   text                            not null,
    latitude      double                          not null,
    longitude     double                          not null,
    impact_radius double                          not null,
    severity      enum ('GREEN', 'YELLOW', 'RED') not null,
    started_at    timestamp                       not null,
    ended_at      timestamp                       null,
    scenario_id   int                             null,
    constraint incident_ibfk_1
        foreign key (scenario_id) references scenario (id)
);

create table storage
(
    id              int auto_increment
        primary key,
    item_id         int                    not null,
    household_id    char(36) charset ascii null,
    unit            varchar(100)           not null,
    amount          int                    not null,
    expiration_date date                   not null,
    date_added      datetime               null,
    constraint storage_ibfk_1
        foreign key (item_id) references item (id)
            on update cascade on delete cascade
);

create index household_id
    on storage (household_id);

create index item_id
    on storage (item_id);

create table unregistered_household_member
(
    id           int auto_increment
        primary key,
    full_name    varchar(255)           not null,
    household_id char(36) charset ascii null
);

create index household_id
    on unregistered_household_member (household_id);

create table user
(
    id                              char(36) charset ascii               not null
        primary key,
    email                           varchar(255)                         not null,
    password                        char(60)                             not null,
    role                            enum ('USER', 'ADMIN', 'SUPERADMIN') not null,
    full_name                       varchar(255)                         not null,
    household_id                    char(36) charset ascii               null,
    tlf                             varchar(20)                          null,
    confirmed                       tinyint(1) default 0                 not null,
    confirmation_token              varchar(255)                         null,
    token_expiry                    datetime                             null,
    address                         varchar(255)                         null,
    longitude                       mediumtext                           null,
    latitude                        mediumtext                           null,
    reset_password_token            varchar(255)                         null,
    reset_password_token_expiration datetime                             null,
    constraint confirmation_token
        unique (confirmation_token),
    constraint email
        unique (email)
);

create table household
(
    id                char(36) charset ascii not null
        primary key,
    name              varchar(255)           not null,
    address           varchar(255)           not null,
    number_of_members int                    null,
    owner_id          char(36) charset ascii null,
    constraint household_ibfk_1
        foreign key (owner_id) references user (id)
);

create index owner_id
    on household (owner_id);

create table membership_request
(
    id           int auto_increment
        primary key,
    household_id char(36) charset ascii                               null,
    sender_id    char(36) charset ascii                               null,
    receiver_id  char(36) charset ascii                               null,
    type         enum ('JOIN_REQUEST', 'INVITATION')                  not null,
    status       enum ('PENDING', 'ACCEPTED', 'REJECTED', 'CANCELED') not null,
    created_at   timestamp default CURRENT_TIMESTAMP                  null,
    constraint membership_request_ibfk_2
        foreign key (sender_id) references user (id),
    constraint membership_request_ibfk_3
        foreign key (receiver_id) references user (id)
);

create index household_id
    on membership_request (household_id);

create index receiver_id
    on membership_request (receiver_id);

create index sender_id
    on membership_request (sender_id);

create table notification
(
    id        int auto_increment
        primary key,
    user_id   char(36) charset ascii                                                        null,
    type      enum ('INCIDENT', 'STOCK_CONTROL', 'INFO', 'MEMBERSHIP_REQUEST', 'HOUSEHOLD') not null,
    is_read   tinyint(1) default 0                                                          null,
    timestamp timestamp  default CURRENT_TIMESTAMP                                          null,
    message   varchar(255)                                                                  not null,
    constraint notification_ibfk_1
        foreign key (user_id) references user (id)
);

create index user_id
    on notification (user_id);

create table prep_group
(
    id       int auto_increment
        primary key,
    name     varchar(255)           not null,
    owner_id char(36) charset ascii null,
    constraint prep_group_user_id_fk
        foreign key (owner_id) references user (id)
);

create table prep_group_household
(
    id            int auto_increment
        primary key,
    prep_group_id int                    not null,
    household_id  char(36) charset ascii null,
    constraint prep_group_household_prep_group_id_fk
        foreign key (prep_group_id) references prep_group (id)
);

create index prep_group_household_household_id_fk
    on prep_group_household (household_id);

create table prep_group_storage
(
    id            int auto_increment
        primary key,
    prep_group_id int not null,
    storage_id    int not null,
    constraint prep_group_storage_prep_group_id_fk
        foreign key (prep_group_id) references prep_group (id),
    constraint prep_group_storage_storage_id_fk
        foreign key (storage_id) references storage (id)
);

create index fk_user_household
    on user (household_id);

