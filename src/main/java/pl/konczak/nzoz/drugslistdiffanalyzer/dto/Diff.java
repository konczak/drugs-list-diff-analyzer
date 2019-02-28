package pl.konczak.nzoz.drugslistdiffanalyzer.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class Diff {

    private final DrugRefund old;
    private final DrugRefund current;
    private final DiffType diffType;

    public static Diff withOld(final DrugRefund drugRefund) {
        return new Diff(drugRefund, null, DiffType.DELETED);
    }

    public static Diff withCurrent(final DrugRefund drugRefund) {
        return new Diff(null, drugRefund, DiffType.ADD);
    }

    public Diff applyCurrent(final DrugRefund drugRefund) {
        Diff diff;

        if (drugRefund.equals(this.old)) {
            diff = new Diff(this.old, drugRefund, DiffType.SAME);
        } else {
            diff = new Diff(this.old, drugRefund, DiffType.UPDATED);
        }

        return diff;
    }
}
