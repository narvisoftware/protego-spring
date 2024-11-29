package app.narvi.protego.signatures.rules.sametenant.protego;

import app.narvi.protego.signatures.rules.SecurityContextProvider;
import app.narvi.protego.signatures.rules.sametenant.Tenant;
import app.narvi.protego.signatures.rules.sametenant.User;
import app.narvi.protego.signatures.rules.sametenant.User.Role;

public class TestSecurityContextProvider implements SecurityContextProvider<TestSecurityContext> {

  @Override
  public TestSecurityContext getCurrentSecurityContext() {
    Tenant tenant = new Tenant("Sample Tenant");
    User user = new User("John Doe", Role.PATIENT, tenant);
    return new TestSecurityContext();
  }
}
