package app.narvi.protego.spring.rules.sametenant.protego;

import static app.narvi.protego.spring.rules.sametenant.User.AUTHENTICATED_USER;

import org.springframework.stereotype.Component;

import app.narvi.protego.spring.rules.sametenant.User;

@Component
public class TestSecurityContext {

  protected TestSecurityContext() {
  }

  public User getCurrentAutenticatedUser() {
    User authenticatedUser = AUTHENTICATED_USER.get();
    return authenticatedUser;
  }
}
