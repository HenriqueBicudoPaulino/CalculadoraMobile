package com.example.testcalculadora.viewmodel;

import com.example.testcalculadora.model.CalculatorState;
import com.example.testcalculadora.model.ExpressionParser;
import com.example.testcalculadora.utils.NumberFormatter;

public class CalculatorViewModel {
    
    private final CalculatorState state;
    private final ExpressionParser parser;
    private CalculatorListener listener;
    
    public interface CalculatorListener {
        void onStateChanged(String expression, String result);
    }
    
    public CalculatorViewModel() {
        this.state = new CalculatorState();
        this.parser = new ExpressionParser();
    }
    
    public void setListener(CalculatorListener listener) {
        this.listener = listener;
    }
    
    public void appendNumber(String number) {
        if (state.isLastWasEquals()) {
            state.setExpression("");
            state.setLastWasEquals(false);
        }
        
        state.setExpression(state.getExpression() + number);
        state.setLastWasOperator(false);
        notifyStateChanged();
        calculatePreview();
    }
    
    public void appendDecimalPoint() {
        if (state.isLastWasEquals()) {
            state.setExpression("0");
            state.setLastWasEquals(false);
        }
        
        String[] parts = state.getExpression().split("[+\\-*/()]");
        if (parts.length > 0) {
            String lastPart = parts[parts.length - 1];
            if (!lastPart.contains(",")) {
                if (state.isLastWasOperator() || state.getExpression().isEmpty()) {
                    state.setExpression(state.getExpression() + "0,");
                } else {
                    state.setExpression(state.getExpression() + ",");
                }
                state.setLastWasOperator(false);
                notifyStateChanged();
                calculatePreview();
            }
        }
    }
    
    public void appendOperator(String operator) {
        if (state.getExpression().isEmpty()) {
            if (operator.equals("-")) {
                state.setExpression("-");
                state.setLastWasOperator(true);
                notifyStateChanged();
            }
            return;
        }
        
        if (state.isLastWasEquals()) {
            String result = state.getResult();
            if (!result.isEmpty()) {
                state.setExpression(result);
            }
            state.setLastWasEquals(false);
        }
        
        if (state.isLastWasOperator()) {
            String expr = state.getExpression();
            state.setExpression(expr.substring(0, expr.length() - 1));
        }
        
        state.setExpression(state.getExpression() + operator);
        state.setLastWasOperator(true);
        notifyStateChanged();
    }
    
    public void appendPercent() {
        if (state.getExpression().isEmpty() || state.isLastWasOperator()) {
            return;
        }
        
        if (state.isLastWasEquals()) {
            String result = state.getResult();
            if (!result.isEmpty()) {
                state.setExpression(result);
            }
            state.setLastWasEquals(false);
        }
        
        try {
            String[] parts = state.getExpression().split("(?=[+\\-*/])|(?<=[+\\-*/])");
            if (parts.length > 0) {
                String lastPart = parts[parts.length - 1].trim();
                if (!lastPart.isEmpty() && !NumberFormatter.isOperator(lastPart)) {
                    double value = NumberFormatter.parse(lastPart);
                    double percentage = value / 100.0;
                    
                    int lastNumberStart = state.getExpression().lastIndexOf(lastPart);
                    String newExpr = state.getExpression().substring(0, lastNumberStart);
                    state.setExpression(newExpr + NumberFormatter.format(percentage));
                    notifyStateChanged();
                    calculatePreview();
                }
            }
        } catch (Exception e) {
            // Ignore errors
        }
    }
    
    public void addParentheses() {
        if (state.isLastWasEquals()) {
            state.setExpression("");
            state.setLastWasEquals(false);
        }
        
        if (state.getExpression().isEmpty() || state.isLastWasOperator() || 
            state.getExpression().endsWith("(")) {
            state.setExpression(state.getExpression() + "(");
            state.incrementOpenParentheses();
        } else if (state.getOpenParentheses() > 0) {
            state.setExpression(state.getExpression() + ")");
            state.decrementOpenParentheses();
        } else {
            if (!state.getExpression().isEmpty()) {
                state.setExpression(state.getExpression() + "*(");
                state.incrementOpenParentheses();
            } else {
                state.setExpression("(");
                state.incrementOpenParentheses();
            }
        }
        
        state.setLastWasOperator(false);
        notifyStateChanged();
        calculatePreview();
    }
    
    public void deleteLast() {
        if (state.getExpression().isEmpty()) {
            return;
        }
        
        if (state.isLastWasEquals()) {
            clear();
            return;
        }
        
        String expr = state.getExpression();
        char lastChar = expr.charAt(expr.length() - 1);
        
        if (lastChar == '(') {
            state.decrementOpenParentheses();
        } else if (lastChar == ')') {
            state.incrementOpenParentheses();
        }
        
        state.setExpression(expr.substring(0, expr.length() - 1));
        
        if (!state.getExpression().isEmpty()) {
            lastChar = state.getExpression().charAt(state.getExpression().length() - 1);
            state.setLastWasOperator(NumberFormatter.isOperator(String.valueOf(lastChar)));
        } else {
            state.setLastWasOperator(false);
        }
        
        notifyStateChanged();
        calculatePreview();
    }
    
    public void clear() {
        state.reset();
        notifyStateChanged();
    }
    
    public void calculateResult() {
        if (state.getExpression().isEmpty()) {
            return;
        }
        try {
            String expression = state.getExpression();
            for (int i = 0; i < state.getOpenParentheses(); i++) {
                expression += ")";
            }
            while (!expression.isEmpty() &&
                    NumberFormatter.isOperator(String.valueOf(expression.charAt(expression.length() - 1)))) {
                expression = expression.substring(0, expression.length() - 1);
            }
            double result = parser.evaluate(expression);
            String formattedResult = NumberFormatter.format(result); // 1. Formata e armazena
            state.setResult(formattedResult);       // 2. Define o resultado (para tvResult)
            state.setExpression(formattedResult);   // 3. Define a expressão (para tvExpression)
            state.setResult("");
            state.setLastWasEquals(true);
            state.setLastWasOperator(false);
            state.setOpenParentheses(0);            // 4. Zera a contagem de parênteses
            notifyStateChanged();
        } catch (Exception e) {
            state.setResult("Erro");
            notifyStateChanged();
        }
    }
    
    private void calculatePreview() {
        if (state.getExpression().isEmpty() || state.isLastWasOperator() || state.isLastWasEquals()) {
            if (!state.isLastWasEquals()) {
                state.setResult("");
                notifyStateChanged();
            }
            return;
        }
        
        try {
            String expr = state.getExpression();
            for (int i = 0; i < state.getOpenParentheses(); i++) {
                expr += ")";
            }
            
            while (!expr.isEmpty() && 
                   NumberFormatter.isOperator(String.valueOf(expr.charAt(expr.length() - 1)))) {
                expr = expr.substring(0, expr.length() - 1);
            }
            
            if (!expr.isEmpty()) {
                double preview = parser.evaluate(expr);
                String previewStr = NumberFormatter.format(preview);
                if (!expr.equals(previewStr)) {
                    state.setResult(previewStr);
                    notifyStateChanged();
                }
            }
        } catch (Exception e) {
            // Don't show error during typing
        }
    }
    
    private void notifyStateChanged() {
        if (listener != null) {
            listener.onStateChanged(state.getExpression(), state.getResult());
        }
    }
    
    // Scientific functions
    
    public void appendSin() {
        appendFunction("sin");
    }
    
    public void appendCos() {
        appendFunction("cos");
    }
    
    public void appendTan() {
        appendFunction("tan");
    }
    
    public void appendSqrt() {
        appendFunction("sqrt");
    }
    
    public void appendLog() {
        appendFunction("log");
    }
    
    public void appendLn() {
        appendFunction("ln");
    }
    
    public void appendPower() {
        if (state.getExpression().isEmpty()) {
            return;
        }
        
        if (state.isLastWasEquals()) {
            String result = state.getResult();
            if (!result.isEmpty()) {
                state.setExpression(result);
            }
            state.setLastWasEquals(false);
        }
        
        // Don't allow power after operator
        if (state.isLastWasOperator()) {
            return;
        }
        
        state.setExpression(state.getExpression() + "^");
        state.setLastWasOperator(true);
        notifyStateChanged();
    }
    
    private void appendFunction(String functionName) {
        if (state.isLastWasEquals()) {
            state.setExpression("");
            state.setLastWasEquals(false);
        }
        
        // If there's a number or closing parenthesis before the function, add multiplication
        if (!state.getExpression().isEmpty() && !state.isLastWasOperator() && 
            !state.getExpression().endsWith("(")) {
            state.setExpression(state.getExpression() + "*");
        }
        
        state.setExpression(state.getExpression() + functionName + "(");
        state.incrementOpenParentheses();
        state.setLastWasOperator(false);
        notifyStateChanged();
    }
}
