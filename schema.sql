CREATE TABLE users (
  email VARCHAR2(20),
  name VARCHAR2(20),
  passwd VARCHAR2(10),
  phone NUMBER(10),
  screenname VARCHAR2(20),
  is_manager SMALLINT /* 0=no 1 =yes */,
  PRIMARY KEY (email)
);

CREATE TABLE friends (
  u1 VARCHAR2(20),
  u2 VARCHAR2(20),
  PRIMARY KEY (u1, u2),
  FOREIGN KEY (u1) REFERENCES users(email) ON DELETE CASCADE,
  FOREIGN KEY (u2) REFERENCES users(email) ON DELETE CASCADE
);
CREATE TABLE topics (
  label VARCHAR2(30),
  PRIMARY KEY (label)
);
CREATE TABLE user_topics (
  email VARCHAR2(20),
  topic VARCHAR2(30),
  PRIMARY KEY (email, topic),
  FOREIGN KEY (email) REFERENCES users ON DELETE CASCADE,
  FOREIGN KEY (topic) REFERENCES topics ON DELETE CASCADE
);
CREATE TABLE messages (
  mid NUMBER(15),
  msg VARCHAR2(1400),
  msg_timestamp TIMESTAMP,
  sender VARCHAR2(20) NOT NULL,
  is_deleted SMALLINT /* 0=no 1 =yes */,
  PRIMARY KEY (mid),
  FOREIGN KEY (sender) REFERENCES users(email) ON DELETE CASCADE
);
CREATE TABLE mc_messages (
  mid NUMBER(15),
  is_public SMALLINT,
  is_broadcast SMALLINT, /* Is message sent to all friends (including future ones). */
  read_count NUMBER(10),
  PRIMARY KEY (mid),
  FOREIGN KEY (mid) REFERENCES messages ON DELETE CASCADE
);
/* MyCircle messages sent to specific friends. */
CREATE TABLE  mc_msg_recipients (
  recipient VARCHAR2(20),
  mid NUMBER(15),
  PRIMARY KEY (recipient, mid),
  FOREIGN KEY (recipient) REFERENCES users(email) ON DELETE CASCADE,
  FOREIGN KEY (mid) REFERENCES mc_messages ON DELETE CASCADE
);
CREATE TABLE mc_msg_topics (
  mid NUMBER(15),
  topic VARCHAR2(20),
  PRIMARY KEY (mid, topic),
  FOREIGN KEY (mid) REFERENCES mc_messages ON DELETE CASCADE,
  FOREIGN KEY (topic) REFERENCES topics ON DELETE CASCADE
);
CREATE TABLE chat_groups (
  name VARCHAR2(20),
  duration NUMBER(3),
  owner VARCHAR2(20) NOT NULL,
  PRIMARY KEY (name),
  FOREIGN KEY (owner) REFERENCES users(email) ON DELETE SET NULL
);
CREATE TABLE chat_group_members (
  email VARCHAR2(20),
  name VARCHAR2(20), /* chat group name */
  PRIMARY KEY (email, name),
  FOREIGN KEY (email) REFERENCES users ON DELETE CASCADE,
  FOREIGN KEY (name) REFERENCES chat_groups ON DELETE CASCADE
);
CREATE TABLE chat_group_invites (
  mid NUMBER(15),
  group_name VARCHAR2(20) NOT NULL,
  recipient VARCHAR2(20) NOT NULL,
  PRIMARY KEY (mid),
  FOREIGN KEY (mid) REFERENCES messages ON DELETE CASCADE,
  FOREIGN KEY (group_name) REFERENCES chat_groups(name) ON DELETE CASCADE,
  FOREIGN KEY (recipient) REFERENCES users(email) ON DELETE CASCADE
);
CREATE TABLE chat_group_messages (
  mid NUMBER(15),
  group_name VARCHAR2(20) NOT NULL,
  PRIMARY KEY (mid),
  FOREIGN KEY (mid) REFERENCES messages,
  FOREIGN KEY (group_name) REFERENCES chat_groups(name) ON DELETE CASCADE
);
CREATE TABLE friend_requests (
  mid NUMBER(15),
  recipient VARCHAR2(20) NOT NULL,
  PRIMARY KEY (mid),
  FOREIGN KEY (mid) REFERENCES messages ON DELETE CASCADE,
  FOREIGN KEY (recipient) REFERENCES users(email) ON DELETE CASCADE
);
CREATE TABLE private_messages (
  mid NUMBER(15),
  recipient VARCHAR2(20) NOT NULL,
  del_by_recipient SMALLINT, /* 0=no 1 =yes */
  PRIMARY KEY (mid),
  FOREIGN KEY (mid) REFERENCES messages ON DELETE CASCADE,
  FOREIGN KEY (recipient) REFERENCES users(email) ON DELETE CASCADE
);
