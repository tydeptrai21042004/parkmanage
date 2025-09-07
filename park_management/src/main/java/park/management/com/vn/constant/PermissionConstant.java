package park.management.com.vn.constant;

public class PermissionConstant {

  // (giữ nguyên các constant cũ)
  public static final String ASSIGN_ROLE = "hasPermission( 'Role manager', 'assign_role')";
  public static final String CREATE_ROLE = "hasPermission( 'Role manager', 'create_role')";
  public static final String UPDATE_ROLE = "hasPermission( 'Role manager', 'update_role')";
  public static final String DELETE_ROLE = "hasPermission( 'Role manager', 'delete_role')";
  public static final String VIEW_ROLE   = "hasPermission( 'Role manager', 'view_role')";

  // Notification
  public static final String NOTIFICATION_CREATE = "hasRole('ADMIN')";
  public static final String NOTIFICATION_UPDATE = "hasRole('ADMIN')";
  public static final String NOTIFICATION_DELETE = "hasRole('ADMIN')";
  public static final String NOTIFICATION_READ   = "isAuthenticated()";

  // NEW: staff validator for redeeming ticket passes
  public static final String TICKET_VALIDATE = "hasAuthority('TICKET_VALIDATE')";
}
