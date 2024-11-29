package app.narvi.protego.signatures.rules.sametenant;

import static app.narvi.protego.CrudAction.UPDATE;
import static app.narvi.protego.signatures.rules.TestExecutionSteps.TestSteps.AND_GIVEN_;
import static app.narvi.protego.signatures.rules.TestExecutionSteps.TestSteps.GIVEN_;
import static app.narvi.protego.signatures.rules.TestExecutionSteps.TestSteps.THEN_;
import static app.narvi.protego.signatures.rules.TestExecutionSteps.TestSteps.WHEN_;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;

import app.narvi.protego.PolicyEvaluator;
import app.narvi.protego.signatures.rules.ScanningPolicyRuleProvider;
import app.narvi.protego.signatures.rules.Test;
import app.narvi.protego.signatures.rules.TestExecutionSteps.Scenario;
import app.narvi.protego.signatures.rules.sametenant.User.Role;
import app.narvi.protego.signatures.rules.sametenant.protego.TenantAccessPermission;

@Order(1)
public class TenantAccessIT extends Test {

  @Scenario("User can have full access to his own tenant resources.")
  public void allowOwnTenant() throws Exception {
    GIVEN_("The framework is initialized");
    PolicyEvaluator.registerProviders(
        new ScanningPolicyRuleProvider("app.narvi.protego")
    );

    AND_GIVEN_("The user is authenticated");
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
}