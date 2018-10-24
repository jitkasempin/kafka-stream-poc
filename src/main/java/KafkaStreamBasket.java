import serde.SpecificAvroSerde;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;

import java.util.Collections;
import java.util.Properties;


public class KafkaStreamBasket {

    private final static String SCHEMA_REGISTRY_URL = "http://localhost:8081";


    public static void main(String... args) throws Exception {

        KStreamBuilder builder = new KStreamBuilder();

        Properties settings = new Properties();
        // Set a few key parameters
        settings.put(StreamsConfig.APPLICATION_ID_CONFIG, "kafka-stream-basket");
        settings.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        settings.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class);
        settings.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, SCHEMA_REGISTRY_URL);
        settings.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        settings.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 10 * 1000);
        settings.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        final Serde<String> stringSerde = Serdes.String();
        final Serde<prices> specificAvroSerde = new SpecificAvroSerde<>();
        final boolean isKeySerde = false;

        specificAvroSerde.configure(
                Collections.singletonMap(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, SCHEMA_REGISTRY_URL),
                isKeySerde);

        StreamsConfig config = new StreamsConfig(settings);

        KStream<String, prices> prices = builder.stream("confluent-in-prices");

        KStream<String, String> priceOutput = prices.map((key, value) -> new KeyValue<String, Long>(value.getItem().toString(), value.getPrice().longValue()))
                .filter((key, value) -> value >= 1000)
                .map((key, value) -> new KeyValue<>(key, value.toString()));



        priceOutput.to(stringSerde, stringSerde,"confluent-out-prices");


        KafkaStreams streams = new KafkaStreams(builder, config);

        streams.start();


        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }
}
