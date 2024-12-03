package app.narvi.protego.spring.permission;

import app.narvi.protego.Permission;

public abstract class BasePermission<A extends Enum> extends Permission<A> {

  public BasePermission(A action, Object... protectedResources) {
    super(action, protectedResources);
  }

  @Override
  public A getAction() {
    return action;
  }

  public String toSimpleString() {
    return this.getClass().getSimpleName() + " (" + action.name() + ")";
  }

}
