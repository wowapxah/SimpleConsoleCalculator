package ru.wowapxah;

import java.util.*;
import java.lang.*;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static boolean flag = true;

    public static void main (String[] args) {
        String tempString = getTempString();
        while (!checkForBrackets(tempString) || !checkForLetters(tempString)) {
            System.out.println("Введена некорректная строка. Повторите или '0' для выхода.");
            tempString = getTempString();
        }
        if (tempString.equals("0") || tempString.equals("")) return;

        List<String> expression = expressionParser(tempString);

        if (flag) System.out.println(calc(expression));
    }

    private static String getTempString() {
        try {
            return new BufferedReader(new InputStreamReader(System.in)).readLine();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private static boolean checkForBrackets(String expression) {
        int bracketsCount = 0;
        for (int i = 0; i < expression.toCharArray().length; i++) {
            if (expression.toCharArray()[i] == '(') bracketsCount++;
            if (expression.toCharArray()[i] == ')') {
                bracketsCount--;
                if (bracketsCount < 0) return false;
            }
        }
        return bracketsCount == 0;
    }

    private static boolean checkForLetters(String expression) {
        Pattern pattern = Pattern.compile("([^\\d+\\-*/()]+)");
        Matcher matcher = pattern.matcher(expression);

        return !matcher.find();
    }

    public static Double calc(List<String> postfix) {
        Deque<Double> deque = new ArrayDeque<>();
        for (String x : postfix) {
            switch (x) {
                case "+" -> deque.push(deque.pop() + deque.pop());
                case "-" -> deque.push(-deque.pop() + deque.pop());
                case "*" -> deque.push(deque.pop() * deque.pop());
                case "/" -> deque.push(1 / deque.pop() * deque.pop());
                default -> deque.push(Double.valueOf(x));
            }
        }
        return deque.pop();
    }

    public static List<String> expressionParser(String inputString) {
        List<String> result = new ArrayList<>();
        Deque<String> deque = new ArrayDeque<>();
        Pattern operatorPattern = Pattern.compile("([\\d+\\-*/]+)");
        Pattern delimPattern = Pattern.compile("([\\d+\\-*/()]+)");

        if (inputString.toCharArray()[0] == '-') inputString = "0" + inputString;

        StringTokenizer tokenizer = new StringTokenizer(inputString, "+-*/()", true);

        String currentToken;
        while (tokenizer.hasMoreTokens()) {
            currentToken = tokenizer.nextToken();

            Matcher matcher = delimPattern.matcher(currentToken);

            if (matcher.find()) {
                switch (currentToken) {
                    case "(" -> deque.push(currentToken);
                    case ")" -> {
                        while (!deque.peek().equals("(")) result.add(deque.pop());
                        deque.pop();
                        if (!deque.isEmpty()) {
                            result.add(deque.pop());
                        }
                    }
                    default -> {
                        while (!deque.isEmpty() && (getPriority(currentToken) <= getPriority(deque.peek()))) {
                            result.add(deque.pop());
                        }
                        deque.push(currentToken);
                    }
                }
            }
            else {
                result.add(currentToken);
            }
        }

        while (!deque.isEmpty())
            if (operatorPattern.matcher(deque.peek()).find())
                result.add(deque.pop());

        return result;
    }

    private static byte getPriority(String token) {
        switch (token) {
            case "(" -> { return 1; }
            case "+", "-" -> { return 2; }
            case "*", "/" -> { return 3; }
            default -> { return 4; }
        }
    }
}