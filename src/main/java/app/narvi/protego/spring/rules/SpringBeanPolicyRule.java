package app.narvi.protego.spring.rules;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import app.narvi.protego.PolicyRule;
import app.narvi.protego.spring.permission.BasePermission;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class SpringBeanPolicyRule<P extends BasePermission, S> extends PolicyRule<P> {

  protected P permission;
  protected S securityContext;

  public SpringBeanPolicyRule(P permission, S securityContext) {
    this.permission = permission;
    this.securityContext = securityContext;
  }

}
