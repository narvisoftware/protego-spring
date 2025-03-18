package app.narvi.protego.spring.rules;

import org.springframework.stereotype.Component;

@Component
public interface SecurityContextProvider {

  SecurityContext getCurrentSecurityContext();

}
