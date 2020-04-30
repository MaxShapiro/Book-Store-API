package bgu.spl.mics.application.passiveObjects;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.*;


public class InventoryTest {

    BookInventoryInfo[] bIIArray;
    Inventory inventory;

    @Before
    public void setUp() throws Exception{
        inventory = Inventory.getInstance();
        //initializing the books array for the inventory testing
        String book_1_name = "Harry Potter";
        int book_1_amount = 1;
        int book_1_price = 10;
        String book_2_name = "Lord Of The Rings";
        int book_2_amount = 2;
        int book_2_price = 5;
        BookInventoryInfo book1 = new BookInventoryInfo(book_1_name,book_1_amount,book_1_price);
        BookInventoryInfo book2 = new BookInventoryInfo(book_2_name,book_2_amount,book_2_price);
        bIIArray = new BookInventoryInfo[]{book1, book2};
    }

    @Test
    public void getInstance() {
        assertNotNull(inventory);
    }

    @Test
    public void load() {
        //initializing the inventory
        inventory.load(bIIArray);
        //checking that all the book are actually in the inventory
        assertEquals(inventory.checkAvailabiltyAndGetPrice("Harry Potter"), 10);
        assertEquals(inventory.checkAvailabiltyAndGetPrice("Lord Of The Rings"), 5);
        assertEquals(inventory.checkAvailabiltyAndGetPrice("Bible"), -1);
    }

    @Test
    public void take() {
        //initializing the inventory
        inventory.load(bIIArray);
        //
        assertEquals(inventory.take("Harry Potter"), OrderResult.SUCCESSFULLY_TAKEN);
        assertEquals(inventory.take("Lord Of The Rings"),OrderResult.SUCCESSFULLY_TAKEN);
        assertEquals(inventory.take("Lord Of The Rings"),OrderResult.SUCCESSFULLY_TAKEN);
        //checking that the books are no longer in the inventory
        assertEquals(inventory.take("Lord Of The Rings"),OrderResult.NOT_IN_STOCK);
        assertEquals(inventory.take("Harry Potter"), OrderResult.NOT_IN_STOCK);
    }

    @Test
    public void checkAvailabiltyAndGetPrice() {
        //initializing the inventory
        inventory.load(bIIArray);
        //
        assertEquals(inventory.checkAvailabiltyAndGetPrice("Harry Potter"), 10);
        assertEquals(inventory.checkAvailabiltyAndGetPrice("Lord Of The Rings"), 5);
        assertEquals(inventory.checkAvailabiltyAndGetPrice("Bible"), -1);
    }

    @Test
    public void printInventoryToFile() {
        //initializing the inventory
        inventory.load(bIIArray);
        //
        inventory.printInventoryToFile("inventory.ser");
        File file = new File("inventory.ser");
        assertTrue(file.exists());
    }

    @After
    public void tearDown() throws Exception {
        bIIArray = null;
        inventory = null;
    }
}