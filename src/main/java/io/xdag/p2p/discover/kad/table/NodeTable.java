package io.xdag.p2p.discover.kad.table;

import io.xdag.p2p.discover.Node;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import org.apache.tuweni.bytes.Bytes;

@Getter
public class NodeTable {

  private final Node node; // our node
  private transient NodeBucket[] buckets;
  private transient Map<String, NodeEntry> nodes;

  public NodeTable(Node n) {
    this.node = n;
    initialize();
  }

  public final void initialize() {
    nodes = new HashMap<>();
    buckets = new NodeBucket[KademliaOptions.BINS];
    for (int i = 0; i < KademliaOptions.BINS; i++) {
      buckets[i] = new NodeBucket(i);
    }
  }

  public synchronized Node addNode(Node n) {
    if (n.getHostKey().equals(node.getHostKey())) {
      return null;
    }

    NodeEntry entry = nodes.get(n.getHostKey());
    if (entry != null) {
      entry.touch();
      return null;
    }

    NodeEntry e = new NodeEntry(node.getId(), n);
    NodeEntry lastSeen = buckets[getBucketId(e)].addNode(e);
    if (lastSeen != null) {
      return lastSeen.getNode();
    }
    nodes.put(n.getHostKey(), e);
    return null;
  }

  public synchronized void dropNode(Node n) {
    NodeEntry entry = nodes.get(n.getHostKey());
    if (entry != null) {
      nodes.remove(n.getHostKey());
      buckets[getBucketId(entry)].dropNode(entry);
    }
  }

  public synchronized boolean contains(Node n) {
    return nodes.containsKey(n.getHostKey());
  }

  public synchronized void touchNode(Node n) {
    NodeEntry entry = nodes.get(n.getHostKey());
    if (entry != null) {
      entry.touch();
    }
  }

  public int getBucketsCount() {
    int i = 0;
    for (NodeBucket b : buckets) {
      if (b.getNodesCount() > 0) {
        i++;
      }
    }
    return i;
  }

  public int getBucketId(NodeEntry e) {
    int id = e.getDistance() - 1;
    return Math.max(id, 0);
  }

  public synchronized int getNodesCount() {
    return nodes.size();
  }

  public synchronized List<NodeEntry> getAllNodes() {
    return new ArrayList<>(nodes.values());
  }

  public synchronized List<Node> getClosestNodes(Bytes targetId) {
    List<NodeEntry> closestEntries = getAllNodes();
    List<Node> closestNodes = new ArrayList<>();
    for (NodeEntry e : closestEntries) {
      closestNodes.add((Node) e.getNode().clone());
    }
    closestNodes.sort(new DistanceComparator(targetId));
    if (closestNodes.size() > KademliaOptions.BUCKET_SIZE) {
      closestNodes = closestNodes.subList(0, KademliaOptions.BUCKET_SIZE);
    }
    return closestNodes;
  }

  public synchronized List<Node> getTableNodes() {
    List<Node> nodeList = new ArrayList<>();
    for (NodeEntry nodeEntry : nodes.values()) {
      nodeList.add(nodeEntry.getNode());
    }
    return nodeList;
  }
}
