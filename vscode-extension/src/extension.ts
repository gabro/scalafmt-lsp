"use strict";

import * as path from "path";
import { spawn } from "child_process";
import { workspace, ExtensionContext, window, commands } from "vscode";
import {
  LanguageClient,
  LanguageClientOptions,
  ServerOptions,
  RevealOutputChannelOn,
} from "vscode-languageclient";
import { exec } from "child_process";

export async function activate(context: ExtensionContext) {
  const coursierPath = path.join(context.extensionPath, "./coursier");

  const serverVersion = workspace
    .getConfiguration("scalafmt")
    .get("serverVersion");

  const javaArgs = [
    `-XX:+UseG1GC`,
    `-XX:+UseStringDeduplication`,
    "-jar",
    coursierPath
  ];

  const artifact = `org.scalameta:scalafmtlsp_2.12:${serverVersion}`;

  // Validate the serverVersion resolves OK before attempting to launch it
  const coursierResolveArgs = [
    "resolve",
    "-r",
    "bintray:scalameta/maven",
    artifact
  ];

  const resolveArgs = javaArgs.concat(coursierResolveArgs);

  const coursierLaunchArgs = [
    "launch",
    "-r",
    "bintray:scalameta/maven",
    artifact,
    "-M",
    "scalafmtlsp.Main"
  ];

  const launchArgs = javaArgs.concat(coursierLaunchArgs);

  const serverOptions: ServerOptions = {
    run: { command: "java", args: launchArgs },
    debug: { command: "java", args: launchArgs }
  };

  const clientOptions: LanguageClientOptions = {
    documentSelector: ["scala"],
    synchronize: {
      fileEvents: [
        workspace.createFileSystemWatcher(".scalafmt.conf")
      ],
      configurationSection: "scalafmt"
    },
    revealOutputChannelOn: RevealOutputChannelOn.Never
  };

  const client = new LanguageClient(
    "scalafmt",
    "Scalafmt",
    serverOptions,
    clientOptions
  );

  spawn("java", resolveArgs).on("exit", code => {
    if (code !== 0) {
      const msg = [
        "Could not find Scalafmt server artifact, ensure that scalafmt.serverVersion setting is correct.",
        `Coursier resolve failed on:${artifact} with exit code:${code}.`
      ].join("\n");
      window.showErrorMessage(msg);
      console.error(msg);
    } else {
      console.log(
        `Successfully resolved Scalafmt server artifact: ${artifact}. Starting LanguageClient.`
      );
      context.subscriptions.push(client.start())
    }
  });
}
