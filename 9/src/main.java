import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;




public class main
{
    public static void main(String[] args)
    {
        ArrayList<Long> arr1 = new ArrayList<Long>();
        ArrayList<Long> arr2 = new ArrayList<Long>();

        for(int i =0; i<10;i++)arr1.add((long)i);//заполнение массивов
        for(int i =10; i<25;i++)arr2.add((long)i);

        Stream<Long> Stream1 = arr1.stream();//создание потоков
        Stream<Long> Stream2 = arr2.stream();
        Stream<Long> result = zip(Stream1,Stream2);//функция перемешки чисел
        List<Long> collect = result
                .collect(Collectors.toList());
        System.out.println(collect);

    }
    public static Stream zip(Stream first, Stream second){
        Stream.Builder<Long> b = Stream.builder();

        Iterator fit = first.iterator();
        Iterator sit = second.iterator();

        while (true)
        {
            if(!(fit.hasNext()&&sit.hasNext())) break;
            else
            {
                b.accept((Long) fit.next());
                b.accept((Long) sit.next());
            }
        }
        Stream<Long> s = b.build();
        return s;
    }
}
