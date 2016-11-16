/* schema.sql
 * Builds the schema in the DB.
 */
CREATE TABLE users (
  userid NUMBER(15),
  email VARCHAR2(20),
  name VARCHAR2(20),
  passwd VARCHAR2(10),
  phone NUMBER(10),
  screenname VARCHAR2(20),
  is_manager SMALLINT /* 0=no 1 =yes */,
  PRIMARY KEY (userid),
  UNIQUE (email)
);
CREATE SEQUENCE userid_seq START WITH 1;
CREATE TRIGGER userid_trig
  BEFORE INSERT ON users
  FOR EACH ROW
  BEGIN
    SELECT userid_seq.nextval
      INTO :NEW.userid
      FROM dual;
  END;
/
CREATE TABLE friends (
  u1 NUMBER(15),
  u2 NUMBER(15),
  PRIMARY KEY (u1, u2),
  FOREIGN KEY (u1) REFERENCES users(userid) ON DELETE CASCADE,
  FOREIGN KEY (u2) REFERENCES users(userid) ON DELETE CASCADE
);
CREATE TRIGGER friends_sym_trig
BEFORE INSERT OR UPDATE
  ON friends
FOR EACH ROW
  DECLARE
    num NUMBER;
  BEGIN
    SELECT COUNT(*) INTO num FROM friends WHERE u2 = :NEW.u1 AND u1 = :NEW.u2;
    IF num > 0
    THEN
      RAISE_APPLICATION_ERROR( -20001, 'A symmetric friendship already exists.' );
    END IF;
  END;
/
CREATE TABLE topics (
  tid NUMBER(15),
  label VARCHAR2(30),
  PRIMARY KEY (tid),
  UNIQUE(label)
);
CREATE SEQUENCE tid_seq START WITH 1;
CREATE TRIGGER tid_trig
BEFORE INSERT ON topics
FOR EACH ROW
  BEGIN
    SELECT tid_seq.nextval
    INTO :NEW.tid
    FROM dual;
  END;
/
CREATE TABLE user_topics (
  userid NUMBER(15),
  tid NUMBER(15),
  PRIMARY KEY (userid, tid),
  FOREIGN KEY (userid) REFERENCES users ON DELETE CASCADE,
  FOREIGN KEY (tid) REFERENCES topics ON DELETE CASCADE
);
CREATE TABLE messages (
  mid NUMBER(15),
  msg VARCHAR2(1400),
  msg_timestamp NUMBER(15), /* utc in milliseconds since epoch */
  sender NUMBER(15) NOT NULL,
  is_deleted SMALLINT /* 0=no 1 =yes */,
  PRIMARY KEY (mid),
  FOREIGN KEY (sender) REFERENCES users(userid) ON DELETE CASCADE
);
CREATE SEQUENCE mid_seq START WITH 1;
CREATE TRIGGER mid_trig
BEFORE INSERT ON messages
FOR EACH ROW
  BEGIN
    SELECT mid_seq.nextval
    INTO :NEW.mid
    FROM dual;
  END;
/
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
  recipient NUMBER(15),
  mid NUMBER(15),
  PRIMARY KEY (recipient, mid),
  FOREIGN KEY (recipient) REFERENCES users(userid) ON DELETE CASCADE,
  FOREIGN KEY (mid) REFERENCES mc_messages ON DELETE CASCADE
);
CREATE TABLE mc_msg_topics (
  mid NUMBER(15),
  tid NUMBER(15),
  PRIMARY KEY (mid, tid),
  FOREIGN KEY (mid) REFERENCES mc_messages ON DELETE CASCADE,
  FOREIGN KEY (tid) REFERENCES topics ON DELETE CASCADE
);
CREATE TABLE chat_groups (
  cgid NUMBER(15),
  name VARCHAR2(20),
  duration NUMBER(3),
  owner NUMBER(15) NOT NULL,
  PRIMARY KEY (cgid),
  FOREIGN KEY (owner) REFERENCES users(userid) ON DELETE SET NULL
);
CREATE SEQUENCE cgid_seq START WITH 1;
CREATE TRIGGER cgid_trig
BEFORE INSERT ON chat_groups
FOR EACH ROW
  BEGIN
    SELECT cgid_seq.nextval
    INTO :NEW.cgid
    FROM dual;
  END;
/
CREATE TABLE chat_group_members (
  userid NUMBER(15),
  cgid NUMBER(15), /* chat group id */
  PRIMARY KEY (userid, cgid),
  FOREIGN KEY (userid) REFERENCES users ON DELETE CASCADE,
  FOREIGN KEY (cgid) REFERENCES chat_groups ON DELETE CASCADE
);
CREATE TABLE chat_group_invites (
  mid NUMBER(15),
  cgid NUMBER(15) NOT NULL,
  recipient NUMBER(15) NOT NULL,
  PRIMARY KEY (mid),
  FOREIGN KEY (mid) REFERENCES messages ON DELETE CASCADE,
  FOREIGN KEY (cgid) REFERENCES chat_groups ON DELETE CASCADE,
  FOREIGN KEY (recipient) REFERENCES users(userid) ON DELETE CASCADE
);
CREATE TABLE chat_group_messages (
  mid NUMBER(15),
  cgid NUMBER(15) NOT NULL,
  PRIMARY KEY (mid),
  FOREIGN KEY (mid) REFERENCES messages ON DELETE CASCADE,
  FOREIGN KEY (cgid) REFERENCES chat_groups ON DELETE CASCADE
);
CREATE TABLE friend_requests (
  mid NUMBER(15),
  recipient NUMBER(15) NOT NULL,
  PRIMARY KEY (mid),
  FOREIGN KEY (mid) REFERENCES messages ON DELETE CASCADE,
  FOREIGN KEY (recipient) REFERENCES users(userid) ON DELETE CASCADE
);
CREATE TABLE private_messages (
  mid NUMBER(15),
  recipient NUMBER(15) NOT NULL,
  del_by_recipient SMALLINT, /* 0=no 1 =yes */
  PRIMARY KEY (mid),
  FOREIGN KEY (mid) REFERENCES messages ON DELETE CASCADE,
  FOREIGN KEY (recipient) REFERENCES users(userid) ON DELETE CASCADE
);
