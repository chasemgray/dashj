package org.bitcoinj.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * Created by Hash Engineering on 2/10/2015.
 */
public class PublicKey extends ChildMessage {
    private static final Logger log = LoggerFactory.getLogger(PublicKey.class);

    byte [] bytes;

    PublicKey(NetworkParameters params)
    {
        super(params);
    }

    public PublicKey(NetworkParameters params, byte[] payload, int offset) throws ProtocolException {
        super(params, payload, offset);
    }

    public PublicKey(NetworkParameters params, byte[] payloadBytes, int cursor, Message parent, boolean parseLazy, boolean parseRetain)
    {
        super(params, payloadBytes, cursor, parent, parseLazy, parseRetain, payloadBytes.length);
    }

    @Override
    protected void parseLite() throws ProtocolException {
        if (parseLazy && length == UNKNOWN_LENGTH) {
            length = calcLength(payload, offset);
            cursor = offset + length;
        }
    }
    protected static int calcLength(byte[] buf, int offset) {
        VarInt varint;

        int cursor = offset;// + 4;
        varint = new VarInt(buf, cursor);
        long len = varint.value;
        len += varint.getOriginalSizeInBytes();
        cursor += len;

        return cursor - offset;
    }
    @Override
    void parse() throws ProtocolException {
        if(parsed)
            return;

        cursor = offset;

        bytes = readByteArray();

        length = cursor - offset;
    }
    @Override
    protected void bitcoinSerializeToStream(OutputStream stream) throws IOException {

        stream.write(new VarInt(bytes.length).encode());
        stream.write(bytes);
    }

    public String toString()
    {
        return "public key:  " + Utils.HEX.encode(bytes);

    }

    public byte [] getBytes() { maybeParse(); return bytes; }

    public boolean equals(Object o)
    {
        maybeParse();
       PublicKey key = (PublicKey)o;
        if(key.bytes.length == this.bytes.length)
        {
            if(Arrays.equals(key.bytes, this.bytes) == true)
                return true;
        }
        return false;
    }

    PublicKey duplicate()
    {
        PublicKey copy = new PublicKey(params, getBytes(), 0);

        return copy;
    }

}