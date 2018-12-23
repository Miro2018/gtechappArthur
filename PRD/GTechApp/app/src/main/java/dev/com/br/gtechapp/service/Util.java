package dev.com.br.gtechapp.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.xml.datatype.Duration;

import static java.util.Calendar.DAY_OF_YEAR;
import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MILLISECOND;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.SECOND;
import static java.util.Calendar.YEAR;

public class Util {

    public static Date convertStringToDate(String dateInString){

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        try {

            return formatter.parse(dateInString);

        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String convertDatToString(Calendar c){
        // Get Current Date
        int mYear = c.get(YEAR);
        int mMonth = c.get(Calendar.MONTH) + 1;
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        String strMes;
        String strDia;

        if (mMonth < 10)
        {
            strMes = "0" + (mMonth);
        }else {
            strMes = (mMonth) + "";
        }

        if (mDay < 10 ){
            strDia = "0" + mDay;
        } else{
            strDia = mDay + "";
        }

        return mYear + "-" + strMes + "-" + strDia;
    }

    public static int obterDiaDaSemana(String strData){
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date date = format.parse(strData);
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
            return dayOfWeek;
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    public static int getIndexOf(String[] strings, String item) {
        for (int i = 0; i < strings.length; i++) {
            if (item.equals(strings[i])) return i;
        }
        return -1;
    }

    public static String converteDataSqlParaBr(String dataSql){
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat format2 = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        try {
            date = format1.parse(dataSql);
            return format2.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String converteDataBrParaSql(String dataSql){
        SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = format1.parse(dataSql);
            return format2.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static long daysBetween(Calendar startDate, Calendar endDate) {
        //assert: startDate must be before endDate
        Calendar date = (Calendar) startDate.clone();
        long daysBetween = 0;
        while (date.before(endDate)) {
            date.add(Calendar.DAY_OF_MONTH, 1);
            daysBetween++;
        }
        return daysBetween;
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
