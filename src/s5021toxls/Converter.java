package s5021toxls;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jxl.Workbook;
import jxl.format.CellFormat;
import jxl.read.biff.BiffException;
import jxl.write.DateFormat;
import jxl.write.DateTime;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class Converter {

    // 2/25/2015  2014.06.13.

    private static String separator = "\t";
    private static String patternData = "([0-9]{4}\\.[0-9]{2}\\.[0-9]{2}\\. [0-9]{1,2}:[0-9]{2}:[0-9]{2}) *(-{0,1}[0-9](\\.[0-9]*){0,1}) *(-{0,1}[0-9](\\.[0-9]*){0,1})*(---)*";
    private static String patternDataEng = "([0-9]{1,2}/[0-9]{1,2}/[0-9]{4} [0-9]{1,2}:[0-9]{2}:[0-9]{2} (AM)*(PM)*) *(-{0,1}[0-9](\\.[0-9]*){0,1}) *(-{0,1}[0-9](\\.[0-9]*){0,1})*(---)*";
    private static String patternColName = " *(\\S+) *(\\S+)";
    private static String patternSerial = "Serial number:  ([0-9]+)";
    private static SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd. H:mm:ss");
    private static SimpleDateFormat dfEng = new SimpleDateFormat("M/dd/yyyy h:mm:ss a", Locale.US);
    private static SimpleDateFormat outputDateTime = new SimpleDateFormat("yyyy.MM.dd. HH:mm");
    private long serialNumber;
    private String name;
    private String[][] colNames;
    private List<Row> data;

    private boolean hasSecondRow = false;

    public Converter(File input) throws FileNotFoundException, NullPointerException, IOException, ParseException {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(input), "Cp1252"));
        colNames = new String[2][2];
        data = new ArrayList<>();

        Pattern pattern;
        Matcher m;

        String line = br.readLine();
        pattern = Pattern.compile(patternSerial);
        m = pattern.matcher(line);
        if (m.matches()) {
            serialNumber = Long.parseLong(m.group(1));
        }
//        System.out.println("serial: " + serialNumber + (serialNumber == 14931137 ? " ok" : " rossz!"));

        name = br.readLine();
//        System.out.println(name);

        line = br.readLine();
        line = br.readLine();
        pattern = Pattern.compile(patternColName);
        m = pattern.matcher(line);
        if (m.matches()) {
//            System.out.println("\t\t" + m.group(1) + "\t" + m.group(2));
            colNames[0][0] = m.group(1);
            colNames[0][1] = m.group(2);
        }
//        System.out.println(Arrays.toString(colNames[0]));

        line = br.readLine();
        m = pattern.matcher(line);
        if (m.matches()) {
//            System.out.println("\t\t" + m.group(1) + "\t" + m.group(2));
            colNames[1][0] = m.group(1);
            colNames[1][1] = m.group(2);
        }
//        System.out.println(Arrays.toString(colNames[1]));

        line = br.readLine();
        pattern = Pattern.compile(patternData);
        Pattern pattern2 = Pattern.compile(patternDataEng);
        Matcher m2;
        while ((line = br.readLine()) != null) {
//            System.out.println(line + " " + line.matches(patternData));
            m = pattern.matcher(line);
            m2 = pattern2.matcher(line);
            if (m.matches()) {
                data.add(new Row(false, m.group(1), m.group(2), m.group(4)));
            } else if (m2.matches()) {
                data.add(new Row(true, m2.group(1), m2.group(4), m2.group(6)));
            } else {
                System.out.println("nem stimm: " + line);
            }
        }
    }

    public void saveToXls(File output) throws IOException, BiffException, WriteException, ParseException {
        WritableWorkbook workbook = Workbook.createWorkbook(output);
        WritableSheet sheet = workbook.createSheet(name, 0);

        List<WritableCell> cells = new ArrayList<>();
        cells.add(new Label(0, 0, name));

        cells.add(new Label(1, 2, colNames[0][0]));
        cells.add(new Label(1, 3, colNames[1][0]));
        if (hasSecondRow) {
            cells.add(new Label(2, 2, colNames[0][1]));
            cells.add(new Label(2, 3, colNames[1][1]));
        }

        WritableCellFormat outFormat = new WritableCellFormat(new DateFormat(outputDateTime.toPattern()));
        for (int i = 0; i < data.size(); i++) {
            Row row = data.get(i);
            cells.add(new DateTime(0, 4 + i, row.getDate(), outFormat));
            cells.add(new Number(1, 4 + i, row.getData()[0]));
            if (hasSecondRow) {
                cells.add(new Number(2, 4 + i, row.getData()[1]));
            }
        }

        for (WritableCell cell : cells) {
            sheet.addCell(cell);
        }

        workbook.write();
        workbook.close();
    }

    public List<Row> getData() {
        return data;
    }

    public String getName() {
        return name;
    }

    private class Row {

        private Date date;
        private double[] data;

        public Row(boolean eng, String... datas) throws ParseException {
//            System.out.println("datas: " + Arrays.toString(datas));
            if (eng) {
                date = dfEng.parse(datas[0]);
//                System.out.println("date: " + date);
            } else {
                date = df.parse(datas[0]);
            }
            data = new double[2];
            data[0] = Double.parseDouble(datas[1]);
            if (datas[2] != null && !datas[2].equals("---")) {
                hasSecondRow = true;
                data[1] = Double.parseDouble(datas[2]);
            }
//            System.out.println(this);
        }

        public Date getDate() {
            return date;
        }

        public String getOutputDateTime() {
            return outputDateTime.format(date);
        }

        public double[] getData() {
            return data;
        }

        @Override
        public String toString() {
            return getOutputDateTime() + separator + data[0] + separator + data[1];
        }
    }
}
