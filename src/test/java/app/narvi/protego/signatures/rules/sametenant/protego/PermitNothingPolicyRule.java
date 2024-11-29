package app.narvi.protego.signatures.rules.sametenant.protego;

import static app.narvi.protego.PermissionDecision.Decision.DENY;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import app.narvi.protego.PermissionDecision;
import app.narvi.protego.signatures.rules.SpringBeanPolicyRule;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PermitNothingPolicyRule extends SpringBeanPolicyRule<TenantAccessPermission, TestSecurityContext> {

  public PermitNothingPolicyRule(TenantAccessPermission permission, TestSecurityContext securityContext) {
    super(permission, securityContext);
  }

  @Override
  public PermissionDecision hasPermission() {
    return new PermissionDecision(DENY, "This policy rules deny everything.");
  }

}
