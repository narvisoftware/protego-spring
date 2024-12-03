package app.narvi.protego.spring.rules.sametenant.protego;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import app.narvi.protego.spring.rules.SecurityContextProvider;

@Component
public class TestSecurityContextProvider implements SecurityContextProvider<TestSecurityContext> {

  @Autowired
  TestSecurityContext testSecurityContext;

  @Override
  public TestSecurityContext getCurrentSecurityContext() {
    return testSecurityContext;
  }
}
