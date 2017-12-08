import java.util.Scanner;

public class main
{
    public static void main(String[] args)
    {
        Scanner in = new Scanner(System.in);
        System.out.println("Enter string: ");
        String sentence = in.nextLine();
        sentence= Rot13.main(sentence);
        System.out.println("Coded string: ");
        System.out.println(sentence);
        sentence= Rot13.main(sentence);
        System.out.println("Encoded string: ");
        System.out.println(sentence);
    }
}
