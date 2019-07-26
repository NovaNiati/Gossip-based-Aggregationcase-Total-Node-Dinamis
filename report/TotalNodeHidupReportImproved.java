package report;
/*
    Nova Niati
    Universitas Sanata Dharma
*/
/*
    Report ini untuk menghitung jumlah node yang hidup
*/
import java.util.*;
import core.DTNHost;
import core.Settings;
import core.SimClock;
import core.UpdateListener;
import routing.DecisionEngineRouterImproved;
import routing.MessageRouter;
import routing.RoutingDecisionEngineImproved;

public class TotalNodeHidupReportImproved extends Report implements UpdateListener {

    public static final String NODE_PERWAKTU = "nodepersatuanwaktu";
    public static final int DEFAULT_WAKTU = 1800;
    private double lastRecord = Double.MIN_VALUE;
    private int interval;

    public TotalNodeHidupReportImproved() {
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
        double active = 0;
        for (DTNHost host : hosts) {
            MessageRouter r = host.getRouter();
            if (!(r instanceof DecisionEngineRouterImproved)) 
                continue;
            RoutingDecisionEngineImproved de = ((DecisionEngineRouterImproved) r).getDecisionEngine();
            boolean inter = host.isRadioActive();
            if (inter == true) {
                active++;
            }
        }
        String print = format((int) SimClock.getTime()) + " \t " + active;
        write(print);
    }
}
