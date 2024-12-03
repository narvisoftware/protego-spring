package app.narvi.protego.spring.rules.sametenant.protego;

import java.nio.file.AccessMode;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import app.narvi.protego.PermissionDecision;
import app.narvi.protego.spring.permission.BasePermission;
import app.narvi.protego.spring.rules.SpringBeanPolicyRule;
import app.narvi.protego.spring.rules.sametenant.protego.AbstainPolicyRule.FakePermission;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AbstainPolicyRule extends SpringBeanPolicyRule<FakePermission, TestSecurityContext> {

  public AbstainPolicyRule(FakePermission permission, TestSecurityContext securityContext) {
    super(permission, securityContext);
  }

  @Override
  public PermissionDecision hasPermission() {
    return null;
  }

  public static class FakePermission extends BasePermission<AccessMode> {

    public FakePermission(AccessMode action, Object... protectedResources) {
      super(action, protectedResources);
    }
  }

}
