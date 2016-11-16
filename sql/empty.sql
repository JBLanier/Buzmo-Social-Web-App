/* Leaves the schema in place, but empties the DB of content. */

/* REMOVE ALL RECORDS */
/* Delete all users. */
DELETE FROM Users;
/* Delete all topics. */
DELETE FROM Topics;
/* Delete all chat groups. */
DELETE FROM chat_groups;

