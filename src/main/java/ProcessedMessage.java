import com.google.cloud.language.v1.AnalyzeEntitySentimentRequest;
import com.google.cloud.language.v1.AnalyzeEntitySentimentResponse;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.EncodingType;
import com.google.cloud.language.v1.Entity;
import com.google.cloud.language.v1.EntityMention;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;

import java.util.List;
import java.util.Map;
import org.telegram.telegrambots.meta.api.objects.Message;

// This is class encapsulating
public class ProcessedMessage {
    // original text
    String text;
    String sender;

    float overallScore;
    float overallMagnitude;
    List<Entity> entityList;
    String entityListDescription;

    // test driver for the class
    public static void main(String[] args) {
        String test = "Britain has blockchain technology";
        String test1 = "I'm happy";
        Message orginalMessage = new Message();
        ProcessedMessage msg = new ProcessedMessage(orginalMessage);
        System.out.println(msg);
        //System.out.println(msg.entityListDescription);
    }

    public ProcessedMessage(Message msg) {
        this.text = msg.getText();
        this.sender= msg.getFrom().getUserName();
        process();
    }

    // process the text using NPL library into useful info
    public void process() {
        // Instantiates a client
        try (LanguageServiceClient language = LanguageServiceClient.create()) {
            // The text to analyze and wrap it in Document
            Document doc = Document.newBuilder()
                    .setContent(text).setType(Document.Type.PLAIN_TEXT).build();

            // Detects the sentiment of the text in overall
            Sentiment sentiment = language.analyzeSentiment(doc).getDocumentSentiment();
            this.overallScore = sentiment.getScore();
            this.overallMagnitude = sentiment.getMagnitude();

            // analyse EntitiesSentimentText

            // request encapsulates the input text we want to analyse on
            AnalyzeEntitySentimentRequest request = AnalyzeEntitySentimentRequest.newBuilder()
                    .setDocument(doc)
                    .setEncodingType(EncodingType.UTF16).build();
            // detect entity sentiments in the given string
            AnalyzeEntitySentimentResponse response = language.analyzeEntitySentiment(request);
            this.entityList = response.getEntitiesList();
            processEntityList();
        } catch (Exception e) {
            System.out.println("Exception thrown when processing the text of ProcessedMessage");
        }

    }

    // process entityList to extract meaningful info
    public void processEntityList() {

        StringBuilder builder = new StringBuilder();

        for (Entity entity : entityList) {
            builder.append(String.format("[ Entity: %s\n", entity.getName()));
            builder.append(String.format("Salience: %.3f\n", entity.getSalience()));
            builder.append(String.format("Sentiment : %s\n", entity.getSentiment()));

            // extracting wikipedia i.e. key--> "wikipedia_url" value --> "https://en.wikipedia.org/wiki/United_Kingdom"
            for (Map.Entry<String, String> entry : entity.getMetadataMap().entrySet()) {
                builder.append(String.format("%s : %s\n", entry.getKey(), entry.getValue()));
            }

            for (EntityMention mention : entity.getMentionsList()) {
                builder.append(String.format("Begin offset: %d\n", mention.getText().getBeginOffset()));
                builder.append(String.format("Content: %s\n", mention.getText().getContent()));
                builder.append(String.format("Magnitude: %.3f\n", mention.getSentiment().getMagnitude()));
                builder.append(String.format("Sentiment score : %.3f\n", mention.getSentiment().getScore()));
                builder.append(String.format("Type: %s ]\n\n", mention.getType()));
            }
        }

        this.entityListDescription = builder.toString();

    }

    public String getText() {
        return text;
    }

    public float getOverallScore() {
        return overallScore;
    }

    public float getOverallMagnitude() {
        return overallMagnitude;
    }

    public List<Entity> getEntityList() {
        return entityList;
    }

    public String getEntityListDescription() {
        return entityListDescription;
    }

    // returns if the processedMsg is a positive msg
    public boolean isPositive() {
        return overallScore > 0;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("the sender: ")
                .append(sender + "\n")
                .append("original text: ")
                .append(text + "\n")
                .append(" the Overall score is: ")
                .append(overallScore + "\n")
                .append(" the overall magnitude is: ")
                .append(overallMagnitude + "\n");
        return builder.toString();
    }

}