/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s5021toxls;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 *
 * @author Katalin
 */
public class Const {
    public static SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd. H:mm:ss");
    public static SimpleDateFormat df2 = new SimpleDateFormat("yyyy.MM.dd H:mm:ss");
    public static SimpleDateFormat dfEng = new SimpleDateFormat("M/dd/yyyy h:mm:ss a", Locale.US);
    public static SimpleDateFormat outputDateTime = new SimpleDateFormat("yyyy.MM.dd. HH:mm");
    public static SimpleDateFormat outputDate = new SimpleDateFormat("yyyy.MM.dd.");
    public static SimpleDateFormat outputTime = new SimpleDateFormat("HH:mm");
}
