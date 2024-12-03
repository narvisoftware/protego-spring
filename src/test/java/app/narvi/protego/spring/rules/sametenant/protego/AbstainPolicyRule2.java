package app.narvi.protego.spring.rules.sametenant.protego;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import app.narvi.protego.PermissionDecision;
import app.narvi.protego.spring.rules.SpringBeanPolicyRule;
import app.narvi.protego.spring.rules.sametenant.protego.AbstainPolicyRule.FakePermission;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AbstainPolicyRule2 extends SpringBeanPolicyRule<FakePermission, TestSecurityContext> {

  public AbstainPolicyRule2(FakePermission permission, TestSecurityContext securityContext) {
    super(permission, securityContext);
  }

  @Override
  public PermissionDecision hasPermission() {
    return null;
  }

}
