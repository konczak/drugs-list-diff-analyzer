package pl.konczak.nzoz.drugslistdiffanalyzer;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.konczak.nzoz.drugslistdiffanalyzer.dto.Diff;
import pl.konczak.nzoz.drugslistdiffanalyzer.dto.DiffType;
import pl.konczak.nzoz.drugslistdiffanalyzer.dto.DrugRefund;
import pl.konczak.nzoz.drugslistdiffanalyzer.service.DifferenceFindingService;
import pl.konczak.nzoz.drugslistdiffanalyzer.service.DrugRefundAnalysisPersistingService;
import pl.konczak.nzoz.drugslistdiffanalyzer.service.DrugRefundRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class AppCommandLineRunner
        implements CommandLineRunner {

    private final DrugRefundRepository drugRefundRepository;
    private final DifferenceFindingService differenceFindingService;
    private final DrugRefundAnalysisPersistingService drugRefundAnalysisPersistingService;

    @Override
    public void run(final String... args) throws Exception {
//        List<DrugRefund> oldDrugRefunds = drugRefundRepository.findDrugRefunds("e://download//Załącznik_do_obwieszczenia_na_2018.11.xlsx");
//        List<DrugRefund> currentDrugRefunds = drugRefundRepository.findDrugRefunds("e://download//Załącznik_do_obwieszczenia_na_2019.01.xlsx");
        List<DrugRefund> oldDrugRefunds = drugRefundRepository.findDrugRefunds("e://download//Załącznik_do_obwieszczenia_na_2019.01.xlsx");
        List<DrugRefund> currentDrugRefunds = drugRefundRepository.findDrugRefunds("e://download//Załącznik_do_obwieszczenia (25.02).xlsx");

        List<Diff> all = differenceFindingService.findDifferences(oldDrugRefunds, currentDrugRefunds);

        Map<DiffType, List<Diff>> diffsByType = all.stream()
                .collect(Collectors.groupingBy(Diff::getDiffType));

        report(diffsByType);

        String filePath = "e://download//bazinga_" + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).replace(":", "-") + ".xlsx";

        drugRefundAnalysisPersistingService.persist(filePath, diffsByType);
    }

    private void report(final Map<DiffType, List<Diff>> diffsByType) {
        log.info("Found: <{}> deleted, <{}> same, <{}> modified, <{}> add drug refunds",
                Optional.ofNullable(diffsByType.get(DiffType.DELETED)).orElse(Collections.emptyList()).size(),
                Optional.ofNullable(diffsByType.get(DiffType.SAME)).orElse(Collections.emptyList()).size(),
                Optional.ofNullable(diffsByType.get(DiffType.UPDATED)).orElse(Collections.emptyList()).size(),
                Optional.ofNullable(diffsByType.get(DiffType.ADD)).orElse(Collections.emptyList()).size());
    }


}
