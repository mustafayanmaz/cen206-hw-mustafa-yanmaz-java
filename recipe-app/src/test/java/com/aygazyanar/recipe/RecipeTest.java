/**

@file RecipeTest.java
@brief This file contains the test cases for the Recipe class.
@details This file includes test methods to validate the functionality of the Recipe class. It uses JUnit for unit testing.
*/
package com.aygazyanar.recipe;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**

@class RecipeTest
@brief This class represents the test class for the Recipe class.
@details The RecipeTest class provides test methods to verify the behavior of the Recipe class. It includes test methods for addition, subtraction, multiplication, and division operations.
@author ugur.coruh
*/
public class RecipeTest {

  /**
   * @brief This method is executed once before all test methods.
   * @throws Exception
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  /**
   * @brief This method is executed once after all test methods.
   * @throws Exception
   */
  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  /**
   * @brief This method is executed before each test method.
   * @throws Exception
   */
  @Before
  public void setUp() throws Exception {
  }

  /**
   * @brief This method is executed after each test method.
   * @throws Exception
   */
  @After
  public void tearDown() throws Exception {
  }

  /**
   * @brief Test method to validate the addition operation.
   *
   * @details This method creates an instance of the Recipe class and calls the `add` method with two integers. It asserts the expected result of the addition operation.
   */
  @Test
  public void testAddition() {
    Recipe recipe = new Recipe();
    int result = recipe.add(2, 3);
    assertEquals(5, result);
  }

}
