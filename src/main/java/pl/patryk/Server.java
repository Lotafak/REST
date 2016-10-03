package pl.patryk;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.linking.DeclarativeLinkingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.mongodb.morphia.Datastore;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;


public class Server {

    public static void main(String[] args) {
        URI baseUri = UriBuilder.fromUri("http://localhost/").port(8000).build();
        ResourceConfig config = new ResourceConfig().
                packages("pl.patryk").
                registerClasses(DeclarativeLinkingFeature.class);
        config.register(CustomHeaders.class);
        config.register(DateParamConverterProvider.class);
        System.out.println("Starting grizzly...");
        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(baseUri, config);
        Model md = new Model();
//        md.addData();

        md.addMongoData();
        Datastore ds = md.ds;
        ds.ensureIndexes();
    }
}