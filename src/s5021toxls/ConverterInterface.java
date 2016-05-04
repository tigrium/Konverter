/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s5021toxls;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import jxl.read.biff.BiffException;
import jxl.write.WriteException;

/**
 *
 * @author Katalin
 */
public interface ConverterInterface {
    public String getName();
    public void saveToXls(File output) throws IOException, BiffException, WriteException, ParseException;
    public List getData();
}
