package app.narvi.protego.spring.rules.sametenant.protego;

import static app.narvi.protego.PermissionDecision.Decision.DENY;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import app.narvi.protego.PermissionDecision;
import app.narvi.protego.spring.rules.SpringBeanPolicyRule;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PermitNothingPolicyRule3 extends SpringBeanPolicyRule<TenantAccessPermission, TestSecurityContext> {

  public PermitNothingPolicyRule3(TenantAccessPermission permission, TestSecurityContext securityContext) {
    super(permission, securityContext);
  }

  @Override
  public PermissionDecision hasPermission() {
    return PermissionDecision.with(DENY)
        .reason("This policy rules deny everything.");
  }

}
