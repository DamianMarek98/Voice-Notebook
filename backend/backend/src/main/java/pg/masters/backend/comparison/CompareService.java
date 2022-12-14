package pg.masters.backend.comparison;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import pg.masters.backend.transcript.Transcript;
import pg.masters.backend.transcript.TranscriptGroup;
import pg.masters.backend.transcript.TranscriptGroupRepository;
import pg.masters.backend.transcript.TranscriptRepository;

import javax.annotation.PostConstruct;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

@Log
@Service
@RequiredArgsConstructor
public class CompareService {
    private final CompareResultRepository compareResultRepository;
    private final TranscriptGroupRepository transcriptGroupRepository;
    private final TranscriptRepository transcriptRepository;
    private final WebClient.Builder webClientBuilder;

    private record CompareReq(String originalText, String toCompare) {

    }

    private record CompareRes(Double wer, Double lev) {

    }

    @PostConstruct
    public void test2() {
        var results = compareResultRepository.findAll();
        final DecimalFormat df = new DecimalFormat("0.00");
        for (var res: results) {
            res.setWordErrorRate(Double.valueOf(df.format(res.getWordErrorRate() * 100)));
        }

        compareResultRepository.saveAll(results);
    }


    //@PostConstruct
    public void test() {
        var transcriptGroups = transcriptGroupRepository.findAllByCreatedOnAfter(LocalDateTime.of(LocalDate.of(2022, 9, 4), LocalTime.MIDNIGHT));
        var results = new ArrayList<CompareResult>();
        for (var tg : transcriptGroups) {
            var transcripts = transcriptRepository.findAllByGroupName(tg.getName());
            for (var transcript: transcripts) {
                var result = generateCompareResult(transcript, tg);

                var requestBody = new CompareReq(result.getText(), transcript.getResultText());
                var response = webClientBuilder.build()
                        .post()
                        .uri("http://127.0.0.1:8000/compare")
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(requestBody)
                        .retrieve()
                        .bodyToMono(CompareRes.class)
                        .block();

                result.setLevenshteinDistance(response.lev());
                result.setWordErrorRate(response.wer());
                results.add(result);
            }
        }

        compareResultRepository.saveAll(results);
    }

    private CompareResult generateCompareResult(Transcript transcript, TranscriptGroup transcriptGroup) {
        var transcriptGroupName = transcriptGroup.getName();
        var compareResult = new CompareResult();
        compareResult.setProvider(transcript.getProvider());
        compareResult.setTranscriptGroupName(transcriptGroupName);
        compareResult.setUserId(transcriptGroup.getUser().getId());
        if ((transcriptGroupName.contains("1") && !transcriptGroupName.contains("0")) || transcriptGroupName.equals("Therr")) {
            compareResult.setTextId(1L);
            compareResult.setText("Lek ma posta?? tabletek, kt??re nale??y przyjmowa?? doustnie, nale??y stosowa?? lek zawsze zgodnie z zaleceniami lekarza. Nie mo??na przekracza?? zalecanej dawki preparatu, poniewa?? nie tylko nie zwi??kszy to skuteczno??ci leku, ale mo??e zaszkodzi?? ??yciu lub zdrowiu. W razie jakichkolwiek uwag lub pyta?? dotycz??cych leku nale??y skonsultowa?? si?? z lekarzem. Doro??li, dzieci po 12 roku ??ycia mog?? przyj???? 2 tabletki 3/4 razy na dob??. Nie mo??na przekroczy?? dawki wi??kszej ni?? 8 tabletek na dob??.");
        } else if (transcriptGroupName.contains("2")) {
            compareResult.setTextId(2L);
            compareResult.setText("Rutinoscorbin w postaci tabletek to lek dedykowany pacjentom w przypadku zwi??kszonego zapotrzebowania organizmu na witamin?? C. Jego stosowanie uzupe??ni niedobory tej witaminy, wspieraj??c jednocze??nie odporno???? organizmu i si???? naturalnych mechanizm??w walki z infekcjami. Szczeg??lnie polecany jest w sezonie zwi??kszonego ryzyka infekcji wirusowych, czyli w okresie jesienno-zimowo-wiosennym. Lek stosowa?? mo??na w ramach prewencji, wspomagaj??co przy walce z infekcjami.");
        } else if (transcriptGroupName.contains("3")) {
            compareResult.setTextId(3L);
            compareResult.setText("??y??k?? zi???? zala?? po??ow?? szklanki wody o temperaturze pokojowej, ogrzewa?? 30-45 min nie doprowadzaj??c do wrzenia, przes??czy??, ostudzi??, odwarem p??uka?? jam?? ustn?? 2 razy dziennie.");
        } else if (transcriptGroupName.contains("4")) {
            compareResult.setTextId(4L);
            compareResult.setText("Substancj?? czynn?? preparatu jest ibuprofen, kt??ry nale??y do grupy niesteroidowych lek??w przeciwzapalnych.");
        } else if (transcriptGroupName.contains("5")) {
            compareResult.setTextId(5L);
            compareResult.setText("Leczenie pierwotnego nadci??nienia t??tniczego u doros??ych oraz dzieci i m??odzie??y od 6 do 18 lat.");
        } else if (transcriptGroupName.contains("6")) {
            compareResult.setTextId(6L);
            compareResult.setText("Aspartam to popularny s??odzik, kt??ry mo??na spotka?? w niekt??rych produktach spo??ywczych. Wiele os??b uwa??a aspartam za ??rodek rakotw??rczy i szkodliwy. Czy faktycznie tak jest? Jakie skutki uboczne powoduje spo??ywanie tego zwi??zku? Oto kilka informacji.");
        } else if (transcriptGroupName.contains("7")) {
            compareResult.setTextId(7L);
            compareResult.setText("Jedna z najwi??kszych bitew w historii ??redniowiecznej Europy, stoczona na polach pod Grunwaldem 15 lipca 1410 roku.");
        } else if (transcriptGroupName.contains("8")) {
            compareResult.setTextId(8L);
            compareResult.setText("Sortowanie b??belkowe jest jednym z najprostszych w implementacji algorytm??w porz??dkuj??cych dane.");
        } else if (transcriptGroupName.contains("9")) {
            compareResult.setTextId(9L);
            compareResult.setText("Rysy to g??ra po??o??ona na granicy polsko-s??owackiej, w Tatrach Wysokich. Ma trzy wierzcho??ki, z kt??rych najwy??szy jest ??rodkowy, znajduj??cy si?? w ca??o??ci na terytorium S??owacji.");
        } else if (transcriptGroupName.contains("10")) {
            compareResult.setTextId(10L);
            compareResult.setText("Oko??o 70 tysi??cy os??b na antyrz??dowej demonstracji w Pradze. Protestuj??cy domagaj?? si?? dymisji rz??du w zwi??zku z cenami energii.");
        } else {
            log.warning("Not matched any id for group name: " + transcriptGroupName);
        }

        return compareResult;
    }
}
