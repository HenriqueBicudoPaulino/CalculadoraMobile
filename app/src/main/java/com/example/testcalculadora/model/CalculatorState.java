package com.example.testcalculadora.model;

public class CalculatorState {
    // Singleton instance
    private static CalculatorState instance;

    private String expression;
    private String result;
    private boolean lastWasOperator;
    private boolean lastWasEquals;
    private int openParentheses;
    private boolean isRadianMode;

    // Singleton getter
    public static synchronized CalculatorState getInstance() {
        if (instance == null) {
            instance = new CalculatorState();
        }
        return instance;
    }

    private CalculatorState() {
        this.expression = "";
        this.result = "";
        this.lastWasOperator = false;
        this.lastWasEquals = false;
        this.openParentheses = 0;
        this.isRadianMode = false; // Começa em modo graus
    }

    // Getters
    public String getExpression() { 
        return expression; 
    }
    
    public String getResult() { 
        return result; 
    }
    
    public boolean isLastWasOperator() { 
        return lastWasOperator; 
    }
    
    public boolean isLastWasEquals() { 
        return lastWasEquals; 
    }
    
    public int getOpenParentheses() { 
        return openParentheses; 
    }

    // Setters
    public void setExpression(String expression) { 
        this.expression = expression; 
    }
    
    public void setResult(String result) { 
        this.result = result; 
    }
    
    public void setLastWasOperator(boolean lastWasOperator) { 
        this.lastWasOperator = lastWasOperator; 
    }
    
    public void setLastWasEquals(boolean lastWasEquals) { 
        this.lastWasEquals = lastWasEquals; 
    }
    
    public void setOpenParentheses(int openParentheses) { 
        this.openParentheses = openParentheses; 
    }

    public void incrementOpenParentheses() { 
        this.openParentheses++; 
    }
    
    public void decrementOpenParentheses() { 
        this.openParentheses--; 
    }

    public void reset() {
        this.expression = "";
        this.result = "";
        this.lastWasOperator = false;
        this.lastWasEquals = false;
        this.openParentheses = 0;
        // Não resetamos o modo radianos, pois é uma configuração do usuário
    }

    // NOVO: Getters e Setters para o modo radianos
    public boolean isRadianMode() {
        return isRadianMode;
    }

    public void setRadianMode(boolean radianMode) {
        isRadianMode = radianMode;
    }

    // NOVO: Toggle do modo radianos
    public void toggleRadianMode() {
        isRadianMode = !isRadianMode;
    }
}
