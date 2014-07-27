/*
 * $ProjectName$
 * $ProjectRevision$
 * -----------------------------------------------------------
 * $Id: FileStream.java,v 1.1 2003/04/10 19:48:22 jarnbjo Exp $
 * -----------------------------------------------------------
 *
 * $Author: jarnbjo $
 *
 * Description:
 *
 * Copyright 2002-2003 Tor-Einar Jarnbjo
 * -----------------------------------------------------------
 *
 * Change History
 * -----------------------------------------------------------
 * $Log: FileStream.java,v $
 * Revision 1.1  2003/04/10 19:48:22  jarnbjo
 * no message
 *
 *
 */

package de.jarnbjo.ogg;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Implementation of the <code>PhysicalOggStream</code> interface for accessing
 * normal disk files.
 */

public class FileStream implements PhysicalOggStream {

    private boolean closed = false;
    private RandomAccessFile source;
    private long[] pageOffsets;

    private HashMap<Integer, LogicalOggStreamImpl> logicalStreams = new HashMap<>();

    /**
     * Creates access to the specified file through the
     * <code>PhysicalOggStream</code> interface. The specified source file must
     * have been opened for reading.
     *
     * @param source
     *            the file to read from
     *
     * @throws OggFormatException
     *             if the stream format is incorrect
     * @throws IOException
     *             if some other IO error occurs when reading the file
     */

    public FileStream(RandomAccessFile source) throws OggFormatException, IOException {
        this.source = source;

        ArrayList<Long> po = new ArrayList<>();
        int pageNumber = 0;
        try {
            while (true) {
                po.add(this.source.getFilePointer());

                // skip data if pageNumber>0
                OggPage op = getNextPage(pageNumber > 0);
                if (op == null) {
                    break;
                }

                LogicalOggStreamImpl los = (LogicalOggStreamImpl) getLogicalStream(op
                        .getStreamSerialNumber());
                if (los == null) {
                    los = new LogicalOggStreamImpl(this);
                    logicalStreams.put(op.getStreamSerialNumber(), los);
                }

                if (pageNumber == 0) {
                    los.checkFormat(op);
                }

                los.addPageNumberMapping(pageNumber);
                los.addGranulePosition(op.getAbsoluteGranulePosition());

                if (pageNumber > 0) {
                    this.source.seek(this.source.getFilePointer() + op.getTotalLength());
                }

                pageNumber++;
            }
        } catch (EndOfOggStreamException e) {
            // ok
        } catch (IOException e) {
            throw e;
        }
        // System.out.println("pageNumber: "+pageNumber);
        this.source.seek(0L);
        pageOffsets = new long[po.size()];
        int i = 0;
        for (Long next : po) {
            pageOffsets[i++] = next;
        }
    }

    public void close() throws IOException {
        closed = true;
        source.close();
    }

    private LogicalOggStream getLogicalStream(int serialNumber) {
        return logicalStreams.get(new Integer(serialNumber));
    }

    public Collection<LogicalOggStreamImpl> getLogicalStreams() {
        return logicalStreams.values();
    }

    private OggPage getNextPage(boolean skipData) throws EndOfOggStreamException, IOException,
            OggFormatException {
        return OggPage.create(source, skipData);
    }

    public OggPage getOggPage(int index) throws IOException {
        source.seek(pageOffsets[index]);
        return OggPage.create(source);
    }

    public boolean isOpen() {
        return !closed;
    }

    /**
     * @return always <code>true</code>
     */

    public boolean isSeekable() {
        return true;
    }

    public void setTime(long granulePosition) throws IOException {
        for (LogicalOggStreamImpl los : logicalStreams.values()) {
            los.setTime(granulePosition);
        }
    }
}