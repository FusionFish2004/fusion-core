package cn.fusionfish.core.exception.command;

/**
 * @author JeremyHu
 */
public class CommandLackPermissionException extends Exception {
    private final String permission;

    public String getPermission() {
        return permission;
    }

    public CommandLackPermissionException(String permission) {
        this.permission = permission;
    }
}
