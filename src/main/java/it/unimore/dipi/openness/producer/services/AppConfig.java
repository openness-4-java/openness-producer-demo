package it.unimore.dipi.openness.producer.services;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import it.unimore.dipi.openness.producer.persistence.DefaultIotInventoryDataManger;
import it.unimore.dipi.openness.producer.persistence.TisDataManager;

public class AppConfig extends Configuration {

    @JsonProperty("swagger")
    public SwaggerBundleConfiguration swaggerBundleConfiguration;

    @JsonProperty("myAuth")
    public String myAuth;
    @JsonProperty("myApi")
    public String myApi;
    @JsonProperty("myWs")
    public String myWs;
    @JsonProperty("endpoint")
    public String endpoint;

    private TisDataManager tisDataManager = null;

    public TisDataManager getTisDataManager(){

        if(this.tisDataManager == null)
            this.tisDataManager = new DefaultIotInventoryDataManger();

        return this.tisDataManager;
    }

}