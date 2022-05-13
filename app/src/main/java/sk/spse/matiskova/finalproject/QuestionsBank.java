package sk.spse.matiskova.finalproject;

import java.util.ArrayList;
import java.util.List;

public class QuestionsBank {

    private static List<QuestionList> biologyQuestions() {

        final List<QuestionList> questionLists = new ArrayList<>();

        //Čo zabezpečuje siniciam modrozelené sfarbenie:

        final QuestionList question1 = new QuestionList("H<sub>2</sub>SO<sub>5</sub> je kyselina peroxosiričitá",
                "fykocyanin", "fykoerytrin", "karotenoid", "fukoxantin",
                "xantofyl", "karoten", "sinicový škrob", "bakteriochlorofyl", "0,1", "");

        final QuestionList question2 = new QuestionList("Kožné alebo pl'úcne ochorenia človeka sposobuje:", "pleseň hlavičkatá", "Penicillium notatum",
                "Candida albicans", "Candida niger", "Mucor mucedo", "kyjanicka purpurova",
                "paplesen stetkovita", "Aspergillus niger", "1", "");

        final QuestionList question3 = new QuestionList("Pre čel'ad' astrovite je charakteristické súkvetie:", "ubor", "suľok", "pavidlica",
                "chocholik", "hlavka", "strapec", "okolik", "metlina", "0", "");

        final QuestionList question4 = new QuestionList("Pre ktorú čel'ad' je najtypickejšie súkvetie okolik:", "mrkovite", "lipnicovité",
                "hlichavkovité", "bôbovité", "silenkovité", "astrovité", "ružovité", "ľaliovité", "0", "");

        final QuestionList question5 = new QuestionList("Druhovo najbohatším kmeňom živošíchov sú: ", "Insecta", "obrúčkavce", "chordáty", "článkonožce",
                "mäkkýše", "pŕhlivce", "hmyz", "stavovce", "3", "");

        questionLists.add(question1);
        questionLists.add(question2);
        questionLists.add(question3);
        questionLists.add(question4);
        questionLists.add(question5);

        return questionLists;
    }

    private static List<QuestionList> chemistryQuestions() {

        final List<QuestionList> questionLists = new ArrayList<>();

        final QuestionList question1 = new QuestionList("Ktorá aminokyselina obsahuje vo svojej molekule síru",
                "metionín", "serín", "treonín", "cysteín",
                "cystín", "prolín", "lyzín", "tryptofán", "cystín", "");

        final QuestionList question2 = new QuestionList("Medzi funkčné deriváty karboxylových kyselín patria:",
                "aminokyseliny", "hydrokyseliny", "anhydridy", "amidy kyselin", "estery kyselin",
                "chloridy kyselin", "kyselina trichlóroctova", "oxykyseliny", "chloridy kyselin", "" );

        final QuestionList question3 = new QuestionList("Ktorá zlúčenina nepatrí medzi fenoly", "pyrokatechol", "rezincinol",
                "hydrochinon", "anizol", "acetofenon", "florochucinol", "benzofenon",
                "cyklohexanol", "anizol", "");

        final QuestionList question4 = new QuestionList("K aromatickým zlúčeninám nepatrí:", "naftalen", "o-krezol",
                "hydrochinon", "cyklohexen", "steran", "kumen", "p-benzochinon", "cyklohexanol",
                "p-benzochinon", "");

        final QuestionList question5 = new QuestionList("Etylenglykol je: ", "jedovatý", "plyn", "kvapalina",
                "sekundárny alkohol", "derivat glycerolu", "dvojsýtny alkohol", "sladkej chuti", "derivatom glycinu",
                "kvapalina", "");

        questionLists.add(question1);
        questionLists.add(question2);
        questionLists.add(question3);
        questionLists.add(question4);
        questionLists.add(question5);

        return questionLists;
    }

    public static List<QuestionList> getQuestions(String selectTopicName) {
        switch (selectTopicName) {
            case "biology":
                return biologyQuestions();
            default:
                return chemistryQuestions();
        }
    }
}