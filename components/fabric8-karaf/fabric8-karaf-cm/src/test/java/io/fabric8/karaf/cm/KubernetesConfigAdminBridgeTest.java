/**
 * Copyright 2005-2016 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version
 * 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package io.fabric8.karaf.cm;

import io.fabric8.kubernetes.api.model.ConfigMapList;
import io.fabric8.kubernetes.api.model.ListMeta;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.mockwebserver.DefaultMockServer;
import org.junit.Test;
import org.mockito.Mock;
import org.osgi.service.cm.ConfigurationAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class KubernetesConfigAdminBridgeTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(KubernetesConfigAdminBridgeTest.class);

    @Mock
    private ConfigurationAdmin caService;

    private ConfigMapList cmEmptyList = new ConfigMapList();

    @Test
    public void testAand(){
        System.setProperty("fabric8.pid.filters", "appName=A,database.name=my.oracle.datasource");

        DefaultMockServer server = new DefaultMockServer();
        server.start();
        String hostname = server.getHostName();
        int port = server.getPort();

        KubernetesConfigAdminBridge kcab = new KubernetesConfigAdminBridge();

        ListMeta lm = new ListMeta();
        lm.setResourceVersion("1");
        cmEmptyList.setMetadata( new ListMeta());

        server.expect().get().withPath("/api/v1/namespaces/test/configmaps?labelSelector=karaf.pid,appName%20in%20(A),database.name%20in%20(my.oracle.datasource)&watch=true").andReturnChunked(200).always();
        server.expect().get().withPath("/api/v1/namespaces/test/configmaps?labelSelector=karaf.pid,appName%20in%20(A),database.name%20in%20(my.oracle.datasource)").andReturn(200, cmEmptyList).always();
        server.expect().get().withPath("/api/v1/namespaces/test/configmaps?labelSelector=karaf.pid,appName%20in%20(A),database.name%20in%20(my.oracle.datasource)&resourceVersion&watch=true")
                .andUpgradeToWebSocket().open()
                .waitFor(2000)
                //this is just to simulate an harmless watch event
                .andEmit("{\"object\": {\"kind\":\"Status\",\"code\": 200}, \"type\":\"HI\" }")
                .done().always();

        Config config = new ConfigBuilder().withMasterUrl(hostname+":"+port).build();
        KubernetesClient client = new DefaultKubernetesClient(config);

        kcab.bindConfigAdmin( caService );
        kcab.bindKubernetesClient( client );

        kcab.activate();
        kcab.deactivate();
        client.close();
        server.shutdown();
    }

    @Test
    public void testOr(){
        System.setProperty("fabric8.pid.filters", "appName=A;B");

        DefaultMockServer server = new DefaultMockServer();
        server.start();
        String hostname = server.getHostName();
        int port = server.getPort();

        KubernetesConfigAdminBridge kcab = new KubernetesConfigAdminBridge();

        ListMeta lm = new ListMeta();
        lm.setResourceVersion("1");
        cmEmptyList.setMetadata( new ListMeta());

        server.expect().get().withPath("/api/v1/namespaces/test/configmaps?labelSelector=karaf.pid,appName%20in%20(A,B)&watch=true").andReturnChunked(200).always();
        server.expect().get().withPath("/api/v1/namespaces/test/configmaps?labelSelector=karaf.pid,appName%20in%20(A,B)").andReturn(200, cmEmptyList).always();
        server.expect().get().withPath("/api/v1/namespaces/test/configmaps?labelSelector=karaf.pid,appName%20in%20(A,B)&resourceVersion&watch=true")
                .andUpgradeToWebSocket().open()
                .waitFor(2000)
                //this is just to simulate an harmless watch event
                .andEmit("{\"object\": {\"kind\":\"Status\",\"code\": 200}, \"type\":\"HI\" }")
                .done().always();

        Config config = new ConfigBuilder().withMasterUrl(hostname+":"+port).build();
        KubernetesClient client = new DefaultKubernetesClient(config);

        kcab.bindConfigAdmin( caService );
        kcab.bindKubernetesClient( client );

        kcab.activate();
        kcab.deactivate();
        client.close();
        server.shutdown();
    }

    @Test
    public void testAndOr(){
        System.setProperty("fabric8.pid.filters", "appName=A;B,database.name=my.oracle.datasource");

        DefaultMockServer server = new DefaultMockServer();
        server.start();
        String hostname = server.getHostName();
        int port = server.getPort();

        KubernetesConfigAdminBridge kcab = new KubernetesConfigAdminBridge();

        ListMeta lm = new ListMeta();
        lm.setResourceVersion("1");
        cmEmptyList.setMetadata( new ListMeta());

        server.expect().get().withPath("/api/v1/namespaces/test/configmaps?labelSelector=karaf.pid,appName%20in%20(A,B),database.name%20in%20(my.oracle.datasource)&watch=true").andReturnChunked(200).always();
        server.expect().get().withPath("/api/v1/namespaces/test/configmaps?labelSelector=karaf.pid,appName%20in%20(A,B),database.name%20in%20(my.oracle.datasource)").andReturn(200, cmEmptyList).always();
        server.expect().get().withPath("/api/v1/namespaces/test/configmaps?labelSelector=karaf.pid,appName%20in%20(A,B),database.name%20in%20(my.oracle.datasource)&resourceVersion&watch=true")
                .andUpgradeToWebSocket().open()
                .waitFor(2000)
                //this is just to simulate an harmless watch event
                .andEmit("{\"object\": {\"kind\":\"Status\",\"code\": 200}, \"type\":\"HI\" }")
                .done().always();

        Config config = new ConfigBuilder().withMasterUrl(hostname+":"+port).build();
        KubernetesClient client = new DefaultKubernetesClient(config);

        kcab.bindConfigAdmin( caService );
        kcab.bindKubernetesClient( client );

        kcab.activate();
        kcab.deactivate();
        client.close();
        server.shutdown();
    }
}

