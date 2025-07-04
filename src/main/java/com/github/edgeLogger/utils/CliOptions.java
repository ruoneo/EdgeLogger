package com.github.edgeLogger.utils;
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.commons.cli.*;

public class CliOptions {

    private static Options options;

    private final String connectionString;
    private final String[] tagAddress;

    public static CliOptions fromArgs(String[] args) {
        options = new Options();
        // Required arguments
        options.addOption(
                Option.builder()
                        .type(String.class)
                        .longOpt("connection-string")
                        .hasArg()
                        .desc("Connection String")
                        .required()
                        .build());
        options.addOption(
                Option.builder()
                        .type(String.class)
                        .longOpt("tag-addresses")
                        .hasArgs()
                        .desc("Tag Addresses (Space separated).")
                        .required()
                        .build());

        CommandLineParser parser = new DefaultParser();
        CommandLine commandLine;
        try {
            commandLine = parser.parse(options, args);

            String connectionString = commandLine.getOptionValue("connection-string");
            String[] tagAddress = commandLine.getOptionValues("tag-addresses");

            return new CliOptions(connectionString, tagAddress);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public static void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("HelloPlc4x", options);
    }

    public CliOptions(String connectionString, String[] tagAddress) {
        this.connectionString = connectionString;
        this.tagAddress = tagAddress;
    }

    public String getConnectionString() {
        return connectionString;
    }

    public String[] getTagAddress() {
        return tagAddress;
    }

}