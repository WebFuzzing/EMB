INSERT INTO CATEGORY (LABEL, DTYPE) VALUES ('group1', 'Group');

INSERT INTO ROLE (AUTHORITY) VALUES ('ROLE_USER');
INSERT INTO ROLE (AUTHORITY) VALUES ('ROLE_ADMIN');
INSERT INTO ROLE (AUTHORITY) VALUES ('ROLE_EDITOR');
INSERT INTO ROLE (AUTHORITY) VALUES ('ROLE_VALIDATOR');
INSERT INTO ROLE (AUTHORITY) VALUES ('ROLE_PROCURING_ENTITY');


INSERT INTO PERSON
(
    CHANGE_PASSWORD,
    COUNTRY,
    EMAIL,
    ENABLED,
    FIRST_NAME,
    LAST_NAME,
    PASSWORD,
    TITLE,
    USERNAME,
    GROUP_ID
)
VALUES
    (
        false,
        NULL,
        NULL,
        true,
        NULL,
        NULL,
        '61f3d99fb8ddfa54b714e84a0238e575930fda10e09dbd030b92edf1cf77c4d947e506e26ae9bf33',
        NULL,
        'admin',
        NULL
    );
-- Adding a new user called user1 with password "password". User details are stored in the database.
INSERT INTO PERSON
(
    CHANGE_PASSWORD,
    COUNTRY,
    EMAIL,
    ENABLED,
    FIRST_NAME,
    LAST_NAME,
    PASSWORD,
    TITLE,
    USERNAME,
    GROUP_ID
)
VALUES
    (
        false,
        NULL,
        NULL,
        true,
        NULL,
        NULL,
        'b65d4278fb536a6e1ea23c87988f5d79ef743ccb07b2067ad8ebd52f1a0c067e5bc3b7f76eca3abc',
        NULL,
        'user1',
        NULL
    );

INSERT INTO PERSON_ROLES(PERSON_ID,ROLES_ID)VALUES(1,2);
INSERT INTO PERSON_ROLES(PERSON_ID,ROLES_ID)VALUES(1,1);
INSERT INTO PERSON_ROLES(PERSON_ID,ROLES_ID)VALUES(1,3);

-- user1 has user privilege only
INSERT INTO PERSON_ROLES(PERSON_ID,ROLES_ID)VALUES(2,1);