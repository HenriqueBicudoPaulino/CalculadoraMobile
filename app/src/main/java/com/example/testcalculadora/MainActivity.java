package com.example.testcalculadora;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.example.testcalculadora.viewmodel.CalculatorViewModel;

public class MainActivity extends AppCompatActivity implements CalculatorViewModel.CalculatorListener {

    private TextView tvExpression;
    private TextView tvResult;
    private CalculatorViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // O Android irá carregar automaticamente res/layout-land/telainicial.xml
        // se estiver na horizontal, ou res/layout/telainicial.xml se estiver na vertical.
        setContentView(R.layout.telainicial);

        initializeViews();
        initializeViewModel();
        setupButtons();
    }

    private void initializeViews() {
        tvExpression = findViewById(R.id.tvExpression);
        tvResult = findViewById(R.id.tvResult);
    }

    private void initializeViewModel() {
        viewModel = new CalculatorViewModel();
        viewModel.setListener(this);
    }

    private void setupButtons() {
        setupNumberButtons();
        setupOperatorButtons();
        setupFunctionButtons();

        // NOVO: Chama o setup para os botões científicos
        setupScientificButtons();
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
                viewModel.appendNumber(button.getText().toString());
            }
        };

        for (int id : numberIds) {
            findViewById(id).setOnClickListener(numberClickListener);
        }

        // Decimal point
        findViewById(R.id.btnDot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.appendDecimalPoint();
            }
        });
    }

    private void setupOperatorButtons() {
        View.OnClickListener operatorClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button button = (Button) v;
                String operator = button.getText().toString();

                // Exceção para x^y, que enviaremos como '^'
                if (v.getId() == R.id.btnPow) {
                    operator = "^";
                }

                viewModel.appendOperator(operator);
            }
        };

        findViewById(R.id.btnAdd).setOnClickListener(operatorClickListener);
        findViewById(R.id.btnSub).setOnClickListener(operatorClickListener);
        findViewById(R.id.btnMul).setOnClickListener(operatorClickListener);
        findViewById(R.id.btnDiv).setOnClickListener(operatorClickListener);

        // NOVO: Adiciona o listener para o botão de potência (btnPow)
        // Usamos um helper para verificar se o botão existe no layout atual
        setSafeClickListener(R.id.btnPow, operatorClickListener);
    }

    private void setupFunctionButtons() {
        // Clear button
        findViewById(R.id.btnClear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.clear();
            }
        });

        // Delete button
        findViewById(R.id.btnDelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.deleteLast();
            }
        });

        // Parentheses button
        findViewById(R.id.btnParentheses).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.addParentheses();
            }
        });

        // Percent button
        findViewById(R.id.btnPercent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.appendPercent();
            }
        });

        // Equals button
        findViewById(R.id.btnEq).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.calculateResult();
            }
        });
    }

    // Método para configurar botões científicos (que só existem no layout landscape)
    private void setupScientificButtons() {
        // Raiz Quadrada (sqrt)
        setSafeClickListener(R.id.btnSqrt, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.appendFunction("sqrt");
            }
        });

        // Funções Trigonométricas
        setSafeClickListener(R.id.btnSin, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.appendTrigFunction("sin");
            }
        });

        setSafeClickListener(R.id.btnCos, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.appendTrigFunction("cos");
            }
        });

        setSafeClickListener(R.id.btnTan, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.appendTrigFunction("tan");
            }
        });

        // Botão RAD/DEG
        final Button btnRad = findViewById(R.id.btnRad);
        if (btnRad != null) {
            btnRad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewModel.toggleRadianMode();
                    btnRad.setText(viewModel.isRadianMode() ? "DEG" : "RAD");
                }
            });
        }
    }

    // NOVO: Método helper para adicionar listeners com segurança (evita crash)
    private void setSafeClickListener(int id, View.OnClickListener listener) {
        View view = findViewById(id);
        if (view != null) {
            view.setOnClickListener(listener);
        }
    }

    @Override
    public void onStateChanged(String expression, String result) {
        tvExpression.setText(expression);
        tvResult.setText(result);
    }
}

