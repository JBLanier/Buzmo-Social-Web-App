package edu.ucsb.engineering.buzmo.daos;

public class AlreadyRequest extends Exception {
    @Override
    public String getMessage() {
        return "A friend request already exists.";
    }
}
