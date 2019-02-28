package pl.konczak.nzoz.drugslistdiffanalyzer;

import pl.konczak.nzoz.drugslistdiffanalyzer.dto.DrugRefund;

import java.util.Comparator;
import java.util.Objects;

import org.springframework.stereotype.Component;

@Component
public class DrugRefundComparator
        implements Comparator<DrugRefund> {

    @Override
    public int compare(final DrugRefund o1, final DrugRefund o2) {
        Objects.requireNonNull(o1);
        Objects.requireNonNull(o2);

        if (Objects.equals(o1.getName(), o2.getName())
                && Objects.equals(o1.getPackageContent(), o2.getPackageContent())
                && Objects.equals(o1.getEan(), o2.getEan())
                && Objects.equals(o1.getIndicationsCoveredByRefund(), o2.getIndicationsCoveredByRefund())
                && Objects.equals(o1.getOffLabelIndicationsCoveredByRefund(), o2.getOffLabelIndicationsCoveredByRefund())) {
            return 0;
        }

        int eansCompareResult = o1.getEan().compareTo(o2.getEan());

        if (eansCompareResult != 0) {
            return eansCompareResult;
        }

        int nameCompareResult = o1.getName().compareTo(o2.getName());

        if (nameCompareResult != 0) {
            return nameCompareResult;
        }

        return -1;
    }
}
