package pg.masters.backend.recognition.speechtext;

import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

@Log4j2
@Service
public class SpeechtextService {

    @Value("${speechtext.key}")
    private String key;

    public String transcriptionFromFile(byte[] post_body) throws Exception {
        HttpURLConnection conn;

        // endpoint and options to start a transcription task
        var endpoint = new URL("https://api.speechtext.ai/recognize?key=" + key + "&language=en-US&punctuation=true&format=wav");

        // send an audio transcription request
        conn = (HttpURLConnection) endpoint.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/octet-stream");

        conn.setDoOutput(true);
        conn.connect();
        OutputStream os = conn.getOutputStream();
        os.write(post_body);
        os.flush();
        os.close();

        var responseCode = conn.getResponseCode();
        if (responseCode == 200) {
            var in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            var response = new StringBuffer();
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();
            String result = response.toString();
            JSONObject json = new JSONObject(result);
            // get the id of the speech recognition task
            String task = json.getString("id");
            log.info("Speechtext task ID: " + task);
            // endpoint to check status of the transcription task
            URL res_endpoint = new URL("https://api.speechtext.ai/results?key=" + key + "&task=" + task + "&summary=true&summary_size=15&highlights=true&max_keywords=15");
            JSONObject results;
            while (true) {
                conn = (HttpURLConnection) res_endpoint.openConnection();
                conn.setRequestMethod("GET");
                in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                response = new StringBuffer();
                String res;
                while ((res = in.readLine()) != null) {
                    response.append(res);
                }
                in.close();
                results = new JSONObject(response.toString());
                if (results.getString("status").equals("failed")) {
                    log.info(" Speechtext failed to transcribe!");
                    break;
                }
                if (results.getString("status").equals("finished")) {
                    var resultString = new JSONObject(results.getString("results")).getString("transcript");
                    log.info("Speechtext transcription with id: " + task + " finished, result:");
                    log.info(resultString);
                    return resultString;
                }

                TimeUnit.SECONDS.sleep(5);
            }
        } else {
            log.error("Speechtext transcription failed with code: " + responseCode);
        }

        return "";
    }
}
