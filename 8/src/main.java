
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;



public class main
{
    public static void main(String[] args)
    {
        long a = 25214903917L;
        long c = 11L;
        long m = (long)Math.pow(2,48);
        Stream<Long> infiniteStream = Stream.iterate(0L, i ->  (a * i+  c) % m);// итерация потока значений


        List<Long> collect = infiniteStream
                .limit(10500)//ограничение значений
                .collect(Collectors.toList());//сбор значений на вывод
        System.out.println(collect);//вывод значений



    }
}
