
package bgu.spl.mics.application.passiveObjects;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;


// Auxiliary class\function for printing a new file
public class PrintFile {

    private String filename;
    private Object object;


    public PrintFile(String filename, Object object){
        this.filename = filename;
        this.object = object;
    }

    public void print(){
        try {
            FileOutputStream fileOut =
                    new FileOutputStream(filename);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(object);
            out.close();
            fileOut.close();
        } catch (IOException i) {
            i.printStackTrace();
        }
    }
}