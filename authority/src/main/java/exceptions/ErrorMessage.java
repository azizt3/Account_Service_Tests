package exceptions;

public class ErrorMessage {
    public static final String BLANK_FIELD = "Name cannot be blank!";
    public static final String USER_EXISTS = "User exist!";
    public static final String PASSWORD_TOO_SHORT = "Password length must be 12 chars minimum!";
    public static final String BREACHED_PASSWORD = "The password is in the hacker's database!";
    public static final String USER_NOT_FOUND = "User not found!";
    public static final String ROLE_NOT_FOUND = "Role not found!";
    public static final String CONFLICTING_ROLE_ASSIGNMENT = "The user cannot combine administrative and business roles!";
    public static final String REMOVING_ADMIN_ROLE = "Can't remove ADMINISTRATOR role!";
    public static final String REMOVING_UNASSIGNED_ROLE = "The user does not have a role!";
    public static final String REMOVING_ONLY_ROLE = "The user must have at least one role!";
    public static final String PASSWORD_NOT_UNIQUE = "The passwords must be different!";
}
