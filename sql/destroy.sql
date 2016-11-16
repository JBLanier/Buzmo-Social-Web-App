
/* REMOVE ALL RECORDS */
/* Delete all users. */
DELETE FROM Users;
/* Delete all topics. */
DELETE FROM Topics;
/* Delete all chat groups. */
DELETE FROM chat_groups;

/* DESTROY SCHEMA */

DROP TABLE private_messages;
DROP TABLE friend_requests;
DROP TABLE chat_group_messages;
DROP TABLE chat_group_invites;
DROP TABLE chat_group_members;
DROP TABLE chat_groups;
DROP SEQUENCE cgid_seq;

DROP TABLE mc_msg_topics;
DROP TABLE mc_msg_recipients;
DROP TABLE mc_messages;
DROP TABLE messages;
DROP SEQUENCE mid_seq;

DROP TABLE user_topics;
DROP TABLE topics;
DROP SEQUENCE tid_seq;

DROP TABLE friends;

DROP TABLE users;
DROP SEQUENCE userid_seq;
