/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package routing.GossipCounting;

import core.Connection;
import core.DTNHost;
import core.Message;
import core.Settings;
import report.Report;
import routing.RoutingDecisionEngine;

/**
 *
 * @author User
 */
public class SprayAndWaitKu extends Report implements RoutingDecisionEngine {
    public static final String NROF_COPIES = "nrofCopies";
    public static final String BINARY_MODE = "binaryMode";
    public static final String SPRAYANDWAIT_NS = "SprayAndWaitKu";
    public static final String MSG_COUNT_PROPERTY = SPRAYANDWAIT_NS + "."
            + "copies";

    protected int initialNrofCopies; //ini jumlah L nya
    protected boolean isBinary; //mode penyebarannya 

    public SprayAndWaitKu(Settings s) {
        initialNrofCopies = s.getInt(NROF_COPIES);
        isBinary = s.getBoolean(BINARY_MODE);
    }

    protected SprayAndWaitKu(SprayAndWaitKu r) {
        this.initialNrofCopies = r.initialNrofCopies;
        this.isBinary = r.isBinary;
    }

    @Override
    public void connectionUp(DTNHost thisHost, DTNHost peer) {
    }

    @Override
    public void connectionDown(DTNHost thisHost, DTNHost peer) {
    }

    @Override
    public void doExchangeForNewConnection(Connection con, DTNHost peer) {
    }

    @Override
    public boolean newMessage(Message m) {
        m.addProperty(MSG_COUNT_PROPERTY, initialNrofCopies);
        return true;
    }

    @Override
    public boolean isFinalDest(Message m, DTNHost aHost) {
        return m.getTo()==aHost;
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
        //kalau sudah ketemu sama destination
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
    public boolean shouldDeleteOldMessage(Message m, DTNHost hostReportingOld
    ) {
        return m.getTo() == hostReportingOld;
    }

    @Override
    public RoutingDecisionEngine replicate() {
        return new SprayAndWaitKu(this);
    }
}