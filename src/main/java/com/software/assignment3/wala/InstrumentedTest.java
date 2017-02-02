package com.software.assignment3.wala;

import com.ibm.wala.shrikeBT.*;
import com.ibm.wala.shrikeBT.analysis.Verifier;
import com.ibm.wala.shrikeBT.shrikeCT.CTDecoder;
import com.ibm.wala.shrikeBT.shrikeCT.ClassInstrumenter;
import com.ibm.wala.shrikeBT.shrikeCT.OfflineInstrumenter;
import com.ibm.wala.shrikeCT.ClassWriter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintStream;
import java.io.Writer;

/**
 * Created by quocnghi on 2/2/17.
 */
public class InstrumentedTest {
    private final static boolean disasm = true;

    private final static boolean verify = true;

    private static OfflineInstrumenter instrumenter = new OfflineInstrumenter(true);

    final private static boolean doEntry = true;

    private static boolean doExit = true;

    private static boolean doException = false;

    public static void main(String[] args) throws Exception {

        args = new String[]{"TestSum.jar", "-o", "/home/quocnghi/codes/SoftwareMining/target/TestSum_instrumented.jar"};
        for (int i = 0; i < 1; i++) {

            Writer w = new BufferedWriter(new FileWriter("report", false));

            args = instrumenter.parseStandardArgs(args);
            if (args.length > 0) {
                if (args[0].equals("-doexit")) {
                    doExit = true;
                } else if (args[0].equals("-doexception")) {
                    doExit = true;
                    doException = true;
                }
            }

            instrumenter.setPassUnmodifiedClasses(true);
            instrumenter.beginTraversal();
            ClassInstrumenter ci;
            while ((ci = instrumenter.nextClass()) != null) {
                doClass(ci, w);
            }
            instrumenter.close();
        }
    }

    static final String fieldName = "_Bench_enable_trace";

    // Keep these commonly used instructions around
    static final Instruction getSysOut = Util.makeGet(System.class, "out");

    static final Instruction callPrintln = Util.makeInvoke(PrintStream.class, "println", new Class[]{String.class});

    private static void doClass(final ClassInstrumenter ci, Writer w) throws Exception {
        final String className = ci.getReader().getName();
        System.out.println("Class name : " + className);
        w.write("Class: " + className + "\n");
        w.flush();

        for (int m = 0; m < ci.getReader().getMethodCount(); m++) {
            MethodData d = ci.visitMethod(m);
            System.out.println(d.getName());
            // d could be null, e.g., if the method is abstract or native
            if (d != null) {
                w.write("Instrumenting " + ci.getReader().getMethodName(m) + " " + ci.getReader().getMethodType(m) + ":\n");
                w.flush();

                if (disasm) {
                    w.write("Initial ShrikeBT code:\n");
                    (new Disassembler(d)).disassembleTo(w);
                    w.flush();
                }

                if (verify) {
                    Verifier v = new Verifier(d);
                    v.verify();
                }

                MethodEditor methodEditor = new MethodEditor(d);
                methodEditor.beginPass();
//                final String msg1 = "Entering call to " + Util.makeClass("L" + ci.getReader().getName() + ";") + "."
//                        + ci.getReader().getMethodName(m);
                final int noTraceLabel = methodEditor.allocateLabel();
//                methodEditor.insertAtStart(new MethodEditor.Patch() {
//                    @Override
//                    public void emitTo(MethodEditor.Output w) {
//                        w.emit(GetInstruction.make(Constants.TYPE_boolean, CTDecoder.convertClassToType(className), fieldName, true));
//                        w.emit(ConstantInstruction.make(0));
//                        w.emit(ConditionalBranchInstruction.make(Constants.TYPE_int, ConditionalBranchInstruction.Operator.EQ, noTraceLabel));
//                        w.emit(getSysOut);
//                        w.emit(ConstantInstruction.makeString(msg1));
//                        w.emit(callPrintln);
//                        w.emitLabel(noTraceLabel);
//                    }
//                });
                IInstruction[] instr = methodEditor.getInstructions();
                final String msg0 = "Loop called at " + Util.makeClass("L" + ci.getReader().getName() + ";") + "."
                        + ci.getReader().getMethodName(m);
                int i = 0;

                for (IInstruction in : instr) {
                    if (in instanceof ConditionalBranchInstruction) {

                        int b = i;
                        methodEditor.insertBefore(i, new MethodEditor.Patch() {
                            @Override
                            public void emitTo(MethodEditor.Output w) {
                                w.emit(getSysOut);
                                w.emit(ConstantInstruction.makeString(msg0));
                                w.emit(callPrintln);
                                w.emitLabel(noTraceLabel);
                            }
                        });
                    }

                    i++;
                    System.out.println(in.toString());
                }
                methodEditor.applyPatches();
                if (disasm) {
                    w.write("Final ShrikeBT code:\n");
                    (new Disassembler(d)).disassembleTo(w);
                    w.flush();
                }
            }
        }
        ClassWriter cw = ci.emitClass();

//        cw.addField(ClassReader.ACC_PUBLIC | ClassReader.ACC_STATIC, "counter", Constants.TYPE_int, new ClassWriter.Element[]{new ConstantValueWriter(cw, 10)});
        instrumenter.outputModifiedClass(ci, cw);
    }
}
