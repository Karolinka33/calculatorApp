package com.example.mycalculator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView txtCalcProcess, txtCalcRes;
    private String currentInput = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtCalcProcess = findViewById(R.id.txtCalcProccess);
        txtCalcRes = findViewById(R.id.txtCalcRes);

        //skaiciu mygtukai
        int[] numberButtons = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        };

        for (int id : numberButtons) {
            findViewById(id).setOnClickListener(numberClickListener);
        }

        //operatoriai ir specialus mygtukai
        findViewById(R.id.btnAdd).setOnClickListener(operatorClickListener);
        findViewById(R.id.btnSubtract).setOnClickListener(operatorClickListener);
        findViewById(R.id.btnMultiply).setOnClickListener(operatorClickListener);
        findViewById(R.id.btnDivide).setOnClickListener(operatorClickListener);

        findViewById(R.id.btnDot).setOnClickListener(numberClickListener);
        findViewById(R.id.btnDelete).setOnClickListener(deleteClickListener);
        findViewById(R.id.btnClear).setOnClickListener(clearClickListener);
        findViewById(R.id.btnSign).setOnClickListener(signClickListener);
        findViewById(R.id.btnSqrt).setOnClickListener(sqrtClickListener);
        findViewById(R.id.btnEquals).setOnClickListener(equalsClickListener);
    }

    // CLICK LISTENER'IAI
    private final View.OnClickListener numberClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button b = (Button) v;
            currentInput += b.getText().toString();
            txtCalcProcess.setText(currentInput);
        }
    };

    private final View.OnClickListener operatorClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button b = (Button) v;
            currentInput += b.getText().toString();
            txtCalcProcess.setText(currentInput);
        }
    };

    private final View.OnClickListener deleteClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!currentInput.isEmpty()) {
                currentInput = currentInput.substring(0, currentInput.length() - 1);
                txtCalcProcess.setText(currentInput);
            }
        }
    };

    private final View.OnClickListener clearClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            currentInput = "";
            txtCalcProcess.setText("");
            txtCalcRes.setText("0");
        }
    };

    private final View.OnClickListener signClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!currentInput.isEmpty()) {
                if (currentInput.startsWith("-")) {
                    currentInput = currentInput.substring(1);
                } else {
                    currentInput = "-" + currentInput;
                }
                txtCalcProcess.setText(currentInput);
            }
        }
    };

    private final View.OnClickListener sqrtClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!currentInput.isEmpty()) {
                try {
                    double value = Double.parseDouble(currentInput);
                    double result = Math.sqrt(value);
                    txtCalcRes.setText(String.valueOf((int)result));
                    currentInput = String.valueOf(result);
                    txtCalcProcess.setText(currentInput);
                } catch (NumberFormatException e) {
                    txtCalcRes.setText("UhOhhhh you did something wrong");
                }
            }
        }
    };

    private final View.OnClickListener equalsClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!currentInput.isEmpty()) {
                try {
                    double result = evaluateExpression(currentInput);
                    //tikimasi, kad txtCalcRes rodys tik sveikus, jei rezultatas sveikas
                    if (result == (int) result) {
                        txtCalcRes.setText(String.valueOf((int) result));
                    } else {
                        txtCalcRes.setText(String.valueOf(result));
                    }
                    //txtCalcProcess lieka toks pats
                } catch (Exception e) {
                    txtCalcRes.setText("UhOhhhh you did something wrong");
                }
            }
        }
    };

    private double evaluateExpression(String expression) {

        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < expression.length()) ? expression.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < expression.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if      (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if      (eat('*')) x *= parseFactor();
                    else if (eat('/')) x /= parseFactor();
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor();
                if (eat('-')) return -parseFactor();

                double x;
                int startPos = this.pos;
                if (eat('(')) {
                    x = parseExpression();
                    eat(')');
                } else {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(expression.substring(startPos, this.pos));
                }

                return x;
            }
        }.parse();
    }
}
