package app.narvi.protego.spring.rules.sametenant;

public class SomeResource extends BaseEntity {

  private String name;

  public SomeResource(String name, Tenant tenantOwner) {
    super(tenantOwner);
    this.name = name;
  }

}
