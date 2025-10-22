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
        double result = parsePower(parser);
        
        while (parser.hasNext()) {
            char op = parser.peek();
            if (op == '*' || op == '/') {
                parser.next();
                double factor = parsePower(parser);
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
    
    private double parsePower(Parser parser) throws Exception {
        double base = parseFactor(parser);
        
        // Right-associative: 2^3^2 = 2^(3^2) = 2^9 = 512
        if (parser.hasNext() && parser.peek() == '^') {
            parser.next();
            double exponent = parsePower(parser); // Recursive call for right associativity
            return Math.pow(base, exponent);
        }
        
        return base;
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
        
        // Handle functions (sin, cos, tan, sqrt, log, ln)
        if (Character.isLetter(c)) {
            return parseFunction(parser);
        }
        
        // Handle numbers
        return parseNumber(parser);
    }
    
    private double parseFunction(Parser parser) throws Exception {
        StringBuilder funcName = new StringBuilder();
        
        // Read function name
        while (parser.hasNext() && Character.isLetter(parser.peek())) {
            funcName.append(parser.next());
        }
        
        String function = funcName.toString().toLowerCase();
        
        // Expect opening parenthesis
        if (!parser.hasNext() || parser.peek() != '(') {
            throw new Exception("Esperado '(' após função");
        }
        parser.next(); // consume '('
        
        double argument = parseExpression(parser);
        
        // Expect closing parenthesis
        if (!parser.hasNext() || parser.next() != ')') {
            throw new Exception("Esperado ')' após argumento da função");
        }
        
        // Calculate function result
        switch (function) {
            case "sin":
                return Math.sin(Math.toRadians(argument));
            case "cos":
                return Math.cos(Math.toRadians(argument));
            case "tan":
                return Math.tan(Math.toRadians(argument));
            case "sqrt":
                if (argument < 0) {
                    throw new Exception("Raiz quadrada de número negativo");
                }
                return Math.sqrt(argument);
            case "log":
                if (argument <= 0) {
                    throw new Exception("Logaritmo de número não positivo");
                }
                return Math.log10(argument);
            case "ln":
                if (argument <= 0) {
                    throw new Exception("Logaritmo de número não positivo");
                }
                return Math.log(argument);
            default:
                throw new Exception("Função desconhecida: " + function);
        }
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
