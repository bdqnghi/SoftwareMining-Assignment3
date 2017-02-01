package com.software.assignment3;

import soot.Pack;
import soot.PackManager;
import soot.Scene;
import soot.Transform;

public class MainDriver {
  public static void main(String[] args) {

    /* check the arguments */
    if (args.length == 0) {
      System.err.println("Usage: java MainDriver [options] classname");
      System.exit(0);
    }
//    Scene.v().addBasicClass("com.software.assignment3.MyCounter");
//    Scene.v().loadClassAndSupport("java.io.ObjectInputStream");
    /* add a phase to transformer pack by call Pack.add */
    Pack jtp = PackManager.v().getPack("jtp");
    jtp.add(new Transform("jtp.instrumenter",
            new InvokeStaticInstrumenter()));

    /* Give control to Soot to process all options, 
     * InvokeStaticInstrumenter.internalTransform will get called.
     */
    soot.Main.main(args);
  }
}