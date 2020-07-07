# GigaSpaces Service Schema Evolution On ElasticGrid - Demo
This project is a demonstration of how to implement service schema evolution in GigaSpaces
The starting point is a running service called v1-service. The service is persistent with mongodb used as the persistence layer. Data is also persisteed to Apache Kafka cluster, which acts as a buffer between the space and mongodb. 
Entries of type Person are continuously written to the service by the v1-feeder service. 

##### The v2 service
In the demo a new service called v2-service is deployed side by side with v1. 
In v2, there is a new version of Person with the following changes:
1. Two new fields are added: calculatedField and newField.
2. Field typeChangeField is of type Integer, where in v1 it was of type String.
This service also contains an implementation of the SpaceTypeSchemaAdapter interface, used to adapt old v1 data to its new schema 

### The demo flow
The flow is composed of the following steps:
1. Setup - in this step connection data to the ElasticGrid is supplied
2. Start - deployment of v1, v2 and v1-feeder services.
3. Load v1 db to v2 - is this step a new v1 mirror is deployed, resulting in pause of v1 mongodb persistence. After mirror redeployment, a v2-load-v1-db service is deployed and data from v1 db is loaded and adapted to v2.
4. V1 traffic redirection to v2 - in this step a final v1 mirror is deployed, resulting in resumption of v1 db persistence. Also, incoming traffic to v1 is replicated and adapted to v2.

### How to run
The setup assumes there is an ElasticGrid deployed, and the GigaSpaces zip exists in demo machine.
1. In terminal, cd to root/scripts directory
2. Setup - run setup.sh and follow its flow. This will generate a new file called env.sh
3. Start - run demo-start.sh and follow its flow.
4. To stop the demo, run demo-stop.sh

Good Luck
 

