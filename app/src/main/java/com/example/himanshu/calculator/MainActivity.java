package com.example.himanshu.calculator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.R.layout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    private static final String OPERATORS =  "*/+-";
    private static final HashMap<String,Integer> prec = new HashMap<>();

    String currentToken = null;
    String tokensString = "";
    boolean isLastTokenOperation = false;
    boolean isLastOperationEvaluation = false;


    ArrayList<String> tokens = new ArrayList<>();
    TextView stackTextView;
    TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prec.put("*",2);
        prec.put("/",2);
        prec.put("+",1);
        prec.put("-",1);
        stackTextView = findViewById(R.id.stack);
        resultTextView = findViewById(R.id.result);
    }

    public void handleClick(View view){
        String text = ((Button)view).getText().toString();
        String tag = (String) view.getTag();
        if(isLastOperationEvaluation){
            tokens.remove(tokens.size()-1);
        }
        if(tag.equals("point")){
            if(currentToken == null){
                currentToken = "0";
            }
            if(!currentToken.contains(".")){
                currentToken = currentToken.concat(text);
                tokensString = tokensString.concat(text);
                isLastTokenOperation = false;
                isLastOperationEvaluation = false;
                stackTextView.setText(tokensString);
            }

        }
        if(tag.equals("value")){
            if(currentToken == null){
                currentToken = "";
            }
            currentToken = currentToken.concat(text);
            tokensString = tokensString.concat(text);
            isLastOperationEvaluation = false;
            isLastTokenOperation = false;
            stackTextView.setText(tokensString);
            eval();
        }
        if(tag.equals("equal")){
            eval();

        }
        if(tag.equals("operation")){
            if(currentToken != null && !isLastTokenOperation){

                tokens.add(currentToken);
                currentToken = "";
                isLastOperationEvaluation = false;
                isLastTokenOperation = true;
                tokens.add(text);
                tokensString = tokensString.concat(text);
                stackTextView.setText(tokensString);
            }
        }
        if(tag.equals("clear")){
            tokens.clear();
            tokensString = "";
            currentToken = null;
            isLastTokenOperation = false;
            isLastOperationEvaluation = false;
            stackTextView.setText("");
            resultTextView.setText("");
        }
    }

    public Double evaluate(){
        ArrayList<String> postfix = infixToPostfix(tokens);
        Stack<String> stack = new Stack<>();
        for(String token: postfix){
            if(OPERATORS.contains(token)){
                Double val2 = Double.parseDouble(stack.pop());
                Double val1 = Double.parseDouble(stack.pop());
                Double result = operate(token,val1,val2);
                stack.push(result+"");
            }
            else {
                stack.push(token);
            }
        }
        return Double.parseDouble(stack.pop());
    }

    private Double operate(String token, Double val1, Double val2) {
        switch (token){
            case "*": return val1 * val2;
            case "/": return val1/val2;
            case "+": return val1+ val2;
            case "-": return val1 -val2;
            default:return  Double.MIN_VALUE;
        }
    }

    public ArrayList<String> infixToPostfix(ArrayList<String> infix){
        ArrayList<String>postfix = new ArrayList<>();
        Stack<String> stack = new Stack<>();
        for(String token: infix){
            if(OPERATORS.contains(token)){
                while (!stack.isEmpty()){
                    String topOperator = stack.peek();
                    if(prec.get(topOperator) >= prec.get(token)){
                        postfix.add(stack.pop());
                    }
                    else {
                        break;
                    }
                }
                stack.push(token);
            }
            else {
                postfix.add(token);
            }

        }
        while (!stack.isEmpty()){
            postfix.add(stack.pop());
        }
        return postfix;
    }

    public void eval(){
        if(tokens.size() >= 2 && !isLastTokenOperation){
            tokens.add(currentToken);
            Double result = evaluate();
            isLastOperationEvaluation = true;
            isLastTokenOperation = false;
            resultTextView.setText(result+"");
        }
    }
}