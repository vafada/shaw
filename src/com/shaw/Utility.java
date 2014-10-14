package com.shaw;

import com.shaw.bean.DateRangeWrapper;

import javax.swing.*;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Calendar;

public class Utility {
    private static DateFormat df = new SimpleDateFormat("MMMM dd, yyyy");
    private static DecimalFormat decf = new DecimalFormat("#,##0.00");

    private Utility() {

    }


    /*
     * Looks up and answers an icon for the specified filename suffix.<p>
     */
    public static ImageIcon readImageIcon(String filename) {
        URL url = Utility.class.getClassLoader().getResource("images/" + filename);
        return new ImageIcon(url);
    }

    public static String formatDate(Date date) {
        if(date == null)
            return "";
        return df.format(date);
    }

    public static Date parseDate(String date) throws ParseException {
        return df.parse(date);
    }

    public static DateRangeWrapper getLastSevenMonths() {
        DateFormatSymbols dateFormatSymbols = new DateFormatSymbols();
        String[] monthNames = dateFormatSymbols.getShortMonths();
        Calendar cal = Calendar.getInstance();

        Date startDate = null;
        Date endDate = null;
        String[] names = new String[7];

        for (int i = 0; i < 7; i++) {
            cal.add(Calendar.MONTH, -1);
            names[i] = monthNames[cal.get(Calendar.MONTH)];
            if (i == 0) { //end date
                int max = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                cal.set(Calendar.DATE, max);
                endDate = cal.getTime();
            } else if (i == 6) {
                int min = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
                cal.set(Calendar.DATE, min);
                startDate = cal.getTime();
            }
        }

        return new DateRangeWrapper(endDate, startDate, names);
    }

    /**
     * Creates and returns a <code>JButton</code>
     * configured for use in a JToolBar.<p>
     *
     * This is a simplified method that is overriden by the Looks Demo.
     * The full code uses the JGoodies UI framework's ToolBarButton
     * that better handles platform differences.
     */
    public static AbstractButton createToolBarButton(String iconName) {
        JButton button = new JButton(Utility.readImageIcon(iconName));
        button.setFocusable(false);
        return button;
    }

    public static String formatDouble(double value) {
        return decf.format(value);
    }

    public static double parseDouble(String value) throws ParseException {
        return decf.parse(value).doubleValue();
    }
}
