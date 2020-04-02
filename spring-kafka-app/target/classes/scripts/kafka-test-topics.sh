kafka-topics --create --zookeeper zookeeper:2181/kafka --replication-factor 1 --partitions 1 --topic test1
kafka-console-producer --broker-list localhost:29092 --topic test1 <<EOF
Test message 1 on topic test1
Test message 2 on topic test1
EOF
kafka-topics --create --zookeeper zookeeper:2181/kafka --replication-factor 1 --partitions 1 --topic test2
kafka-console-producer --broker-list localhost:29092 --topic test2 <<EOF
Test message 1 on topic test2
Test message 2 on topic test2
EOF
kafka-topics --create --zookeeper zookeeper:2181/kafka --replication-factor 1 --partitions 1 --topic test3
kafka-console-producer --broker-list localhost:29092 --topic test3 <<EOF
Test message 1 on topic test3
Test message 2 on topic test3
EOF