package app.narvi.protego.spring.rules.sametenant;

import static org.junit.jupiter.api.Assertions.assertThrows;

import static app.narvi.protego.CrudAction.UPDATE;
import static app.narvi.protego.spring.rules.TestExecutionSteps.TestSteps.AND_GIVEN_;
import static app.narvi.protego.spring.rules.TestExecutionSteps.TestSteps.GIVEN_;
import static app.narvi.protego.spring.rules.TestExecutionSteps.TestSteps.THEN_;
import static app.narvi.protego.spring.rules.TestExecutionSteps.TestSteps.WHEN_;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import app.narvi.protego.PolicyEvaluator;
import app.narvi.protego.PolicyException;
import app.narvi.protego.spring.rules.ScanningPolicyRuleProvider;
import app.narvi.protego.spring.rules.Test;
import app.narvi.protego.spring.rules.TestExecutionSteps.Scenario;
import app.narvi.protego.spring.rules.TestSpringConfiguration;
import app.narvi.protego.spring.rules.sametenant.User.Role;
import app.narvi.protego.spring.rules.sametenant.protego.TenantAccessPermission;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestSpringConfiguration.class})
@ActiveProfiles("test")
public class TenantAccessIT extends Test implements InitializingBean {

  @Autowired
  ScanningPolicyRuleProvider scanningPolicyRuleProvider;

  private static boolean protegoInitialized;

  @Override
  public void afterPropertiesSet() {
    if (!protegoInitialized) {
      PolicyEvaluator.registerProviders(scanningPolicyRuleProvider);
      protegoInitialized = true;
    }
  }

  @Scenario("User can have full access to his own tenant resources.")
  public void permitAllowOwnTenant() throws Exception {
    GIVEN_("The user is authenticated");
    Tenant tenant = new Tenant("Sample Tenant");
    User user = new User("John Doe", Role.PATIENT, tenant);

    ScopedValue.where(User.AUTHENTICATED_USER, user).run(() -> {
      AND_GIVEN_("The user and resource has the same tenant");
      SomeResource someResource = new SomeResource("Test Resource", tenant);

      WHEN_("User tests access to resource");
      TenantAccessPermission tenantAccessPermission = new TenantAccessPermission(UPDATE, someResource);
      PolicyEvaluator.evaluatePermission(tenantAccessPermission);
      THEN_("The user has access.");
      Assertions.assertTrue(PolicyEvaluator.hasPermission(tenantAccessPermission));
    });
  }

  @Scenario("User can have full access to his own tenant resources.")
  public void denyAllowOwnTenant() throws Exception {
    GIVEN_("The user1 is authenticated");
    Tenant tenant1 = new Tenant("Sample Tenant");
    User user1 = new User("John Doe", Role.PATIENT, tenant1);

    Tenant tenant2 = new Tenant("Sample Tenant 2");

    ScopedValue.where(User.AUTHENTICATED_USER, user1).run(() -> {
      AND_GIVEN_("The user1 and resource has the same tenant1");
      SomeResource someResource = new SomeResource("Test Resource", tenant2);

      WHEN_("User tests access to resource");
      TenantAccessPermission tenantAccessPermission = new TenantAccessPermission(UPDATE, someResource);
      assertThrows(
          PolicyException.class,
          () -> PolicyEvaluator.evaluatePermission(tenantAccessPermission),
          "Expected PolicyException to throw, but it didn't"
      );

      THEN_("The user has access.");
      Assertions.assertFalse(PolicyEvaluator.hasPermission(tenantAccessPermission));
    });
  }

}