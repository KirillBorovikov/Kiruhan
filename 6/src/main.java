
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class main
{
    public static void main(String[] args)
    {
        long countOfChar = 0;
        Scanner in = new Scanner(System.in);//объект для ввода
        System.out.print("Please enter the char: ");//Вводим искомую букву
        String key = in.next();
        try{
            String pathOfFile = "D:\\Java\\6\\Newby.txt";//Файл для поиска
            String SS = new String(Files.readAllBytes(Paths.get(pathOfFile)));
            Pattern p = Pattern.compile(key);
            Matcher m = p.matcher(SS);
            while(m.find())//Подсчет самой буквы
            {
                countOfChar++;
            }
   }catch (Exception e){}
        System.out.println(countOfChar);
}
}


