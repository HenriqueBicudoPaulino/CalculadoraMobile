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
        this.state = CalculatorState.getInstance();
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

        String[] parts = state.getExpression().split("[+\\-*/()^]"); // Adicionado ^
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
            // MODIFICADO: usa a expressão (que agora é o resultado)
            if (!state.getExpression().isEmpty()) {
                // state.setExpression(state.getExpression()); // Não precisa
            }
            state.setLastWasEquals(false);
        }

        if (state.isLastWasOperator()) {
            // Não permite ** ou // ou ^^
            if (!operator.equals("^") && !state.getExpression().endsWith("^")) {
                String expr = state.getExpression();
                state.setExpression(expr.substring(0, expr.length() - 1));
            } else if (operator.equals("^") && state.getExpression().endsWith("^")) {
                return; // Impede ^^
            }
        }

        state.setExpression(state.getExpression() + operator);
        state.setLastWasOperator(true);
        notifyStateChanged();
    }

    // Método para adicionar funções matemáticas
    // Método para adicionar expressões (como parênteses)
    public void appendExpression(String expression) {
        if (state.isLastWasEquals()) {
            state.setExpression("");
            state.setLastWasEquals(false);
        }
        
        state.setExpression(state.getExpression() + expression);
        state.setLastWasOperator(false);
        notifyStateChanged();
        calculatePreview();
    }

    public void appendFunction(String function) {
        if (state.isLastWasEquals()) {
            state.setExpression("");
            state.setLastWasEquals(false);
        }

        String expr = state.getExpression();
        // Adiciona * se estiver digitando um número antes: 5sqrt( -> 5*sqrt(
        if (!expr.isEmpty() && (Character.isDigit(expr.charAt(expr.length()-1)) || expr.endsWith(")"))) {
            state.setExpression(expr + "*" + function + "(");
        } else {
            state.setExpression(expr + function + "(");
        }

        state.incrementOpenParentheses();
        state.setLastWasOperator(false);
        notifyStateChanged();
    }

    // Método atualizado para funções trigonométricas
    public void appendTrigFunction(String function) {
        if (state.isLastWasEquals()) {
            state.setExpression("");
            state.setLastWasEquals(false);
        }

        String expr = state.getExpression();
        // Adiciona * se estiver digitando um número antes: 5sin( -> 5*sin(
        if (!expr.isEmpty() && (Character.isDigit(expr.charAt(expr.length()-1)) || expr.endsWith(")"))) {
            expr = expr + "*";
        }
        
        // Usa sen em vez de sin para português
        String displayFunction = function;
        if (function.equals("sin")) {
            displayFunction = "sen";
        }
        
        state.setExpression(expr + displayFunction + "(");
        state.incrementOpenParentheses();
        state.setLastWasOperator(false);
        notifyStateChanged();
    }

    // Método para alternar entre radianos e graus
    public void toggleRadianMode() {
        state.toggleRadianMode();
        // Recalcula o resultado quando mudar o modo
        calculatePreview();
        notifyStateChanged();
    }

    // Método para verificar se está em modo radianos
    public boolean isRadianMode() {
        return state.isRadianMode();
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
            String[] parts = state.getExpression().split("(?=[+\\-*/^])|(?<=[+\\-*/^])"); // Adicionado ^
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

        // NOVO: Lógica para apagar "sqrt("
        if (expr.endsWith("sqrt(")) {
            state.setExpression(expr.substring(0, expr.length() - 5));
            state.decrementOpenParentheses();
        } else {
            char lastChar = expr.charAt(expr.length() - 1);

            if (lastChar == '(') {
                state.decrementOpenParentheses();
            } else if (lastChar == ')') {
                state.incrementOpenParentheses();
            }
            state.setExpression(expr.substring(0, expr.length() - 1));
        }

        if (!state.getExpression().isEmpty()) {
            char lastChar = state.getExpression().charAt(state.getExpression().length() - 1);
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

    // MÉTODO MODIFICADO (Lógica de "passar resultado para cima")
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
            String formattedResult = NumberFormatter.format(result);

            state.setExpression(formattedResult);   // 1. Coloca o resultado em tvExpression
            state.setResult("");                  // 2. Limpa tvResult

            state.setLastWasEquals(true);
            state.setLastWasOperator(false);
            state.setOpenParentheses(0);

            notifyStateChanged();

        } catch (Exception e) {
            state.setResult("Erro: " + e.getMessage()); // Mostra o erro
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
            state.setResult(""); // Limpa o preview se houver erro de digitação
            notifyStateChanged();
        }
    }

    private void notifyStateChanged() {
        if (listener != null) {
            listener.onStateChanged(state.getExpression(), state.getResult());
        }
    }
}
