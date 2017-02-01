package com.software.assignment3;

import soot.Pack;
import soot.PackManager;
import soot.Scene;
import soot.Transform;
import soot.options.Options;

public class MainDriver {
  public static void main(String[] args) {

    /* check the arguments */
//    if (args.length == 0) {
//      System.err.println("Usage: java MainDriver [options] classname");
//      System.exit(0);
//    }
    Scene.v().addBasicClass("com.software.assignment3.MyCounter");
    Scene.v().loadClassAndSupport("com.software.assignment3.MyCounter");
      System.out.println(Scene.v().getSootClassPath());

    Options.v().set_src_prec(Options.src_prec_java);
    Options.v().set_whole_program(true);
//        Options.v().set_allow_phantom_refs(true);
    Options.v().set_output_format(Options.output_format_none);
//        Scene.v().loadNecessaryClasses();
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