package bgu.spl.mics.application;

import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.passiveObjects.*;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.util.*;


/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class BookStoreRunner {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        MessageBus messageBus = MessageBusImpl.getInstance();

        Gson gson = new Gson();

        try {
            JsonReader reader = new JsonReader(new FileReader(args[0]));
            Parser parser = gson.fromJson(reader, Parser.class);
            //sets customer and book
            parser.setBooks();
            parser.getServices().setCustomer();
            for (Customer c : parser.getServices().getCustomers())
                c.setCustomerService();
            //initializing services
            Inventory inventory = Inventory.getInstance();
            inventory.load(parser.getInitialInventory());
            ResourcesHolder resourcesHolder = ResourcesHolder.getInstance();
            resourcesHolder.load(parser.getInitialResources()[0].getVehicles());
            MoneyRegister moneyRegister = MoneyRegister.getInstance();
            HashMap<Integer, Customer> outputMap = new HashMap<>();
            for (Customer c : parser.getServices().getCustomers())
                outputMap.put(c.getId(), c);
            // starting the program
            parser.getServices().start();

            // making serialized output
            inventory.printInventoryToFile(args[2]);
            moneyRegister.printOrderReceipts(args[3]);
            PrintFile customerPrint = new PrintFile(args[1], outputMap);
            customerPrint.print();
            PrintFile moneyReg = new PrintFile(args[4], MoneyRegister.getInstance());
            moneyReg.print();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


}

