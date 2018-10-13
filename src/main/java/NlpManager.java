import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.telegram.telegrambots.meta.api.objects.Message;


/**
 * this class serves as an npl processor for any String inputs from MyAmazingBot
 */
public class NlpManager {

    private List<Message> unprocessedMessage;
    private List<ProcessedMessage> processedMessages;

    // driver test method
    public static void main(String[] args) {
        String test1 = "Britain has blockchain technology";
        String test2 = "I'm happy";
        //List<Message> list = Arrays.asList(new Message(), test2);
        //NlpManager manager = new NlpManager(list);
        //System.out.println("highest scored message: \n" + manager.getHighestScoredMsg());
        //System.out.println(manager.processedMessages);

    }

    public NlpManager(List<Message> unprocessedString) {
        this.unprocessedMessage = unprocessedString;
        processMessages(unprocessedString);
    }

    public void processMessages(List<Message> l) {
        this.processedMessages = unprocessedMessage.stream()
                .map(s -> new ProcessedMessage(s)).collect(Collectors.toList());
    }

    public ProcessedMessage getHighestScoredMsg() {
        int currentHighest = 0;
        int counter = 0;
        for (ProcessedMessage m : processedMessages) {
            if (currentHighest < m.getOverallScore()) {
                currentHighest = counter;
            }
            counter++;
        }
        return processedMessages.get(currentHighest);
    }

    public List<ProcessedMessage> getFilteredList(Predicate<ProcessedMessage> p) {
        return processedMessages.stream()
                .filter(m -> p.test(m))
                .collect(Collectors.toList());
    }

}