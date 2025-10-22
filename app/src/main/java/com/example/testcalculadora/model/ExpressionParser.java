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
        // MODIFICADO: Chama parsePower em vez de parseFactor para
        // dar prioridade para a exponenciação
        double result = parsePower(parser);

        while (parser.hasNext()) {
            char op = parser.peek();
            if (op == '*' || op == '/') {
                parser.next();
                // Chama parsePower aqui também
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

    // NOVO: Método para lidar com exponenciação (^)
    // Tem precedência maior que * e /
    private double parsePower(Parser parser) throws Exception {
        double result = parseFactor(parser);

        while (parser.hasNext() && parser.peek() == '^') {
            parser.next();
            // Para exponenciação, é comum ser associativo à direita,
            // mas implementaremos associativo à esquerda por simplicidade,
            // que é como o parseTerm funciona. (ex: 2^3^2 = (2^3)^2 = 64)
            double exponent = parseFactor(parser);
            result = Math.pow(result, exponent);
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

        // NOVO: Handle functions (sqrt, sin, cos, tan...)
        if (Character.isLetter(c)) {
            String func = parseFunction(parser);

            if (!parser.hasNext() || parser.next() != '(') {
                throw new Exception("Esperado '(' após " + func);
            }

            double value = parseExpression(parser);

            if (!parser.hasNext() || parser.next() != ')') {
                throw new Exception("Esperado ')' após " + func);
            }

            // Adiciona a lógica da função
            switch (func) {
                case "sqrt":
                    if (value < 0) {
                        throw new Exception("Raiz de núm. negativo");
                    }
                    return Math.sqrt(value);
                // Futuramente:
                // case "sin":
                //    return Math.sin(value);
                // case "cos":
                //    return Math.cos(value);
                // case "tan":
                //    return Math.tan(value);
                default:
                    throw new Exception("Função '" + func + "' desconhecida");
            }
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

    // NOVO: Helper para ler nomes de funções
    private String parseFunction(Parser parser) {
        StringBuilder sb = new StringBuilder();
        while (parser.hasNext() && Character.isLetter(parser.peek())) {
            sb.append(parser.next());
        }
        return sb.toString();
    }

    private double parseNumber(Parser parser) throws Exception {
        StringBuilder sb = new StringBuilder();

        while (parser.hasNext() && (Character.isDigit(parser.peek()) || parser.peek() == '.')) {
            sb.append(parser.next());
        }

        if (sb.length() == 0) {
            // Verificação para o caso de "sqrt(5)"
            if (parser.hasNext() && (parser.peek() == ')' || Character.isLetter(parser.peek()))) {
                throw new Exception("Número esperado");
            }
            // Pode ser o final da expressão
            throw new Exception("Expressão inválida");
        }

        return Double.parseDouble(sb.toString());
    }

    // A classe Parser interna permanece a mesma
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
