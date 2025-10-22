package com.example.testcalculadora;

import com.example.testcalculadora.viewmodel.CalculatorViewModel;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for CalculatorViewModel scientific functions
 */
public class CalculatorViewModelScientificTest {
    
    private CalculatorViewModel viewModel;
    private TestListener listener;
    
    private static class TestListener implements CalculatorViewModel.CalculatorListener {
        String expression = "";
        String result = "";
        
        @Override
        public void onStateChanged(String expression, String result) {
            this.expression = expression;
            this.result = result;
        }
    }
    
    @Before
    public void setUp() {
        viewModel = new CalculatorViewModel();
        listener = new TestListener();
        viewModel.setListener(listener);
    }
    
    // Sin function tests
    
    @Test
    public void appendSin_creates_sin_function() {
        viewModel.appendSin();
        assertEquals("sin(", listener.expression);
    }
    
    @Test
    public void sin_90_calculates_correctly() {
        viewModel.appendSin();
        viewModel.appendNumber("9");
        viewModel.appendNumber("0");
        viewModel.calculateResult();
        assertEquals("1", listener.expression);
    }
    
    // Cos function tests
    
    @Test
    public void appendCos_creates_cos_function() {
        viewModel.appendCos();
        assertEquals("cos(", listener.expression);
    }
    
    @Test
    public void cos_0_calculates_correctly() {
        viewModel.appendCos();
        viewModel.appendNumber("0");
        viewModel.calculateResult();
        assertEquals("1", listener.expression);
    }
    
    // Tan function tests
    
    @Test
    public void appendTan_creates_tan_function() {
        viewModel.appendTan();
        assertEquals("tan(", listener.expression);
    }
    
    @Test
    public void tan_45_calculates_correctly() {
        viewModel.appendTan();
        viewModel.appendNumber("4");
        viewModel.appendNumber("5");
        viewModel.calculateResult();
        assertEquals("1", listener.expression);
    }
    
    // Power function tests
    
    @Test
    public void appendPower_with_number() {
        viewModel.appendNumber("2");
        viewModel.appendPower();
        assertEquals("2^", listener.expression);
    }
    
    @Test
    public void appendPower_empty_expression_does_nothing() {
        viewModel.appendPower();
        assertEquals("", listener.expression);
    }
    
    @Test
    public void power_2_to_3_calculates_correctly() {
        viewModel.appendNumber("2");
        viewModel.appendPower();
        viewModel.appendNumber("3");
        viewModel.calculateResult();
        assertEquals("8", listener.expression);
    }
    
    @Test
    public void appendPower_after_operator_does_nothing() {
        viewModel.appendNumber("2");
        viewModel.appendOperator("+");
        viewModel.appendPower();
        assertEquals("2+", listener.expression);
    }
    
    // Square root tests
    
    @Test
    public void appendSqrt_creates_sqrt_function() {
        viewModel.appendSqrt();
        assertEquals("sqrt(", listener.expression);
    }
    
    @Test
    public void sqrt_4_calculates_correctly() {
        viewModel.appendSqrt();
        viewModel.appendNumber("4");
        viewModel.calculateResult();
        assertEquals("2", listener.expression);
    }
    
    @Test
    public void sqrt_9_calculates_correctly() {
        viewModel.appendSqrt();
        viewModel.appendNumber("9");
        viewModel.calculateResult();
        assertEquals("3", listener.expression);
    }
    
    // Logarithm tests
    
    @Test
    public void appendLog_creates_log_function() {
        viewModel.appendLog();
        assertEquals("log(", listener.expression);
    }
    
    @Test
    public void log_100_calculates_correctly() {
        viewModel.appendLog();
        viewModel.appendNumber("1");
        viewModel.appendNumber("0");
        viewModel.appendNumber("0");
        viewModel.calculateResult();
        assertEquals("2", listener.expression);
    }
    
    // Natural logarithm tests
    
    @Test
    public void appendLn_creates_ln_function() {
        viewModel.appendLn();
        assertEquals("ln(", listener.expression);
    }
    
    @Test
    public void ln_1_calculates_correctly() {
        viewModel.appendLn();
        viewModel.appendNumber("1");
        viewModel.calculateResult();
        assertEquals("0", listener.expression);
    }
    
    // Implicit multiplication with functions
    
    @Test
    public void function_after_number_adds_multiplication() {
        viewModel.appendNumber("2");
        viewModel.appendSin();
        assertEquals("2*sin(", listener.expression);
    }
    
    @Test
    public void function_after_closing_parenthesis_adds_multiplication() {
        viewModel.addParentheses(); // (
        viewModel.appendNumber("5");
        viewModel.addParentheses(); // )
        viewModel.appendCos();
        assertEquals("(5)*cos(", listener.expression);
    }
    
    @Test
    public void function_after_operator_no_multiplication() {
        viewModel.appendNumber("2");
        viewModel.appendOperator("+");
        viewModel.appendSqrt();
        assertEquals("2+sqrt(", listener.expression);
    }
    
    // Complex expressions
    
    @Test
    public void complex_expression_sin_plus_cos() {
        viewModel.appendSin();
        viewModel.appendNumber("3");
        viewModel.appendNumber("0");
        viewModel.addParentheses(); // close sin
        viewModel.appendOperator("+");
        viewModel.appendCos();
        viewModel.appendNumber("6");
        viewModel.appendNumber("0");
        viewModel.calculateResult();
        assertEquals("1", listener.expression);
    }
    
    @Test
    public void complex_expression_with_power_and_sqrt() {
        viewModel.appendNumber("2");
        viewModel.appendPower();
        viewModel.appendNumber("4");
        viewModel.appendOperator("+");
        viewModel.appendSqrt();
        viewModel.appendNumber("9");
        viewModel.calculateResult();
        assertEquals("19", listener.expression);
    }
    
    // Clear after scientific function
    
    @Test
    public void clear_after_function() {
        viewModel.appendSin();
        viewModel.appendNumber("3");
        viewModel.appendNumber("0");
        viewModel.clear();
        assertEquals("", listener.expression);
        assertEquals("", listener.result);
    }
    
    // Delete with scientific functions
    
    @Test
    public void delete_removes_last_character_from_function() {
        viewModel.appendSin();
        viewModel.deleteLast();
        assertEquals("sin", listener.expression);
        viewModel.deleteLast();
        assertEquals("si", listener.expression);
    }
    
    // Function after equals
    
    @Test
    public void function_after_equals_clears_expression() {
        viewModel.appendNumber("5");
        viewModel.appendOperator("+");
        viewModel.appendNumber("3");
        viewModel.calculateResult();
        viewModel.appendSin();
        assertEquals("sin(", listener.expression);
    }
    
    // Power after equals uses result
    
    @Test
    public void power_after_equals_uses_result() {
        viewModel.appendNumber("5");
        viewModel.calculateResult();
        assertEquals("5", listener.expression);
        viewModel.appendPower();
        assertEquals("5^", listener.expression);
        viewModel.appendNumber("2");
        viewModel.calculateResult();
        assertEquals("25", listener.expression);
    }
}
