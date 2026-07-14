package com.vanxacloud.appstudio.mitmproxy;

import ch.qos.logback.classic.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

@CommandLine.Command(name = "mitmproxy", subcommands = {
        CommandLine.HelpCommand.class}, description = "mitmproxy")
public class MitmProxy implements Runnable {

    public static final Logger log = LoggerFactory.getLogger("mitmproxy");

    public static final int ERR_DEFAULT = -1;

    @CommandLine.Spec
    CommandLine.Model.CommandSpec spec;


    @Override
    public void run() {
        ProxyServer proxyServer = new ProxyServer.Builder().port(port).build();
        proxyServer.start();
    }

    @CommandLine.Option(names = "-v", description = "Increase log verbosity. Eg. -vvv for TRACE", scope = CommandLine.ScopeType.INHERIT)
    public void setVerbose(boolean[] verbose) {
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        int len = verbose.length;
        if (len > 1) {
            root.setLevel(Level.TRACE);
        } else if (len == 1) {
            root.setLevel(Level.DEBUG);
        } else {
            root.setLevel(Level.INFO);
        }
    }

    @CommandLine.Option(names = {"-p", "--port"}, description = "port to listen to", defaultValue = "8080")
    private int port = 8080;

    public static void main(String[] args) {
        CommandLine.IExecutionExceptionHandler handler = (ex, commandLine, parseResult) -> {
            log.error("Caught exception while running command [{}] - [{}} - {}}]", commandLine.getCommandName(), ex.getClass()
                    .getSimpleName(), ex.getMessage());
            return ERR_DEFAULT;
        };
        System.exit(new CommandLine(new MitmProxy()).setExecutionExceptionHandler(handler).execute(args));
    }

}
