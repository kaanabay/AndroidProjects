package com.example.kaanabay.arithmeticgame;

import java.util.ArrayList;
import java.util.Random;

import static com.example.kaanabay.arithmeticgame.operatorEnum.divide;

/**
 * Created by Kaan Abay on 23.08.2017.
 */


enum operatorEnum {add, extract, multiply, divide}

final class Question {
    private int operand1;
    private int operand2;
    private operatorEnum operator;
    private int result;

    Question(int o1, int o2, operatorEnum op, int r) {
        operand1 = o1;
        operand2 = o2;
        operator = op;
        result = r;
    }

    public int getOperand1() {
        return operand1;
    }
    public int getOperand2() {
        return operand2;
    }
    public int getResult() {
        return result;
    }

    public String getQuestionText() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(operand1).append(" ? ").append(operand2)
                .append(" = ").append(result);
        return stringBuilder.toString();
    }

    public boolean checkAnswer(operatorEnum op) {
        return operator == op;
    }
}

final class Game {

    private int countTrue = 0;
    private int countFalse = 0;
    private int countQuestion = 0;
    private ArrayList<Question> questions = new ArrayList<>();

    Game() {}
    Game(int n) {
        fillRandomNQuestions(n);
    }

    public Question getNextQuestion() {
        countQuestion++;
        if(countQuestion == questions.size()) {
            Question ret = questions.get(countQuestion-1);
            questions.clear();
            fillRandomNQuestions(100);
            return ret;
        }
        return questions.get(countQuestion-1);
    }

    public int getTrue() {
        return countTrue;
    }

    public int getFalse() {
        return countFalse;
    }

    public int getScore() {
        return countTrue - 2*countFalse;
    }

    public void addTrue() {
        countTrue++;
    }

    public void addFalse() {
        countFalse++;
    }

    public void fillRandomNQuestions(int N) {
        for(int i=0; i<N; i++) {
            int i1 = new Random().nextInt(7) + 3;
            int i2 = new Random().nextInt(7) + 3;
            operatorEnum op = operatorEnum.values()[new Random().nextInt(4)];
            int res = -1;
            if(op == divide) {
                res = i1;
                i1 *= i2;
            } else {
                switch (op) {
                    case add:
                        res = i1+i2;
                        break;
                    case extract:
                        res = i1-i2;
                        break;
                    case multiply:
                        res = i1*i2;
                        break;
                }
            }
            Question push = new Question(i1,i2,op,res);
            questions.add(push);
        }
    }


}
