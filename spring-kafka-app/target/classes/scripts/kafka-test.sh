kafka-topics --create --zookeeper zookeeper:2181/kafka --replication-factor 1 --partitions 1 --topic test
kafka-topics --describe --zookeeper zookeeper:2181/kafka --topic test
kafka-console-producer --broker-list localhost:29092 --topic test <<EOF
Test message 1
Test message 2
EOF
kafka-console-consumer --topic test --from-beginning --timeout-ms 5000 --bootstrap-server localhost:29092
kafka-topics --delete --zookeeper zookeeper:2181/kafka --topic test