package app.narvi.protego.spring.rules;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.NamedBeanHolder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.stereotype.Component;

import app.narvi.protego.AuditProvider;
import app.narvi.protego.AuditServices;
import app.narvi.protego.Permission;
import app.narvi.protego.PolicyRulesProvider;

@Component
public class ScanningPolicyRuleProvider implements PolicyRulesProvider, InitializingBean {

  @Autowired
  ApplicationContext applicationContext;

  @Autowired
  SecurityContextProvider securityContextProvider;

  private static Set<Class<? extends SpringBeanPolicyRule>> policyRules;

  @Value("${protego.policyRules.packageToScan}")
  private String baseScanPackage;

  protected ScanningPolicyRuleProvider() {

  }

  @Override
  public void afterPropertiesSet() {
    if (baseScanPackage == null) {
      throw new IllegalArgumentException("Spring property 'protego.policyRules.packageToScan' must be defined");
    }
    baseScanPackage = baseScanPackage.replace('.', '/');
    loadAuditProvidersAsSpringBeans();
    findPolicyRulesInClasspath();
  }

  private void loadAuditProvidersAsSpringBeans() {
    AutowireCapableBeanFactory beanFactory = applicationContext.getAutowireCapableBeanFactory();
    Set<AuditProvider> auditProviders = AuditServices.getAuditProviders();
    for (AuditProvider anAuditProvider : auditProviders) {
      NamedBeanHolder<? extends AuditProvider> namedBeanHolder = beanFactory.resolveNamedBean(
          anAuditProvider.getClass());
      String beanName = namedBeanHolder.getBeanName();
      beanFactory.configureBean(anAuditProvider, beanName);
    }
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
      Constructor[] constructors = policyRule.getConstructors();
      boolean hasSameType = Arrays.stream(constructors)
          .filter(c -> c.getParameterTypes()[0].isAssignableFrom(permission.getClass()))
          .findFirst().isPresent();
      Permission permissionForConstructor = null;
      if (hasSameType) {
        permissionForConstructor = permission;
      }
      newPolicyRules.add(
          applicationContext.getBean(policyRule, permissionForConstructor,
              securityContextProvider.getCurrentSecurityContext())
      );
    }
    return newPolicyRules;
  }


}
