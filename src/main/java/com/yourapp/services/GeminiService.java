package com.yourapp.services;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class GeminiService {



    // 🔴 PASTE YOUR *NEW* API KEY HERE
    private static final String MODEL_NAME = "models/gemini-2.5-flash";
    private static final String API_KEY = "api key"; // add ur api here
    // Uses the MODEL_NAME constant you defined above
    private static final String API_URL =
            "https://generativelanguage.googleapis.com/v1beta/" + MODEL_NAME + ":generateContent?key=" + API_KEY;


    private final HttpClient client;

    public GeminiService() {
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public String askGemini(String userMessage, String context) {
        try {
            String prompt = buildPrompt(userMessage, context);

            // 1. Construct the JSON Payload
            JSONObject textPart = new JSONObject();
            textPart.put("text", prompt);

            JSONArray parts = new JSONArray();
            parts.put(textPart);

            JSONObject content = new JSONObject();
            content.put("parts", parts);

            JSONArray contents = new JSONArray();
            contents.put(content);

            JSONObject payload = new JSONObject();
            payload.put("contents", contents);

            // 2. Build Request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                    .build();

            // 3. Send Request
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // 4. Debugging Output (Check your console!)
            System.out.println("--- API DEBUG INFO ---");
            System.out.println("Status Code: " + response.statusCode());
            System.out.println("Response Body: " + response.body());
            System.out.println("----------------------");

            // 5. Handle Response
            if (response.statusCode() == 200) {
                JSONObject jsonResponse = new JSONObject(response.body());
                // deep parsing to get the text
                return jsonResponse.getJSONArray("candidates")
                        .getJSONObject(0)
                        .getJSONObject("content")
                        .getJSONArray("parts")
                        .getJSONObject(0)
                        .getString("text");
            } else {
                return "Error: API returned status " + response.statusCode();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Exception occurred: " + e.getMessage();
        }
    }

    public static void main(String[] args) {
        System.out.println("Connecting to AuditDoc AI Brain...");

        GeminiService service = new GeminiService();

        // Test Question
        String question = "Hello Gemini! I am building a JavaFX app called AuditDoc AI. Can you verify you are receiving this?";

        System.out.println("Sending: " + question);
        String response = service.askGemini(question, "Contexte de test minimal");

        System.out.println("\n--- GEMINI REPLY ---");
        System.out.println(response);
        System.out.println("--------------------");
    }

    private String buildPrompt(String userMessage, String context) {
        StringBuilder builder = new StringBuilder();
        builder.append("Tu es l'assistant AuditDoc AI. Réponds en français en t'appuyant uniquement sur le contexte fourni.\\n");
        builder.append("Contexte disponible :\\n");
        builder.append(context == null || context.isBlank() ? "Aucun contexte fourni." : context);
        builder.append("\\n\\nQuestion utilisateur : ").append(userMessage).append("\\n");
        builder.append("Si une information manque dans le contexte, précise-le au lieu de l'inventer.");
        return builder.toString();
    }
}