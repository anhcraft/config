# Internal (dev)

## Embedding
- Embedding is a feature to compose and flatten fields from another schema in a schema without inheritance:
    - Composition: the composed schema is nested in the code
    - Flattening: the composed schema is flattened in the configuration
    - Favor composition over inheritance
- When flattening, naming conflict can happen; as such, by default, all members of the mixin field is prefixed with the field name. Config follows Java convention which prefers `camelCase`.
- Embedded fields are inherently unique at runtime. If conflicts occur in configuration, the later one will override prior ones
- For example:
    - Note that `Gun#ammoDamage` can override `ammoDamage` composed by `ammo` field plus `damage`
```java
public class Gun {
  private String name = "Desert Eagle";
  @Embedded private Ammo ammo;
  private int ammoCount = 7;
  private int ammoDamage = 50;
}

public class Ammo {
  private float damage = 20;
  private float weight = 0.02;
}
```
```yaml
name: "Desert Eagle"
ammoCount: 7
ammoDamage: 50.0
ammoWeight: 0.02
```
