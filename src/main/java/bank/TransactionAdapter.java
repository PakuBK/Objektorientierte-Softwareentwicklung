package bank;

import com.google.gson.*;
import java.lang.reflect.Type;

/**
 * A custom Gson adapter for serializing and deserializing {@link Transaction} objects.
 *
 * <p>This adapter wraps every transaction into the following JSON structure:
 * <pre>
 * {
 *   "CLASSNAME": "Payment",
 *   "INSTANCE": { ... }
 * }
 * </pre>
 */
public class TransactionAdapter implements JsonSerializer<Transaction>, JsonDeserializer<Transaction> {

    /**
     * Serializes a {@link Transaction} instance into a JSON structure that includes
     * its simple class name and its serialized fields inside an {@code INSTANCE} object.
     *
     * @param transaction the transaction to serialize
     * @param type the declared type
     * @param context the serialization context provided by Gson
     * @return a JSON object containing {@code CLASSNAME} and {@code INSTANCE}
     */
    @Override
    public JsonElement serialize(Transaction transaction, Type type, JsonSerializationContext context) {
        JsonObject wrapper = new JsonObject();
        wrapper.addProperty("CLASSNAME", transaction.getClass().getSimpleName());

        JsonElement instance = context.serialize(transaction, transaction.getClass());
        wrapper.add("INSTANCE", instance);

        return wrapper;
    }

    /**
     * Deserializes a JSON structure containing {@code CLASSNAME} and {@code INSTANCE}
     * back into the appropriate subclass of {@link Transaction}.
     *
     * @param jsonElement the JSON element to deserialize
     * @param type the expected type
     * @param context the deserialization context provided by Gson
     * @return a concrete {@link Transaction} instance matching the stored {@code CLASSNAME}
     * @throws JsonParseException if the transaction type is unknown or deserialization fails
     */
    @Override
    public Transaction deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context)
            throws JsonParseException {

        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String className = jsonObject.get("CLASSNAME").getAsString();
        JsonObject instance = jsonObject.get("INSTANCE").getAsJsonObject();

        try {
            return switch (className) {
                case "Payment" -> context.deserialize(instance, Payment.class);
                case "IncomingTransfer" -> context.deserialize(instance, IncomingTransfer.class);
                case "OutgoingTransfer" -> context.deserialize(instance, OutgoingTransfer.class);
                case "Transfer" -> context.deserialize(instance, Transfer.class);
                default -> throw new JsonParseException("Unknown Transaction type: " + className);
            };
        } catch (Exception e) {
            throw new JsonParseException("Failed to deserialize " + className, e);
        }
    }
}
