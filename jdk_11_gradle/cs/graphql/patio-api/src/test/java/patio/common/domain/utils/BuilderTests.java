/*
 * Copyright (C) 2019 Kaleidos Open Source SL
 *
 * This file is part of PATIO.
 * PATIO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PATIO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PATIO.  If not, see <https://www.gnu.org/licenses/>
 */
package patio.common.domain.utils;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.validation.constraints.NotNull;
import org.junit.jupiter.api.Test;

public class BuilderTests {

  static class Car {
    private String model;
    private String brand;

    private Car() {
      /* empty */
    }

    String getModel() {
      return model;
    }

    void setModel(@NotNull String model) {
      requireNonNull(model, "domain.car.model");
      this.model = model;
    }

    String getBrand() {
      return brand;
    }

    void setBrand(@NotNull String brand) {
      requireNonNull(brand, "domain.car.brand");
      this.brand = brand;
    }

    public static Builder<Car> builder() {
      return Builder.build(Car::new);
    }

    static Car create(@NotNull String brand, @NotNull String model) {
      return Car.builder().with(c -> c.setBrand(brand)).with(c -> c.setModel(model)).build();
    }
  }

  @Test
  void testBuilderChaining() {
    Car car = Car.builder().with(c -> c.setBrand("Ferrari")).with(c -> c.setModel("F450")).build();

    assertEquals(car.getBrand(), "Ferrari");
    assertEquals(car.getModel(), "F450");
  }

  @Test
  void testNotNullConstraints() {
    assertThrows(NullPointerException.class, () -> Car.create("Ferrari", null));
  }

  @Test
  void testIfs() {
    String brand = "Ferrari";

    Car car =
        Car.builder()
            .ifMatches(() -> false)
            .with(c -> c.setModel("never reached"))
            .endIfMatches()
            .with(c -> c.setBrand(brand))
            .build();

    assertNull(car.getModel());
    assertEquals("Ferrari", car.getBrand());
  }
}
