import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.Document.Type;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class MyAmazingBot extends TelegramLongPollingBot {

    public Sentiment getSentiment(String message) throws Exception {
        Sentiment sentiment = null;
        // Instantiates a client
        try (LanguageServiceClient language = LanguageServiceClient.create()) {

            // The text to analyze

            Document doc = Document.newBuilder()
                    .setContent(message).setType(Type.PLAIN_TEXT).build();

            // Detects the sentiment of the text
            sentiment = language.analyzeSentiment(doc).getDocumentSentiment();

            System.out.printf("Text: %s%n", message);
            System.out.printf("Sentiment: %s, %s%n", sentiment.getScore(), sentiment.getMagnitude());
        }

        return sentiment;
    }

    @Override
    public void onUpdateReceived(Update update) {

        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chat_id = update.getMessage().getChatId();
            String messageReceived = update.getMessage().getText();
            // add inlinekeyboardbutton
            if (messageReceived.equals("/get")){

                InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                List<InlineKeyboardButton> rowInline = new ArrayList<>();
                rowInline.add(new InlineKeyboardButton().setText("Get sentiment").setCallbackData("get_sentiment_button"));
                // Set the keyboard to the markup
                rowsInline.add(rowInline);

                executeSendMessageWithButtons("You send /get", chat_id, markupInline, rowsInline);
            } else {
                Sentiment sentiment = null;
                try {
                    sentiment = getSentiment(messageReceived);
                } catch (Exception e) {
                    System.err.print(e);
                }

                if (sentiment == null) {
                    executeSendMessage("can't analyze the sentiment.", chat_id);
                    return;
                }
                // Set variables
                String responseMessage = "Sentiment: " + sentiment.getScore() + ", " + sentiment.getMagnitude();


                executeSendMessage(responseMessage, chat_id);
            }

        } else if (update.hasCallbackQuery()) {
            // Set variables
            String call_data = update.getCallbackQuery().getData();
            long chat_id = update.getCallbackQuery().getMessage().getChatId();

            // see which button is pressed.
            if (call_data.equals("get_sentiment_button")) {
                String answer = "Updated message text";
                executeSendMessage(answer, chat_id);
            }
        }
    }

    public void executeSendMessage(String message, Long chat_id) {
        SendMessage send = new SendMessage() // Create a message object object
                .setChatId(chat_id)
                .setText(message);
        try {
            execute(send); // Sending our message object to user
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void executeSendMessageWithButtons(String message, Long chat_id, InlineKeyboardMarkup markupInline , List<List<InlineKeyboardButton>> rowsInline) {
        SendMessage send = new SendMessage() // Create a message object object
                .setChatId(chat_id)
                .setText(message);
        markupInline.setKeyboard(rowsInline);
        send.setReplyMarkup(markupInline);
        try {
            execute(send); // Sending our message object to user
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        // Return bot username
        // If bot username is @MyAmazingBot, it must return 'MyAmazingBot'
        return "kopiko_bot";
    }

    @Override
    public String getBotToken() {
        // Return bot token from BotFather
        return "589260614:AAEbTxcM3RIhIvieepLjPPxNv2ft3d--MjM";
    }
}