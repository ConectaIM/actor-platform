package im.actor.core.entity;

import com.google.j2objc.annotations.Property;

import org.jetbrains.annotations.NotNull;

public class AuthStartRes {

    @NotNull
    @Property("readonly, nonatomic")
    private final String transactionHash;
    @NotNull
    @Property("readonly, nonatomic")
    private final int authMode;
    @Property("readonly, nonatomic")
    private final boolean isRegistered;

    public AuthStartRes(@NotNull String transactionHash, @NotNull int authMode, boolean isRegistered) {
        this.transactionHash = transactionHash;
        this.authMode = authMode;
        this.isRegistered = isRegistered;
    }

    @NotNull
    public String getTransactionHash() {
        return transactionHash;
    }

    @NotNull
    public int getAuthMode() {
        return authMode;
    }

    public boolean isRegistered() {
        return isRegistered;
    }
}
