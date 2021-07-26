package com.partsavatar.estimator;

import Jama.Matrix;
import com.partsavatar.estimator.productdimension.ProductDimension;
import com.partsavatar.estimator.productdimension.ProductDimensionDAO;
import com.partsavatar.estimator.productdimension.ProductDimensionDAOImpl;
import com.partsavatar.estimator.sinequality.SInequality;
import lombok.NonNull;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Vector;

public class EstimateFromSquares {

    private ProductDimension getMergedEstimate(@NonNull final ProductDimension p1, @NonNull final ProductDimension p2) {
        if (p1.getSku() == null || p2.getSku() == null) throw new IllegalArgumentException();
        if (!Objects.equals(p1.getSku(), p2.getSku())) throw new IllegalArgumentException();

        return new ProductDimension(
                p1.getSku(),
                Math.min(p1.getLength(), p2.getLength()),
                Math.min(p1.getBreadth(), p2.getBreadth()),
                Math.min(p1.getHeight(), p2.getHeight()),
                Math.min(p1.getWeight(), p2.getWeight())
        );
    }

    private boolean pushEstimate(@NonNull final ProductDimension newDimension) {
        String sku = newDimension.getSku();

        ProductDimensionDAO productDimensionDAO = new ProductDimensionDAOImpl();
        ProductDimension oldDimension = productDimensionDAO.getBySku(sku);

        ProductDimension mergedEstimate = getMergedEstimate(newDimension, oldDimension);

        return productDimensionDAO.updateOrSave(mergedEstimate);
    }

    private void pushNewDimensions(@NonNull final Vector<String> skus, final double[][] newDimensions) {
        for (int i = 0; i < skus.size(); i++) {
            ProductDimension ith = new ProductDimension(skus.get(i),
                    newDimensions[i][0],
                    newDimensions[i][1],
                    newDimensions[i][2],
                    newDimensions[i][3]
            );
            pushEstimate(ith);
        }
    }

    private void estimateFromSquareSet(@NonNull final Vector<SInequality> squareSet) {

        double[][] a = new double[squareSet.size()][squareSet.size()];
        double[][] b = new double[squareSet.size()][4];

        for (int i = 0; i < squareSet.size(); i++) {
            SInequality sInequality = squareSet.get(i);
            a[i] = sInequality.getCoefficientRow();
            b[i] = sInequality.getConstantRow();
        }

        Matrix A = new Matrix(a);
        Matrix B = new Matrix(b);

        if (A.det() != 0) {
            Matrix X = A.solve(B);
            double[][] estimates = X.getArray();

            boolean has_non_positive_dimension = false;

            for (double[] estimate : estimates) {
                for (double dimension : estimate) {
                    if (dimension <= 0) {
                        has_non_positive_dimension = true;
                        break;
                    }
                }
                if (has_non_positive_dimension) break;
            }

            if (has_non_positive_dimension) {
                // todo : specially solve these
            } else {
                pushNewDimensions(squareSet.get(0).getVariableRow(), estimates);
            }

        } else {
            //  todo : throw these into left overs
        }

    }

    void estimateFromAllSquareSets(@NonNull final Map<Set<String>, Vector<Vector<SInequality>>> squareSets) {
        for (Map.Entry<Set<String>, Vector<Vector<SInequality>>> squareSetsFromThis : squareSets.entrySet()) {
            for (Vector<SInequality> squareSet : squareSetsFromThis.getValue()) {
                estimateFromSquareSet(squareSet);
            }
        }
    }

}
