package im.actor.core.api.rpc;
/*
 *  Generated by the Actor API Scheme generator.  DO NOT EDIT!
 */

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

import im.actor.core.api.ApiPhoneActivationType;
import im.actor.core.network.parser.Response;
import im.actor.runtime.bser.Bser;
import im.actor.runtime.bser.BserValues;
import im.actor.runtime.bser.BserWriter;

public class ResponseStartPhoneAuth extends Response {

    public static final int HEADER = 0xc1;
    public static ResponseStartPhoneAuth fromBytes(byte[] data) throws IOException {
        return Bser.parse(new ResponseStartPhoneAuth(), data);
    }

    private String transactionHash;
    private boolean isRegistered;
    private ApiPhoneActivationType activationType;

    public ResponseStartPhoneAuth(@NotNull String transactionHash, boolean isRegistered, @Nullable ApiPhoneActivationType activationType) {
        this.transactionHash = transactionHash;
        this.isRegistered = isRegistered;
        this.activationType = activationType;
    }

    public ResponseStartPhoneAuth() {

    }

    @NotNull
    public String getTransactionHash() {
        return this.transactionHash;
    }

    public boolean isRegistered() {
        return this.isRegistered;
    }

    @Nullable
    public ApiPhoneActivationType getActivationType() {
        return this.activationType;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.transactionHash = values.getString(1);
        this.isRegistered = values.getBool(2);
        int val_activationType = values.getInt(3, 0);
        if (val_activationType != 0) {
            this.activationType = ApiPhoneActivationType.parse(val_activationType);
        }
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.transactionHash == null) {
            throw new IOException();
        }
        writer.writeString(1, this.transactionHash);
        writer.writeBool(2, this.isRegistered);
        if (this.activationType != null) {
            writer.writeInt(3, this.activationType.getValue());
        }
    }

    @Override
    public String toString() {
        String res = "tuple StartPhoneAuth{";
        res += "}";
        return res;
    }

    @Override
    public int getHeaderKey() {
        return HEADER;
    }
}
