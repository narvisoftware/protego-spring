package app.narvi.protego.spring.auditlog;

import static app.narvi.protego.PermissionDecision.Decision.DENY;
import static app.narvi.protego.PermissionDecision.Decision.PERMIT;
import static app.narvi.protego.spring.auditlog.LoggingAuditProvider.LoggingAttributes.ACTION;
import static app.narvi.protego.spring.auditlog.LoggingAuditProvider.LoggingAttributes.CURRENT_SUBJECT_IDENTIFICATION_DESCRIPTION;
import static app.narvi.protego.spring.auditlog.LoggingAuditProvider.LoggingAttributes.LOG_TYPE;
import static app.narvi.protego.spring.auditlog.LoggingAuditProvider.LoggingAttributes.PERMISSION;
import static app.narvi.protego.spring.auditlog.LoggingAuditProvider.LoggingAttributes.PERMISSION_NAME;
import static app.narvi.protego.spring.auditlog.LoggingAuditProvider.LoggingAttributes.PROTECTED_RESOURCES;
import static app.narvi.protego.spring.auditlog.LoggingAuditProvider.LoggingAttributes.SKIPPED_FROM_VOTING;
import static app.narvi.protego.spring.auditlog.LoggingAuditProvider.LoggingAttributes.VOTING_DESCRIPTION;

import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import app.narvi.protego.AuditProvider;
import app.narvi.protego.AuditServices;
import app.narvi.protego.PolicyRule;
import app.narvi.protego.spring.permission.BasePermission;
import app.narvi.protego.spring.rules.SecurityContextProvider;
import app.narvi.protego.spring.rules.SpringBeanPolicyRule;

@Component
public class LoggingAuditProvider implements AuditProvider<BasePermission, SpringBeanPolicyRule>, InitializingBean {

  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  public static class LoggingAttributes {
    public static String LOG_TYPE = "logType";
    public static String PERMISSION_NAME = "permissionName";
    public static String PERMISSION = "permission";
    public static String ACTION = "action";
    public static String PROTECTED_RESOURCES = "protectedResources";
    public static String SKIPPED_FROM_VOTING = "SKIPPED_FROM_VOTING";
    public static String VOTING_DESCRIPTION = "votingDescription";
    public static String CURRENT_SUBJECT_IDENTIFICATION_DESCRIPTION = "currentSubjectIdentificationDescription";
  }

  @Autowired
  SecurityContextProvider securityContextProvider;

  @Override
  public void afterPropertiesSet() {
    if (AuditServices.containsAuditProvider(this)) {
      AuditServices.removeAuditProvider(this);
    }
    AuditServices.addProvider(this);
  }

  @Override
  public void audit(
      BasePermission permission,
      Votes<SpringBeanPolicyRule> votes,
      Set<SpringBeanPolicyRule> rulesSkippedFromVoting) {

    String message;
    String shortMessage;
    if (votes.getDecision() == PERMIT) {
      message = "PERMIT was voted for \"" +
          securityContextProvider.getCurrentSecurityContext().getCurrentSubjectIdentificationDescription() + "\"" +
          " to access " + permission.toSimpleString() +
          " by " + votes.getRulesVotingDecision(PERMIT).stream().findFirst().get().getClass().getSimpleName();
      shortMessage = "permission: PERMIT for " + permission.toSimpleString();
    } else if (votes.getDecision() == DENY) {
      message = "DENY was voted for \"" +
          securityContextProvider.getCurrentSecurityContext().getCurrentSubjectIdentificationDescription() + "\"" +
          " to access " + permission.toSimpleString() +
          " by rules: " +
          votes.getRulesVotingDecision(DENY).stream()
              .map(pr -> pr.getClass().getSimpleName())
              .collect(Collectors.joining(", "));
      shortMessage = "permission: DENY for " + permission.toSimpleString();
    } else {
      message = "Permission " + permission.toSimpleString() + " not grated to \"" +
          securityContextProvider.getCurrentSecurityContext().getCurrentSubjectIdentificationDescription() + "\". " +
          "All policy rules ABSTAIN from voting.";
      shortMessage = "permission: ABSTAIN for " + permission.toSimpleString();
    }

    Map<String, Map<String, String>> votesDetails = new HashMap<>();
    for (Vote aVote : votes.getVotes()) {
      String decision = aVote.getPermissionDecision().getDecision().name();
      votesDetails
          .merge(decision,
              Maps.newHashMap(ImmutableMap.of(
                  aVote.getPolicyRule().getClass().getSimpleName(),
                  aVote.getPermissionDecision().getReasonDescription())),
              (soFar, newVal) -> {
                soFar.put(
                    aVote.getPolicyRule().getClass().getSimpleName(),
                    aVote.getPermissionDecision().getReasonDescription());
                return soFar;
              }
          );
    }

    Map<String, String> skippedDetail = new HashMap<>();
    for (PolicyRule aPolicyRule : rulesSkippedFromVoting) {
      skippedDetail.put(aPolicyRule.getClass().getSimpleName(), "");
    }
    votesDetails.put(SKIPPED_FROM_VOTING, skippedDetail);

    LOG.info(shortMessage);

    LOG.atDebug()
        .setMessage(message)
        .addKeyValue(CURRENT_SUBJECT_IDENTIFICATION_DESCRIPTION, securityContextProvider.getCurrentSecurityContext().getCurrentSubjectIdentificationDescription())
        .addKeyValue(LOG_TYPE, "PERMISSION_GRANTING_DECISION")
        .addKeyValue(PERMISSION_NAME, permission.getClass().getCanonicalName())
        .addKeyValue(PERMISSION, ImmutableMap.builder()
            .put(ACTION, permission.getAction().name())
            .put(PROTECTED_RESOURCES, permission.getProtectedResources()).build())
        .addKeyValue(VOTING_DESCRIPTION, votesDetails)
        .log();
  }

}