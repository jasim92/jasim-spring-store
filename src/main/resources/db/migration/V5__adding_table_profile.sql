CREATE TABLE profiles
(
    id             BIGINT AUTO_INCREMENT NOT NULL,
    bio            VARCHAR(255)          NULL,
    phone_number   VARCHAR(255)          NULL,
    date_of_birth  date                  NULL,
    loyalty_points INT                   NULL,
    CONSTRAINT pk_profiles PRIMARY KEY (id)
);