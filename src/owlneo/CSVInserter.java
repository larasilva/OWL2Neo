/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package owlneo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 *
 * @author lara
 */
public class CSVInserter {

    private BufferedReader reader;
    private int line;
    private int column;
    private String delimiter;
    private String literal;

    public CSVInserter(File filelocal) {
        line = 0;
        column = 0;
        delimiter = ";";
        literal = "\"";
        try {
            reader = new BufferedReader(new FileReader(filelocal));
        } catch (FileNotFoundException e) {
            System.out.println("Reading Error - CSV File");
        }
    }
    
    public CSVInserter(File filelocal, String delimiter, String literal){
        line = 0;
        column = 0;
        this.delimiter=delimiter;
        this.literal = literal;
        try {
            reader = new BufferedReader(new FileReader(filelocal));
        } catch (FileNotFoundException e) {
            System.out.println("Reading Error - CSV File");
        }
    }
    
    public void setDelimiter (String delimiter){
        this.delimiter=delimiter;
    }
    
    public void setLiteral (String literal){
        this.literal=literal;
    }

    public boolean isCSVLoaded() {
        try {
            return reader.ready();
        } catch (IOException e) {
            return false;
        }

    }

    // method that return a CSV line in the ArrayList <String> format

    private ArrayList<String> lineList(String line) {
        //TODO verify if empty columns are also read and sent as empty string

        ArrayList<String> result = new ArrayList();
        result.addAll(Arrays.asList(line.split(delimiter)));

        return result;
    }

    /**
     *
     * @return the next line in the reader with the values separated in th form
     * of a ArrayList<String>
     * 
     * 
     */
    public int getLineIndex(){
        return line;
    }
    
    public int getColumnIndex(){
        return column;
    }
    
    public ArrayList<String> getNextColumn(){
        column++;
        return getColumnAtIndex(column-1);
    }
    
    public ArrayList<String> getNextLine() {
        try {
            line++;
            return lineList(reader.readLine());
        } catch (IOException ex) {
            line--;
            return null;
        }
    }

    private ArrayList<String> getColumnAtIndex(int index) {
        String lineValue="";
        ArrayList<String> lineValues=new ArrayList();
        try {
            reader.reset();
            String currentline;
            int linecount = 0;
            while ((currentline = reader.readLine()) != null) {
                linecount++;
                String[] temp = currentline.split(delimiter);
                if (temp.length > index) {
                    lineValue = temp[index];
                }
                else{
                    lineValue = "<Error>";
                    System.out.println("Error at line "+linecount+":"
                            + "No column "+index+" found.");
                }
                lineValues.add(lineValue);
            }
        } catch (IOException ex) {
            Logger.getLogger(CSVInserter.class.getName()).log(Level.SEVERE, null, ex);
            lineValue="<Error>";
            lineValues.add(lineValue);
        }

        return lineValues;
    }

    //reads a arraylist containing a column and gives an arraylist containing only
    //unique values in the column
    public static ArrayList<String> getUniques(ArrayList<String> values) {
        ArrayList<String> result = new ArrayList();

        for (String v : values) {
            //TODO: remove the extra check
            boolean contains = false;
            //if the result arraylist is empty the 1st value of the column is added
            //to the unique list
            if (result.isEmpty()) {
                result.add(v);
            } //if not, it checks the result arraylist
            else {
                for (String r : result) {
                //if it finds a value equal already stored in the arraylist, 
                    //the contains value is changed to true
                    if (r.equalsIgnoreCase(v)) {
                        contains = true;
                    }
                }
            }
            //if it's not contained in the arraylist, it's added to it
            if (!contains) {
                result.add(v);
            }
        }

        return result;
    }

    public ArrayList<String> getHeader(String fileurl) {
        String header;
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileurl));
            header = br.readLine();

        } catch (FileNotFoundException ex) {
            System.out.println("File not found.");
            return null;
        } catch (IOException ex) {
            System.out.println("File has no readable header.");
            return null;
        }
        ArrayList<String> headerList = new ArrayList();
        String[] headerarray = header.split(delimiter);
        //transforms the array into a arraylist
        headerList.addAll(Arrays.asList(headerarray));

        return headerList;
    }
    
    public boolean isColumnIDCandidate(int index){
        ArrayList <String> column2check = getColumnAtIndex(index);
        ArrayList <String> uniques = getUniques(column2check);
        
        //true if the unique values multiplied by 3 are more than the 
        //whole array of values
        return (uniques.size()*3>column2check.size());
    }

}
