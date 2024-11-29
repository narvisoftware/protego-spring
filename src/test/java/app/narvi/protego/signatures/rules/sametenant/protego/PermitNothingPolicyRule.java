package app.narvi.protego.signatures.rules.sametenant.protego;

import static app.narvi.protego.PermissionDecision.Decision.DENY;

import app.narvi.protego.PermissionDecision;
import app.narvi.protego.signatures.rules.SpringBeanPolicyRule;

public class PermitNothingPolicyRule extends SpringBeanPolicyRule<TenantAccessPermission, TestSecurityContext> {

  public PermitNothingPolicyRule(TenantAccessPermission permission, TestSecurityContext securityContext) {
    super(permission, securityContext);
  }

  @Override
  public PermissionDecision hasPermission() {
    return new PermissionDecision(DENY, "This policy rules deny everything.");
  }

}
