package com.karnwal.handwritingrater;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.github.anastr.speedviewlib.components.Style;
import com.google.mlkit.common.MlKitException;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.common.model.RemoteModelManager;
import com.google.mlkit.vision.digitalink.DigitalInkRecognition;
import com.google.mlkit.vision.digitalink.DigitalInkRecognitionModel;
import com.google.mlkit.vision.digitalink.DigitalInkRecognitionModelIdentifier;
import com.google.mlkit.vision.digitalink.DigitalInkRecognizer;
import com.google.mlkit.vision.digitalink.DigitalInkRecognizerOptions;
import com.google.mlkit.vision.digitalink.RecognitionCandidate;
import com.karnwal.handwritingrater.databinding.ActivityMainBinding;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.clearCanvas.setOnClickListener(v -> {
            binding.canvas.clear();
            binding.speedView.speedTo(0);
        });
        DigitalInkRecognitionModelIdentifier modelIdentifier = null;
        try {
            modelIdentifier = DigitalInkRecognitionModelIdentifier.fromLanguageTag("en-US");
        }
        catch (MlKitException ignored) {}
        DigitalInkRecognitionModel model = DigitalInkRecognitionModel.builder(modelIdentifier).build();
        RemoteModelManager.getInstance().download(model, new DownloadConditions.Builder().build());
        DigitalInkRecognizer recognizer = DigitalInkRecognition.getClient(DigitalInkRecognizerOptions.builder(model).build());
        binding.evaluate.setOnClickListener(v -> {
            recognizer.recognize(binding.canvas.getInk())
                    .addOnSuccessListener(result -> {
                            List<Character> wordCharacterList = Arrays.asList('F', 'e', 'a', 's', 't');
                        List<String> results = result.getCandidates().stream().map(RecognitionCandidate::getText).collect(Collectors.toList());
                        Log.e("NotAnError", String.valueOf(results));
                        try {results.get(9);}
                        catch (IndexOutOfBoundsException indexOutOfBoundsException) {Log.e("Error", String.valueOf(indexOutOfBoundsException)); return;}
                        int count = 0;
                        float score = 0F;
                        int position = 0;
                        for (int i = 0; i < 50; i++) {
                            try {
                                if (count == 5) {count = 0;}
                                if (wordCharacterList.get(count) == (convertStrToChars(results.get(position))).get(count)) {
                                    score += 2.7F;
                                }
                                else {
                                    score -= 1.1F;
                                }
                                if (count == 4) {
                                    if (wordCharacterList.size() < convertStrToChars(results.get(position)).size()
                                            || wordCharacterList.size() > convertStrToChars(results.get(position)).size()) {
                                        score -= 5.4F;
                                    }
                                }
                                if (((i+1) % 5) == 0) {position++;}
                                count++;
                            } catch (Exception ignored) {}
                        }
                        Log.e("Score", String.valueOf(score));
                        binding.speedView.speedTo(score);
                        if (score < 30) {
                            binding.suggestionsText.setText("To improve your handwriting follow the tips below\n" +
                                    "1. Write slower in order to get better quality in the writing\n" +
                                    "2. Have a relaxed grip when writing\n" +
                                    "3. Practice writing on paper. It is important to find a writing utensil that can give the best output when writing\n" +
                                    "4. For more tips visit https://tinyurl.com/3hae3mya");
                            binding.suggestionsText.setTextColor(Color.WHITE);
                        }
                        else if (score < 65) {
                            binding.suggestionsText.setText("To improve your handwriting follow the tips below\n" +
                                    "1. Try writing letters closer to improve quality\n" +
                                    "2. Find the best way to hold the writing utensil\n" +
                                    "3. Avoid excess lines or dots and keep a consistent and proper letter shape\n" +
                                    "4. For more tips visit https://tinyurl.com/3hae3mya");
                            binding.suggestionsText.setTextColor(Color.WHITE);
                        }
                        else if (score < 100) {
                            binding.suggestionsText.setText("Congratulations, your handwriting is excellent here are some fun things you can do\n" +
                                    "1. Try to learn cursive (If not known)\n" +
                                    "2. Experiment with new writing styles\n" +
                                    "3. Find new writing utensils and see which one is best\n" +
                                    "4. For more tips visit https://tinyurl.com/2bs4s6pc");
                            binding.suggestionsText.setTextColor(Color.WHITE);
                        }
                    });
        });
        binding.speedView.clearSections();
        binding.speedView.makeSections(3, Color.CYAN, Style.BUTT);
        binding.speedView.getSections().get(0).setColor(Color.RED);
        binding.speedView.getSections().get(1).setColor(Color.YELLOW);
        binding.speedView.getSections().get(2).setColor(Color.GREEN);
        binding.speedView.setUnit("");
        binding.speedView.setWithTremble(false);
        binding.speedView.setSpeedTextColor(Color.YELLOW);
        binding.speedView.setTextColor(Color.YELLOW);
    }

    private List<Character> convertStrToChars(String str) {
        List<Character> chars = new ArrayList<>();
        for (char character : str.toCharArray()) {
            chars.add(character);
        }
        return chars;
    }
}