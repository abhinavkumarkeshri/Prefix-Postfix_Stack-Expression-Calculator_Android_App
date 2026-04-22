package com.example.stackexpressioncalculator;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import java.util.*;

public class MainActivity extends AppCompatActivity {

    private EditText inputInfix;
    private RadioGroup convertOptionGroup;
    private Button calculateButton;
    private TextView resultText;
    private ListView stepsListView;
    private ArrayAdapter<String> stepsAdapter;
    private ArrayList<String> stepsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputInfix = findViewById(R.id.inputInfix);
        convertOptionGroup = findViewById(R.id.convertOptionGroup);
        calculateButton = findViewById(R.id.calculateButton);
        resultText = findViewById(R.id.resultText);
        stepsListView = findViewById(R.id.stepsListView);

        stepsList = new ArrayList<>();
        stepsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, stepsList);
        stepsListView.setAdapter(stepsAdapter);

        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String infix = inputInfix.getText().toString().trim();

                if (infix.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter an infix expression", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check for spaces in the expression
                if (infix.contains(" ")) {
                    Toast.makeText(MainActivity.this, "Please remove the spaces present between the characters", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check for invalid characters
                if (!isValidExpression(infix)) {
                    Toast.makeText(MainActivity.this, "Invalid characters in expression. Only A-Z, a-z, 0-9, and +, -, *, /, ^, (, ) are allowed.", Toast.LENGTH_SHORT).show();
                    return;
                }

                int selectedOption = convertOptionGroup.getCheckedRadioButtonId();
                if (selectedOption == R.id.optionPostfix) {
                    convertToPostfix(infix);
                } else if (selectedOption == R.id.optionPrefix) {
                    convertToPrefix(infix);
                }
            }
        });
    }

    private void convertToPostfix(String infix) {
        stepsList.clear();
        Stack<Character> stack = new Stack<>();
        StringBuilder postfix = new StringBuilder();

        for (int i = 0; i < infix.length(); i++) {
            char c = infix.charAt(i);

            if (Character.isLetterOrDigit(c)) {
                postfix.append(c);
            } else if (c == '(') {
                stack.push(c);
            } else if (c == ')') {
                while (!stack.isEmpty() && stack.peek() != '(') {
                    postfix.append(stack.pop());
                }
                stack.pop();
            } else {
                while (!stack.isEmpty() && precedence(c) <= precedence(stack.peek())) {
                    postfix.append(stack.pop());
                }
                stack.push(c);
            }

            stepsList.add(String.format("Expression: %s, Stack: %s", postfix.toString(), stack.toString()));
        }

        while (!stack.isEmpty()) {
            postfix.append(stack.pop());
            stepsList.add(String.format("Expression: %s, Stack: %s", postfix.toString(), stack.toString()));
        }

        resultText.setText("Result Expression: " + postfix.toString());
        stepsAdapter.notifyDataSetChanged();
    }

    private void convertToPrefix(String infix) {
        stepsList.clear();
        Stack<Character> stack = new Stack<>();
        Stack<String> resultStack = new Stack<>();

        for (int i = 0; i < infix.length(); i++) {
            char c = infix.charAt(i);

            if (Character.isLetterOrDigit(c)) {
                resultStack.push(Character.toString(c));
            } else if (c == '(') {
                stack.push(c);
            } else if (c == ')') {
                while (!stack.isEmpty() && stack.peek() != '(') {
                    String op2 = resultStack.pop();
                    String op1 = resultStack.pop();
                    char op = stack.pop();
                    resultStack.push(op + op1 + op2);
                }
                stack.pop();
            } else {
                while (!stack.isEmpty() && precedence(c) <= precedence(stack.peek())) {
                    String op2 = resultStack.pop();
                    String op1 = resultStack.pop();
                    char op = stack.pop();
                    resultStack.push(op + op1 + op2);
                }
                stack.push(c);
            }

            stepsList.add(String.format("Expression: %s, Stack: %s", resultStack.toString(), stack.toString()));
        }

        while (!stack.isEmpty()) {
            String op2 = resultStack.pop();
            String op1 = resultStack.pop();
            char op = stack.pop();
            resultStack.push(op + op1 + op2);
            stepsList.add(String.format("Expression: %s, Stack: %s", resultStack.toString(), stack.toString()));
        }

        resultText.setText("Result Expression: " + resultStack.pop());
        stepsAdapter.notifyDataSetChanged();
    }

    private boolean isValidExpression(String expression) {
        // Regular expression allows only A-Z, a-z, 0-9, and the operators +, -, *, /, ^, (, )
        return expression.matches("[a-zA-Z0-9+\\-*/^()]+");
    }

    private int precedence(char ch) {
        switch (ch) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
                return 2;
            case '^':
                return 3;
        }
        return -1;
    }
}