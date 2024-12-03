package app.narvi.protego.spring.rules.sametenant.protego;

import app.narvi.protego.CrudAction;
import app.narvi.protego.spring.permission.BasePermission;
import app.narvi.protego.spring.rules.sametenant.BaseEntity;

public class TenantAccessPermission extends BasePermission<CrudAction> {

  BaseEntity baseEntityResource;

  public TenantAccessPermission(CrudAction action, BaseEntity protectedResource) {
    super(action, protectedResource);
    this.baseEntityResource = protectedResource;
  }
}
