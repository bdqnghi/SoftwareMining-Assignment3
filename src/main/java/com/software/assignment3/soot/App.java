package com.software.assignment3.soot;

import soot.Pack;
import soot.PackManager;
import soot.Scene;
import soot.Transform;
import soot.options.Options;

import java.util.Collections;

/**
 * Created by quocnghi on 3/2/17.
 */
public class App {
    public static void main(String[] args) {

        soot.G.reset();
        String classPath = "/usr/lib/jvm/java-7-oracle/jre/lib/rt.jar:"+
                "/usr/lib/jvm/java-7-oracle/jre/lib/jce.jar:"+
                "/home/quocnghi/codes/SoftwareMining/src/main/resources/TestSum.jar:"+
                "/home/quocnghi/codes/SoftwareMining/src/main/resources/js.jar:"+
                "/home/quocnghi/codes/SoftwareMining/src/main/resources/xmlbeans-2.6.0.jar:"+
                "/home/quocnghi/codes/SoftwareMining/target/classes";
        Scene.v().setSootClassPath(classPath);

//        Options.v().set_process_dir(Collections.singletonList("/home/quocnghi/codes/SoftwareMining/src/main/resources/TestSum.jar"));
        Options.v().set_process_dir(Collections.singletonList("/home/quocnghi/codes/SoftwareMining/src/main/resources/js.jar"));
//        Options.v().set_process_dir(Collections.singletonList("/home/quocnghi/codes/SoftwareMining/src/main/resources/js.jar"));
//        Options.v().set_process_dir(Collections.singletonList("/home/quocnghi/codes/SoftwareMining/process"));

        Options.v().set_src_prec(Options.src_prec_java);
        Options.v().set_whole_program(true);
        Options.v().set_keep_line_number(true);
        Options.v().set_keep_offset(true);
//        Options.v().set_allow_phantom_refs(true);
//        Options.v().set_output_format(Options.output_format_none);

        Scene.v().loadClassAndSupport("MyCounter");
        Scene.v().loadNecessaryClasses();

        Pack jtp = PackManager.v().getPack("jtp");
        jtp.add(new Transform("jtp.instrumenter", new LoopCountInstrumenter()));
        PackManager.v().runPacks();
        PackManager.v().writeOutput();
    }

}
