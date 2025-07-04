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
package io.xdag.p2p.discover.dns.sync;

import io.xdag.p2p.DnsException;
import io.xdag.p2p.discover.dns.DnsNode;
import io.xdag.p2p.discover.dns.tree.LinkEntry;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Getter
@Slf4j(topic = "net")
public class RandomIterator implements Iterator<DnsNode> {

  private final Client client;
  private Map<String, ClientTree> clientTrees;
  private DnsNode cur;
  private final LinkCache linkCache;
  private final Random random;

  public RandomIterator(Client client) {
    this.client = client;
    clientTrees = new ConcurrentHashMap<>();
    linkCache = new LinkCache();
    random = new Random();
  }

  // syncs random tree entries until it finds a node.
  @Override
  public DnsNode next() {
    int i = 0;
    while (i < Client.randomRetryTimes) {
      i += 1;
      ClientTree clientTree = pickTree();
      if (clientTree == null) {
        log.error("clientTree is null");
        return null;
      }
      log.info(
          "Choose clientTree:{} from {} ClientTree",
          clientTree.getLinkEntry().represent(),
          clientTrees.size());
      DnsNode dnsNode;
      try {
        dnsNode = clientTree.syncRandom();
      } catch (Exception e) {
        log.warn(
            "Error in DNS random node sync, tree:{}, cause:[{}]",
            clientTree.getLinkEntry().domain(),
            e.getMessage());
        continue;
      }
      if (dnsNode != null && dnsNode.getPreferInetSocketAddress() != null) {
        return dnsNode;
      }
    }
    return null;
  }

  @Override
  public boolean hasNext() {
    this.cur = next();
    return this.cur != null;
  }

  public void addTree(String url) throws DnsException {
    LinkEntry linkEntry = LinkEntry.parseEntry(url);
    linkCache.addLink("", linkEntry.represent());
  }

  // the first random
  private ClientTree pickTree() {
    if (clientTrees == null) {
      log.info("clientTrees is null");
      return null;
    }
    if (linkCache.isChanged()) {
      rebuildTrees();
      linkCache.setChanged(false);
    }

    int size = clientTrees.size();
    List<ClientTree> allTrees = new ArrayList<>(clientTrees.values());

    return allTrees.get(random.nextInt(size));
  }

  // rebuilds the 'trees' map.
  // if urlScheme is not contain in any other link, wo delete it from clientTrees
  // then create one ClientTree using this urlScheme， add it to clientTrees
  private void rebuildTrees() {
    log.info("rebuildTrees...");
    Iterator<Entry<String, ClientTree>> it = clientTrees.entrySet().iterator();
    while (it.hasNext()) {
      Entry<String, ClientTree> entry = it.next();
      String urlScheme = entry.getKey();
      if (!linkCache.isContainInOtherLink(urlScheme)) {
        log.info("remove tree from trees:{}", urlScheme);
        it.remove();
      }
    }

    for (Entry<String, Set<String>> entry : linkCache.backrefs.entrySet()) {
      String urlScheme = entry.getKey();
      if (!clientTrees.containsKey(urlScheme)) {
        try {
          LinkEntry linkEntry = LinkEntry.parseEntry(urlScheme);
          clientTrees.put(urlScheme, new ClientTree(client, linkCache, linkEntry));
          log.info("add tree to clientTrees:{}", urlScheme);
        } catch (DnsException e) {
          log.error("Parse LinkEntry failed", e);
        }
      }
    }
    log.info("Exist clientTrees: {}", StringUtils.join(clientTrees.keySet(), ","));
  }

  public void close() {
    clientTrees = null;
  }
}
