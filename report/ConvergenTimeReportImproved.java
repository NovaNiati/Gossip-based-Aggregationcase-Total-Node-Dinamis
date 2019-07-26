package report;
/*
    Nova Niati
    Universitas Sanata Dharma
*/
/*
    Report ini digunakan untuk menghitung jumlah node per interval waktu
*/
import java.util.*;
import core.DTNHost;
import core.Settings;
import core.SimClock;
import core.UpdateListener;
import routing.DecisionEngineRouterImproved;
import routing.MessageRouter;
import routing.RoutingDecisionEngineImproved;

public class ConvergenTimeReportImproved extends Report implements UpdateListener {

    public static final String NODE_PERWAKTU = "nodepersatuanwaktu";
    public static final int DEFAULT_WAKTU = 1800;
    private double lastRecord = Double.MIN_VALUE;
    private int interval;
    private Map<DTNHost, ArrayList<Double>> countingGossip = new HashMap<DTNHost, ArrayList<Double>>();
    private int updateCounter = 0;

    public ConvergenTimeReportImproved() {
        super();

        Settings settings = getSettings();
        if (settings.contains(NODE_PERWAKTU)) {
            interval = settings.getInt(NODE_PERWAKTU);
        } else {
            interval = -1;
        }
        if (interval < 0) {
            interval = DEFAULT_WAKTU;
        }
    }

    public void updated(List<DTNHost> hosts) {
        double simTime = getSimTime();
        if (isWarmup()) {
            return;
        }

        if (simTime - lastRecord >= interval) {
            printLine(hosts);
            this.lastRecord = simTime - simTime % interval;
        }
    }

    private void printLine(List<DTNHost> hosts) {
        for (DTNHost h : hosts) {

            MessageRouter r = h.getRouter();
            if (!(r instanceof DecisionEngineRouterImproved)) 
                continue;
            RoutingDecisionEngineImproved de = ((DecisionEngineRouterImproved) r).getDecisionEngine();
            if (!(de instanceof NilaiInisiator))
                continue;
            NilaiInisiator n = (NilaiInisiator) de;

            ArrayList<Double> NodeList = new ArrayList<Double>();

            double temp = n.getNilai_val();
            if (countingGossip.containsKey(h)) {
                NodeList = countingGossip.get(h);
                NodeList.add(temp);
                countingGossip.put(h, NodeList);
            } else {
                countingGossip.put(h, NodeList);
            }
        }
    }

    @Override
    public void done() {
        for (Map.Entry<DTNHost, ArrayList<Double>> entry : countingGossip.entrySet()) {
            String printHost = entry.getKey().getAddress() + "\t";
            for (Double NodeList : entry.getValue()) {
                printHost = printHost + "\t" + NodeList;
            }
            write(printHost);
        }
        super.done();
    }
}
