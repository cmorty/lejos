package lejos.tutorial.speech;

import java.io.*;
import java.util.*;
import javax.speech.*;
import javax.speech.recognition.*;
import javax.speech.synthesis.*;
import josx.rcxcomm.*;
import josx.vision.*;

public class SpeechControl {

    static RuleGrammar ruleGrammar;
    static DictationGrammar dictationGrammar;
    static Recognizer recognizer;
    static Synthesizer synthesizer;
    static ResourceBundle resources;

    //
    // This is the listener for rule grammar results.  The
    // resultAccepted method is called when the user issues a command.
    // We then request the tags that we associated with the grammar in
    // rover.gram, and take an action based on the tag.  Using tags
    // rather than looking directly at what the user said means we can
    // change the grammar without having to change our code.
    //
    static ResultListener ruleListener = new ResultAdapter() {

        // accepted result
        public void resultAccepted(ResultEvent e) {
            try {

                // get the result
                FinalRuleResult result = (FinalRuleResult) e.getSource();
                String tags[] = result.getTags();
                System.out.println("Said " + tags[0]);

                // The user has said "forwards"
                if (tags[0].equals("forwards")) {
                    RCX.forward();

                // the user has said "backwards"
                } else if (tags[0].equals("backwards")) {
                    RCX.backward();

                // the user has said "stop"
                } else if (tags[0].equals("stop")) {
                    RCX.stop();

                // the user has said "left"
                } else if (tags[0].equals("left")) {
                    System.out.println("Turning left");
                    RCX.spinLeft();
                    System.out.println("Turned left");

                // the user has said "right"
                } else if (tags[0].equals("right")) {
                    RCX.spinRight();

                // the user has said "up"
                } else if (tags[0].equals("up")) {
                    RCX.tiltUp(1);

                // the user has said "down"
                } else if (tags[0].equals("down")) {
                   RCX.tiltDown(1);
 
                // the user has said "good bye"
                } else if (tags[0].equals("bye")) {
                    speak(resources.getString("bye"));
		    if (synthesizer!=null)
			synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);
                    RCXRemote.stop();
		    Thread.sleep(1000);
                    System.exit(0);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        // rejected result - say "eh?" etc.
        int i = 0;
        String eh[] = null;
        public void resultRejected(ResultEvent e) {
            if (eh==null) {
                String s = resources.getString("eh");
                StringTokenizer t = new StringTokenizer(s);
                int n = t.countTokens();
                eh = new String[n];
                for (int i=0; i<n; i++)
                    eh[i] = t.nextToken();
            }
	    if (((Result)(e.getSource())).numTokens() > 2)
		speak(eh[(i++)%eh.length]);
        }
    };


    //
    // This is the listener for dictation results.  The resultUpdated
    // method is called for every recognized token.  The
    // resultAccepted method is called when the dictation result
    // completes, which in this application occurs when the user says
    // "that's all".
    //
    static ResultListener dictationListener = new ResultAdapter() {

        int n = 0; // number of tokens seen so far

        public void resultUpdated(ResultEvent e) {
            Result result = (Result) e.getSource();
            for (int i=n; i<result.numTokens(); i++)
                System.out.println(result.getBestToken(i).getSpokenText());
            n = result.numTokens();
        }

        public void resultAccepted(ResultEvent e) {
            Result result = (Result) e.getSource();
            String s = "";
            for (int i=0; i<n; i++)
                s += result.getBestToken(i).getSpokenText() + " ";
            speak(s);
            n = 0;
        }
    };


    //
    // Audio listener prints out audio levels to help diagnose problems.
    //
    static RecognizerAudioListener audioListener =new RecognizerAudioAdapter(){
        public void audioLevel(RecognizerAudioEvent e) {
            if (e.getAudioLevel() > 0.2) System.out.println("volume " + e.getAudioLevel());
        }
    };


    //
    // EngineListener reports engine errors, such as audio busy.
    //
    static EngineListener engineListener = new EngineAdapter() {
	public void engineError(EngineErrorEvent e) {
	    System.out.println
		("Engine error: " + e.getEngineError().getMessage());
	}
    };


    //
    // Here's a method to say something. If the synthesizer isn't
    // available, we just print the message.
    //
    static void speak(String s) {
        if (synthesizer!=null) {
            try {
                synthesizer.speak(s, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else
            System.out.println(s);
    }
 
    //
    // main
    //
    public static void main(String args[]) {

        try {

            // locale, resources
            if (args.length>0) Locale.setDefault(new Locale(args[0], ""));
            if (args.length>1) Locale.setDefault(new Locale(args[0], args[1]));
            System.out.println("locale is " + Locale.getDefault());
            resources = ResourceBundle.getBundle("res");
            System.out.println(resources);
            // create a recognizer matching default locale, add audio listener
            recognizer = Central.createRecognizer(null);
            System.out.println(recognizer);
            recognizer.allocate();
            recognizer.getAudioManager().addAudioListener(audioListener);
	    recognizer.addEngineListener(engineListener);

            // create dictation grammar
            dictationGrammar = recognizer.getDictationGrammar(null);
            dictationGrammar.addResultListener(dictationListener);
            
            // create a rule grammar, activate it
            String grammarName = resources.getString("grammar");
            Reader reader = new FileReader(grammarName);
            ruleGrammar = recognizer.loadJSGF(reader);
            ruleGrammar.addResultListener(ruleListener);
            ruleGrammar.setEnabled(true);
        
            // commit new grammars, start recognizer
            recognizer.commitChanges();
            recognizer.requestFocus();
            recognizer.resume();

            // create a synthesizer, speak a greeting
            synthesizer = Central.createSynthesizer(null);
            if (synthesizer!=null) {
		synthesizer.allocate();
		synthesizer.addEngineListener(engineListener);
	    }
            speak(resources.getString("greeting"));

        } catch (Exception e) {

            e.printStackTrace();
            System.exit(-1);

        }
    }

}

