package com.example.testcalculadora;

import com.example.testcalculadora.model.ExpressionParser;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Integration tests for realistic scientific calculator scenarios
 */
public class ScientificCalculatorIntegrationTest {
    
    private ExpressionParser parser;
    private static final double DELTA = 0.0001;
    
    @Before
    public void setUp() {
        parser = new ExpressionParser();
    }
    
    // Real-world physics calculations
    
    @Test
    public void physics_projectile_angle_calculation() throws Exception {
        // sin(45)^2 for projectile motion
        double result = parser.evaluate("sin(45)^2");
        assertEquals(0.5, result, DELTA);
    }
    
    @Test
    public void physics_wave_interference() throws Exception {
        // cos(30) + cos(60)
        double result = parser.evaluate("cos(30)+cos(60)");
        assertEquals(1.366, result, 0.001);
    }
    
    // Engineering calculations
    
    @Test
    public void engineering_voltage_calculation() throws Exception {
        // sqrt(100^2 + 50^2) for AC circuit
        double result = parser.evaluate("sqrt(100^2+50^2)");
        assertEquals(111.803, result, 0.001);
    }
    
    @Test
    public void engineering_power_dissipation() throws Exception {
        // 10*log(100/1) for decibel calculation
        double result = parser.evaluate("10*log(100)");
        assertEquals(20.0, result, DELTA);
    }
    
    // Mathematical sequences
    
    @Test
    public void compound_interest_calculation() throws Exception {
        // 1000*(1+0.05)^10
        double result = parser.evaluate("1000*1.05^10");
        assertEquals(1628.89, result, 0.01);
    }
    
    @Test
    public void exponential_decay() throws Exception {
        // 100*0.5^3
        double result = parser.evaluate("100*0.5^3");
        assertEquals(12.5, result, DELTA);
    }
    
    // Trigonometry identities verification
    
    @Test
    public void pythagorean_identity_sin_cos() throws Exception {
        // sin(30)^2 + cos(30)^2 should equal 1
        double result = parser.evaluate("sin(30)^2+cos(30)^2");
        assertEquals(1.0, result, DELTA);
    }
    
    @Test
    public void double_angle_formula() throws Exception {
        // 2*sin(30)*cos(30) should equal sin(60)
        double expected = parser.evaluate("sin(60)");
        double result = parser.evaluate("2*sin(30)*cos(30)");
        assertEquals(expected, result, DELTA);
    }
    
    // Logarithm properties
    
    @Test
    public void log_product_property() throws Exception {
        // log(100) + log(10) = log(1000)
        double expected = parser.evaluate("log(1000)");
        double result = parser.evaluate("log(100)+log(10)");
        assertEquals(expected, result, DELTA);
    }
    
    @Test
    public void log_quotient_property() throws Exception {
        // log(1000) - log(10) = log(100)
        double expected = parser.evaluate("log(100)");
        double result = parser.evaluate("log(1000)-log(10)");
        assertEquals(expected, result, DELTA);
    }
    
    @Test
    public void natural_log_exponential() throws Exception {
        // ln(2.71828^2) approximately equals 2
        double result = parser.evaluate("ln(2.71828^2)");
        assertEquals(2.0, result, 0.01);
    }
    
    // Complex nested expressions
    
    @Test
    public void nested_sqrt_and_power() throws Exception {
        // sqrt(sqrt(16)^2 + sqrt(9)^2)
        double result = parser.evaluate("sqrt(sqrt(16)^2+sqrt(9)^2)");
        assertEquals(5.0, result, DELTA);
    }
    
    @Test
    public void nested_trigonometric_functions() throws Exception {
        // sin(cos(0)*90)
        double result = parser.evaluate("sin(cos(0)*90)");
        assertEquals(1.0, result, DELTA);
    }
    
    @Test
    public void mixed_logarithms_and_powers() throws Exception {
        // log(10^3) + ln(2.71828)
        double result = parser.evaluate("log(10^3)+ln(2.71828)");
        assertEquals(4.0, result, 0.01);
    }
    
    // Statistics and probability
    
    @Test
    public void standard_deviation_component() throws Exception {
        // sqrt((5-3)^2 + (7-3)^2 + (9-3)^2)
        double result = parser.evaluate("sqrt((5-3)^2+(7-3)^2+(9-3)^2)");
        assertEquals(6.928, result, 0.001);
    }
    
    // Scientific notation emulation
    
    @Test
    public void scientific_notation_calculation() throws Exception {
        // 6.022*10^23 (part of Avogadro's number)
        double result = parser.evaluate("6.022*10^23");
        assertEquals(6.022e23, result, 1e19);
    }
    
    @Test
    public void small_scientific_notation() throws Exception {
        // 1.602*10^-19 (electron charge)
        double result = parser.evaluate("1.602*10^-19");
        assertEquals(1.602e-19, result, 1e-23);
    }
    
    // Navigation and surveying
    
    @Test
    public void distance_calculation_triangle() throws Exception {
        // Using law of cosines: sqrt(10^2 + 15^2 - 2*10*15*cos(60))
        double result = parser.evaluate("sqrt(10^2+15^2-2*10*15*cos(60))");
        assertEquals(13.229, result, 0.001);
    }
    
    // Temperature and conversion calculations
    
    @Test
    public void celsius_to_kelvin_offset() throws Exception {
        // (25 + 273.15) for Celsius to Kelvin
        double result = parser.evaluate("25+273.15");
        assertEquals(298.15, result, DELTA);
    }
    
    // Financial calculations
    
    @Test
    public void loan_payment_calculation() throws Exception {
        // Monthly payment component: (1+0.05/12)^360
        double result = parser.evaluate("(1+0.05/12)^360");
        assertEquals(4.4677, result, 0.001);
    }
    
    @Test
    public void present_value_calculation() throws Exception {
        // 1000/(1.08^10)
        double result = parser.evaluate("1000/1.08^10");
        assertEquals(463.19, result, 0.01);
    }
    
    // Geometry calculations
    
    @Test
    public void circle_area_with_sqrt() throws Exception {
        // Area = pi*r^2, using r = sqrt(4)
        double result = parser.evaluate("3.14159*sqrt(4)^2");
        assertEquals(12.566, result, 0.001);
    }
    
    @Test
    public void sphere_volume_component() throws Exception {
        // (4/3)*r^3 where r=3
        double result = parser.evaluate("4/3*3^3");
        assertEquals(36.0, result, DELTA);
    }
    
    // Computer science - binary/logarithmic
    
    @Test
    public void binary_tree_depth() throws Exception {
        // log(1024)/log(2) = 10 levels
        double result = parser.evaluate("log(1024)/log(2)");
        assertEquals(10.0, result, DELTA);
    }
    
    @Test
    public void algorithm_complexity() throws Exception {
        // n*log(n) where n=100
        double result = parser.evaluate("100*log(100)");
        assertEquals(200.0, result, DELTA);
    }
    
    // Signal processing
    
    @Test
    public void fourier_component() throws Exception {
        // cos(0)*cos(0) + sin(0)*sin(0)
        double result = parser.evaluate("cos(0)*cos(0)+sin(0)*sin(0)");
        assertEquals(1.0, result, DELTA);
    }
    
    // Edge cases with scientific functions
    
    @Test
    public void very_small_number_calculation() throws Exception {
        double result = parser.evaluate("10^-10");
        assertEquals(1e-10, result, 1e-14);
    }
    
    @Test
    public void very_large_number_calculation() throws Exception {
        double result = parser.evaluate("10^10");
        assertEquals(1e10, result, 1e6);
    }
    
    @Test
    public void mixed_operations_precedence() throws Exception {
        // 2 + 3 * 4^2 - sqrt(16)
        double result = parser.evaluate("2+3*4^2-sqrt(16)");
        assertEquals(46.0, result, DELTA);
    }
}
