/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.read.biff.BiffException;
import jxl.write.DateFormat;
import jxl.write.DateTime;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

/**
 *
 * @author Katalin
 */
public class Converter2 implements ConverterInterface {

    private static final String SEPARATOR = "\t";

    private String name; // itt valójában mérési időszak lesz, mert nincs név a fájlban
    private List<Row> data;

    public Converter2(File input) throws FileNotFoundException, NullPointerException, IOException, ParseException {
        System.out.println(new SimpleDateFormat("yy.MM.dd. H:mm:ss").parse("2016.05.04. 14:10:35"));
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(input), "Cp1252"));
        data = new ArrayList<>();
        String line;

        // Első sorból a mérési időszak kell, ez lesz a név.
        line = br.readLine();
        String[] parts = line.split(SEPARATOR);
        name = parts[2].replace(":", ".");

        // Következő két sort eldobjuk, mert mindig ugyan azok a nevek
        br.readLine();
        br.readLine();

        // Elkezdjük olvasni az adatokat. Lehet benne üres mező is (elvétve szöveg is, de ez élesben elvileg nem lesz)
        // Minden, ami nem szám, figyelmen kívül hagyjuk
        int i = 1;
        while ((line = br.readLine()) != null) {
            try {
                data.add(new Row(line.split(SEPARATOR)));
//                System.out.println(i + " " + data.get(data.size() - 1));
            } catch (Exception ex) {
            }
            i++;
        }
        br.close();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void saveToXls(File output) throws IOException, BiffException, WriteException, ParseException {
        WritableWorkbook workbook = Workbook.createWorkbook(output);
        WritableSheet sheet = workbook.createSheet(name, 0);

        List<WritableCell> cells = new ArrayList<>();
        cells.add(new Label(0, 0, "Dátum"));
        cells.add(new Label(1, 0, "Időpont"));
        cells.add(new Label(2, 0, "KD481D1.PV"));
        cells.add(new Label(3, 0, "KD481D2.PV"));
        cells.add(new Label(4, 0, "KD481D3.PV"));
        cells.add(new Label(2, 1, "Oxigén"));
        cells.add(new Label(3, 1, "PV"));
        cells.add(new Label(4, 1, "Gáz"));
        WritableCellFormat cf = new WritableCellFormat();
        cf.setAlignment(Alignment.CENTRE);
        for (WritableCell cell : cells) {
            cell.setCellFormat(cf);
        }

        //jönnek az adatok
        WritableCellFormat outDateFormat = new WritableCellFormat(new DateFormat(Const.outputDate.toPattern()));
        WritableCellFormat outTimeFormat = new WritableCellFormat(new DateFormat(Const.outputTime.toPattern()));
        for (int i = 0; i < data.size(); i++) {
            Row row = data.get(i);
            cells.add(new DateTime(0, 2 + i, row.getDate(), outDateFormat));
            cells.add(new DateTime(1, 2 + i, row.getDate(), outTimeFormat));
            if (row.getData()[0] != null) {
                cells.add(new jxl.write.Number(2, 2 + i, row.getData()[0]));
            }
            if (row.getData()[1] != null) {
                cells.add(new jxl.write.Number(3, 2 + i, row.getData()[1]));
            }
            if (row.getData()[2] != null) {
                cells.add(new jxl.write.Number(4, 2 + i, row.getData()[2]));
            }
        }

        // hozzáadjuk a munkalaphoz
        for (WritableCell cell : cells) {
            sheet.addCell(cell);
        }

        workbook.write();
        workbook.close();
    }

    @Override
    public List getData() {
        return data;
    }

    private class Row {

        private Date date;
        private Double[] data;

        public Row(String[] datas) throws ParseException, Exception {
            try {
                date = Const.df.parse(datas[0]);
            } catch (Exception ex) {
                try {
                    date = Const.df2.parse(datas[0]);
                } catch (Exception ex2) {
                    try {
                        date = Const.dfEng.parse(datas[0]);
                    } catch (Exception ex3) {

                    }
                }
            }
            if (date == null) {
                throw new Exception("First data is not date.");
            }

            data = new Double[3];
            for (int i = 1; i < datas.length; i++) {
                try {
                    data[i - 1] = Double.parseDouble(datas[i]);
                } catch (Exception ex) {
                    data[i - 1] = null;
                }
            }
        }

        public Date getDate() {
            return date;
        }

        public Double[] getData() {
            return data;
        }

        @Override
        public String toString() {
            return Const.outputDateTime.format(date) + " " + Arrays.toString(data);
        }
    }

}
