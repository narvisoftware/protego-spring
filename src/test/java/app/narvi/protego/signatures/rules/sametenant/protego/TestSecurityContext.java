package app.narvi.protego.signatures.rules.sametenant.protego;

import static app.narvi.protego.signatures.rules.sametenant.User.AUTHENTICATED_USER;

import app.narvi.protego.signatures.rules.sametenant.User;

public class TestSecurityContext {

  public TestSecurityContext() {
  }

  public User getCurrentAutenticatedUser() {
    User authenticatedUser = AUTHENTICATED_USER.get();
    return authenticatedUser;
  }
}
