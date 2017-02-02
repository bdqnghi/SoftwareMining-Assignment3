package com.software.assignment3.soot;

import org.apache.bcel.Repository;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;

import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by quocnghi on 31/1/17.
 */
public class InjectCode {
    public static void main( String[] args ) throws IOException, ClassNotFoundException {

        String jarfile = "/home/quocnghi/codes/SoftwareMining/src/main/resources/js.jar";

        JarFile jar = new JarFile(jarfile);

        JavaClass targetClass = null;

        Enumeration<JarEntry> entries = jar.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();

            if (!entry.getName().endsWith(".class")) {
                continue;
            }

            ClassParser parser = new ClassParser(jarfile, entry.getName());
            JavaClass javaClass = parser.parse();
            println(javaClass.getClassName());

            if(javaClass.getClassName().compareTo("tests.TestSum") == 0){

            }

        }
    }

    static void println(String s){
        System.out.println(s);
    }

//    public static Method insertCodeMethod(JavaClass javaClass,Method method){
//
//    }
}

