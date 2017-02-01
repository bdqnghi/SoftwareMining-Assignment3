package com.software.assignment3;

import soot.*;
import soot.options.Options;
import soot.util.Chain;

import java.util.Collections;
import java.util.Scanner;

/**
 * Created by quocnghi on 31/1/17.
 */
public class MainDriver2 {
    public static void main(String[] args) {
//        if(args.length == 0){
//            System.err.println("Args please");
//            System.exit(0);
//        }

//        Options.v().set_process_dir(Collections.singletonList("/home/quocnghi/codes/SoftwareMining/src/main/resources/TestSum.jar"));
//        Options.v().set_process_dir(Collections.singletonList("/home/quocnghi/codes/SoftwareMining/src/main/resources/js.jar"));
//        Options.v().set_process_dir(Collections.singletonList("/home/quocnghi/codes/SoftwareMining/process"));
//        Options.v().set_src_prec(Options.src_prec_java);
//        Options.v().set_whole_program(true);
//        Options.v().set_allow_phantom_refs(true);
//        Options.v().set_output_format(Options.output_format_none);
//        Scene.v().loadNecessaryClasses();
//        println("Args 0 : " + args[0]);
        Scene.v().loadClassAndSupport("java.lang.Object");
        Scene.v().loadClassAndSupport("java.lang.System");

//        Scene.v().addBasicClass("com.software.assignment3.MyCounter");
//        Scene.v().loadClassAndSupport(args[0]);

//        targetClass.setApplicationClass();

//        println("Processing entry: " + targetClass.getName());
//        Chain<SootClass> classes = Scene.v().getApplicationClasses();
//
//        SootClass targetClass = null;
//        for(SootClass sootClass : classes){
////            println(sootClass.getName());
//            if(sootClass.getName().equals("tests.TestSum")){
//                targetClass = sootClass;
//
//                println("Found");
//                break;
//            }
//        }
//        System.out.println(targetClass.getMethodCount());
//        Body body = null;
//
//        for (SootMethod method : targetClass.getMethods()) {
//            println(method.getName());
//            if (method.getName().equals("main")) {
//                if (method.isConcrete()) {
//                    body = method.retrieveActiveBody();
//                }
//            }
//        }
//
//        System.out.println(body);
        System.out.println(Scene.v().getSootClassPath());
        Pack jtp = PackManager.v().getPack("jtp");
        jtp.add(new Transform("jtp.instrumenter", new InvokeStaticInstrumenter()));
        soot.Main.main(args);
    }

    private static void println(String s) {
        System.out.println(s);
    }
}
