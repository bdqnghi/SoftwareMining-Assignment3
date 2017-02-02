package com.software.assignment3;

import com.ibm.wala.shrikeBT.*;
import com.ibm.wala.shrikeBT.MethodEditor.Output;
import com.ibm.wala.shrikeBT.analysis.Verifier;
import com.ibm.wala.shrikeBT.info.LocalAllocator;
import com.ibm.wala.shrikeBT.shrikeCT.ClassInstrumenter;
import com.ibm.wala.shrikeBT.shrikeCT.OfflineInstrumenter;
import com.ibm.wala.shrikeCT.ClassWriter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Random;

/**
 * This is a demo class.
 * <p/>
 * Class files are taken as input arguments (or if there are none, from standard input). The methods in those files are
 * instrumented: we insert a System.err.println() at ever method call, and a System.err.println() at every method entry.
 * <p/>
 * In Unix, I run it like this: java -cp ~/dev/shrike/shrike com.ibm.wala.shrikeBT.shrikeCT.tools.Bench test.jar -o output.jar
 * <p/>
 * The instrumented classes are placed in the directory "output" under the current directory. Disassembled code is written to the
 * file "report" under the current directory.
 */
public class Mangler {
    private static OfflineInstrumenter instrumenter = new OfflineInstrumenter(true);

    private static final boolean verify = true;

    private static final boolean disasm = true;

    public static void main(String[] args) throws Exception {
        args = new String[]{"operators.jar", "-o", "/Users/quocnghi/codes/SoftwareMining-Assignment3/target/out_mangler.jar"};
        for (int i = 0; i < 1; i++) {


            Writer w = new BufferedWriter(new FileWriter("report", false));

            args = instrumenter.parseStandardArgs(args);
            int seed;
            try {
                seed = Integer.parseInt("10");
            } catch (NumberFormatException ex) {
                System.err.println("Invalid number: " + args[0]);
                w.close();
                return;
            }

            Random r = new Random(seed);
            instrumenter.setPassUnmodifiedClasses(true);
            instrumenter.beginTraversal();
//            instrumenter.setOutputJar(new File("output.jar"));
            ClassInstrumenter ci;
            while ((ci = instrumenter.nextClass()) != null) {
                doClass(ci, w, r);
            }
            instrumenter.close();
        }
    }

    private static void doClass(final ClassInstrumenter ci, Writer w, final Random r) throws Exception {
        final String className = ci.getReader().getName();
        w.write("Class: " + className + "\n");
        w.flush();

        for (int m = 0; m < ci.getReader().getMethodCount(); m++) {
            MethodData d = ci.visitMethod(m);

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

                final int passes = r.nextInt(4) + 1;

                for (int i = 0; i < passes; i++) {
                    final boolean doGet = true; // r.nextBoolean();
                    final boolean doPut = true; // r.nextBoolean();
                    final boolean doArrayStore = true; // r.nextBoolean();
                    final int tmpInt = LocalAllocator.allocate(d, "I");
                    final int tmpAny = LocalAllocator.allocate(d);

                    final MethodEditor me = new MethodEditor(d);
                    me.beginPass();

                    me.visitInstructions(new MethodEditor.Visitor() {
                        @Override
                        public void visitGet(IGetInstruction instruction) {
                            if (doGet && !instruction.isStatic()) {
                                insertBefore(new MethodEditor.Patch() {
                                    @Override
                                    public void emitTo(Output w) {
                                        w.emit(DupInstruction.make(0));
                                    }
                                });
                                insertAfter(new MethodEditor.Patch() {
                                    @Override
                                    public void emitTo(Output w) {
                                        w.emit(SwapInstruction.make());
                                        w.emit(Util.makePut(Slots.class, "o"));
                                    }
                                });
                            }
                        }

                        @Override
                        public void visitPut(IPutInstruction instruction) {
                            if (doPut && !instruction.isStatic()) {
                                insertBefore(new MethodEditor.Patch() {
                                    @Override
                                    public void emitTo(Output w) {
                                        w.emit(SwapInstruction.make());
                                        w.emit(DupInstruction.make(1));
                                        w.emit(SwapInstruction.make());
                                    }
                                });
                                insertAfter(new MethodEditor.Patch() {
                                    @Override
                                    public void emitTo(Output w) {
                                        w.emit(Util.makePut(Slots.class, "o"));
                                    }
                                });
                            }
                        }

                        @Override
                        public void visitArrayStore(final IArrayStoreInstruction instruction) {
                            if (doArrayStore) {
                                final int label = me.allocateLabel();
                                insertBefore(new MethodEditor.Patch() {
                                    @Override
                                    public void emitTo(Output w) {
                                        String t = Util.getStackType(instruction.getType());
                                        w.emit(StoreInstruction.make(t, tmpAny));
                                        w.emit(StoreInstruction.make(Constants.TYPE_int, tmpInt));
                                        w.emit(DupInstruction.make(0));
                                        w.emit(LoadInstruction.make(Constants.TYPE_int, tmpInt));
                                        w.emit(LoadInstruction.make(t, tmpAny));
                                        if (t.equals(Constants.TYPE_int)) {
                                            w.emit(DupInstruction.make(0));
                                            w.emit(ConstantInstruction.make(0));
                                            w.emit(ConditionalBranchInstruction.make(t, ConditionalBranchInstruction.Operator.EQ, label));
                                            w.emit(DupInstruction.make(0));
                                            w.emit(Util.makePut(Slots.class, "i"));
                                            w.emitLabel(label);
                                        }
                                    }
                                });
                                insertAfter(new MethodEditor.Patch() {
                                    @Override
                                    public void emitTo(Output w) {
                                        w.emit(Util.makePut(Slots.class, "o"));
                                        w.emit(LoadInstruction.make(Constants.TYPE_int, tmpInt));
                                        w.emit(Util.makePut(Slots.class, "i"));
                                    }
                                });
                            }
                        }
                    });

                    // this updates the data d
                    me.applyPatches();
                }

                if (disasm) {
                    w.write("Final ShrikeBT code:\n");
                    (new Disassembler(d)).disassembleTo(w);
                    w.flush();
                }
            }
        }

        if (ci.isChanged()) {
            ClassWriter cw = ci.emitClass();
            instrumenter.outputModifiedClass(ci, cw);
        }
    }
}