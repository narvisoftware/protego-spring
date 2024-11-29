package app.narvi.protego.signatures.rules.sametenant.protego;

import app.narvi.protego.CrudAction;
import app.narvi.protego.signatures.permission.SpringBeanPermission;
import app.narvi.protego.signatures.rules.sametenant.BaseEntity;

public class TenantAccessPermission extends SpringBeanPermission<CrudAction> {

  BaseEntity baseEntityResource;

  public TenantAccessPermission(CrudAction action, BaseEntity protectedResource) {
    super(action, protectedResource);
    this.baseEntityResource = protectedResource;
  }
}
