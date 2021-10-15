package org.lean.presentation.page;

import junit.framework.TestCase;
import org.junit.Test;
import org.lean.core.exception.LeanException;
import org.lean.presentation.component.LeanComponent;
import org.lean.presentation.layout.LeanLayoutBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class LeanPageTest extends TestCase {

  @Test
  public void testGetSortedComponents() throws LeanException {
    LeanPage page = new LeanPage();

    // D
    {
      LeanComponent component = new LeanComponent("D", null);
      component.setLayout(new LeanLayoutBuilder().below("C", 0).build());
      page.getComponents().add(component);
    }

    // C
    {
      LeanComponent component = new LeanComponent("C", null);
      component.setLayout(new LeanLayoutBuilder().below("B", 0).build());
      page.getComponents().add(component);
    }

    // B
    {
      LeanComponent component = new LeanComponent("B", null);
      component.setLayout(new LeanLayoutBuilder().below("A", 0).build());
      page.getComponents().add(component);
    }

    // A
    {
      LeanComponent component = new LeanComponent("A", null);
      component.setLayout(new LeanLayoutBuilder().top().left().build());
      page.getComponents().add(component);
    }

    // C2
    {
      LeanComponent component = new LeanComponent("C2", null);
      component.setLayout(new LeanLayoutBuilder().beside("C1", 5).build());
      page.getComponents().add(component);
    }

    // C1
    {
      LeanComponent component = new LeanComponent("C1", null);
      component.setLayout(new LeanLayoutBuilder().beside("C", 5).build());
      page.getComponents().add(component);
    }

    // E : not referencing anything, needs to go to the front
    {
      LeanComponent component = new LeanComponent("E", null);
      component.setLayout(new LeanLayoutBuilder().top().right().build());
      page.getComponents().add(component);
    }

    List<LeanComponent> sortedComponents = page.getSortedComponents();
    verifySortedList(sortedComponents);

    // Do a random sort of the components.
    //
    final Random random = new Random();
    for (int i = 0; i < 1000; i++) {
      Collections.sort(page.getComponents(), (a, b) -> random.nextInt());
      verifySortedList(page.getSortedComponents());
    }
  }

  private void verifySortedList(List<LeanComponent> sortedComponents) {
    String orderString = "";
    for (LeanComponent component : sortedComponents) {
      orderString += component.getName();
    }
    Set<String> possibilities = new HashSet<>(Arrays.asList("AEBCC1C2D", "AEBCDC1C2", "AEBCC1DC2"));
    assertTrue(orderString+" not valid", possibilities.contains(orderString));
  }
}
