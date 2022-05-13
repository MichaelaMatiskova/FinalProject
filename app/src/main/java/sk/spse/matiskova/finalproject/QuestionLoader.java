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

    void CloseDatabase()
    {
        db.close();
    }
}
