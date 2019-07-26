package report;

/*
    Nova Niati
    Universitas Sanata Dharma
 */
/*
    Report ini untuk menghitung rata-rata jumlah node yang didapatkan per interval waktu
*/
import java.util.*;
import core.DTNHost;
import core.Settings;
import core.SimClock;
import core.UpdateListener;
import routing.DecisionEngineRouterImproved;
import routing.MessageRouter;
import routing.RoutingDecisionEngineImproved;

public class AverageConvergenTimeReportImproved extends Report implements UpdateListener {

    public static final String NODE_PERWAKTU = "nodepersatuanwaktu";
    public static final int DEFAULT_WAKTU = 1800;
    private double lastRecord = Double.MIN_VALUE;
    private int interval;

    public AverageConvergenTimeReportImproved() {
        super();

        Settings settings = getSettings();
        if (settings.contains(NODE_PERWAKTU)) {
            interval = settings.getInt(NODE_PERWAKTU);
        } else {
            interval = DEFAULT_WAKTU;
        }
    }

    public void updated(List<DTNHost> hosts) {
        if (SimClock.getTime() - lastRecord >= interval) {
            lastRecord = SimClock.getTime();
            printLine(hosts);
        }
    }

    private void printLine(List<DTNHost> hosts) {
        double rata = 0;
        for (DTNHost h : hosts) {
            MessageRouter r = h.getRouter();
            if (!(r instanceof DecisionEngineRouterImproved))
                continue;
            RoutingDecisionEngineImproved de = ((DecisionEngineRouterImproved) r).getDecisionEngine();
            NilaiInisiator n = (NilaiInisiator) de;

            int temp = (int) n.getNilai_val();
            rata += temp;
        }
        double AV_Rata = rata / hosts.size();
        String output = format((int) SimClock.getTime()) + " \t " + format(AV_Rata);
        write(output);
    }
}
