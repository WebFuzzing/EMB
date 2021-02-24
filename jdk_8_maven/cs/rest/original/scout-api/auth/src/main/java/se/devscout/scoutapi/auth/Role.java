package se.devscout.scoutapi.auth;

public enum Role {
    limited_user(-1),
    user(0),
    moderator(10),
    administrator(20);

    private int level;

    Role(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

}
