package db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import model.City;

public class CityDB {
    private static final String DB_NAME = "city.db";
    private static final String TABLE_NAME = "city";
    private SQLiteDatabase db;

    private CityDB(Context context, String path) {
        db = context.openOrCreateDatabase(path, Context.MODE_PRIVATE, null);
    }

    public List<City> getAllCity() {
        List<City> cities = new ArrayList<>();
        Cursor cursor = db.rawQuery(String.format("select * from %s", TABLE_NAME), null);
        while (cursor.moveToNext()) {
            City city = new City();
            city.setCityCode(cursor.getString(cursor.getColumnIndex("number")));
            city.setCityName(cursor.getString(cursor.getColumnIndex("city")));
            cities.add(city);
        }

        cursor.close();

        return cities;
    }

    public static CityDB openCityDB(Context context) {
        String dirPath = File.separator +
                "data" + Environment.getDataDirectory().getAbsolutePath() +
                File.separator + context.getPackageName() +
                File.separator + "database" +
                File.separator;
        String path = dirPath + DB_NAME;
        Log.d("db: ", path);

        File dbFile = new File(path);
        if (!dbFile.exists()) {

            File dir = new File(dirPath);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    Log.e("db:", "can't not create db file, because we can not create dir!");
                    System.exit(0);
                }
            }

            int len;
            byte[] buffer = new byte[1024];
            InputStream in = null;
            FileOutputStream out = null;


            try {
                in = context.getAssets().open(DB_NAME);
                out = new FileOutputStream(dbFile);

                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }

                out.flush();


            } catch (IOException e) {
                e.printStackTrace();

            } finally {
                try {
                    if(in != null) in.close();
                    if(out != null) out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return new CityDB(context, path);
    }
}
