package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.application.services.*;

import java.util.concurrent.CountDownLatch;

public class Services {

    private TimeVariables time;
    private int selling;
    private int inventoryService;
    private int logistics;
    private int resourcesService;
    private Customer[] customers;
    private CountDownLatch countDownLatch;
    private Thread[] threads;



    public TimeVariables getTime() {
        return time;
    }

    public int getSelling() {
        return selling;
    }

    public int getInventoryService() {
        return inventoryService;
    }

    public int getLogistics() {
        return logistics;
    }

    public int getResourcesService() {
        return resourcesService;
    }

    public Customer[] getCustomers() {
        return customers;
    }
    public void setCustomer(){
        for(int i=0;i<customers.length;i++){
            customers[i].setCustomer();
        }
    }
    //initializing the threads for the micro services
    public  void start(){
        //for initializing time service after everyone is synced
        int sum = selling+inventoryService+logistics+resourcesService+customers.length;
        this.threads = new Thread[sum];
        int counter = 0;
        countDownLatch = new CountDownLatch(sum);

        //initializing all the services
        for(int i=0;i<selling;i++){
            Thread t = new Thread(new SellingService("SellingService"+i,countDownLatch));
            threads[counter] = t;
            counter++;
            t.start();
        }
        for(int i=0;i<inventoryService;i++){
            Thread t = new Thread(new InventoryService("InventoryService"+i ,countDownLatch));
            threads[counter] = t;
            counter++;
            t.start();
        }
        for(int i=0;i<logistics;i++){
            Thread t = new Thread(new LogisticsService("LogisticsService"+i , countDownLatch, time.getSpeed()));
            threads[counter] = t;
            counter++;
            t.start();
        }
        for(int i=0;i<resourcesService;i++){
            Thread t = new Thread(new ResourceService("ResourceService"+i , countDownLatch));
            threads[counter] = t;
            counter++;
            t.start();
        }
        for(int i=0;i<customers.length;i++){
            Thread t = new Thread(new APIService(customers[i],"APIService"+i , countDownLatch));
            threads[counter] = t;
            counter++;
            t.start();
        }

        try{
            countDownLatch.await();
        }
        catch (InterruptedException ignored){}
        Thread timeThread = new Thread(new TimeService("time" ,time.getSpeed(),time.getDuration()));
        timeThread.start();
        try {
            timeThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i = 0; i<threads.length; i++){
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }


}
