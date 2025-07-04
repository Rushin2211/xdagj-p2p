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
package io.xdag.p2p.discover.dns.update;

import lombok.Getter;

/**
 * Enumeration of supported DNS service providers. Defines the available DNS services that can be
 * used for DNS tree publishing.
 */
@Getter
public enum DnsType {
  /** Alibaba Cloud DNS service */
  AliYun(0, "aliyun dns server"),

  /** Amazon Web Services Route53 DNS service */
  AwsRoute53(1, "aws route53 server");

  private final Integer value;
  private final String desc;

  /**
   * Constructor for DNS type enum.
   *
   * @param value the numeric value of the DNS type
   * @param desc the description of the DNS service
   */
  DnsType(Integer value, String desc) {
    this.value = value;
    this.desc = desc;
  }
}
