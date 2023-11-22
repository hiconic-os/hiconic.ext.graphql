package hiconic.ext.graphql;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.function.Supplier;

import com.braintribe.cfg.Configurable;
import com.braintribe.cfg.Required;
import com.braintribe.exception.Exceptions;
import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.model.processing.service.api.ReasonedServiceProcessor;
import com.braintribe.model.processing.service.api.ServiceRequestContext;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.model.resource.Resource;
import com.braintribe.model.service.api.result.Neutral;

import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import hiconic.ext.graphql.schema_api.model.ConvertGraphQlSchemaToGm;

public class GraphQlSchemaToGm implements ReasonedServiceProcessor<ConvertGraphQlSchemaToGm, Neutral> { // returns: list
																										// of Gm-models

	private Supplier<PersistenceGmSession> cortexSessionSupplier;

	@Configurable
	@Required
	public void setCortexSession(Supplier<PersistenceGmSession> cortexSession) {
		this.cortexSessionSupplier = cortexSession;
	}

	private InputStream openStream(Resource resource) throws IOException {
		if (resource.isStreamable())
			return resource.openStream();
		else
			return cortexSessionSupplier.get().resources().openStream(resource);
	}

	private Maybe<TypeDefinitionRegistry> openResource(Resource resource) {
		try (Reader reader = new BufferedReader(new InputStreamReader(openStream(resource), "UTF-8"))) {

			// read and store in string
			StringBuilder stringBuilder = new StringBuilder();
			int valChar;
			while ((valChar = reader.read()) != -1) {
				stringBuilder.append((char) valChar);
			}
			String str = stringBuilder.toString();

			SchemaParser schemaParser = new SchemaParser();
			return Maybe.complete(schemaParser.parse(str));

		} catch (Exception e) {
			throw Exceptions.unchecked(e);
		}
	}

	@Override
	public Maybe<? extends Neutral> processReasoned(ServiceRequestContext context, ConvertGraphQlSchemaToGm request) {

		Resource schema = request.getSchema();

		Maybe<TypeDefinitionRegistry> openResource = openResource(schema);
		

		return null;
	}

}
