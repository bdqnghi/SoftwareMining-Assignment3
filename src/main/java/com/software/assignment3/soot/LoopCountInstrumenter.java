package com.software.assignment3.soot;

import soot.*;
import soot.jimple.*;
import soot.util.Chain;

import java.util.Iterator;
import java.util.Map;

public class LoopCountInstrumenter extends BodyTransformer {

    static SootClass counterClass;
    static SootMethod increaseCounter, reportCounter,countExecutedCounter;

    static {
        counterClass = Scene.v().loadClassAndSupport("MyCounter");
        increaseCounter = counterClass.getMethod("void increase(java.lang.String)");
        reportCounter = counterClass.getMethod("void report(java.lang.String)");
        countExecutedCounter = counterClass.getMethod("void count_branching_executed()");
    }
    protected void internalTransform(Body body, String phase, Map options) {

        SootMethod method = body.getMethod();
        if(method.getSignature().contains("org.mozilla.javascript.tools.shell.Main")){
            System.out.println("Instrumenting method : " + method.getSignature());
        }
        //System.out.println("Instrumenting method : " + method.getSignature());

        Chain units = body.getUnits();

        Iterator stmtIt = units.snapshotIterator();

        while (stmtIt.hasNext()) {
            Stmt stmt = (Stmt) stmtIt.next();

            String methodId = method.getSignature();
            String ID = methodId + "," + stmt.getJavaSourceStartLineNumber();

            if (stmt.branches()) {
                //System.out.println("Found for loop or while loop in method : " + methodId);
                InvokeExpr incExpr = Jimple.v().newStaticInvokeExpr(increaseCounter.makeRef(),
                        StringConstant.v(ID));

                Stmt incStmt = Jimple.v().newInvokeStmt(incExpr);

                units.insertBefore(incStmt, stmt);
            }

            if ((stmt instanceof ReturnStmt)
                    ||(stmt instanceof ReturnVoidStmt)) {

                InvokeExpr reportExpr= Jimple.v().newStaticInvokeExpr(reportCounter.makeRef(),StringConstant.v(ID));

                Stmt reportStmt = Jimple.v().newInvokeStmt(reportExpr);
                units.insertBefore(reportStmt, stmt);

                if(method.getSignature().contains("<org.mozilla.javascript.tools.shell.Main: int exec(java.lang.String[])>")){
                    InvokeExpr countExpr = Jimple.v().newStaticInvokeExpr(countExecutedCounter.makeRef());

                    Stmt countStmt = Jimple.v().newInvokeStmt(countExpr);
                    units.insertBefore(countStmt, stmt);
                }
            }
        }
    }
}