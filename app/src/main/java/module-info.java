module app {
    requires info.picocli;
    requires org.slf4j;
    requires ch.qos.logback.classic;
    requires org.eclipse.jetty.server;
    requires org.eclipse.jetty.ee10.servlet;
    requires org.eclipse.jetty.proxy;
    requires org.apache.commons.lang3;
    requires com.googlecode.lanterna;
    requires java.desktop;

    exports com.vanxacloud.appstudio.mitmproxy;
    opens com.vanxacloud.appstudio.mitmproxy to info.picocli;
}