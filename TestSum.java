import java.util.Scanner;

public class TestSum {

	public static void main(String[] args) {
		int sum = 0;
		System.out.print("Please enter starting i: ");
		int i = new Scanner(System.in).nextInt();
		while ( i < 11 ) {
			sum = sum + i;
			i = i + 1;
		}
		System.out.println("sum = " + sum);
		System.out.println("Ending i = " + i);
	}
}
