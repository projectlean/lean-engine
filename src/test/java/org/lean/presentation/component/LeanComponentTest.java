package org.lean.presentation.component;

import junit.framework.TestCase;
import org.lean.core.exception.LeanException;
import org.lean.presentation.layout.LeanLayoutBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class LeanComponentTest extends TestCase {

  public void testGetDependentComponents() throws LeanException {
    Map<String, LeanComponent> components = new HashMap<>();

    LeanComponent a = new LeanComponent("A", null);
    a.setLayout(new LeanLayoutBuilder().top().left().build());
    components.put(a.getName(), a);

    LeanComponent b = new LeanComponent("B", null);
    b.setLayout(new LeanLayoutBuilder().below("A", 5).build());
    components.put(b.getName(), b);

    LeanComponent c = new LeanComponent("C", null);
    c.setLayout(new LeanLayoutBuilder().below("B", 5).build());
    components.put(c.getName(), c);

    LeanComponent d = new LeanComponent("D", null);
    d.setLayout(new LeanLayoutBuilder().below("C", 5).build());
    components.put(d.getName(), d);

    LeanComponent e = new LeanComponent("E", null);
    e.setLayout(new LeanLayoutBuilder().beside("B", 5).build());
    components.put(d.getName(), e);

    Set<LeanComponent> aDependencies = a.getDependentComponents(components);
    assertTrue(aDependencies.isEmpty());

    Set<LeanComponent> bDependencies = b.getDependentComponents(components);
    assertEquals( 1, bDependencies.size() );
    assertTrue(bDependencies.contains(a));

    Set<LeanComponent> cDependencies = c.getDependentComponents(components);
    assertEquals( 2, cDependencies.size() );
    assertTrue(cDependencies.contains(a));
    assertTrue(cDependencies.contains(b));

    Set<LeanComponent> dDependencies = d.getDependentComponents(components);
    assertEquals( 3, dDependencies.size() );
    assertTrue(dDependencies.contains(a));
    assertTrue(dDependencies.contains(b));
    assertTrue(dDependencies.contains(c));

    Set<LeanComponent> eDependencies = e.getDependentComponents(components);
    assertEquals( 2, eDependencies.size() );
    assertTrue(eDependencies.contains(b));
    assertTrue(eDependencies.contains(a));
  }
}
