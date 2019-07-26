package routing.GossipCounting;
/*
    Nova Niati
    Universitas Sanata Dharma
*/
/*
    Class ini digunakan untuk menghitung node yang ada di jaringan
*/
import core.*;
import java.util.*;
import report.*;
import routing.DecisionEngineRouterImproved;
import routing.MessageRouter;
import routing.RoutingDecisionEngineImproved;

public class GossipCountRandomImprovedNia extends Report implements RoutingDecisionEngineImproved, NilaiInisiator {

    public static final String BINARY_MODE = "binaryMode";
    public static final String SPRAYANDWAIT_NS = "GossipCountRandomImprovedNia";
    public static final String MSG_COUNT_PROPERTY = SPRAYANDWAIT_NS + "." + "copies";
    public static final String INISIATOR_SETTING = "inisiator";
    public static final String CONVERGEN_INTERVAL = "convergeninterval";
    public static final int DEFAULT_UPDATE = 0;

    protected double waktuconvergen;
    private double interval;
    protected double inisial_val;
    protected int Inisiator;
    private double rata;
    private boolean isBinary;

    public GossipCountRandomImprovedNia(Settings s) {
        if (s.contains(BINARY_MODE)) {
            isBinary = s.getBoolean(BINARY_MODE);
        } else {
            this.isBinary = false;
        }
        if (s.contains(INISIATOR_SETTING)) {
            Inisiator = s.getInt(INISIATOR_SETTING);
        }
        if (s.contains(CONVERGEN_INTERVAL)) {
            waktuconvergen = s.getDouble(CONVERGEN_INTERVAL);
        }
        inisial_val = 0;
    }

    public GossipCountRandomImprovedNia(GossipCountRandomImprovedNia r) {
        this.waktuconvergen = r.waktuconvergen;
        this.Inisiator = r.Inisiator;
        this.isBinary = r.isBinary;
        this.inisial_val = r.inisial_val;
    }

    @Override
    public void connectionUp(DTNHost thisHost, DTNHost peer) {
        if (thisHost.getAddress() == Inisiator && this.getInisial_val() == 0) {
            this.setInisial_val(1);
        }

    }

    @Override
    public void connectionDown(DTNHost thisHost, DTNHost peer) {
    }

    @Override
    public void doExchangeForNewConnection(Connection con, DTNHost peer) {
        DTNHost thisHost = con.getOtherNode(peer);
        GossipCountRandomImprovedNia pr = getOtherSnWDecisionEngine(peer);
        if (thisHost.isRadioActive() == true && peer.isRadioActive() == true) {
                if (this.getInisial_val() + pr.getInisial_val() != 0) {
                    double tempValue = (this.getInisial_val() + pr.getInisial_val()) / 2;
                    this.setInisial_val(tempValue);
                    pr.setInisial_val(tempValue);
                    this.setRata((int) Math.floor(1 / tempValue));
            }
        }
    }

    @Override
    public boolean newMessage(Message m) {
        m.addProperty(MSG_COUNT_PROPERTY, nilai());
        return true;
    }

    @Override
    public boolean isFinalDest(Message m, DTNHost aHost) {
        return m.getTo() == aHost;
    }

    @Override
    public boolean shouldSaveReceivedMessage(Message m, DTNHost thisHost) {
        Integer nrofCopies = (Integer) m.getProperty(MSG_COUNT_PROPERTY);
        if (isBinary) {
            nrofCopies = (int) Math.ceil(nrofCopies / 2.0);
        } else {
            nrofCopies = 1;
        }
        m.updateProperty(MSG_COUNT_PROPERTY, nrofCopies);
        return m.getTo() != thisHost;
    }

    @Override
    public boolean shouldSendMessageToHost(Message m, DTNHost otherHost) {
        if (m.getTo() == otherHost) {
            return true;
        }
        Integer nrofCopies = (Integer) m.getProperty(MSG_COUNT_PROPERTY);
        if (nrofCopies > 1) {
            return true;
        }
        return false;
    }

    @Override
    public boolean shouldDeleteSentMessage(Message m, DTNHost otherHost) {
        if (m.getTo() == otherHost) {
            return false;
        }
        Integer nrofCopies = (Integer) m.getProperty(MSG_COUNT_PROPERTY);
        if (isBinary) {
            nrofCopies /= 2;
        } else {
            nrofCopies--;
        }
        m.updateProperty(MSG_COUNT_PROPERTY, nrofCopies);
        return false;
    }

    @Override
    public boolean shouldDeleteOldMessage(Message m, DTNHost hostReportingOld) {
        return m.getTo() == hostReportingOld;
    }

    @Override
    public RoutingDecisionEngineImproved replicate() {
        return new GossipCountRandomImprovedNia(this);
    }

    private GossipCountRandomImprovedNia getOtherSnWDecisionEngine(DTNHost h) {
        MessageRouter otherRouter = h.getRouter();
        assert otherRouter instanceof DecisionEngineRouterImproved : "This router only works "
                + " with other routers of same type";
        return (GossipCountRandomImprovedNia) ((DecisionEngineRouterImproved) otherRouter).getDecisionEngine();
    }

    private double getInisial_val() {
        return inisial_val;
    }

    private void setInisial_val(double inisial_val) {
        this.inisial_val = inisial_val;
    }

    private double getRata() {
        return rata;
    }

    private void setRata(double rata) {
        this.rata = rata;
    }

    @Override
    public double getNilai_val() {
        return this.getRata();
    }

    private int nilai() {
        return (int) this.getNilai_val() / 2;
    }

    @Override
    public void update(DTNHost thisHost) {
        double currentTime = SimClock.getTime();
        if (currentTime == waktuconvergen) {
            if (thisHost.getAddress() == Inisiator) {
                setInisial_val(1);
                setRata(0);
                System.out.println(thisHost + " counting = " + getInisial_val());
            } else {
                setInisial_val(0);
                setRata(0);
                System.out.println(thisHost + " counting = " + getInisial_val());
            }
        }
    }

    @Override
    public void transferDone(Connection con) {
    }
}
