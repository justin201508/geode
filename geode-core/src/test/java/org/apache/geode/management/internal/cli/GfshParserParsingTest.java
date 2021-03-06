/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.management.internal.cli;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import org.apache.geode.management.internal.cli.i18n.CliStrings;
import org.apache.geode.test.junit.categories.IntegrationTest;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.shell.event.ParseResult;

import java.util.Map;

@Category(IntegrationTest.class)
public class GfshParserParsingTest {
  private static GfshParser parser;
  private String buffer;

  @BeforeClass
  public static void setUpClass() throws Exception {
    parser = new GfshParser();
  }

  private Map<String, String> parseParams(String input, String commandMethod) {
    ParseResult parseResult = parser.parse(input);

    GfshParseResult gfshParseResult = (GfshParseResult) parseResult;
    Map<String, String> params = gfshParseResult.getParamValueStrings();
    for (String param : params.keySet()) {
      System.out.println(param + "=" + params.get(param));
    }

    assertThat(gfshParseResult.getMethod().getName()).isEqualTo(commandMethod);
    assertThat(gfshParseResult.getUserInput()).isEqualTo(input.trim());

    return params;
  }

  @Test
  public void getSimpleParserInputTest() {
    buffer = "start locator  --J=\"-Dgemfire.http-service-port=8080\" --name=loc1";
    assertEquals("start locator --J \"-Dgemfire.http-service-port=8080\" --name loc1",
        GfshParser.convertToSimpleParserInput(buffer));

    buffer = "start locator --J=-Dgemfire.http-service-port=8080 --name=loc1 --J=-Ddummythinghere";
    assertEquals(
        "start locator --J \"-Dgemfire.http-service-port=8080,-Ddummythinghere\" --name loc1",
        GfshParser.convertToSimpleParserInput(buffer));

    buffer = "start locator --";
    assertThat(GfshParser.convertToSimpleParserInput(buffer)).isEqualTo("start locator --");

    buffer =
        "start locator --J=-Dgemfire.http-service-port=8080 --name=loc1 --J=-Ddummythinghere --";
    assertEquals(
        "start locator --J \"-Dgemfire.http-service-port=8080,-Ddummythinghere\" --name loc1 --",
        GfshParser.convertToSimpleParserInput(buffer));

    buffer = "start server --name=name1 --locators=localhost --J=-Dfoo=bar";
    assertEquals("start server --name name1 --locators localhost --J \"-Dfoo=bar\"",
        GfshParser.convertToSimpleParserInput(buffer));
  }

  @Test
  public void testParseOptionStartsWithHyphenWithoutQuotes() throws Exception {
    String input =
        "rebalance --exclude-region=/GemfireDataCommandsDUnitTestRegion2 --simulate=true --time-out=-1";
    Map<String, String> params = parseParams(input, "rebalance");
    assertThat(params.get("exclude-region")).isEqualTo("/GemfireDataCommandsDUnitTestRegion2");
    assertThat(params.get("simulate")).isEqualTo("true");
    assertThat(params.get("time-out")).isEqualTo("-1");
  }

  @Test
  public void testParseOptionStartsWithHyphenWithQuotes() throws Exception {
    String input =
        "rebalance --exclude-region=/GemfireDataCommandsDUnitTestRegion2 --simulate=true --time-out=\"-1\"";
    Map<String, String> params = parseParams(input, "rebalance");

    assertThat(params.get("exclude-region")).isEqualTo("/GemfireDataCommandsDUnitTestRegion2");
    assertThat(params.get("simulate")).isEqualTo("true");
    assertThat(params.get("time-out")).isEqualTo("-1");
  }

  @Test
  public void testParseOptionContainingHyphen() throws Exception {
    String input = "rebalance --exclude-region=/The-Region --simulate=true";
    Map<String, String> params = parseParams(input, "rebalance");

    assertThat(params.get("exclude-region")).isEqualTo("/The-Region");
    assertThat(params.get("simulate")).isEqualTo("true");
  }

  @Test
  public void testParseOptionContainingUnderscore() throws Exception {
    String input = "rebalance --exclude-region=/The_region --simulate=true";
    Map<String, String> params = parseParams(input, "rebalance");

    assertThat(params.get("exclude-region")).isEqualTo("/The_region");
    assertThat(params.get("simulate")).isEqualTo("true");
  }

  @Test
  public void testParseOneJOptionWithQuotes() throws Exception {
    String input = "start locator  --J=\"-Dgemfire.http-service-port=8080\" --name=loc1";
    Map<String, String> params = parseParams(input, "startLocator");

    assertThat(params.get("name")).isEqualTo("loc1");
    assertThat(params.get("J")).isEqualTo("-Dgemfire.http-service-port=8080");
  }

  @Test
  public void testParseOneJOptionWithSpaceInQuotes() throws Exception {
    String input = "start locator  --J=\"-Dgemfire.http-service-port= 8080\" --name=loc1";
    Map<String, String> params = parseParams(input, "startLocator");

    assertThat(params.get("name")).isEqualTo("loc1");
    assertThat(params.get("J")).isEqualTo("'-Dgemfire.http-service-port= 8080'");
  }

  @Test
  public void testParseOneJOption() throws Exception {
    String input = "start locator --J=-Dgemfire.http-service-port=8080 --name=loc1";
    Map<String, String> params = parseParams(input, "startLocator");

    assertThat(params.get("name")).isEqualTo("loc1");
    assertThat(params.get("J")).isEqualTo("-Dgemfire.http-service-port=8080");
  }

  @Test
  public void testParseTwoJOptions() throws Exception {
    String input =
        "start locator --J=-Dgemfire.http-service-port=8080 --name=loc1 --J=-Ddummythinghere";
    Map<String, String> params = parseParams(input, "startLocator");

    assertThat(params.get("name")).isEqualTo("loc1");
    assertThat(params.get("J")).isEqualTo("-Dgemfire.http-service-port=8080,-Ddummythinghere");
  }

  @Test
  public void testParseTwoJOptionsOneWithQuotesOneWithout() throws Exception {
    String input =
        "start locator --J=\"-Dgemfire.http-service-port=8080\" --name=loc1 --J=-Ddummythinghere";
    Map<String, String> params = parseParams(input, "startLocator");

    assertThat(params.get("name")).isEqualTo("loc1");
    assertThat(params.get("J")).isEqualTo("-Dgemfire.http-service-port=8080,-Ddummythinghere");
  }

  @Test
  public void testParseOneJOptionWithQuotesAndLotsOfSpaces() throws Exception {
    String input =
        "start locator       --J=\"-Dgemfire.http-service-port=8080\"      --name=loc1         ";
    Map<String, String> params = parseParams(input, "startLocator");

    assertThat(params.get("name")).isEqualTo("loc1");
    assertThat(params.get("J")).isEqualTo("-Dgemfire.http-service-port=8080");
  }

  @Test
  public void testObtainHelp() {
    String command = CliStrings.START_PULSE;
    String helpString = "NAME\n" + "start pulse\n" + "IS AVAILABLE\n" + "true\n" + "SYNOPSIS\n"
        + "Open a new window in the default Web browser with the URL for the Pulse application.\n"
        + "SYNTAX\n" + "start pulse [--url=value]\n" + "PARAMETERS\n" + "url\n"
        + "URL of the Pulse Web application.\n" + "Required: false\n"
        + "Default (if the parameter is not specified): http://localhost:7070/pulse\n";
    assertThat(parser.getCommandManager().obtainHelp(command)).isEqualTo(helpString);
  }

  @Test
  public void testDeployCommand() throws Exception {
    String command = "deploy --jar=/tmp/junit7552412945092669041/jar1.jar";
    GfshParseResult result = parser.parse(command);
    assertThat(result).isNotNull();
  }

}
