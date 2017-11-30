package im.actor.core.network.parser;

import java.io.IOException;
import java.util.ArrayList;

import im.actor.runtime.Log;

public class ApiParserConfig {

    private ArrayList<ParsingExtension> extensions = new ArrayList<>();

    public void addExtension(ParsingExtension extension) {
        extensions.add(extension);
    }

    public RpcScope parseRpc(int header, byte[] content) throws IOException {
        for (ParsingExtension ex : extensions) {
            try {
                Log.d("ApiParserConfig", "Vai ler o header: "+header);
                return ex.getRpcScopeParser().read(header, content);
            } catch (Exception e) {
                // Ignore
            }
        }

        throw new IOException("Unknown package for header "+header);
    }
}
