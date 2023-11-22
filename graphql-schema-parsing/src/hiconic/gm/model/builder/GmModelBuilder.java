package hiconic.gm.model.builder;

import static com.braintribe.utils.lcd.CollectionTools2.newMap;
import static com.braintribe.utils.lcd.CollectionTools2.newSet;
import static com.braintribe.utils.lcd.CollectionTools2.nullSafe;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

import com.braintribe.common.artifact.ArtifactReflection;
import com.braintribe.gm._RootModel_;
import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.BaseType;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.Model;
import com.braintribe.model.generic.reflection.SimpleTypes;
import com.braintribe.model.meta.GmBaseType;
import com.braintribe.model.meta.GmBooleanType;
import com.braintribe.model.meta.GmDateType;
import com.braintribe.model.meta.GmDecimalType;
import com.braintribe.model.meta.GmDoubleType;
import com.braintribe.model.meta.GmEntityType;
import com.braintribe.model.meta.GmFloatType;
import com.braintribe.model.meta.GmIntegerType;
import com.braintribe.model.meta.GmLongType;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.meta.GmStringType;
import com.braintribe.model.meta.GmType;

/**
 * Not thread safe!!!
 * 
 * @author peter.gazdik
 */
public class GmModelBuilder {

	private final Map<String, GmType> typeMap = newMap();
	private final Map<String, GmCustomTypeBuilder<?>> builderMap = newMap();

	public final GmMetaModel newModel;
	private final List<GmMetaModel> dependencies;

	private GmBaseType baseType;

	private GmStringType stringType;
	private GmFloatType floatType;
	private GmDoubleType doubleType;
	private GmBooleanType booleanType;
	private GmIntegerType integerType;
	private GmLongType longType;
	private GmDateType dateType;
	private GmDecimalType decimalType;

	private GmEntityType genericEntityType;

	public static GmMetaModel findModel(ArtifactReflection reflection) {
		return GMF.getTypeReflection().findModel(reflection.name()).getMetaModel();
	}

	public static GmModelBuilder create(String name, String version) {
		return create(name, version, findModel(_RootModel_.reflection));
	}

	public static GmModelBuilder create(String name, String version, GmMetaModel... dependencies) {
		return new GmModelBuilder(name, version, dependencies);
	}

	private GmModelBuilder(String name, String version, GmMetaModel... dependencies) {
		this.dependencies = Arrays.asList(dependencies);

		this.newModel = GmMetaModel.T.create();
		this.newModel.setGlobalId(Model.modelGlobalId(name));
		this.newModel.setName(name);
		this.newModel.setVersion(version);
		this.newModel.getDependencies().addAll(this.dependencies);

		indexModels(this.dependencies, newSet());

		indexRootModel();
	}

	private void indexModels(Collection<GmMetaModel> gmModels, Set<GmMetaModel> indexed) {
		for (GmMetaModel dep : nullSafe(gmModels))
			indexModel(dep, indexed);
	}

	private void indexModel(GmMetaModel gmModel, Set<GmMetaModel> indexed) {
		if (!indexed.add(gmModel))
			return;

		indexModels(gmModel.getDependencies(), indexed);

		for (GmType gmType : gmModel.getTypes())
			typeMap.put(gmType.getTypeSignature(), gmType);
	}

	private void indexRootModel() {
		baseType = getType(BaseType.INSTANCE);
		stringType = getType(SimpleTypes.TYPE_STRING);
		floatType = getType(SimpleTypes.TYPE_FLOAT);
		doubleType = getType(SimpleTypes.TYPE_DOUBLE);
		booleanType = getType(SimpleTypes.TYPE_BOOLEAN);
		integerType = getType(SimpleTypes.TYPE_INTEGER);
		longType = getType(SimpleTypes.TYPE_LONG);
		dateType = getType(SimpleTypes.TYPE_DATE);
		decimalType = getType(SimpleTypes.TYPE_DECIMAL);
		genericEntityType = getType(GenericEntity.T);
	}

	// @formatter:off
	public GmBaseType baseType() { return baseType; }
	public GmStringType stringType() { return stringType; }
	public GmFloatType floatType() { return floatType; }
	public GmDoubleType doubleType() { return doubleType; }
	public GmBooleanType booleanType() { return booleanType; }
	public GmIntegerType integerType() { return integerType; }
	public GmLongType longType() { return longType; }
	public GmDateType dateType() { return dateType; }
	public GmDecimalType decimalType() { return decimalType; }
	public GmEntityType genericEntityType() { return genericEntityType; }
	// @formatter:on	

	public <T extends GmType> T getType(GenericModelType type) {
		return getType(type.getTypeSignature());
	}

	public <T extends GmType> T getType(String typeSignature) {
		T result = (T) typeMap.get(typeSignature);
		if (result == null)
			throw new IllegalStateException("Type not found: " + typeSignature + " Building model : " + newModel.getName());

		return result;
	}

	public GmEntityType acquireEntityType(String typeSignature) {
		GmEntityType type = findType(typeSignature, GmEntityType.T);
		if (type != null)
			return type;
		else
			return createTypeBuilder(typeSignature, GmEntityTypeBuilder::new).it;
	}

	private <T extends GmType> T findType(String typeSignature, EntityType<T> typeType) {
		T gmType = (T) typeMap.get(typeSignature);
		if (!typeType.isInstance(gmType))
			throw new IllegalStateException("Trying to acquire " + typeType.getShortName() + " '" + typeSignature
					+ "', but given type is already registered as a " + gmType.entityType().getTypeSignature());

		return gmType;
	}

	public GmEntityTypeBuilder acquireEntityTypeBuilder(String typeSignature) {
		return acquireTypeBuilder(typeSignature, GmEntityTypeBuilder.class, GmEntityTypeBuilder::new);
	}

	public GmEnumTypeBuilder acquireEnumTypeBuilder(String typeSignature) {
		return acquireTypeBuilder(typeSignature, GmEnumTypeBuilder.class, GmEnumTypeBuilder::new);
	}

	private <B extends GmCustomTypeBuilder<?>> B acquireTypeBuilder(String typeSignature, Class<B> builderClass,
			BiFunction<GmModelBuilder, String, B> builderConstructor) {

		GmCustomTypeBuilder<?> builder = builderMap.get(typeSignature);
		if (builder == null)
			return createTypeBuilder(typeSignature, builderConstructor);

		if (!builderClass.isInstance(builder))
			throw new IllegalStateException("Trying to acquire " + builderClass.getSimpleName() + " for '" + typeSignature
					+ "', but given type is being built with a " + builder.getClass().getSimpleName());

		return (B) builder;
	}

	private <B extends GmCustomTypeBuilder<?>> B createTypeBuilder(String typeSignature, BiFunction<GmModelBuilder, String, B> builderConstructor) {
		B newBuilder = builderConstructor.apply(this, typeSignature);

		builderMap.put(typeSignature, newBuilder);
		typeMap.put(typeSignature, newBuilder.it);

		newModel.getTypes().add(newBuilder.it);

		return newBuilder;
	}

	public GmMetaModel buildAndVerify() {
		finishAndVerify();
		return newModel;
	}

	private void finishAndVerify() {
		for (GmCustomTypeBuilder<?> gmTypeBuilder : builderMap.values())
			gmTypeBuilder.finishAndVerify();
	}

}
