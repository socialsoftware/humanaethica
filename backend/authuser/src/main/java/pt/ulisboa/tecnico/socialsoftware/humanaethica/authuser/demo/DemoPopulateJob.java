package pt.ulisboa.tecnico.socialsoftware.humanaethica.authuser.demo;



import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.scheduling.annotation.Scheduled;

import io.grpc.StatusRuntimeException;


@Component
@ConditionalOnProperty(value= "demo.populate.enabled", havingValue = "true", matchIfMissing = true)
public class DemoPopulateJob {


    private static final Logger log = LoggerFactory.getLogger(DemoPopulateJob.class);

    private final DiscoveryClient discoveryClient;
    private final DemoUtils demoUtils;
    private final AtomicBoolean done = new AtomicBoolean(false);

    public DemoPopulateJob(DiscoveryClient discoveryClient, DemoUtils demoUtils) {
        this.discoveryClient = discoveryClient;
        this.demoUtils = demoUtils;
    }

    @Scheduled(
            initialDelayString = "${demo.populate.initial-delay:5000}",
            fixedDelayString   = "${demo.populate.fixed-delay:5000}"
    )
    public void tryPopulate() {
        if (done.get()) {
            return;
        }

        boolean monolithicUp = !discoveryClient.getInstances("monolithic").isEmpty();

        if (!monolithicUp) {
            log.debug("Monolithic is not registered in Eureka");
            return;
        }

        try {
            log.info("Monolithic available in Eureka — executing demoUtils.populateDemo()...");
            demoUtils.populateDemo();
            done.set(true);
        } catch (StatusRuntimeException ex) {
            log.warn("Service not available because {}", ex.getStatus());
        } catch (Exception ex) {
            log.error("Erro ao executar populate; volto a tentar no próximo ciclo.", ex);
        }
    }

}