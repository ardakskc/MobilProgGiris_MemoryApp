package com.example.noteapp;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class Database extends SQLiteOpenHelper {

        private static final String DB_NAME = "noteapp";

        private static final int DB_VERSION = 1;

        private static final String TABLE_NAME = "notes";

        private static final String ID_COL = "id";

        private static final String TITLE_COL = "title";

        private static final String DATE_COL = "date";

        private static final String ENTRY_COL = "entry";

        private static final String LOC_COL = "location";

        private static final String MOOD_COL = "mood";

        private static final String IMG_COL = "image";

        private static final String PASS_COL = "password";


        public Database(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            String query = "CREATE TABLE " + TABLE_NAME + " ("
                    + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + TITLE_COL + " TEXT,"
                    + DATE_COL + " TEXT,"
                    + ENTRY_COL + " TEXT,"
                    + LOC_COL + " TEXT,"
                    + MOOD_COL + " INTEGER,"
                    + PASS_COL + " TEXT,"
                    + IMG_COL + " TEXT )";

            db.execSQL(query);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // this method is called to check if the table exists already.
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }

        public void addNote(Notes note) {

            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues cv = new ContentValues();

            cv.put(TITLE_COL, note.getBaslik());
            cv.put(DATE_COL, note.getDate());
            cv.put(ENTRY_COL, note.getText());
            cv.put(LOC_COL, note.getLokasyon());
            cv.put(MOOD_COL, note.getMood());
            cv.put(PASS_COL, note.getSifre());
            cv.put(IMG_COL, note.getFoto());
            db.insert(TABLE_NAME, null, cv);

            db.close();
        }

        public ArrayList<Notes> getAll() {
            ArrayList<Notes> list = new ArrayList<Notes>();

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cr =  db.rawQuery( "SELECT * FROM "+ TABLE_NAME, null );
            cr.moveToFirst();

            while(cr.isAfterLast() == false){

                Notes tmp = new Notes(cr.getInt(0),cr.getString(1),cr.getString(2),cr.getString(3),cr.getInt(5),cr.getString(4),cr.getString(6), cr.getString(7));
                list.add(tmp);
                cr.moveToNext();
            }

            cr.close();
            return list;
        }

        public int updateNote(Notes note) {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues cv = new ContentValues();
            cv.put(TITLE_COL, note.getBaslik());
            cv.put(DATE_COL, note.getDate());
            cv.put(ENTRY_COL, note.getText());
            cv.put(LOC_COL, note.getLokasyon());
            cv.put(MOOD_COL, note.getMood());
            cv.put(PASS_COL, note.getSifre());
            cv.put(IMG_COL, note.getFoto());

            // updating row
            return db.update(TABLE_NAME, cv, ID_COL + " = ?",
                    new String[] { String.valueOf(note.getId()) });
        }

        public void deleteNote(Notes note) {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(TABLE_NAME, ID_COL + " = ?",
                    new String[] { String.valueOf(note.getId()) });
            db.close();
        }

        public int getNoteCount() {
            String countQuery = "SELECT  * FROM " + TABLE_NAME;
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(countQuery, null);
            cursor.close();

            return cursor.getCount();
        }
}
