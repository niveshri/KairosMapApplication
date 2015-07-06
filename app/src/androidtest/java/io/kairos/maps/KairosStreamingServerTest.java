package io.kairos.maps;

import junit.framework.TestCase;

import io.kairos.maps.providers.KairosStreamingServer;

public class KairosStreamingServerTest extends TestCase {
    public void testSimpleHttpServing() throws Exception {
        KairosStreamingServer.instance();
    }
}