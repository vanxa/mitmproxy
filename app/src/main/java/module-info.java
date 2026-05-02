module app {
    requires info.picocli;
    requires org.slf4j;
    requires ch.qos.logback.classic;

    opens com.vanxacloud.appstudio.mitmproxy.cli to info.picocli;

    exports com.vanxacloud.appstudio.mitmproxy;
}