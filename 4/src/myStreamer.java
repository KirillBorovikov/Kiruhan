import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

public class myStreamer
{


    public static Stream<String> toStringStream(String s)
    {
        Stream.Builder<String> b = Stream.builder();//новый поток строк
        Scanner scanner = new Scanner(s);//объект для считывания данных
        while (scanner.hasNext())//проверка на наличие символов в потоке
        {
            b.accept(scanner.next());
        }
        Stream<String> stream = b.build();
        return stream;
    }

    public static Stream<Integer> toIntStream(String s)
    {
        Stream.Builder<Integer> b = Stream.builder();
        Scanner scanner = new Scanner(s);
        while (scanner.hasNext())
        {
            b.accept(scanner.nextInt());
        }
        Stream<Integer> stream = b.build();
        return stream;
    }

    public static Stream<Double> toDoubleStream(String s)
    {
        Stream.Builder<Double> b = Stream.builder();
        Scanner scanner = new Scanner(s);
        while (scanner.hasNext())
        {
            b.accept(scanner.nextDouble());
        }
        Stream<Double> stream = b.build();
        return stream;
    }
}
