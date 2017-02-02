package com.software.assignment3.soot;

import java.io.PrintStream;
import java.util.Scanner;

public class TestSum
{
  public static void main(String[] args)
  {
    int sum = 0;
    System.out.print("Please enter starting i: ");int i = new Scanner(System.in).nextInt();
    do
    {
      sum += i;i++;System.out.println("Loop called at tests.TestSum.main");
    } while (i < 11);
    System.out.println(new StringBuilder("sum = ").append(sum).toString());System.out.println(new StringBuilder("Ending i = ").append(i).toString());
  }
}