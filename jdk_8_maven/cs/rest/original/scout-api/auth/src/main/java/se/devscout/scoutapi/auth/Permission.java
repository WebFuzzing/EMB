package se.devscout.scoutapi.auth;

import se.devscout.scoutapi.model.User;

public enum Permission {
    // Permissions for all users (level <0):

    system_message_read(-100),

    // Permissions for regular users (level 0):

    comment_create(0),

    activity_create(0),
    activity_edit_own(0),
    comment_edit_own(0),
    auth_profile_edit(0),

    // Permission to set personal rating for any activity. NOT the same as changing other users' ratings.
    rating_set_own(0),

    // Create media items, e.g. by uploading a photo, associated with activities created by same user
    mediaitem_edit_own(0),

    // Create media items, e.g. by uploading a photo, associated with activities created by same user
    reference_edit_own(0),

    // Permissions for moderators (level 10):

    activity_edit(10),
    comment_edit(10),
    category_create(10),
    category_edit(10),

    // Create media items, e.g. by uploading a photo, associated with any activity
    mediaitem_create(10),
    mediaitem_edit(10),
    reference_create(10),
    reference_edit(10),

    // Assign user's role (or lesser) to any user with a role lesser than the user.
    auth_role_assignown(10),

    // Permissions for administrators (level 20):

    system_message_manage(20),
    auth_role_assign(20),
    auth_role_list(20),
    auth_user_create(20),
    auth_user_edit(20);

    private int level;

    Permission(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public boolean isGrantedTo(User user) {
        return level <= user.getAuthorizationLevel();
    }
}
