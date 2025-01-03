package api;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.api.server.spi.auth.common.User;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.config.Nullable;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.api.server.spi.auth.EspAuthenticator;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreNeedIndexException;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.PropertyProjection;
import com.google.appengine.api.datastore.PreparedQuery.TooManyResultsException;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.api.datastore.Transaction;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.lang.reflect.Type;

@Api(name = "myApi",
     version = "v1",
     audiences = "884230662780-m2v4ucelmmfiho8pirgc694vq4d683fv.apps.googleusercontent.com",
  	 clientIds = {"927375242383-t21v9ml38tkh2pr30m4hqiflkl3jfohl.apps.googleusercontent.com",
        "70609645985-gkmqrn9keiilqrla80e13hpgbjceb1ir.apps.googleusercontent.com",
		"884230662780-m2v4ucelmmfiho8pirgc694vq4d683fv.apps.googleusercontent.com"},
     namespace =
     @ApiNamespace(
		   ownerDomain = "helloworld.example.com",
		   ownerName = "helloworld.example.com",
		   packagePath = "")
     )

public class Quad {
	
	@ApiMethod(name = "insertQuad", path="insertQuad/{subject}/{predicate}/{object}/{graph}", httpMethod = ApiMethod.HttpMethod.GET)
public Entity insertQuad(User user, 
                         @Named("subject") String subject, 
                         @Named("predicate") String predicate, 
                         @Named("object") String object, 
                         @Named("graph") String graph) throws UnauthorizedException {
    if (user == null) {
        throw new UnauthorizedException("Invalid credentials");
    }

    Entity quad = new Entity("Quad");
    quad.setProperty("subject", subject);
    quad.setProperty("predicate", predicate);
    quad.setProperty("object", object);
    quad.setProperty("graph", graph);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    Transaction txn = datastore.beginTransaction();
    try {
        datastore.put(quad);

        Key counterKey = KeyFactory.createKey("Counter", "QuadCounter");
        Entity counter;
        try {
            counter = datastore.get(counterKey);
        } catch (EntityNotFoundException e) {
            counter = new Entity(counterKey);
            counter.setProperty("count", 0L);
        }

        long currentCount = (long) counter.getProperty("count");
        counter.setProperty("count", currentCount + 1);

        datastore.put(counter);

        txn.commit();

        return quad;
    } finally {
        if (txn.isActive()) {
            txn.rollback();
        }
    }
}

	
	
		@ApiMethod(name = "getQuads",path="getQuads/{cursor}",httpMethod = ApiMethod.HttpMethod.GET)
		public Map<String, Object> getQuads(@Named("subject") String subject, 
		@Named("predicate") String predicate, 
		@Named("object") String object, 
		@Named("cursor") String cursorString) {
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			Query query = new Query("Quad");
			if (subject != null && subject != ""){ 
				query.setFilter(new FilterPredicate("subject", FilterOperator.EQUAL, subject));
			}
            if (predicate != null && predicate != ""){ 
                query.setFilter(new FilterPredicate("predicate", FilterOperator.EQUAL, predicate));
			}
            if (object != null && object != ""){
				query.setFilter(new FilterPredicate("object", FilterOperator.EQUAL, object));
			}
			PreparedQuery preparedQuery = datastore.prepare(query);
			FetchOptions fetchOptions = FetchOptions.Builder.withLimit(100);
			if (!cursorString.equals("null") && cursorString != null) {
				fetchOptions.startCursor(Cursor.fromWebSafeString(cursorString));
			}
	
			QueryResultList<Entity> results = preparedQuery.asQueryResultList(fetchOptions);
			Map<String, Object> response = new HashMap<>();
			response.put("quads", results);
			response.put("nextCursor", results.getCursor().toWebSafeString());
			return response;
		}

        @ApiMethod(name = "getCounter",path="getCounter", httpMethod = ApiMethod.HttpMethod.GET)
        public Object getCounter() throws EntityNotFoundException{
            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			Key counterKey = KeyFactory.createKey("Counter", "QuadCounter");
            Entity counter;
            counter = datastore.get(counterKey);
            return counter;
        }

class QuadDTO {
    private String subject;
    private String predicate;
    private String object;
    private String graph;

    // Getters et setters
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    
    public String getPredicate() { return predicate; }
    public void setPredicate(String predicate) { this.predicate = predicate; }
    
    public String getObject() { return object; }
    public void setObject(String object) { this.object = object; }
    
    public String getGraph() { return graph; }
    public void setGraph(String graph) { this.graph = graph; }
}
}
	