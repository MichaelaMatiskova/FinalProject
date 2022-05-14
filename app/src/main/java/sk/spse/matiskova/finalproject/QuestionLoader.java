package sk.spse.matiskova.finalproject;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class QuestionLoader {
    private SQLiteDatabase db;
    private String dbPath;

    public QuestionLoader(String dbPath) {
        this.dbPath = dbPath;
    }

    boolean OpenReadOnlyDatabase() {
        db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY, null);
        return db.isOpen() && db.isReadOnly();
    }

    Question SelectQuestionFromTable(String tableId, int id) {
        String query = "SELECT * FROM " + tableId + " WHERE id=?";
        Cursor cs = db.rawQuery(query, new String[]{ String.valueOf(id) });
        String[] answers = new String[8];
        for (int i = 0; i < 8; ++i) {
            answers[i] = cs.getString(i + 2);
        }
        Question q = new Question(cs.getString(1), answers, cs.getString(10));
        cs.close();
        return q;
    }

    void DoSomeSketchySelect() {
        Cursor cs = db.rawQuery("SELECT * FROM chemistry WHERE id=1", null);
        //db.execSQL("SELECT * FROM chemisty WHERE id=1");
        cs.moveToFirst();
        Log.i("Super duper tag",
                "Question is: " + cs.getString(1)
                        + "first option is: " + cs.getString(2)
                        + "last option is: " + cs.getString(9)
                        + "correct answers are: " + cs.getString(10));
    }

    void CloseDatabase() {
        db.close();
    }
}
