USE spring_restful_api;


USE spring_restful_api;
CREATE TABLE users (
    username VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    name VARCHAR(100) NOT NULL,
    token VARCHAR(100),
    token_expired_at BIGINT,
    PRIMARY KEY (username),
    UNIQUE (token)
) ENGINE innoDB;

SELECT users;

DESC users;


CREATE TABLE contact (
    id VARCHAR(100) NOT NULL,
    username VARCHAR(100) NOT NULL,
    first_name VARCHAR (100) NOt NULL,
    last_name VARCHAR (100) NOT NULL,
    phone VARCHAR(100),
    email VARCHAR(100),
    PRIMARY KEY (id),
    FOREIGN KEY fk_users_contacts (username) REFERENCES users(username)
) ENGINE innoDB;


SELECT * FROM contact;

CREATE TABLE addresses
(
    id          VARCHAR(100) NOT NULL,
    contact_id  VARCHAR(100) NOT NULL,
    street      VARCHAR(200),
    city        VARCHAR(100),
    province    VARCHAR(100),
    country     VARCHAR(100) NOT NULL,
    postal_code VARCHAR(10),
    PRIMARY KEY (id),
    FOREIGN KEY fk_contacts_addresses (contact_id) REFERENCES contact (id)
) ENGINE InnoDB;

SELECT * FROM addresses;

DESC addresses;

RENAME TABLE contact TO contacts;
