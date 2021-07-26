package com.partsavatar.estimator.shipment;

import java.time.LocalDate;
import java.util.Vector;

public interface ShipmentDAO {
    public Vector<Shipment> getShipmentsByCreatedDate(final LocalDate date);
}
