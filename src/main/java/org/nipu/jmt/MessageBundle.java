package org.nipu.jmt;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Container of messages.
 *
 * @author Nikita_Puzankov
 */
public class MessageBundle {

    private ResourceBundle messages;

    public MessageBundle() {
        Locale locale = Locale.ENGLISH;
        this.messages = ResourceBundle.getBundle("localization/messages", locale);
    }

    public MessageBundle(String languageTag) {
        Objects.nonNull(languageTag);
        Locale locale = new Locale(languageTag);
        this.messages = ResourceBundle.getBundle("localization/messages", locale);
    }

    public String get(String message) {
        return messages.getString(message);
    }

    public final String get(final String key, final Object... args) {
        return MessageFormat.format(get(key), args);
    }

}
