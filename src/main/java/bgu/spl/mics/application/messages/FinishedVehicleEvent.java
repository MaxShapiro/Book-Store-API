package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

public class FinishedVehicleEvent implements Event {

    private Future<DeliveryVehicle> futureDeliveryVehicle;

    public FinishedVehicleEvent(Future<DeliveryVehicle> f){
        this.futureDeliveryVehicle = f;
    }

    public Future<DeliveryVehicle> getFutureDeliveryVehicle() {
        return futureDeliveryVehicle;
    }

}
