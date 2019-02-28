package pl.konczak.nzoz.drugslistdiffanalyzer.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.konczak.nzoz.drugslistdiffanalyzer.DrugRefundComparator;
import pl.konczak.nzoz.drugslistdiffanalyzer.dto.Diff;
import pl.konczak.nzoz.drugslistdiffanalyzer.dto.DrugRefund;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class DifferenceFindingService {

    private final DrugRefundComparator drugRefundComparator;

    public List<Diff> findDifferences(final List<DrugRefund> oldDrugRefunds, final List<DrugRefund> currentDrugRefunds) {
        List<Diff> oldDiffs = oldDrugRefunds.stream()
                .map(drugRefund -> Diff.withOld(drugRefund))
                .collect(Collectors.toList());
        List<Diff> addOrUpdatedDiffs = new ArrayList<>();

        long counter = 0;
        for (DrugRefund currentDrugRefund : currentDrugRefunds) {
            counter++;
            if (counter % 100 == 0) {
                log.info("Compared <{}> of <{}> current drug refunds", counter, currentDrugRefunds.size());
            }
            boolean missing = true;

            Iterator<Diff> iterator = oldDiffs.iterator();

            while (iterator.hasNext()) {
                Diff diff = iterator.next();
                DrugRefund oldDrugRefund = diff.getOld();

                if (drugRefundComparator.compare(oldDrugRefund, currentDrugRefund) == 0) {
                    Diff updated = diff.applyCurrent(currentDrugRefund);

                    addOrUpdatedDiffs.add(updated);
                    iterator.remove();
                    missing = false;
                    break;
                }
            }
            if (missing) {
                addOrUpdatedDiffs.add(Diff.withCurrent(currentDrugRefund));
            }
        }

        List<Diff> all = new ArrayList<>(oldDiffs);
        all.addAll(addOrUpdatedDiffs);

        return all;
    }
}
