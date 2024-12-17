CREATE TABLE IF NOT EXISTS bu_users
(
    id          DATA_TYPE_SERIAL,
    uuid        DATA_TYPE_VARCHAR UNIQUE NOT NULL,
    username    DATA_TYPE_VARCHAR        NOT NULL,
    ip          DATA_TYPE_VARCHAR        NOT NULL,
    language    DATA_TYPE_VARCHAR        NOT NULL,
    firstlogin  DATA_TYPE_DATETIME       NOT NULL,
    lastlogout  DATA_TYPE_DATETIME       NOT NULL,
    joined_host TEXT,
    PRIMARY KEY (id)
);

CREATE INDEX idx_users_u ON bu_users (uuid);
CREATE INDEX idx_users_un ON bu_users (username);
CREATE INDEX idx_users_i ON bu_users (ip);

CREATE TABLE IF NOT EXISTS bu_ignoredusers
(
    `user`  DATA_TYPE_VARCHAR NOT NULL,
    ignored DATA_TYPE_VARCHAR NOT NULL,
    PRIMARY KEY (`user`, `ignored`)
);

CREATE TABLE IF NOT EXISTS bu_messagequeue
(
    id      DATA_TYPE_SERIAL,
    `user`  DATA_TYPE_VARCHAR  NOT NULL,
    message TEXT               NOT NULL,
    date    DATA_TYPE_DATETIME NOT NULL,
    type    DATA_TYPE_VARCHAR  NOT NULL,
    active  DATA_TYPE_BOOLEAN  NOT NULL,
    PRIMARY KEY (id)
);

CREATE INDEX idx_messagequeue_u ON bu_messagequeue (`user`, type);