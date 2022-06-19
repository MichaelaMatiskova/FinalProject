package sk.spse.matiskova.finalproject;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class QuestionLoader {
    private SQLiteDatabase db;
    private final String dbPath;

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
        cs.moveToFirst();
        String[] answers = new String[8];
        for (int i = 0; i < 8; ++i) {
            answers[i] = cs.getString(i + 2);
        }
        Question q = new Question(cs.getString(1), answers, cs.getString(10));
        cs.close();
        return q;
    }

    public int getRowCount(String tableId) {
        String query = "SELECT COUNT(*) FROM " + tableId;
        Cursor cs = db.rawQuery(query, null);
        cs.moveToFirst();
        int count = cs.getInt(0);
        cs.close();
        return count;
    }

    void CloseDatabase() {
        db.close();
    }
}
