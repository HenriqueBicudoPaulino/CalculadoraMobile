package com.example.testcalculadora.model;

public class ExpressionParser {
    
    public double evaluate(String expression) throws Exception {
        // Replace comma with dot for parsing
        expression = expression.replace(",", ".");
        expression = expression.replaceAll("\\s+", "");
        
        return parseExpression(new Parser(expression));
    }
    
    private double parseExpression(Parser parser) throws Exception {
        double result = parseTerm(parser);
        
        while (parser.hasNext()) {
            char op = parser.peek();
            if (op == '+' || op == '-') {
                parser.next();
                double term = parseTerm(parser);
                result = (op == '+') ? result + term : result - term;
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
