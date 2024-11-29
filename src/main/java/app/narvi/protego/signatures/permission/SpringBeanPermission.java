package app.narvi.protego.signatures.permission;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import app.narvi.protego.Permission;

public abstract class SpringBeanPermission<A> extends Permission<A> {

  public SpringBeanPermission(A action, Object... protectedResources) {
    super(action, protectedResources);
  }

}
