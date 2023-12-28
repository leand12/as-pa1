# start server with custom configuration file
bin/kafka-server-start.sh ./server.properties

# create topic with partitions and replication factor 1
bin/kafka-topics.sh --create --topic Sensor --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1

# delete topic
bin/kafka-topics.sh --bootstrap-server localhost:9092 --delete --topic Sensor
# ... may require:
# - closing all processes
# or:
# - going to config/server.properties
# - adding "delete.topic.enable=true"

# list messages
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic Sensor --from-beginning

# list topics
bin/kafka-topics.sh  --bootstrap-server localhost:9092 --list

# update messages' retention time to 10s
bin/kafka-configs.sh --bootstrap-server localhost:9092 --entity-type topics --alter --entity-name Sensor --add-config retention.ms=10000

# describe topic
bin/kafka-topics.sh  --bootstrap-server localhost:9092 --describe --topic Sensor

# list consumer groups
bin/kafka-consumer-groups.sh --list --bootstrap-server localhost:9092

# list consumers part of a consumer group
bin/kafka-consumer-groups.sh --bootstrap-server localhost:9092 --describe --group Group
# ... LAG means the amount of messages left
