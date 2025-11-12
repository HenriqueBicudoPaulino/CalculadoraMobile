package com.example.testcalculadora;

import com.example.testcalculadora.model.ExpressionParser;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for scientific calculator functions
 */
public class ScientificCalculatorTest {
    
    private ExpressionParser parser;
    private static final double DELTA = 0.0001; // Tolerance for floating point comparisons
    
    @Before
    public void setUp() {
        parser = new ExpressionParser();
    }
    
    // Trigonometric functions tests
    
    @Test
    public void sin_0_equals_0() throws Exception {
        double result = parser.evaluate("sin(0)");
        assertEquals(0.0, result, DELTA);
    }
    
    @Test
    public void sin_30_equals_0_5() throws Exception {
        double result = parser.evaluate("sin(30)");
        assertEquals(0.5, result, DELTA);
    }
    
    @Test
    public void sin_90_equals_1() throws Exception {
        double result = parser.evaluate("sin(90)");
        assertEquals(1.0, result, DELTA);
    }
    
    @Test
    public void cos_0_equals_1() throws Exception {
        double result = parser.evaluate("cos(0)");
        assertEquals(1.0, result, DELTA);
    }
    
    @Test
    public void cos_60_equals_0_5() throws Exception {
        double result = parser.evaluate("cos(60)");
        assertEquals(0.5, result, DELTA);
    }
    
    @Test
    public void cos_90_equals_0() throws Exception {
        double result = parser.evaluate("cos(90)");
        assertEquals(0.0, result, DELTA);
    }
    
    @Test
    public void tan_0_equals_0() throws Exception {
        double result = parser.evaluate("tan(0)");
        assertEquals(0.0, result, DELTA);
    }
    
    @Test
    public void tan_45_equals_1() throws Exception {
        double result = parser.evaluate("tan(45)");
        assertEquals(1.0, result, DELTA);
    }
    
    // Power tests
    
    @Test
    public void power_2_to_3_equals_8() throws Exception {
        double result = parser.evaluate("2^3");
        assertEquals(8.0, result, DELTA);
    }
    
    @Test
    public void power_5_to_2_equals_25() throws Exception {
        double result = parser.evaluate("5^2");
        assertEquals(25.0, result, DELTA);
    }
    
    @Test
    public void power_10_to_0_equals_1() throws Exception {
        double result = parser.evaluate("10^0");
        assertEquals(1.0, result, DELTA);
    }
    
    @Test
    public void power_2_to_negative_2_equals_0_25() throws Exception {
        double result = parser.evaluate("2^-2");
        assertEquals(0.25, result, DELTA);
    }
    
    @Test
    public void power_with_parentheses() throws Exception {
        double result = parser.evaluate("(2+3)^2");
        assertEquals(25.0, result, DELTA);
    }
    
    // Square root tests
    
    @Test
    public void sqrt_4_equals_2() throws Exception {
        double result = parser.evaluate("sqrt(4)");
        assertEquals(2.0, result, DELTA);
    }
    
    @Test
    public void sqrt_9_equals_3() throws Exception {
        double result = parser.evaluate("sqrt(9)");
        assertEquals(3.0, result, DELTA);
    }
    
    @Test
    public void sqrt_16_equals_4() throws Exception {
        double result = parser.evaluate("sqrt(16)");
        assertEquals(4.0, result, DELTA);
    }
    
    @Test
    public void sqrt_2_approximately_1_414() throws Exception {
        double result = parser.evaluate("sqrt(2)");
        assertEquals(1.41421356, result, DELTA);
    }
    
    @Test(expected = Exception.class)
    public void sqrt_negative_throws_exception() throws Exception {
        parser.evaluate("sqrt(-4)");
    }
    
    // Logarithm tests
    
    @Test
    public void log_10_equals_1() throws Exception {
        double result = parser.evaluate("log(10)");
        assertEquals(1.0, result, DELTA);
    }
    
    @Test
    public void log_100_equals_2() throws Exception {
        double result = parser.evaluate("log(100)");
        assertEquals(2.0, result, DELTA);
    }
    
    @Test
    public void log_1000_equals_3() throws Exception {
        double result = parser.evaluate("log(1000)");
        assertEquals(3.0, result, DELTA);
    }
    
    @Test(expected = Exception.class)
    public void log_0_throws_exception() throws Exception {
        parser.evaluate("log(0)");
    }
    
    @Test(expected = Exception.class)
    public void log_negative_throws_exception() throws Exception {
        parser.evaluate("log(-10)");
    }
    
    // Natural logarithm tests
    
    @Test
    public void ln_e_equals_1() throws Exception {
        double result = parser.evaluate("ln(2.71828)");
        assertEquals(1.0, result, DELTA);
    }
    
    @Test
    public void ln_1_equals_0() throws Exception {
        double result = parser.evaluate("ln(1)");
        assertEquals(0.0, result, DELTA);
    }
    
    @Test(expected = Exception.class)
    public void ln_0_throws_exception() throws Exception {
        parser.evaluate("ln(0)");
    }
    
    @Test(expected = Exception.class)
    public void ln_negative_throws_exception() throws Exception {
        parser.evaluate("ln(-5)");
    }
    
    // Complex expressions with scientific functions
    
    @Test
    public void complex_expression_sin_plus_cos() throws Exception {
        double result = parser.evaluate("sin(30)+cos(60)");
        assertEquals(1.0, result, DELTA);
    }
    
    @Test
    public void complex_expression_sqrt_of_power() throws Exception {
        double result = parser.evaluate("sqrt(2^4)");
        assertEquals(4.0, result, DELTA);
    }
    
    @Test
    public void complex_expression_log_of_power() throws Exception {
        double result = parser.evaluate("log(10^3)");
        assertEquals(3.0, result, DELTA);
    }
    
    @Test
    public void complex_expression_mixed_operations() throws Exception {
        double result = parser.evaluate("2*sin(30)+sqrt(16)");
        assertEquals(5.0, result, DELTA);
    }
    
    @Test
    public void complex_expression_with_parentheses() throws Exception {
        double result = parser.evaluate("(sin(30)+cos(60))*2");
        assertEquals(2.0, result, DELTA);
    }
    
    @Test
    public void complex_expression_nested_functions() throws Exception {
        double result = parser.evaluate("sqrt(sin(90)*16)");
        assertEquals(4.0, result, DELTA);
    }
    
    @Test
    public void power_precedence_over_multiplication() throws Exception {
        double result = parser.evaluate("2*3^2");
        assertEquals(18.0, result, DELTA); // 2 * (3^2) = 2 * 9 = 18
    }
    
    @Test
    public void multiple_powers_right_associative() throws Exception {
        double result = parser.evaluate("2^3^2");
        assertEquals(512.0, result, DELTA); // 2^(3^2) = 2^9 = 512
    }
    
    // Edge cases
    
    @Test
    public void function_with_expression_argument() throws Exception {
        double result = parser.evaluate("sin(45+45)");
        assertEquals(1.0, result, DELTA);
    }
    
    @Test
    public void function_multiplication_implicit() throws Exception {
        double result = parser.evaluate("2*sqrt(4)");
        assertEquals(4.0, result, DELTA);
    }
    
    @Test(expected = Exception.class)
    public void function_without_parentheses_throws_exception() throws Exception {
        parser.evaluate("sin30");
    }
    
    @Test(expected = Exception.class)
    public void unknown_function_throws_exception() throws Exception {
        parser.evaluate("unknown(5)");
    }
}
