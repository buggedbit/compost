package Abstract.LeftOvers;

import Abstract.SInequality;

import java.util.Vector;

public interface LeftOversDAO {

    Vector<SInequality> getAll();

    boolean clearTableAndInsert(Vector<SInequality> partEstimates);

}
