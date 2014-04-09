/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package owlneo;

import au.com.bytecode.opencsv.*;
import au.com.bytecode.opencsv.bean.HeaderColumnNameMappingStrategy;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author lara
 */
public class CSVParse {
    
    CSVParser csvp;
    CSVReader csvr;
    BufferedReader br;
    boolean csvLoaded;
    boolean csvInList;
    List <String[]> csvlist;
    HeaderColumnNameMappingStrategy headerMapper;
    
    public CSVParse(File file){
        //initializing checkers
        csvLoaded = false;       
        csvInList = false;
        //setting of the parser and reader
        try {
            br = new BufferedReader(new FileReader(file));
            csvLoaded = true;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CSVParse.class.getName()).log(Level.SEVERE, null, ex);
        }
        csvp = new CSVParser(';', '\"');
        csvr = new CSVReader(br,';', '\"');
        headerMapper = new HeaderColumnNameMappingStrategy();
        try {
            genList();
        } catch (IOException ex) {
            Logger.getLogger(CSVParse.class.getName()).log(Level.SEVERE, null, ex);
            csvInList=false;
        }
    }
    
    public CSVParse(File file, char delim, char quote){
        csvInList = false;
        csvLoaded = false;
        try {
            br = new BufferedReader(new FileReader(file));
            csvLoaded = true;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CSVParse.class.getName()).log(Level.SEVERE, null, ex);
        }
        csvp = new CSVParser(delim, quote);
        csvr = new CSVReader(br,delim, quote);
        headerMapper = new HeaderColumnNameMappingStrategy();
        try {
            genList();
        } catch (IOException ex) {
            Logger.getLogger(CSVParse.class.getName()).log(Level.SEVERE, null, ex);
            csvInList=false;
        }
    }
    
    private void genList() throws IOException{
        csvlist = csvr.readAll();
        System.out.println("In genlist. \nlist size: " + csvlist.size());
        csvInList = true;
    }
    
    public ArrayList<String> getColumn(int index){
        ArrayList<String> result = new ArrayList();
        for (String [] s: csvlist){
            System.out.println("In getColumn: ");
            result.add(s[index]);
        }
        System.out.println("result size:" + result.size());
        return result;
    }
    
    public HashSet<String> getColumnUniques(int index){
        HashSet<String> result = new HashSet(getColumn(index));
        return result;
    }
    
    public ArrayList<String> getHeaderList(){
        try {
            headerMapper.captureHeader(csvr);
        } catch (IOException ex) {
            Logger.getLogger(CSVParse.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
}
