package app.narvi.protego.signatures.rules;

import org.springframework.stereotype.Component;

@Component
public interface SecurityContextProvider<C> {

  C getCurrentSecurityContext();

}
