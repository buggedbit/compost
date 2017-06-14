package com.partsavatar;

import Jama.Matrix;
import com.partsavatar.abstracts.SInequality;
import com.partsavatar.productdimension.ProductDimension;
import com.partsavatar.productdimension.ProductDimensionDAOImpl;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Vector;

class Estimator {

    private static ProductDimension getMergedEstimate(ProductDimension p1, ProductDimension p2) {
        if (p1.getSku() == null || p2.getSku() == null) throw new IllegalArgumentException();
        if (!Objects.equals(p1.getSku(), p2.getSku())) throw new IllegalArgumentException();

        p1.setLength(Math.min(p1.getLength(), p2.getLength()));
        p1.setBreadth(Math.min(p1.getBreadth(), p2.getBreadth()));
        p1.setHeight(Math.min(p1.getHeight(), p2.getHeight()));
        p1.setWeight(Math.min(p1.getWeight(), p2.getWeight()));

        return p1;
    }

    private static boolean pushEstimate(final ProductDimension newDimension) {
        String sku = newDimension.getSku();

        ProductDimensionDAOImpl productDimensionDAO = new ProductDimensionDAOImpl();
        ProductDimension oldDimension = productDimensionDAO.getBySku(sku);

        ProductDimension mergedEstimate = getMergedEstimate(newDimension, oldDimension);

        return productDimensionDAO.updateOrSave(mergedEstimate);
    }

    private static void pushNewDimensions(final Vector<String> skus, final double[][] newDimensions) {
        for (int i = 0; i < skus.size(); i++) {
            ProductDimension ith = new ProductDimension(skus.get(i),
                    newDimensions[i][0],
                    newDimensions[i][1],
                    newDimensions[i][2],
                    newDimensions[i][3]
            );
            Estimator.pushEstimate(ith);
        }
    }

    private static void estimateFromSquareSet(final Vector<SInequality> squareSet) {

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
            //  System.out.println("Note : Singular matrix found");
        }

    }

    static void estimateFromAllSquareSets(final Map<Set<String>, Vector<Vector<SInequality>>> squareSets) {
        for (Map.Entry<Set<String>, Vector<Vector<SInequality>>> squareSetsFromThis : squareSets.entrySet()) {
            for (Vector<SInequality> squareSet : squareSetsFromThis.getValue()) {
                Estimator.estimateFromSquareSet(squareSet);
            }
        }
    }

}
