This kafka-stream poc project is copy and modify from kafka-stream-basket (https://github.com/ArthurBaudry/kafka-stream-basket)

It consume from the kafka topic and use Kafka Specific Avro Serde to be able to read the avro data from the consuming-topic.

I remove the Thrift endpoint from the original code to make it able to run as standalone-jar and modify the kafka-stream topology to filter some value and also make it can print String (or Text) to the destination topic.

How to run and compile...
1. mvn schema-registry:download
2. mvn avro:schema
3. mvn package
4. run the jar file inside the target folder using "java -jar executable_jar.jar"


