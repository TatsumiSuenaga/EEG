package com.example.grant.bluetooth_elicited_brain_stimulation;

import java.io.File;
import java.util.UUID;

/**
 * Created by ryan on 2/23/16.
 */
public class Reading {
    private UUID mID;
    private Graph mGraph;
    private File mFile;
    private String mMuscle; // this might become a different type of object!

    public Reading(File file) {
        mFile = file;
        mID = UUID.randomUUID();
    }

    public UUID getID() {
        return mID;
    }

    public Graph getGraph() {
        return mGraph;
    }

    public void setGraph(Graph graph) {
        mGraph = graph;
    }

    public File getFile() {
        return mFile;
    }

    public String getMuscle() {
        return mMuscle;
    }

    public void setMuscle(String muscle) {
        mMuscle = muscle;
    }
}
