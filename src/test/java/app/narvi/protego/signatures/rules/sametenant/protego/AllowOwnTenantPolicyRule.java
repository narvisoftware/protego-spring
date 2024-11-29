package app.narvi.protego.signatures.rules.sametenant.protego;

import static app.narvi.protego.PermissionDecision.Decision.DENY;
import static app.narvi.protego.PermissionDecision.Decision.PERMIT;

import app.narvi.protego.PermissionDecision;
import app.narvi.protego.signatures.rules.SpringBeanPolicyRule;

public class AllowOwnTenantPolicyRule extends SpringBeanPolicyRule<TenantAccessPermission, TestSecurityContext> {

  public AllowOwnTenantPolicyRule(TenantAccessPermission permission, TestSecurityContext securityContext) {
    super(permission, securityContext);
  }

  @Override
  public PermissionDecision hasPermission() {
    if (permission.anEntityResource.getTenantOwner()
        .equals(securityContext.getCurrentAutenticatedUser().getTenantOwner())) {
      return new PermissionDecision(PERMIT,
          "The tenant id of the protected resource is the same with tenant id from security context: " +
              permission.anEntityResource.getId());
    } else {
      return new PermissionDecision(DENY,
          "The tenant id of the protected resource: " +
              permission.anEntityResource.getId() +
              "is different from tenant id from security context: " +
              securityContext.getCurrentAutenticatedUser().getTenantOwner()
      );
    }
  }
}
