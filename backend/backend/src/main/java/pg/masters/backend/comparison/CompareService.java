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
            compareResult.setText("Lek ma postać tabletek, które należy przyjmować doustnie, należy stosować lek zawsze zgodnie z zaleceniami lekarza. Nie można przekraczać zalecanej dawki preparatu, ponieważ nie tylko nie zwiększy to skuteczności leku, ale może zaszkodzić życiu lub zdrowiu. W razie jakichkolwiek uwag lub pytań dotyczących leku należy skonsultować się z lekarzem. Dorośli, dzieci po 12 roku życia mogą przyjąć 2 tabletki 3/4 razy na dobę. Nie można przekroczyć dawki większej niż 8 tabletek na dobę.");
        } else if (transcriptGroupName.contains("2")) {
            compareResult.setTextId(2L);
            compareResult.setText("Rutinoscorbin w postaci tabletek to lek dedykowany pacjentom w przypadku zwiększonego zapotrzebowania organizmu na witaminę C. Jego stosowanie uzupełni niedobory tej witaminy, wspierając jednocześnie odporność organizmu i siłę naturalnych mechanizmów walki z infekcjami. Szczególnie polecany jest w sezonie zwiększonego ryzyka infekcji wirusowych, czyli w okresie jesienno-zimowo-wiosennym. Lek stosować można w ramach prewencji, wspomagająco przy walce z infekcjami.");
        } else if (transcriptGroupName.contains("3")) {
            compareResult.setTextId(3L);
            compareResult.setText("Łyżkę ziół zalać połową szklanki wody o temperaturze pokojowej, ogrzewać 30-45 min nie doprowadzając do wrzenia, przesączyć, ostudzić, odwarem płukać jamę ustną 2 razy dziennie.");
        } else if (transcriptGroupName.contains("4")) {
            compareResult.setTextId(4L);
            compareResult.setText("Substancją czynną preparatu jest ibuprofen, który należy do grupy niesteroidowych leków przeciwzapalnych.");
        } else if (transcriptGroupName.contains("5")) {
            compareResult.setTextId(5L);
            compareResult.setText("Leczenie pierwotnego nadciśnienia tętniczego u dorosłych oraz dzieci i młodzieży od 6 do 18 lat.");
        } else if (transcriptGroupName.contains("6")) {
            compareResult.setTextId(6L);
            compareResult.setText("Aspartam to popularny słodzik, który można spotkać w niektórych produktach spożywczych. Wiele osób uważa aspartam za środek rakotwórczy i szkodliwy. Czy faktycznie tak jest? Jakie skutki uboczne powoduje spożywanie tego związku? Oto kilka informacji.");
        } else if (transcriptGroupName.contains("7")) {
            compareResult.setTextId(7L);
            compareResult.setText("Jedna z największych bitew w historii średniowiecznej Europy, stoczona na polach pod Grunwaldem 15 lipca 1410 roku.");
        } else if (transcriptGroupName.contains("8")) {
            compareResult.setTextId(8L);
            compareResult.setText("Sortowanie bąbelkowe jest jednym z najprostszych w implementacji algorytmów porządkujących dane.");
        } else if (transcriptGroupName.contains("9")) {
            compareResult.setTextId(9L);
            compareResult.setText("Rysy to góra położona na granicy polsko-słowackiej, w Tatrach Wysokich. Ma trzy wierzchołki, z których najwyższy jest środkowy, znajdujący się w całości na terytorium Słowacji.");
        } else if (transcriptGroupName.contains("10")) {
            compareResult.setTextId(10L);
            compareResult.setText("Około 70 tysięcy osób na antyrządowej demonstracji w Pradze. Protestujący domagają się dymisji rządu w związku z cenami energii.");
        } else {
            log.warning("Not matched any id for group name: " + transcriptGroupName);
        }

        return compareResult;
    }
}
