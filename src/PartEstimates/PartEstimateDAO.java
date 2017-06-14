package PartEstimates;

import PartEstimates.PartEstimate;

import java.util.ArrayList;
import java.util.List;

public interface PartEstimateDAO {

    ArrayList<PartEstimate> getAll();

    boolean clearTableAndInsert(ArrayList<PartEstimate> partEstimates);

}
