package PartEstimates.DAO;

import PartEstimates.PartEstimate;

import java.util.ArrayList;
import java.util.List;

public interface PartEstimateDAO {

    List<PartEstimate> getAll();

    boolean insert(PartEstimate partEstimate);

    boolean update(PartEstimate partEstimate);

    boolean insertOrUpdate(PartEstimate partEstimate);

    boolean clearAndInsert(ArrayList<PartEstimate> partEstimates);
}
