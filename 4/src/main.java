import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class main
{
    public static void main(String[] args)
    {
        List<String> collect = myStreamer.toStringStream("что где когда")
                .collect(Collectors.toList());
        System.out.println(collect);

    }
}
