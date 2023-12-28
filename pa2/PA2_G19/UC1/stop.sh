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

echo "Stopping kafka services..."
$kafka_home/bin/kafka-topics.sh --bootstrap-server localhost:9092 --delete --topic Sensor 2>&1 &
$kafka_home/bin/kafka-server-stop.sh > $kafka_home/logs/servers_ending.out 2>&1 &
sleep 5
$kafka_home/bin/zookeeper-server-stop.sh > $kafka_home/logs/zookeeper_ending.out 2>&1 &
