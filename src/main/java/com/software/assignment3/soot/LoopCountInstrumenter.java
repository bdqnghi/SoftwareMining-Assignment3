package com.software.assignment3.soot;

import soot.*;
import soot.jimple.*;
import soot.util.Chain;

import java.util.Iterator;
import java.util.Map;

public class LoopCountInstrumenter extends BodyTransformer {

    static SootClass counterClass;
    static SootMethod increaseCounter, reportCounter;

    static {
        counterClass = Scene.v().loadClassAndSupport("MyCounter");
        increaseCounter = counterClass.getMethod("void increase(java.lang.String,int)");
        reportCounter = counterClass.getMethod("void report(java.lang.String)");
    }
    protected void internalTransform(Body body, String phase, Map options) {

        SootMethod method = body.getMethod();
        System.out.println("Instrumenting method : " + method.getSignature());

        Chain units = body.getUnits();

        Iterator stmtIt = units.snapshotIterator();

        while (stmtIt.hasNext()) {
            Stmt stmt = (Stmt) stmtIt.next();

            String methodId = method.getSignature();

//            String line = (LineNumberTag)(stmt.getTag("LineNumberTag")).getLineNumber();

            String ID = methodId;

            if (stmt.branches()) {
                System.out.println("Found for loop or while loop in method : " + methodId);
                InvokeExpr incExpr = Jimple.v().newStaticInvokeExpr(increaseCounter.makeRef(),
                        StringConstant.v(ID), IntConstant.v(1));

                Stmt incStmt = Jimple.v().newInvokeStmt(incExpr);

                units.insertBefore(incStmt, stmt);
            }

            if ((stmt instanceof ReturnStmt)
                    ||(stmt instanceof ReturnVoidStmt)) {
                System.out.println("Reaching the end of method :" + methodId);
                InvokeExpr reportExpr= Jimple.v().newStaticInvokeExpr(reportCounter.makeRef(),StringConstant.v(ID));

                Stmt reportStmt = Jimple.v().newInvokeStmt(reportExpr);
                units.insertBefore(reportStmt, stmt);
            }

        }


    }
}