CREATE TABLE IF NOT EXISTS `{users-table}`
(
    id         INT(11) AUTO_INCREMENT NOT NULL,
    uuid       VARCHAR(48) UNIQUE     NOT NULL,
    username   VARCHAR(32)            NOT NULL,
    ip         VARCHAR(32)            NOT NULL,
    language   VARCHAR(24)            NOT NULL,
    firstlogin DATETIME               NOT NULL,
    lastlogout DATETIME               NOT NULL,
    PRIMARY KEY (id)
)
    ENGINE = InnoDB
    AUTO_INCREMENT = 1
    DEFAULT CHARSET = UTF8MB4;

CREATE INDEX idx_users ON `{users-table}` (id, uuid, username, ip);

CREATE TABLE IF NOT EXISTS `{ignoredusers-table}`
(
    user    INT(11) NOT NULL,
    ignored INT(11) NOT NULL,
    PRIMARY KEY (user, ignored),
    FOREIGN KEY (user) REFERENCES `{users-table}` (id),
    FOREIGN KEY (ignored) REFERENCES `{users-table}` (id)
)
    ENGINE = InnoDB
    AUTO_INCREMENT = 1
    DEFAULT CHARSET = UTF8MB4;

CREATE TABLE IF NOT EXISTS `{friendsettings-table}`
(
    user     VARCHAR(48) NOT NULL,
    requests TINYINT(1)  NOT NULL,
    messages TINYINT(1)  NOT NULL,
    PRIMARY KEY (user)
)
    ENGINE = InnoDB
    AUTO_INCREMENT = 1
    DEFAULT CHARSET = UTF8MB4;

CREATE INDEX idx_friendset ON `{friendsettings-table}` (user, requests, messages);

CREATE TABLE IF NOT EXISTS `{friends-table}`
(
    user    VARCHAR(48)                        NOT NULL,
    friend  VARCHAR(48)                        NOT NULL,
    created DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    PRIMARY KEY (user, friend)
)
    ENGINE = InnoDB
    AUTO_INCREMENT = 1
    DEFAULT CHARSET = UTF8MB4;

CREATE INDEX idx_friends ON `{friends-table}` (user, friend);

CREATE TABLE IF NOT EXISTS `{friendrequests-table}`
(
    user         VARCHAR(48)                        NOT NULL,
    friend       VARCHAR(48)                        NOT NULL,
    requested_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    PRIMARY KEY (user, friend),
    KEY idx_friendreq (user, friend)
)
    ENGINE = InnoDB
    AUTO_INCREMENT = 1
    DEFAULT CHARSET = UTF8MB4;

CREATE INDEX idx_friendreq ON `{friendrequests-table}` (user, friend);

CREATE TABLE IF NOT EXISTS `{bans-table}`
(
    id          INT(11) AUTO_INCREMENT NOT NULL,
    uuid        VARCHAR(48)            NOT NULL,
    user        VARCHAR(32)            NOT NULL,
    ip          VARCHAR(32)            NOT NULL,
    reason      TEXT                   NOT NULL,
    server      VARCHAR(32)            NOT NULL,
    date        DATETIME               NOT NULL DEFAULT CURRENT_TIMESTAMP,
    active      TINYINT(1)             NOT NULL,
    executed_by VARCHAR(64)            NOT NULL,
    duration    LONG                   NOT NULL,
    type        VARCHAR(16)            NOT NULL,
    removed     TINYINT(1)             NOT NULL DEFAULT 0,
    removed_by  VARCHAR(32),
    PRIMARY KEY (id),
    FOREIGN KEY (uuid) REFERENCES `{users-table}` (uuid)
)
    ENGINE = InnoDB
    AUTO_INCREMENT = 1
    DEFAULT CHARSET = UTF8MB4;

CREATE INDEX idx_bans ON `{bans-table}` (id, uuid, user, ip, active, server);

CREATE TABLE IF NOT EXISTS `{mutes-table}`
(
    id          INT(11) AUTO_INCREMENT NOT NULL,
    uuid        VARCHAR(48)            NOT NULL,
    user        VARCHAR(32)            NOT NULL,
    ip          VARCHAR(32)            NOT NULL,
    reason      TEXT                   NOT NULL,
    server      VARCHAR(32)            NOT NULL,
    date        DATETIME               NOT NULL DEFAULT CURRENT_TIMESTAMP,
    active      TINYINT(1)             NOT NULL,
    executed_by VARCHAR(64)            NOT NULL,
    duration    LONG                   NOT NULL,
    type        VARCHAR(16)            NOT NULL,
    removed     TINYINT(1)             NOT NULL DEFAULT 0,
    removed_by  VARCHAR(32),
    PRIMARY KEY (id),
    FOREIGN KEY (uuid) REFERENCES `{users-table}` (uuid)
)
    ENGINE = InnoDB
    AUTO_INCREMENT = 1
    DEFAULT CHARSET = UTF8MB4;

CREATE INDEX idx_mutes ON `{mutes-table}` (id, uuid, user, ip, active, server);


CREATE TABLE IF NOT EXISTS `{kicks-table}`
(
    id          INT(11) AUTO_INCREMENT             NOT NULL,
    uuid        VARCHAR(36)                        NOT NULL,
    user        VARCHAR(32)                        NOT NULL,
    ip          VARCHAR(32)                        NOT NULL,
    reason      TEXT                               NOT NULL,
    server      VARCHAR(32)                        NOT NULL,
    date        DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    executed_by VARCHAR(64)                        NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (uuid) REFERENCES `{users-table}` (uuid)
)
    ENGINE = InnoDB
    AUTO_INCREMENT = 1
    DEFAULT CHARSET = UTF8MB4;

CREATE INDEX idx_kicks ON `{kicks-table}` (id, uuid, user, ip);

CREATE TABLE IF NOT EXISTS `{warns-table}`
(
    id          INT(11) AUTO_INCREMENT             NOT NULL,
    uuid        VARCHAR(36)                        NOT NULL,
    user        VARCHAR(32)                        NOT NULL,
    ip          VARCHAR(32)                        NOT NULL,
    reason      TEXT                               NOT NULL,
    server      VARCHAR(32)                        NOT NULL,
    date        DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    executed_by VARCHAR(64)                        NOT NULL,
    PRIMARY KEY (id),
    KEY idx_warns (id, uuid, user, ip),
    FOREIGN KEY (uuid) REFERENCES `{users-table}` (uuid)
)
    ENGINE = InnoDB
    AUTO_INCREMENT = 1
    DEFAULT CHARSET = UTF8MB4;

CREATE INDEX idx_warns ON `{warns-table}` (id, uuid, user, ip);

CREATE TABLE IF NOT EXISTS `{punishmentactions-table}`
(
    id       INT(11) AUTO_INCREMENT             NOT NULL,
    uuid     VARCHAR(36)                        NOT NULL,
    user     VARCHAR(32)                        NOT NULL,
    ip       VARCHAR(32)                        NOT NULL,
    actionid VARCHAR(36)                        NOT NULL,
    date     DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (uuid) REFERENCES `{users-table}` (uuid)
)
    ENGINE = InnoDB
    AUTO_INCREMENT = 1
    DEFAULT CHARSET = UTF8MB4;

CREATE INDEX idx_punishactions ON `{punishmentactions-table}` (id, uuid, user, ip, actionid);