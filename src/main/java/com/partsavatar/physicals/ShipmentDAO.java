package com.partsavatar.physicals;

import java.util.Date;
import java.util.Vector;

public interface ShipmentDAO {
    Vector<Shipment> getAllAfter(final Date date);
}
