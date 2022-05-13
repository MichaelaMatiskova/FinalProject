package sk.spse.matiskova.finalproject;

import java.util.ArrayList;

public class QuestionList {

    private String question;
    private ArrayList<Integer> answers;
    private String usersSelectedAnswer;

    private String[] options = new String[8];
    public QuestionList(String question, String optionA, String optionB, String optionC, String optionD,
                        String optionE, String optionF, String optionG, String optionH, String answer, String usersSelectedAnswer) {
        this.question = question;

        answer = answer.replace(" ", "");
        String[] temp = answer.trim().split(",");
        answers = new ArrayList<>();
        for (int i = 0; i < temp.length; i++) {
            answers.add(Integer.parseInt(temp[i]));
        }

        this.usersSelectedAnswer = usersSelectedAnswer;

        this.options[0] = optionA;
        this.options[1] = optionB;
        this.options[2] = optionC;
        this.options[3] = optionD;
        this.options[4] = optionE;
        this.options[5] = optionF;
        this.options[6] = optionG;
        this.options[7] = optionH;

    }

    public String getQuestion() {
        return question;
    }

    public String getOption(int i) {
        return options[i];
    }

    public ArrayList<Integer> getAnswer() {
        return answers;
    }

    public String getUsersSelectedAnswer() {
        return usersSelectedAnswer;
    }
}
