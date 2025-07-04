/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2030 The XdagJ Developers
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.xdag.p2p.config;

import static org.junit.jupiter.api.Assertions.*;

import io.xdag.p2p.P2pEventHandler;
import io.xdag.p2p.P2pException;
import io.xdag.p2p.discover.dns.update.PublishConfig;
import io.xdag.p2p.proto.Discover;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.tuweni.bytes.Bytes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for P2pConfig configuration class. Tests default values, parameter setting/getting,
 * validation, and edge cases.
 *
 * @author XDAG Team
 * @since 0.1.0
 */
@Slf4j(topic = "test")
public class P2pConfigTest {

  private P2pConfig p2pConfig;

  @BeforeEach
  void setUp() {
    p2pConfig = new P2pConfig();
  }

  /** Test default configuration values. */
  @Test
  void testDefaultValues() {
    // Test default network settings
    assertEquals(16783, p2pConfig.getPort(), "Default port should be 16783");
    assertEquals(1, p2pConfig.getNetworkId(), "Default network ID should be 1");

    // Test default connection settings
    assertEquals(8, p2pConfig.getMinConnections(), "Default min connections should be 8");
    assertEquals(50, p2pConfig.getMaxConnections(), "Default max connections should be 50");
    assertEquals(
        2, p2pConfig.getMinActiveConnections(), "Default min active connections should be 2");
    assertEquals(
        2,
        p2pConfig.getMaxConnectionsWithSameIp(),
        "Default max connections with same IP should be 2");

    // Test default feature flags
    assertTrue(p2pConfig.isDiscoverEnable(), "Discovery should be enabled by default");
    assertFalse(
        p2pConfig.isDisconnectionPolicyEnable(),
        "Disconnection policy should be disabled by default");
    assertFalse(p2pConfig.isNodeDetectEnable(), "Node detection should be disabled by default");

    // Test default collections
    assertNotNull(p2pConfig.getSeedNodes(), "Seed nodes list should not be null");
    assertNotNull(p2pConfig.getActiveNodes(), "Active nodes list should not be null");
    assertNotNull(p2pConfig.getTrustNodes(), "Trust nodes list should not be null");
    assertNotNull(p2pConfig.getTreeUrls(), "Tree URLs list should not be null");
    assertNotNull(p2pConfig.getHandlerList(), "Handler list should not be null");
    assertNotNull(p2pConfig.getHandlerMap(), "Handler map should not be null");

    assertTrue(p2pConfig.getSeedNodes().isEmpty(), "Seed nodes should be empty initially");
    assertTrue(p2pConfig.getActiveNodes().isEmpty(), "Active nodes should be empty initially");
    assertTrue(p2pConfig.getTrustNodes().isEmpty(), "Trust nodes should be empty initially");
    assertTrue(p2pConfig.getTreeUrls().isEmpty(), "Tree URLs should be empty initially");
    assertTrue(p2pConfig.getHandlerList().isEmpty(), "Handler list should be empty initially");
    assertTrue(p2pConfig.getHandlerMap().isEmpty(), "Handler map should be empty initially");

    // Test default objects
    assertNotNull(p2pConfig.getNodeID(), "Node ID should not be null");
    assertNotNull(p2pConfig.getPublishConfig(), "Publish config should not be null");

    // Test IP addresses (may be null depending on network environment)
    // These are environment-dependent, so we just check they don't throw exceptions
    assertDoesNotThrow(() -> p2pConfig.getIp(), "Getting IP should not throw exception");
    assertDoesNotThrow(() -> p2pConfig.getLanIp(), "Getting LAN IP should not throw exception");
    assertDoesNotThrow(() -> p2pConfig.getIpv6(), "Getting IPv6 should not throw exception");
  }

  /** Test basic parameter setting and getting. */
  @Test
  void testParameterSettersAndGetters() {
    // Test port setting
    p2pConfig.setPort(17000);
    assertEquals(17000, p2pConfig.getPort(), "Port should be updated");

    // Test network ID setting
    p2pConfig.setNetworkId(2);
    assertEquals(2, p2pConfig.getNetworkId(), "Network ID should be updated");

    // Test connection settings
    p2pConfig.setMinConnections(10);
    p2pConfig.setMaxConnections(100);
    p2pConfig.setMinActiveConnections(5);
    p2pConfig.setMaxConnectionsWithSameIp(3);

    assertEquals(10, p2pConfig.getMinConnections(), "Min connections should be updated");
    assertEquals(100, p2pConfig.getMaxConnections(), "Max connections should be updated");
    assertEquals(
        5, p2pConfig.getMinActiveConnections(), "Min active connections should be updated");
    assertEquals(
        3,
        p2pConfig.getMaxConnectionsWithSameIp(),
        "Max connections with same IP should be updated");

    // Test feature flags
    p2pConfig.setDiscoverEnable(false);
    p2pConfig.setDisconnectionPolicyEnable(true);
    p2pConfig.setNodeDetectEnable(true);

    assertFalse(p2pConfig.isDiscoverEnable(), "Discovery should be disabled");
    assertTrue(p2pConfig.isDisconnectionPolicyEnable(), "Disconnection policy should be enabled");
    assertTrue(p2pConfig.isNodeDetectEnable(), "Node detection should be enabled");

    // Test IP settings
    p2pConfig.setIp("192.168.1.100");
    p2pConfig.setLanIp("192.168.1.101");
    p2pConfig.setIpv6("2001:db8::1");

    assertEquals("192.168.1.100", p2pConfig.getIp(), "IP should be updated");
    assertEquals("192.168.1.101", p2pConfig.getLanIp(), "LAN IP should be updated");
    assertEquals("2001:db8::1", p2pConfig.getIpv6(), "IPv6 should be updated");

    // Test node ID setting
    Bytes newNodeId = Bytes.fromHexString("0x1234567890abcdef");
    p2pConfig.setNodeID(newNodeId);
    assertEquals(newNodeId, p2pConfig.getNodeID(), "Node ID should be updated");
  }

  /** Test seed nodes management. */
  @Test
  void testSeedNodesManagement() {
    List<InetSocketAddress> seedNodes = new ArrayList<>();
    seedNodes.add(new InetSocketAddress("127.0.0.1", 16783));
    seedNodes.add(new InetSocketAddress("192.168.1.100", 16784));

    p2pConfig.setSeedNodes(seedNodes);

    assertEquals(2, p2pConfig.getSeedNodes().size(), "Should have 2 seed nodes");
    assertTrue(
        p2pConfig.getSeedNodes().contains(new InetSocketAddress("127.0.0.1", 16783)),
        "Should contain first seed node");
    assertTrue(
        p2pConfig.getSeedNodes().contains(new InetSocketAddress("192.168.1.100", 16784)),
        "Should contain second seed node");

    // Test adding individual seed node
    p2pConfig.getSeedNodes().add(new InetSocketAddress("10.0.0.1", 16785));
    assertEquals(3, p2pConfig.getSeedNodes().size(), "Should have 3 seed nodes after adding");
  }

  /** Test active nodes management. */
  @Test
  void testActiveNodesManagement() {
    List<InetSocketAddress> activeNodes = new ArrayList<>();
    activeNodes.add(new InetSocketAddress("127.0.0.1", 16783));
    activeNodes.add(new InetSocketAddress("192.168.1.200", 16784));

    p2pConfig.setActiveNodes(activeNodes);

    assertEquals(2, p2pConfig.getActiveNodes().size(), "Should have 2 active nodes");
    assertTrue(
        p2pConfig.getActiveNodes().contains(new InetSocketAddress("127.0.0.1", 16783)),
        "Should contain first active node");
    assertTrue(
        p2pConfig.getActiveNodes().contains(new InetSocketAddress("192.168.1.200", 16784)),
        "Should contain second active node");
  }

  /** Test trust nodes management. */
  @Test
  void testTrustNodesManagement() throws UnknownHostException {
    List<InetAddress> trustNodes = new ArrayList<>();
    trustNodes.add(InetAddress.getByName("127.0.0.1"));
    trustNodes.add(InetAddress.getByName("192.168.1.50"));

    p2pConfig.setTrustNodes(trustNodes);

    assertEquals(2, p2pConfig.getTrustNodes().size(), "Should have 2 trust nodes");
    assertTrue(
        p2pConfig.getTrustNodes().contains(InetAddress.getByName("127.0.0.1")),
        "Should contain first trust node");
    assertTrue(
        p2pConfig.getTrustNodes().contains(InetAddress.getByName("192.168.1.50")),
        "Should contain second trust node");
  }

  /** Test DNS tree URLs management. */
  @Test
  void testTreeUrlsManagement() {
    List<String> treeUrls = new ArrayList<>();
    treeUrls.add("tree://APFGGTFOBVE2ZNAB3CSMNNX6RRK3ODIRLP2AA5U4YFAA6MSYZUYTQ@mainnet.xdag.io");
    treeUrls.add("tree://BQHGGTFOBVE2ZNAB3CSMNNX6RRK3ODIRLP2AA5U4YFAA6MSYZUYTQ@testnet.xdag.io");

    p2pConfig.setTreeUrls(treeUrls);

    assertEquals(2, p2pConfig.getTreeUrls().size(), "Should have 2 tree URLs");
    assertTrue(
        p2pConfig
            .getTreeUrls()
            .contains(
                "tree://APFGGTFOBVE2ZNAB3CSMNNX6RRK3ODIRLP2AA5U4YFAA6MSYZUYTQ@mainnet.xdag.io"),
        "Should contain mainnet tree URL");
    assertTrue(
        p2pConfig
            .getTreeUrls()
            .contains(
                "tree://BQHGGTFOBVE2ZNAB3CSMNNX6RRK3ODIRLP2AA5U4YFAA6MSYZUYTQ@testnet.xdag.io"),
        "Should contain testnet tree URL");
  }

  /** Test publish config management. */
  @Test
  void testPublishConfigManagement() {
    PublishConfig publishConfig = new PublishConfig();
    p2pConfig.setPublishConfig(publishConfig);

    assertSame(
        publishConfig, p2pConfig.getPublishConfig(), "Publish config should be the same instance");
  }

  /** Test event handler registration. */
  @Test
  void testEventHandlerRegistration() throws P2pException {
    TestEventHandler handler1 = new TestEventHandler("handler1", (byte) 0x01);
    TestEventHandler handler2 = new TestEventHandler("handler2", (byte) 0x02);

    // Register handlers
    p2pConfig.addP2pEventHandle(handler1);
    p2pConfig.addP2pEventHandle(handler2);

    // Verify handlers are registered
    assertEquals(2, p2pConfig.getHandlerList().size(), "Should have 2 handlers in list");
    assertEquals(2, p2pConfig.getHandlerMap().size(), "Should have 2 handlers in map");

    assertTrue(
        p2pConfig.getHandlerList().contains(handler1), "Handler list should contain handler1");
    assertTrue(
        p2pConfig.getHandlerList().contains(handler2), "Handler list should contain handler2");

    assertEquals(
        handler1,
        p2pConfig.getHandlerMap().get((byte) 0x01),
        "Handler map should map type 0x01 to handler1");
    assertEquals(
        handler2,
        p2pConfig.getHandlerMap().get((byte) 0x02),
        "Handler map should map type 0x02 to handler2");
  }

  /** Test event handler registration with duplicate message type. */
  @Test
  void testEventHandlerRegistrationDuplicate() throws P2pException {
    TestEventHandler handler1 = new TestEventHandler("handler1", (byte) 0x01);
    TestEventHandler handler2 = new TestEventHandler("handler2", (byte) 0x01); // Same type

    // Register first handler
    p2pConfig.addP2pEventHandle(handler1);

    // Register second handler with same type should fail
    P2pException exception =
        assertThrows(
            P2pException.class,
            () -> p2pConfig.addP2pEventHandle(handler2),
            "Should throw P2pException for duplicate message type");

    assertTrue(
        exception.getMessage().contains("type:1"),
        "Exception message should mention the duplicate type");

    // Verify only first handler is registered
    assertEquals(1, p2pConfig.getHandlerList().size(), "Should have only 1 handler in list");
    assertEquals(1, p2pConfig.getHandlerMap().size(), "Should have only 1 handler in map");
    assertEquals(
        handler1,
        p2pConfig.getHandlerMap().get((byte) 0x01),
        "Should have only handler1 registered");
  }

  /** Test event handler registration with null message types. */
  @Test
  void testEventHandlerRegistrationNullTypes() {
    TestEventHandler handlerWithNullTypes = new TestEventHandler("nullHandler", null);

    // Should not throw exception
    assertDoesNotThrow(
        () -> p2pConfig.addP2pEventHandle(handlerWithNullTypes),
        "Should handle null message types gracefully");

    assertEquals(1, p2pConfig.getHandlerList().size(), "Should have 1 handler in list");
    assertEquals(0, p2pConfig.getHandlerMap().size(), "Should have 0 handlers in map (no types)");
  }

  /** Test home node endpoint generation. */
  @Test
  void testHomeNodeGeneration() {
    // Set specific values for testing
    p2pConfig.setPort(17000);
    p2pConfig.setIp("192.168.1.100");
    p2pConfig.setIpv6("2001:db8::1");
    Bytes nodeId = Bytes.fromHexString("0x1234567890abcdef1234567890abcdef12345678");
    p2pConfig.setNodeID(nodeId);

    Discover.Endpoint homeNode = p2pConfig.getHomeNode();

    assertNotNull(homeNode, "Home node should not be null");
    assertEquals(17000, homeNode.getPort(), "Home node port should match config");
    assertArrayEquals(
        nodeId.toArray(), homeNode.getNodeId().toByteArray(), "Home node ID should match config");

    // Verify IP addresses are set (if not empty)
    if (!p2pConfig.getIp().isEmpty()) {
      assertFalse(homeNode.getAddress().isEmpty(), "Home node address should not be empty");
    }
    if (p2pConfig.getIpv6() != null && !p2pConfig.getIpv6().isEmpty()) {
      assertFalse(
          homeNode.getAddressIpv6().isEmpty(), "Home node IPv6 address should not be empty");
    }
  }

  /** Test home node generation with empty IP addresses. */
  @Test
  void testHomeNodeGenerationEmptyIps() {
    p2pConfig.setIp("");
    p2pConfig.setIpv6("");

    Discover.Endpoint homeNode = p2pConfig.getHomeNode();

    assertNotNull(homeNode, "Home node should not be null even with empty IPs");
    assertTrue(homeNode.getAddress().isEmpty(), "Address should be empty");
    assertTrue(homeNode.getAddressIpv6().isEmpty(), "IPv6 address should be empty");
  }

  /** Simple test event handler for testing registration. */
  private static class TestEventHandler extends P2pEventHandler {
    private final String name;

    public TestEventHandler(String name, Byte messageType) {
      this.name = name;
      if (messageType != null) {
        this.messageTypes = new HashSet<>();
        this.messageTypes.add(messageType);
      } else {
        this.messageTypes = null;
      }
    }

    @Override
    public String toString() {
      return "TestEventHandler{name='" + name + "'}";
    }
  }
}
