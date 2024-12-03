package app.narvi.protego.spring.rules.sametenant.protego;

import static app.narvi.protego.PermissionDecision.Decision.DENY;
import static app.narvi.protego.PermissionDecision.Decision.PERMIT;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import app.narvi.protego.PermissionDecision;
import app.narvi.protego.spring.rules.SpringBeanPolicyRule;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AllowOwnTenantPolicyRule extends SpringBeanPolicyRule<TenantAccessPermission, TestSecurityContext> {

  public AllowOwnTenantPolicyRule(TenantAccessPermission permission, TestSecurityContext securityContext) {
    super(permission, securityContext);
  }

  @Override
  public PermissionDecision hasPermission() {
    if (permission.baseEntityResource.getTenantOwner()
        .equals(securityContext.getCurrentAutenticatedUser().getTenantOwner())) {
      return PermissionDecision.with(PERMIT)
          .reason("The tenant id of the protected resource is the same with tenant id from security context: " +
              permission.baseEntityResource.getId());
    } else {
      return PermissionDecision.with(DENY)
          .reason("The tenant id of the protected resource: " +
              permission.baseEntityResource.getId() +
              "is different from tenant id from security context: " +
              securityContext.getCurrentAutenticatedUser().getTenantOwner());
    }
  }
}
