package com.example.testcalculadora;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    
    private TextView tvExpression;
    private TextView tvResult;
    private String currentExpression = "";
    private boolean lastWasOperator = false;
    private boolean lastWasEquals = false;
    private int openParentheses = 0;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.telaInicial);
        
        tvExpression = findViewById(R.id.tvExpression);
        tvResult = findViewById(R.id.tvResult);
        
        setupNumberButtons();
        setupOperatorButtons();
        setupFunctionButtons();
    }
    
    private void setupNumberButtons() {
        int[] numberIds = {
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        };
        
        View.OnClickListener numberClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button button = (Button) v;
                appendToExpression(button.getText().toString());
            }
        };
        
        for (int id : numberIds) {
            findViewById(id).setOnClickListener(numberClickListener);
        }
        
        // Decimal point
        findViewById(R.id.btnDot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appendDecimalPoint();
            }
        });
    }
    
    private void setupOperatorButtons() {
        View.OnClickListener operatorClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button button = (Button) v;
                String operator = button.getText().toString();
                appendOperator(operator);
            }
        };
        
        findViewById(R.id.btnAdd).setOnClickListener(operatorClickListener);
        findViewById(R.id.btnSub).setOnClickListener(operatorClickListener);
        findViewById(R.id.btnMul).setOnClickListener(operatorClickListener);
        findViewById(R.id.btnDiv).setOnClickListener(operatorClickListener);
    }
    
    private void setupFunctionButtons() {
        // Clear button
        findViewById(R.id.btnClear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
            }
        });
        
        // Delete button
        findViewById(R.id.btnDelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteLast();
            }
        });
        
        // Parentheses button
        findViewById(R.id.btnParentheses).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addParentheses();
            }
        });
        
        // Percent button
        findViewById(R.id.btnPercent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appendPercent();
            }
        });
        
        // Equals button
        findViewById(R.id.btnEq).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateResult();
            }
        });
    }
    
    private void appendToExpression(String value) {
        if (lastWasEquals) {
            currentExpression = "";
            lastWasEquals = false;
        }
        currentExpression += value;
        lastWasOperator = false;
        updateDisplay();
    }
    
    private void appendDecimalPoint() {
        if (lastWasEquals) {
            currentExpression = "0";
            lastWasEquals = false;
        }
        
        // Check if current number already has a decimal point
        String[] parts = currentExpression.split("[+\\-*/()]");
        if (parts.length > 0) {
            String lastPart = parts[parts.length - 1];
            if (!lastPart.contains(",")) {
                if (lastWasOperator || currentExpression.isEmpty()) {
                    currentExpression += "0,";
                } else {
                    currentExpression += ",";
                }
                lastWasOperator = false;
                updateDisplay();
            }
        }
    }
    
    private void appendOperator(String operator) {
        if (currentExpression.isEmpty()) {
            if (operator.equals("-")) {
                currentExpression = "-";
                lastWasOperator = true;
                updateDisplay();
            }
            return;
        }
        
        if (lastWasEquals) {
            String result = tvResult.getText().toString();
            if (!result.isEmpty()) {
                currentExpression = result;
            }
            lastWasEquals = false;
        }
        
        if (lastWasOperator) {
            // Replace last operator
            currentExpression = currentExpression.substring(0, currentExpression.length() - 1);
        }
        
        currentExpression += operator;
        lastWasOperator = true;
        updateDisplay();
    }
    
    private void appendPercent() {
        if (currentExpression.isEmpty() || lastWasOperator) {
            return;
        }
        
        if (lastWasEquals) {
            String result = tvResult.getText().toString();
            if (!result.isEmpty()) {
                currentExpression = result;
            }
            lastWasEquals = false;
        }
        
        // Get the last number and convert to percentage
        try {
            String[] parts = currentExpression.split("(?=[+\\-*/])|(?<=[+\\-*/])");
            if (parts.length > 0) {
                String lastPart = parts[parts.length - 1].trim();
                if (!lastPart.isEmpty() && !isOperator(lastPart)) {
                    double value = parseNumber(lastPart);
                    double percentage = value / 100.0;
                    
                    // Remove the last number and add the percentage
                    int lastNumberStart = currentExpression.lastIndexOf(lastPart);
                    currentExpression = currentExpression.substring(0, lastNumberStart);
                    currentExpression += formatNumber(percentage);
                    updateDisplay();
                }
            }
        } catch (Exception e) {
            // Ignore errors
        }
    }
    
    private void addParentheses() {
        if (lastWasEquals) {
            currentExpression = "";
            lastWasEquals = false;
        }
        
        // Decide whether to add opening or closing parenthesis
        if (currentExpression.isEmpty() || lastWasOperator || 
            currentExpression.endsWith("(")) {
            currentExpression += "(";
            openParentheses++;
        } else if (openParentheses > 0) {
            currentExpression += ")";
            openParentheses--;
        } else {
            // Add operator and opening parenthesis
            if (!currentExpression.isEmpty()) {
                currentExpression += "*(";
                openParentheses++;
            } else {
                currentExpression += "(";
                openParentheses++;
            }
        }
        
        lastWasOperator = false;
        updateDisplay();
    }
    
    private void deleteLast() {
        if (currentExpression.isEmpty()) {
            return;
        }
        
        if (lastWasEquals) {
            clear();
            return;
        }
        
        char lastChar = currentExpression.charAt(currentExpression.length() - 1);
        
        if (lastChar == '(') {
            openParentheses--;
        } else if (lastChar == ')') {
            openParentheses++;
        }
        
        currentExpression = currentExpression.substring(0, currentExpression.length() - 1);
        
        if (!currentExpression.isEmpty()) {
            lastChar = currentExpression.charAt(currentExpression.length() - 1);
            lastWasOperator = isOperator(String.valueOf(lastChar));
        } else {
            lastWasOperator = false;
        }
        
        updateDisplay();
    }
    
    private void clear() {
        currentExpression = "";
        lastWasOperator = false;
        lastWasEquals = false;
        openParentheses = 0;
        updateDisplay();
    }
    
    private void calculateResult() {
        if (currentExpression.isEmpty()) {
            return;
        }
        
        try {
            // Close any open parentheses
            String expression = currentExpression;
            for (int i = 0; i < openParentheses; i++) {
                expression += ")";
            }
            
            // Remove trailing operator
            while (!expression.isEmpty() && isOperator(String.valueOf(expression.charAt(expression.length() - 1)))) {
                expression = expression.substring(0, expression.length() - 1);
            }
            
            double result = evaluateExpression(expression);
            
            String resultStr = formatNumber(result);
            tvResult.setText(resultStr);
            lastWasEquals = true;
            lastWasOperator = false;
            
        } catch (Exception e) {
            tvResult.setText("Erro");
        }
    }
    
    private double evaluateExpression(String expression) throws Exception {
        // Replace comma with dot for parsing
        expression = expression.replace(",", ".");
        
        // Remove spaces
        expression = expression.replaceAll("\\s+", "");
        
        return evaluate(expression);
    }
    
    // Recursive descent parser for mathematical expressions
    private double evaluate(String expression) throws Exception {
        return parseExpression(new Parser(expression));
    }
    
    private double parseExpression(Parser parser) throws Exception {
        double result = parseTerm(parser);
        
        while (parser.hasNext()) {
            char op = parser.peek();
            if (op == '+' || op == '-') {
                parser.next();
                double term = parseTerm(parser);
                if (op == '+') {
                    result += term;
                } else {
                    result -= term;
                }
            } else {
                break;
            }
        }
        
        return result;
    }
    
    private double parseTerm(Parser parser) throws Exception {
        double result = parseFactor(parser);
        
        while (parser.hasNext()) {
            char op = parser.peek();
            if (op == '*' || op == '/') {
                parser.next();
                double factor = parseFactor(parser);
                if (op == '*') {
                    result *= factor;
                } else {
                    if (factor == 0) {
                        throw new Exception("Divisão por zero");
                    }
                    result /= factor;
                }
            } else {
                break;
            }
        }
        
        return result;
    }
    
    private double parseFactor(Parser parser) throws Exception {
        if (!parser.hasNext()) {
            throw new Exception("Expressão incompleta");
        }
        
        char c = parser.peek();
        
        // Handle negative numbers
        if (c == '-') {
            parser.next();
            return -parseFactor(parser);
        }
        
        // Handle positive sign
        if (c == '+') {
            parser.next();
            return parseFactor(parser);
        }
        
        // Handle parentheses
        if (c == '(') {
            parser.next();
            double result = parseExpression(parser);
            if (!parser.hasNext() || parser.next() != ')') {
                throw new Exception("Parênteses não fechado");
            }
            return result;
        }
        
        // Handle numbers
        return parseNumber(parser);
    }
    
    private double parseNumber(Parser parser) throws Exception {
        StringBuilder sb = new StringBuilder();
        
        while (parser.hasNext() && (Character.isDigit(parser.peek()) || parser.peek() == '.')) {
            sb.append(parser.next());
        }
        
        if (sb.length() == 0) {
            throw new Exception("Número esperado");
        }
        
        return Double.parseDouble(sb.toString());
    }
    
    private double parseNumber(String str) {
        return Double.parseDouble(str.replace(",", "."));
    }
    
    private String formatNumber(double number) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');
        
        DecimalFormat df = new DecimalFormat("#.##########", symbols);
        String result = df.format(number);
        
        // Remove trailing zeros after decimal point
        if (result.contains(",")) {
            result = result.replaceAll("0*$", "").replaceAll(",$", "");
        }
        
        return result;
    }
    
    private boolean isOperator(String str) {
        return str.equals("+") || str.equals("-") || str.equals("*") || str.equals("/");
    }
    
    private void updateDisplay() {
        tvExpression.setText(currentExpression);
        
        // Show live calculation preview (optional)
        if (!currentExpression.isEmpty() && !lastWasOperator) {
            try {
                String expr = currentExpression;
                for (int i = 0; i < openParentheses; i++) {
                    expr += ")";
                }
                
                // Remove trailing operator
                while (!expr.isEmpty() && isOperator(String.valueOf(expr.charAt(expr.length() - 1)))) {
                    expr = expr.substring(0, expr.length() - 1);
                }
                
                if (!expr.isEmpty()) {
                    double preview = evaluateExpression(expr);
                    // Only show preview if it's different from the expression
                    if (!expr.equals(formatNumber(preview))) {
                        tvResult.setText(formatNumber(preview));
                    }
                }
            } catch (Exception e) {
                // Don't show error during typing
                if (lastWasEquals) {
                    tvResult.setText("");
                }
            }
        } else if (lastWasEquals) {
            // Keep result visible after equals
        } else {
            tvResult.setText("");
        }
    }
    
    // Helper class for parsing expressions
    private static class Parser {
        private final String expression;
        private int position;
        
        public Parser(String expression) {
            this.expression = expression;
            this.position = 0;
        }
        
        public boolean hasNext() {
            return position < expression.length();
        }
        
        public char peek() {
            return expression.charAt(position);
        }
        
        public char next() {
            return expression.charAt(position++);
        }
    }
}