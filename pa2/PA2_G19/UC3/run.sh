#!/bin/bash

if [ -z "$1" ]
then
  echo "Error: <kafka-home> not supplied"
  echo "Usage: $0 <kafka-home>"
  exit 0
fi

# check whether user had supplied -h or --help . If yes display usage
if [[ ( $@ == "--help") ||  $@ == "-h" ]]
then
	echo "Usage: $0 <kafka-home>"
	exit 0
fi

kafka_home=$1
# remove trailing slash if given
length=${#kafka_home}
last_char=${kafka_home:length-1:1}
[[ $last_char == "/" ]] && kafka_home=${kafka_home:0:length-1}; :

# start zookeper
echo "Starting zookeeper..."
$kafka_home/bin/zookeeper-server-start.sh $kafka_home/config/zookeeper.properties > $kafka_home/logs/zookeeper.out 2>&1 &
sleep 5
echo "Starting kafka broker..."
$kafka_home/bin/kafka-server-start.sh $kafka_home/config/server.properties > $kafka_home/logs/server.out 2>&1 &
sleep 10

# create topic
echo 'Creating topic "Sensor"...'
$kafka_home/bin/kafka-topics.sh --create --topic Sensor --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1 2>&1 &
