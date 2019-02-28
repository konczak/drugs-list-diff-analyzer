package pl.konczak.nzoz.drugslistdiffanalyzer.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
@EqualsAndHashCode
public class DrugRefund {

    private final String name;
    private final String packageContent;
    private final String ean;
    private final String refundSince;
    private final String refundPeriod;
    private final String indicationsCoveredByRefund;
    private final String offLabelIndicationsCoveredByRefund;

}
