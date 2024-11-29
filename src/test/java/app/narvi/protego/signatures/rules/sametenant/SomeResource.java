package app.narvi.protego.signatures.rules.sametenant;

public class SomeResource extends AnEntityResource {

  private String name;

  public SomeResource(String name, Tenant tenantOwner) {
    super(tenantOwner);
    this.name = name;
  }

}
