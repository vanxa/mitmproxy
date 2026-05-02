package com.vanxacloud.appstudio.proximity.app.event.listener;

import com.vanxacloud.appstudio.proximity.app.event.Events;
import com.vanxacloud.appstudio.proximity.fx.control.wizard.Wizard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class WizardStageListener implements ApplicationListener<Events.ReadyForWizardEvent> {

    private final ApplicationContext ac;
    private static final Logger log = LoggerFactory.getLogger(WizardStageListener.class);

    WizardStageListener(ApplicationContext ac) {
        this.ac = ac;
    }

    @Override
    public void onApplicationEvent(Events.ReadyForWizardEvent event) {
        Wizard wizard = ac.getBean(Wizard.class);
        Wizard.Settings settings = wizard.show();
        if (settings == null) {
            // Cancelled, closed
            return;
        }
        log.info("{}", settings);
        this.ac.publishEvent(new Events.ApplicationStartingEvent(event.getStage(), settings));
    }
}
