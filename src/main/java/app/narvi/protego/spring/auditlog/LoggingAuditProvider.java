package app.narvi.protego.spring.auditlog;

import static app.narvi.protego.PermissionDecision.Decision.DENY;
import static app.narvi.protego.PermissionDecision.Decision.PERMIT;
import static app.narvi.protego.spring.auditlog.LoggingAuditProvider.LoggingAttributes.ACTION;
import static app.narvi.protego.spring.auditlog.LoggingAuditProvider.LoggingAttributes.LOG_TYPE;
import static app.narvi.protego.spring.auditlog.LoggingAuditProvider.LoggingAttributes.PERMISSION;
import static app.narvi.protego.spring.auditlog.LoggingAuditProvider.LoggingAttributes.PERMISSION_NAME;
import static app.narvi.protego.spring.auditlog.LoggingAuditProvider.LoggingAttributes.PROTECTED_RESOURCES;
import static app.narvi.protego.spring.auditlog.LoggingAuditProvider.LoggingAttributes.SKIPPED_FROM_VOTING;
import static app.narvi.protego.spring.auditlog.LoggingAuditProvider.LoggingAttributes.VOTING_DESCRIPTION;

import java.lang.invoke.MethodHandles;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import app.narvi.protego.AuditProvider;
import app.narvi.protego.spring.permission.BasePermission;
import app.narvi.protego.spring.rules.SpringBeanPolicyRule;

@Component
public class LoggingAuditProvider implements AuditProvider<BasePermission, SpringBeanPolicyRule> {

  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  public static class LoggingAttributes {

    public static String LOG_TYPE = "logType";
    public static String PERMISSION_NAME = "permissionName";
    public static String PERMISSION = "permission";
    public static String ACTION = "action";
    public static String PROTECTED_RESOURCES = "protectedResources";
    public static String SKIPPED_FROM_VOTING = "SKIPPED_FROM_VOTING";
    public static String VOTING_DESCRIPTION = "votingDescription";
  }

  @Override
  public void audit(
      BasePermission permission,
      Votes<SpringBeanPolicyRule> votes,
      Set<SpringBeanPolicyRule> rulesSkippedFromVoting) {
    String message;
    if (votes.getDecision() == PERMIT) {
      message = "PERMIT was voted for " + permission.toSimpleString() +
          " by " + votes.getRulesVotingDecision(PERMIT).stream().findFirst().get().getClass().getSimpleName();
    } else if (votes.getDecision() == DENY) {
      message = "DENY was voted for " + permission.toSimpleString() +
          " by rules: " +
          votes.getRulesVotingDecision(DENY).stream()
              .map(pr -> pr.getClass().getSimpleName())
              .collect(Collectors.joining(", "));
    } else {
      message = "Permission " + permission.toSimpleString() + " not grated." +
          "All policy rules ABSTAIN from voting.";
    }

    Map<String, Set<String>> votesDescription = votes.getVotes().stream()
        .map(v ->
            new AbstractMap.SimpleImmutableEntry<String, String>(
                v.getPermissionDecision().getDecision().name(),
                v.getPolicyRule().getClass().getSimpleName())
        )
        .collect(Collectors.toMap(
            e -> e.getKey(),
            e -> Set.of(e.getValue()),
            (s1, s2) -> Sets.union(s1, s2)
        ));
    votesDescription.put(SKIPPED_FROM_VOTING, rulesSkippedFromVoting.stream()
        .map(pr -> pr.getClass().getSimpleName())
        .collect(Collectors.toSet()));

    LOG.atInfo()
        .setMessage(message)
        .addKeyValue(LOG_TYPE, "PERMISSION_GRANTING_DECISION")
        .addKeyValue(PERMISSION_NAME, permission.getClass().getCanonicalName())
        .addKeyValue(PERMISSION, ImmutableMap.builder()
            .put(ACTION, permission.getAction().name())
            .put(PROTECTED_RESOURCES, permission.getProtectedResources()).build())
        .addKeyValue(VOTING_DESCRIPTION, votesDescription)
        .log();
  }

}