# Kafka Integration with Spring Cloud
Spring Cloud is a Spring project which aims at providing tools for developers 
helping them to quickly implement some of the most common design patterns like:
configuration management, service discovery, circuit breakers, routing, proxy,
control bus, one-time tokens, global locks, leadership election, distributed 
sessions and much more.
 
One of the most interesting Spring Cloud sub-projects is Spring Cloud Streams 
which provides an annotation driven framework to build message publishers and 
subscribers. It supports the most recent messaging platforms like RabbitMQ and
Kafka and abstracts away their implementation details.
  
This project is demonstrating Spring Cloud Streams with Kafka platforms.
 
## The Kafka Infrastructure
In a most authentic devops approach, our project is structured such that to 
use Docker containers. Our Kafla infrastructure is defined in the 
docker-compose.yml file, as follows:
 

    version: '3.7'
    services:
      zookeeper:
        image: confluentinc/cp-zookeeper:5.3.1
        hostname: zookeeper
        container_name: zookeeper
        ports:
          - 2181:2181
        environment:
          ZOOKEEPER_SERVER_ID: 1
          ZOOKEEPER_CLIENT_PORT: 2181
          ZOOKEEPER_TICK_TIME: 2000
          ZOOKEEPER_INIT_LIMIT: 5
          ZOOKEEPER_SYNC_LIMIT: 2
          ZOOKEEPER_SERVERS: zookeeper:2888:3888
        volumes:
          - /var/lib/zookeeper:/var/lib/zookeeper
      kafka:
        image: confluentinc/cp-kafka:5.3.1
        hostname: kafka
        container_name: kafka-broker
        ports:
          - "29092:29092"
          - "9092:9092"
        depends_on:
          - zookeeper
        environment:
          KAFKA_BROKER_ID: 1
          KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181/kafka
          KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:29092,PLAINTEXT_HOST://kafka:9092
          KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT
          KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
          KAFKA_GROUP_INITIAL_REBALANCE_DELAY_MS: 0
        volumes:
          - /var/lib/kafka:/var/lib/kafka
          - ./scripts/:/scripts
      schema-registry:
        image: confluentinc/cp-schema-registry:5.3.1
        container_name: schema-registry
        depends_on:
          - zookeeper
        ports:
          - 8081:8081
        environment:
          SCHEMA_REGISTRY_KAFKASTORE_CONNECTION_URL: zookeeper:2181/kafka
          SCHEMA_REGISTRY_LISTENERS: "http://0.0.0.0:8081"
          SCHEMA_REGISTRY_HOST_NAME: schema-registry
      kafka-rest-proxy:
        image: confluentinc/cp-kafka-rest:5.3.1
        hostname: kafka-rest-proxy
        container_name: kafka-rest-proxy
        depends_on:
          - zookeeper
          - kafka
          - schema-registry
        ports:
          - 8082:8082
        environment:
          KAFKA_REST_HOST_NAME: kafka-rest-proxy
          KAFKA_REST_BOOTSTRAP_SERVERS: kafka:29092
          KAFKA_REST_LISTENERS: "http://0.0.0.0:8082"
          KAFKA_REST_SCHEMA_REGISTRY_URL: 'http://schema-registry:8081'
          KAFKA_REST_CONSUMER_REQUEST_TIMEOUT_MS: 30000
          TZ: "${TZ-Europe/Paris}"
      kafka-topics-ui:
        image: landoop/kafka-topics-ui:0.9.4
        container_name: kafka-ui
        depends_on:
          - kafka-rest-proxy
        ports:
          - 8000:8000
        environment:
          KAFKA_REST_PROXY_URL: http://kafka-rest-proxy:8082
          PROXY: "true"
      zoonavigator:
        image: elkozmon/zoonavigator:0.7.1
        container_name: zoonavigator
        depends_on:
          - zookeeper
        environment:
          HTTP_PORT: 9000
          AUTO_CONNECT_CONNECTION_STRING: zookeeper:2181
      kafka_manager:
        image: hlebalbau/kafka-manager:stable
        container_name: kafka-manager
        ports:
          - "9000:9000"
        depends_on:
          - kafka
          - zookeeper
        environment:
          ZK_HOSTS: "zookeeper:2181"
          APPLICATION_SECRET: "random-secret"
          KAFKA_MANAGER_AUTH_ENABLED: "true"
          KAFKA_MANAGER_USERNAME: username
          KAFKA_MANAGER_PASSWORD: password
        command: -Dpidfile.path=/dev/null

As this infrastructure might seem quite complex, it is explained below.

### Zookeeper
Kafka is, besides other, a message broker and, like any other message broker, 
it may be clusterized. This means that several Kafka message brokers might be 
connected such that to provide a distributed messaging environment. 

ZooKeeper is a centralized service for storing and maintaining configuration 
and naming information. It provides grouping and distributed synchronization to
other services. 

Kafka uses Apache Zookeeper to maintain the list of brokers that are currently 
 members of a cluster. Every broker has a unique identifier that is either set
 in the broker configuration file or automatically generated. Every time a 
broker process starts, it registers itself with its ID in Zookeeper by 
creating an ephemeral node. Different Kafka components subscribe to the 
brokers defined path in Zookeeper.

The first Docker container in our infrastructure above is then running an 
instance of the Apache Zookeeper service. The Docker image 
`confluentinc/cp-zookeeper`  comes from Docker Hub and is provided by 
Confluent. It exposes the TCP port number 2181 and mounts the `/var/lib/zookeeper`
as a read-write volume. Several environment variables are defined, as documented
at DockerHub (https://hub.docker.com/r/confluentinc/cp-zookeeper). In a real 
infrastructure several Zookeeper instances would probably be required but here,
 for simplicity sake, we're using only one.

### Kafka
The second piece in our puzzle is the Kafka broker itself. The Docker image
`confluentinc/cp-kafka:5.3.1` is provided by Cofluent as well and the container
configuration is self-explanatory. The documentation 
(https://hub.docker.com/r/confluentinc/cp-kafka) provides full details.

### Schema Registry
As a messaging and streaming platform, Kafka is used to exchange information in
 the form of business objects that are published by message producers to 
 Kafka topics, to which message consumers are subscribing in order to retrieve 
them. Hence, these business objects have to be serialized by the producer and 
deserialized by the consumer.

Kafka includes out-of-the-box serializers/deserializers for different data 
types like integers, `ByteArrays`, etc. but they don't cover most use cases. 
When the data to be exchanged is not in the form of simple strings or integers,
a more elaborated serialization/deserialization is required. This is done using
specialized libraries like Avro, Thrift or Protobuf.

The prefered way to serialze.unserialize data in Kafka is on the behalf of the 
Apache Avro library. But whatever the library is, it is based on a so called 
serialization/deserialization schema. This is a JSON file describing the 
serialization/deserialization rules. So, whatever the library is, it requires a
way to store the this schema. Avro, for example, stores it directly in the binary
file hosting the serialized objects, but there is a better way to handle this 
for Kafka messages.

Since locating the serialization/deserialization schema in each serialized file
might come with some overhead, the best practices are to use a schema registry 
for this purpose. The Schema Registry is not part of Apache Kafka but there are
several open source options to choose from. Here we’ll use the Confluent Schema
Registry. The idea is to store all the schemas used to write data to Kafka in 
the registry. Then we simply store the identifier for the schema in the record 
we produce to Kafka. The consumers can then use the identifier to pull the record
out of the schema registry and deserialize the data. The key is that all this 
work, which consists in storing the schema in the registry and pulling it up when
required, is done in the serializers and deserializers. The code that produces 
data to Kafka or that consumes data from Kafka simply uses the Avro serializer 
/ deserializer, without any concern of where the associated schema is stored.

The figure below illustrates this process.

![schema-registry](./schema-registry.png)

So, the next Docker container of our infrastructure is the one running the 
Confluent Schema Registry. Noting of particular here other then that it exposes
the TCP port 8081 and that it defines a couple of environment variables, as
required by the documentation https://hub.docker.com/r/confluentinc/cp-schema-registry.

### Kafka REST Proxy
The Kafka REST Proxy is a RESTful interface to a Kafka cluster, making it 
easy to produce and consume messages, view the state of the cluster, and 
perform administrative actions without using the native Kafka protocol or clients.
This is a very useful component which is not a part of the Kafka itself neither,
but it belongs to the Confluent Kafka adds-on series. 

The docker image ``confluentinc/cp-kafka-rest`` contains the Confluent REST Proxy
for Kafka. Its documentation may be found here: 
https://hub.docker.com/r/confluentinc/cp-kafka-rest. The configuration is simple
and it doesn't require anything of special. The environment variables defined 
here are explained in the documentation. To resume, we're configuring the Kafka
broker address, the schema-registry one, as well as the REST proxy hostname. An
interesting point to be noticed is the listener at ``0.0.0.0:8082`` which is 
the addres of the kafka-topics-ui container, explained below.

### Kafka Topics UI
The Kafka Topics UI is a user interface that interacts with the Kafka REST Proxy
to allow browsing Kafka topics, to inspect messages and, more generally, see 
what exactly happens in your Kafka clusters. Hence, the next piece of our puzzle
is a Docker container running the image named ``landoop/kafka-topics-ui`` which 
documentation may be found here: https://hub.docker.com/r/landoop/kafka-topics-ui.
The configuration is just exposing the TCP port number 8000 and setting the Kafka
REST proxy IP address (DNS name) and TCP port.

### Zoo Navigator
As we have seen, Kafka clusters are using Apache ZooKeeper in order to persist the
required information concerning brokers, nodes, topics, etc. Zoo Navigator is 
another add-on tool which allows for browsing, in an user friendly way, the 
information stored in the ZooKeeper repositories. The associated Docker container 
is based, as shown, on the ``elkozmon/zoonavigator`` image which documentation may 
be found here. The configuration exposes the TCP port number 9000 and defines the 
ZooKeeper server IP address (DNS name) and TCP port number.                                                                  required aithentication credentials

### Kafka Manager
The last piece of our puzzle is the Kafka Manager. This component is another 
optional add-on which provides the confort of a GUI on the behalf of which the 
most common administration operations on the Kafka brokers and topics may be 
performed. Of course, all these operations can be done using the Kafka CLI 
(Command Line Interface), which is a part of the Kafka package, but for those 
who prefer the click and drag approach to the austerity of a CLI, this manager 
is a nice alternative.

This Docker container is based on the image ``hlebalbau/kafka-manager`` which 
documentation may be found here: https://github.com/hleb-albau/kafka-manager-docker.
The configuration exposes the TCP port number 9000and defines the ZooKeeper server 
IP address (DNS name) and TCP port number, as well as the required authentication 
credentials.

## Exercicing the Infrastructure
To exercice the presented infrastructure, just proceed as follows:
1. Clone the project from GitHub:

        git clone https://github.com/nicolasduminil/kafka-spring-integration.git  
2. Build the project:       

        mvn clean install
3. Check wether the Docker containers are up and running:        

        docker ps
You'll se the following listing:        
        