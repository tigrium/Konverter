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
import jxl.read.biff.BiffException;
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
        name = parts[2];

        // Következő két sort eldobjuk, mert mindig ugyan azok a nevek
        br.readLine();
        br.readLine();

        // Elkezdjük olvasni az adatokat. Lehet benne üres mező is (elvétve szöveg is, de ez élesben elvileg nem lesz)
        // Minden, ami nem szám, figyelmen kívül hagyjuk
        int i = 1;
        while ((line = br.readLine()) != null) {
            try {
                data.add(new Row(line.split(SEPARATOR)));
                System.out.println(i + " " + data.get(data.size() - 1));
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
            if (datas.length != 4) {
                throw new Exception("Data length != 4");
            }
            data = new Double[3];
            for (int i = 1; i < 4; i++) {
                try {
                    data[i - 1] = Double.parseDouble(datas[i]);
                } catch (Exception ex) {
                    data[i - 1] = null;
                }
            }
        }

        @Override
        public String toString() {
            return Const.outputDateTime.format(date) + Arrays.toString(data);
        }
    }

}
