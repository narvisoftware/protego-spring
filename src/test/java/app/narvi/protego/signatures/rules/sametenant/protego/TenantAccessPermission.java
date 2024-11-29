package app.narvi.protego.signatures.rules.sametenant.protego;

import app.narvi.protego.CrudAction;
import app.narvi.protego.signatures.permission.SpringBeanPermission;
import app.narvi.protego.signatures.rules.sametenant.AnEntityResource;

public class TenantAccessPermission extends SpringBeanPermission<CrudAction> {

  AnEntityResource anEntityResource;

  public TenantAccessPermission(CrudAction action, AnEntityResource protectedResource) {
    super(action, protectedResource);
    this.anEntityResource = protectedResource;
  }
}
