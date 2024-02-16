package com.telerikacademy.web.forumsystem.helpers;

import com.telerikacademy.web.forumsystem.exceptions.NotAllowedContentException;
import com.telerikacademy.web.forumsystem.models.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TextPurifier {
    public static final String THE_CONTENT_CONTAINS_FORBIDDEN_WORD = "The content contains forbidden word/s your account has been banned!";
    private static final List<String> nonAllowedWords = List.of("arse", " ass ", "ballsack", "balls", "bastard",
            "bitch", "biatch", "bloody", "blowjob", "blow job", "bollock", "bollok", "boner",
            "boob", "bugger", "bum", "butt", "buttplug", "clitoris", "cock", "coon", "crap",
            "cunt", "damn", "dick", "dildo", "dyke", "fag", "feck", "fellate", "fellatio", "felching",
            "fuck", "f u c k", "fudgepacker", "fudge packer", "flange", "Goddamn", "God damn", "hell",
            "homo", "jerk", "jizz", "knobend", "knob end", "labia", "lmao", "lmfao", "muff", "nigger",
            "nigga", "omg", "penis", "piss", "poop", "prick", "pube", "pussy", "queer", "scrotum", "sex",
            "shit", " s hit ", "sh1t", "slut", "smegma", "spunk", " tit ", "tosser", "turd", "twat", "vagina",
            "wank", "whore", "wtf");

            public void checkTextAndBan(String text, User user) throws NotAllowedContentException {
                if (containsNonAllowedWords(text)){

                    user.setIsBlocked(true);
                    throw new NotAllowedContentException(THE_CONTENT_CONTAINS_FORBIDDEN_WORD);
                }
            }

    private boolean containsNonAllowedWords(String text){
        for (String word : nonAllowedWords) {
            if (text.contains(word)){
                return true;
            }
        }
        return false;
    }
}