package bank;

import com.google.gson.*;
import java.lang.reflect.Type;

/**
 * A custom Gson adapter that serializes and deserializes {@link Payment} objects.
 *
 * <p>Unlike the default Gson behavior, this adapter ensures that {@link Payment}
 * is also wrapped inside a {@code CLASSNAME / INSTANCE} structure, matching the same
 * output format used by {@link TransactionAdapter}.
 */
public class PaymentAdapter implements JsonSerializer<Payment>, JsonDeserializer<Payment> {

    /**
     * Serializes a {@link Payment} into a JSON structure containing {@code CLASSNAME}
     * and an {@code INSTANCE} object with all payment attributes.
     *
     * @param src the payment to serialize
     * @param typeOfSrc the declared type
     * @param context the serialization context
     * @return a JSON object containing {@code CLASSNAME} and {@code INSTANCE}
     */
    @Override
    public JsonElement serialize(Payment src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject wrapper = new JsonObject();
        wrapper.addProperty("CLASSNAME", "Payment");

        JsonObject instance = new JsonObject();
        instance.addProperty("incomingInterest", src.getIncomingInterest());
        instance.addProperty("outgoingInterest", src.getOutgoingInterest());
        instance.addProperty("date", src.getDate());
        instance.addProperty("amount", src.getAmount());
        instance.addProperty("description", src.getDescription());

        wrapper.add("INSTANCE", instance);
        return wrapper;
    }

    /**
     * Deserializes a flat JSON structure into a {@link Payment}.
     * This method expects the JSON to already be unwrapped (no CLASSNAME/INSTANCE wrapper).
     *
     * @param json the JSON element representing the payment (already unwrapped)
     * @param typeOfT the expected type
     * @param context the deserialization context
     * @return a fully constructed {@link Payment} instance
     * @throws JsonParseException if the JSON is invalid or object creation fails
     */
    @Override
    public Payment deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {

        // json ist bereits das INSTANCE-Objekt (kein Wrapper mehr!)
        JsonObject instance = json.getAsJsonObject();

        double incoming = instance.get("incomingInterest").getAsDouble();
        double outgoing = instance.get("outgoingInterest").getAsDouble();
        String date = instance.get("date").getAsString();
        double amount = instance.get("amount").getAsDouble();
        String description = instance.get("description").getAsString();

        try {
            return new Payment(date, amount, description, incoming, outgoing);
        } catch (Exception e) {
            throw new JsonParseException("Failed to deserialize Payment", e);
        }
    }
}
