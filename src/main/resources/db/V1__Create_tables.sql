CREATE TABLE user (
    id INT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL UNIQUE,
    password CHAR(60) NOT NULL,
    role ENUM('USER', 'ADMIN', 'SUPERADMIN') NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    household_id INT
);

CREATE TABLE incident (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    impact_radius DOUBLE NOT NULL,
    severity INT NOT NULL,
    started_at TIMESTAMP NOT NULL,
    ended_at TIMESTAMP,
    created_by INT NOT NULL,
    FOREIGN KEY (created_by) REFERENCES user(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE household (
                           id INT PRIMARY KEY AUTO_INCREMENT,
                           name VARCHAR(255) NOT NULL UNIQUE,
                           address VARCHAR(255) NOT NULL,
                           number_of_members INT,
                           owner_id INT NOT NULL,
                           FOREIGN KEY (owner_id) REFERENCES user(id)
                               ON DELETE CASCADE
                               ON UPDATE CASCADE
);

CREATE TABLE unregistered_household_member (
                                               id INT PRIMARY KEY AUTO_INCREMENT,
                                               full_name VARCHAR(255) NOT NULL,
                                               household_id INT NOT NULL,
                                               FOREIGN KEY (household_id) REFERENCES household(id)
                                                   ON DELETE CASCADE
                                                   ON UPDATE CASCADE
);

CREATE TABLE item (
                      id INT PRIMARY KEY AUTO_INCREMENT,
                      name VARCHAR(255) NOT NULL UNIQUE,
                      item_type ENUM('LIQUIDS', 'FOOD', 'FIRST AID', 'TOOL', 'OTHER') NOT NULL,
                      caloric_amount INT
);

CREATE TABLE storage (
                         id INT PRIMARY KEY AUTO_INCREMENT,
                         item_id INT NOT NULL,
                         household_id INT NOT NULL,
                         unit VARCHAR(100) NOT NULL,
                         amount INT NOT NULL,
                         expiration_date DATE NOT NULL,
                         FOREIGN KEY (item_id) REFERENCES item(id)
                             ON DELETE CASCADE
                             ON UPDATE CASCADE,
                         FOREIGN KEY (household_id) REFERENCES household(id)
                             ON DELETE CASCADE
                             ON UPDATE CASCADE
);

CREATE TABLE membership_request (
    id INT PRIMARY KEY AUTO_INCREMENT,
    household_id INT NOT NULL,
    sender_id INT NOT NULL,
    receiver_id INT NOT NULL,
    type ENUM('JOIN_REQUEST', 'ACCEPTED', 'REJECTED') NOT NULL,
    status ENUM('PENDING', 'ACCEPTED', 'REJECTED') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (household_id) REFERENCES household(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (sender_id) REFERENCES user(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (receiver_id) REFERENCES user(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE notification (
    id int PRIMARY KEY AUTO_INCREMENT,
    user_id int NOT NULL,
    type ENUM('MEMBERSHIP_REQUEST', 'INCIDENT'),
    is_read BOOLEAN DEFAULT false,
    FOREIGN KEY (user_id) REFERENCES user(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE map_icon (
    id INT PRIMARY KEY AUTO_INCREMENT,
    type ENUM('INCIDENT', 'INFORMATION'),
    address VARCHAR(255),
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL
);

ALTER TABLE user
    ADD CONSTRAINT fk_user_household
        FOREIGN KEY (household_id) REFERENCES household(id)
            ON DELETE SET NULL
            ON UPDATE CASCADE;