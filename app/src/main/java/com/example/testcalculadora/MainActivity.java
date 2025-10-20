package com.example.testcalculadora;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * MainActivity - Calculadora completa com operações básicas
 * Implementa adição, subtração, multiplicação, divisão e entrada de números
 * com padrão brasileiro (vírgula como separador decimal)
 */
public class MainActivity extends Activity implements View.OnClickListener {

    // Constants
    private static final int MAX_DIGIT_LENGTH = 16;
    private static final String INITIAL_DISPLAY = "0";
    private static final String DECIMAL_SEPARATOR = ",";

    // UI Components
    private TextView tvDisplay;

    // State
    private StringBuilder currentInput;
    private double firstOperand;
    private String currentOperator;
    private boolean isNewInput;
    private boolean operationJustCompleted;

    // Formatter para exibição com padrão brasileiro
    private DecimalFormat decimalFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeFormatter();
        initializeViews();
        initializeState(savedInstanceState);
        setupButtons();
    }

    /**
     * Inicializa o formatador de números com padrão brasileiro
     */
    private void initializeFormatter() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("pt", "BR"));
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');
        decimalFormat = new DecimalFormat("#,##0.##########", symbols);
        decimalFormat.setMaximumFractionDigits(10);
    }

    /**
     * Inicializa as views da interface
     */
    private void initializeViews() {
        tvDisplay = findViewById(R.id.tvDisplay);
    }

    /**
     * Inicializa o estado da calculadora
     * @param savedInstanceState estado salvo anteriormente
     */
    private void initializeState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            String savedDisplay = savedInstanceState.getString("display", INITIAL_DISPLAY);
            currentInput = new StringBuilder(savedDisplay);
            firstOperand = savedInstanceState.getDouble("firstOperand", 0.0);
            currentOperator = savedInstanceState.getString("operator", "");
            isNewInput = savedInstanceState.getBoolean("isNewInput", true);
            operationJustCompleted = savedInstanceState.getBoolean("operationCompleted", false);
        } else {
            currentInput = new StringBuilder();
            firstOperand = 0.0;
            currentOperator = "";
            isNewInput = true;
            operationJustCompleted = false;
        }
        updateDisplay();
    }

    /**
     * Configura os listeners para todos os botões
     */
    private void setupButtons() {
        setupNumberButtons();
        setupOperationButtons();
    }

    /**
     * Configura os listeners para os botões numéricos e vírgula decimal
     */
    private void setupNumberButtons() {
        int[] numberButtonIds = {
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9,
            R.id.btnComma
        };

        for (int id : numberButtonIds) {
            Button button = findViewById(id);
            if (button != null) {
                button.setOnClickListener(this);
            }
        }
    }

    /**
     * Configura os listeners para os botões de operação
     */
    private void setupOperationButtons() {
        // Botão Clear
        Button btnClear = findViewById(R.id.btnClear);
        if (btnClear != null) {
            btnClear.setOnClickListener(v -> clearAll());
        }

        // Botão Delete
        Button btnDelete = findViewById(R.id.btnDelete);
        if (btnDelete != null) {
            btnDelete.setOnClickListener(v -> deleteLastCharacter());
        }

        // Botões de operação aritmética
        int[] operationButtonIds = {
            R.id.btnAdd, R.id.btnSubtract, R.id.btnMultiply, R.id.btnDivide
        };

        for (int id : operationButtonIds) {
            Button button = findViewById(id);
            if (button != null) {
                button.setOnClickListener(v -> {
                    Button btn = (Button) v;
                    handleOperation(btn.getText().toString());
                });
            }
        }

        // Botão Equals
        Button btnEquals = findViewById(R.id.btnEquals);
        if (btnEquals != null) {
            btnEquals.setOnClickListener(v -> calculateResult());
        }
    }

    @Override
    public void onClick(View v) {
        Button button = (Button) v;
        String buttonText = button.getText().toString();
        
        handleNumberInput(buttonText);
    }

    /**
     * Processa a entrada de números e vírgula decimal
     * @param input texto do botão pressionado
     */
    private void handleNumberInput(String input) {
        // Se uma operação acabou de ser completada e o usuário digita um número,
        // limpa tudo para começar um novo cálculo
        if (operationJustCompleted) {
            clearAll();
            operationJustCompleted = false;
        }

        // Se for uma nova entrada, limpa o buffer
        if (isNewInput) {
            currentInput.setLength(0);
            isNewInput = false;
        }

        // Validações
        if (!canAddCharacter(input)) {
            return;
        }

        // Adiciona o caractere
        currentInput.append(input);
        updateDisplay();
    }

    /**
     * Verifica se um caractere pode ser adicionado ao display
     * @param input caractere a ser adicionado
     * @return true se pode adicionar, false caso contrário
     */
    private boolean canAddCharacter(String input) {
        // Verifica o limite de caracteres
        if (currentInput.length() >= MAX_DIGIT_LENGTH) {
            return false;
        }

        // Não permite múltiplas vírgulas decimais
        if (DECIMAL_SEPARATOR.equals(input) && currentInput.toString().contains(DECIMAL_SEPARATOR)) {
            return false;
        }

        // Não permite vírgula decimal como primeiro caractere
        if (DECIMAL_SEPARATOR.equals(input) && currentInput.length() == 0) {
            currentInput.append("0");
        }

        return true;
    }

    /**
     * Processa uma operação aritmética
     * @param operator operador (+, -, ×, ÷)
     */
    private void handleOperation(String operator) {
        // Se há uma operação pendente, calcula o resultado primeiro
        if (!currentOperator.isEmpty() && !isNewInput) {
            calculateResult();
        } else if (!isNewInput || operationJustCompleted) {
            // Pega o valor atual como primeiro operando
            firstOperand = getCurrentValue();
        }

        currentOperator = operator;
        isNewInput = true;
        operationJustCompleted = false;
    }

    /**
     * Calcula o resultado da operação atual
     */
    private void calculateResult() {
        if (currentOperator.isEmpty() || isNewInput) {
            return;
        }

        double secondOperand = getCurrentValue();
        double result = 0.0;
        boolean validOperation = true;

        switch (currentOperator) {
            case "+":
                result = firstOperand + secondOperand;
                break;
            case "-":
                result = firstOperand - secondOperand;
                break;
            case "×":
                result = firstOperand * secondOperand;
                break;
            case "÷":
                if (secondOperand == 0.0) {
                    displayError("Erro: Divisão por zero");
                    validOperation = false;
                } else {
                    result = firstOperand / secondOperand;
                }
                break;
        }

        if (validOperation) {
            displayResult(result);
            firstOperand = result;
            currentOperator = "";
            isNewInput = true;
            operationJustCompleted = true;
        }
    }

    /**
     * Exibe o resultado no display
     * @param result resultado a ser exibido
     */
    private void displayResult(double result) {
        String formattedResult = formatNumber(result);
        
        // Verifica se o resultado ultrapassa o limite de caracteres
        if (formattedResult.length() > MAX_DIGIT_LENGTH) {
            // Tenta formato científico se o número for muito grande
            if (Math.abs(result) >= 1e10 || (Math.abs(result) < 1e-6 && result != 0)) {
                formattedResult = String.format(Locale.US, "%.6e", result).replace('.', ',');
            } else {
                // Reduz as casas decimais
                DecimalFormat shortFormat = (DecimalFormat) decimalFormat.clone();
                shortFormat.setMaximumFractionDigits(6);
                formattedResult = shortFormat.format(result);
            }
        }
        
        currentInput.setLength(0);
        currentInput.append(formattedResult);
        updateDisplay();
    }

    /**
     * Exibe uma mensagem de erro
     * @param message mensagem de erro
     */
    private void displayError(String message) {
        tvDisplay.setText(message);
        currentInput.setLength(0);
        currentOperator = "";
        firstOperand = 0.0;
        isNewInput = true;
        operationJustCompleted = false;
    }

    /**
     * Formata um número seguindo o padrão brasileiro
     * @param value valor a ser formatado
     * @return string formatada
     */
    private String formatNumber(double value) {
        // Remove zeros desnecessários à direita
        String formatted = decimalFormat.format(value);
        
        // Remove separador de milhar para simplificar
        formatted = formatted.replace(".", "");
        
        return formatted;
    }

    /**
     * Limpa tudo e reseta a calculadora
     */
    private void clearAll() {
        currentInput.setLength(0);
        firstOperand = 0.0;
        currentOperator = "";
        isNewInput = true;
        operationJustCompleted = false;
        updateDisplay();
    }

    /**
     * Remove o último caractere digitado
     */
    private void deleteLastCharacter() {
        if (currentInput.length() > 0 && !isNewInput) {
            currentInput.setLength(currentInput.length() - 1);
            updateDisplay();
        }
    }

    /**
     * Atualiza o display com o valor atual
     */
    private void updateDisplay() {
        String displayText;
        
        if (currentInput.length() == 0) {
            displayText = INITIAL_DISPLAY;
        } else {
            displayText = currentInput.toString();
        }
        
        tvDisplay.setText(displayText);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("display", currentInput.toString());
        outState.putDouble("firstOperand", firstOperand);
        outState.putString("operator", currentOperator);
        outState.putBoolean("isNewInput", isNewInput);
        outState.putBoolean("operationCompleted", operationJustCompleted);
    }

    /**
     * Obtém o valor numérico atual do display
     * @return valor atual ou 0.0 se inválido
     */
    private double getCurrentValue() {
        try {
            String value = currentInput.toString();
            if (value.isEmpty()) {
                return 0.0;
            }
            // Substitui vírgula por ponto para parsing
            value = value.replace(',', '.');
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
