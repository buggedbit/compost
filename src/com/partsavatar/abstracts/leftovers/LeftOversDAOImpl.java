package com.partsavatar.abstracts.leftovers;

import com.partsavatar.abstracts.SInequality;

import java.io.*;
import java.util.Vector;

public class LeftOversDAOImpl implements LeftOversDAO {
    @Override
    public Vector<SInequality> getAll() {
        Vector<SInequality> old_sInequalities = new Vector<>();

        final String old_sInequalities_path = "../db/raw/old/sInequalities";

        try {
            BufferedReader br = new BufferedReader(new FileReader(old_sInequalities_path));

            int no_of_old_sInequalities = Integer.parseInt(br.readLine());
            for (int i = 0; i < no_of_old_sInequalities; i++) {
                old_sInequalities.add(new SInequality(br));
            }
            return old_sInequalities;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public boolean clearTableAndInsert(Vector<SInequality> partEstimates) {
        final String old_sInequalities_path = "../db/raw/old/sInequalities";

        try {
            // Write to file
            BufferedWriter bw = new BufferedWriter(new FileWriter(old_sInequalities_path));

            bw.write(LeftOvers.size() + "\n");
            for (SInequality sInequality : LeftOvers.getAll()) {
                bw.write(sInequality.format());
                bw.flush();
            }
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

}
