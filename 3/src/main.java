
import java.util.Scanner;
public class main
{
    public static void main(String[] args)
    {
        System.out.print("Please input HEX string: ");
        Scanner sc = new Scanner(System.in);
        String s = sc.nextLine();
        System.out.print("Converted to decimal: ");
        System.out.println((Long.parseLong(s, 16)));
    }
}
