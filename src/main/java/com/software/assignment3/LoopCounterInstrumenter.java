package com.software.assignment3;

import soot.Body;
import soot.BodyTransformer;
import soot.SootMethod;
import soot.util.Chain;

import java.util.Map;

/**
 * Created by quocnghi on 31/1/17.
 */
public class LoopCounterInstrumenter extends BodyTransformer {

    private static void println(String s){
        System.out.println(s);
    }

    @Override
    protected void internalTransform(Body body, String s, Map map) {
        SootMethod method = body.getMethod();
        println("Instrumenting method : " + method.getSignature());

        Chain units = body.getUnits();
    }
}
