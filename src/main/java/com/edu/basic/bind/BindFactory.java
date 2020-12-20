package com.edu.basic.bind;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class BindFactory {
    private static Logger logger = LoggerFactory.getLogger(BindFactory.class);
    public static LinkedBlockingQueue<BindModel> barCodeQueue = new LinkedBlockingQueue<>(20);
    public static LinkedBlockingQueue<BindModel> imageQueue = new LinkedBlockingQueue<>(20);
    public static List<List<BindModel>> bindSuccessList = new ArrayList<>();
    public static List<BindModel> unbindImage = new ArrayList<>();
    public static List<BindModel> unbindBarcode = new ArrayList<>();
    public static List<BindModel> unmatchModel = new ArrayList<>();





}
