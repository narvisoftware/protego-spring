package app.narvi.protego.signatures.rules;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.stereotype.Component;

import app.narvi.protego.Permission;
import app.narvi.protego.PolicyRulesProvider;

@Component
public class ScanningPolicyRuleProvider implements PolicyRulesProvider {

  @Autowired
  ApplicationContext applicationContext;

  @Autowired
  SecurityContextProvider securityContextProvider;

  private final String baseScanPackage;
  private static Set<Class<? extends SpringBeanPolicyRule>> policyRules;

  public ScanningPolicyRuleProvider(String baseScanpackage) {
    baseScanpackage = baseScanpackage.replace('.', '/');
    this.baseScanPackage = baseScanpackage;
    findPolicyRulesInClasspath();
  }

  private void findPolicyRulesInClasspath() {
    ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
    provider.addIncludeFilter(new AssignableTypeFilter(SpringBeanPolicyRule.class));
    Set<BeanDefinition> components = provider.findCandidateComponents(baseScanPackage);

    policyRules = components.stream()
        .map(BeanDefinition::getBeanClassName)
        .map(className -> {
          try {
            return (Class<? extends SpringBeanPolicyRule>) Class.forName(className);
          } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
          }
        })
        .collect(Collectors.toSet());

  }

  @Override
  public Set<? extends SpringBeanPolicyRule> collect(Permission permission) {
    Set<SpringBeanPolicyRule> newPolicyRules = new HashSet<>();
    for (Class<? extends SpringBeanPolicyRule> policyRule : policyRules) {
      newPolicyRules.add(
          applicationContext.getBean(policyRule, permission, securityContextProvider.getCurrentSecurityContext())
      );
    }
    return newPolicyRules;
  }


}
