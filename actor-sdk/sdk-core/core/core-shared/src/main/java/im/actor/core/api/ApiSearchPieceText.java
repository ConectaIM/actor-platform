package im.actor.core.api;
/*
 *  Generated by the Actor API Scheme generator.  DO NOT EDIT!
 */

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import im.actor.runtime.bser.BserValues;
import im.actor.runtime.bser.BserWriter;

public class ApiSearchPieceText extends ApiSearchCondition {

    private String query;

    public ApiSearchPieceText(@NotNull String query) {
        this.query = query;
    }

    public ApiSearchPieceText() {

    }

    public int getHeader() {
        return 2;
    }

    @NotNull
    public String getQuery() {
        return this.query;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.query = values.getString(1);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        if (this.query == null) {
            throw new IOException();
        }
        writer.writeString(1, this.query);
    }

    @Override
    public String toString() {
        String res = "struct SearchPieceText{";
        res += "query=" + this.query;
        res += "}";
        return res;
    }

}
