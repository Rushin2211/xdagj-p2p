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

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/** Unit tests for DnsType enum. Tests DNS service provider enumeration functionality. */
class DnsTypeTest {

  @Test
  void testAliYunValues() {
    DnsType aliYun = DnsType.AliYun;
    assertEquals(0, aliYun.getValue());
    assertEquals("aliyun dns server", aliYun.getDesc());
  }

  @Test
  void testAwsRoute53Values() {
    DnsType awsRoute53 = DnsType.AwsRoute53;
    assertEquals(1, awsRoute53.getValue());
    assertEquals("aws route53 server", awsRoute53.getDesc());
  }

  @Test
  void testEnumValues() {
    DnsType[] values = DnsType.values();
    assertEquals(2, values.length);
    assertEquals(DnsType.AliYun, values[0]);
    assertEquals(DnsType.AwsRoute53, values[1]);
  }

  @Test
  void testValueOf() {
    assertEquals(DnsType.AliYun, DnsType.valueOf("AliYun"));
    assertEquals(DnsType.AwsRoute53, DnsType.valueOf("AwsRoute53"));
  }

  @Test
  void testValueOfInvalidName() {
    assertThrows(IllegalArgumentException.class, () -> DnsType.valueOf("InvalidType"));
  }

  @Test
  void testEnumOrder() {
    DnsType[] values = DnsType.values();
    assertTrue(values[0].getValue() < values[1].getValue());
  }
} 