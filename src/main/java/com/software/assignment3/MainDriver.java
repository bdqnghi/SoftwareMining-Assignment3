package com.software.assignment3;

import soot.*;
import soot.options.Options;
import soot.util.Chain;

import java.util.Collections;

/**
 * Created by quocnghi on 31/1/17.
 */
public class MainDriver {
    public static void main(String[] args) {
//        if(args.length == 0){
//            System.err.println("Args please");
//            System.exit(0);
//        }

//        Options.v().set_process_dir(Collections.singletonList("/home/quocnghi/Downloads/tests.jar"));
        Options.v().set_src_prec(Options.src_prec_java);
        Options.v().set_whole_program(true);
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_output_format(Options.output_format_none);

//        Scene.v().loadNecessaryClasses();
        SootClass targetClass = Scene.v().loadClassAndSupport(args[0]);
        println("Processing entry: " + targetClass.getName());
//        Chain<SootClass> classes = Scene.v().getApplicationClasses();
//        SootClass targetClass = null;
//        for(SootClass sootClass : classes){
//            println(sootClass.getName());
//            if(sootClass.getName().equals("tests.TestSum")){
//                targetClass = sootClass;
//                println("Found");
//            }
//        }

        Body body = null;

        for(SootMethod method : targetClass.getMethods()){
            if(method.getName().equals("main")){
                if(method.isConcrete()){
                    body = method.retrieveActiveBody();
                }
            }
        }

        System.out.println(body);
        Pack jtp = PackManager.v().getPack("jtp");
        jtp.add(new Transform("jtp.instrumenter",new LoopCounterInstrumenter()));
        soot.Main.main(args);
    }

    private static void println(String s){
        System.out.println(s);
    }
}
