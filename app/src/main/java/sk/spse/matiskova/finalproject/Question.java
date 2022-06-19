package sk.spse.matiskova.finalproject;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

public class Question {
    private final String question;
    private final String[] answers;
    private final Set<Integer> correctAnswers = new TreeSet<>();

    public Question(String question, String[] possibleAnswers, String correctAnswers) {
        this.question = question;
        if (possibleAnswers.length != 8)
            throw new IllegalArgumentException("Possible answers should have size 8!");
        this.answers = possibleAnswers;
        ParseCorrectAnswers(correctAnswers);
    }

    private void ParseCorrectAnswers(String correctAnswers) {
        correctAnswers = correctAnswers.replace(" ", "");
        String[] tempArr = correctAnswers.trim().split(",");
        FillCorrectAnswers(tempArr);
    }

    private void FillCorrectAnswers(String[] rawAnswers)
    {
        Arrays.stream(rawAnswers)
              .mapToInt(Integer::parseInt)
              .forEach(this.correctAnswers::add);
    }

    public String GetQuestion() {
        return question;
    }

    public String GetAnswer(int index) {
        return answers[index];
    }

    public boolean IsAnswerCorrect(int answerIndex) {
        return correctAnswers.contains(answerIndex);
    }
}
