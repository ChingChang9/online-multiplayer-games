package system.utilities;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import system.entities.game.hangman.HangmanGame;
import system.entities.template.HangmanTemplate;
import system.use_cases.builders.HangmanGameInteractiveBuilder;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Email Composer Class
 */
public class PseudoEmailComposer implements EmailService {

    private enum TemplateTag {
        RESET_PASSWORD
    }

    private final Map<TemplateTag, String> emailTemplates;

    public PseudoEmailComposer() {
        Gson gson = new Gson();
        try {
            JsonReader reader = new JsonReader(new FileReader(
                    "src/system/configuration_files/email_templates.json"));
            Type type = new TypeToken<Map<TemplateTag, String>>(){}.getType();
            emailTemplates = gson.fromJson(reader, type);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Fatal: Can't find the configuration file for hangman design questions.");
        }
    }

    public void sendResetPasswordEmail(String receiverEmail, String tempPassword) {
        String text = emailTemplates.get(TemplateTag.RESET_PASSWORD) + tempPassword;
        try {
            PrintWriter writer = new PrintWriter("to:" + receiverEmail + ".txt", "UTF-8");
            writer.println(text);
            writer.close();
        } catch (UnsupportedEncodingException | FileNotFoundException e) {
            System.out.println("Email service failure.");
            e.printStackTrace();
        }

    }
}
